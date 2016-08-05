package com.github.walterfan.util.cassandra;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;

import com.github.walterfan.util.ConfigLoader;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

public class CassandraConnection {
	
	private static Log logger = LogFactory.getLog(CassandraConnection.class);
	
	private Cluster cluster;
	private Session session;

	private volatile boolean isInited = false;
	private volatile boolean isConnected = false;
	
	private final CassandraConfig config;

	public CassandraConnection(CassandraConfig casCfg) {
		this.config = casCfg;
	}

	public int init() {
		if (isInited) {
			return 0;
		}
		Builder builder = Cluster.builder().addContactPoints(config.getNodes().split(","))
				.withCredentials(config.getUsername(), config.getPassword()).withPort(config.getPort());
		// .build();

		if (null != config.getReconnectPolicy())
			builder = builder.withReconnectionPolicy(config.getReconnectPolicy());

		if (null != config.getRetryPolicy())
			builder = builder.withRetryPolicy(config.getRetryPolicy());

		if (null != config.getPoolingOptions())
			builder = builder.withPoolingOptions(config.getPoolingOptions());

		if (null != config.getLoadBalancingPolicy())
			builder = builder.withLoadBalancingPolicy(config.getLoadBalancingPolicy());

		if (!config.isJmxEnabled())
			builder = builder.withoutJMXReporting();

		cluster = builder.build();

		
		if(logger.isDebugEnabled()) {
			Metadata metadata = cluster.getMetadata();
			logger.debug(String.format("Connected to cluster: %s\n", metadata.getClusterName()));
			for (Host host : metadata.getAllHosts()) {
				logger.debug(String.format("Datatacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(),
						host.getRack()));
			}
		}
		isInited = true;
		return 0;
	}

	public void connect() {
		init();

		if (isConnected) {
			return;
		}

		if (null == session) {
			session = cluster.connect();
			this.isConnected = true;
		}
	}

	public void close() {
		cluster.close();
		this.isConnected = false;
		this.session = null;
	}

	public ResultSet executeSql(String sql) {
		if (null == session)
			return null;
		return session.execute(sql);

	}

	public ResultSet executeStmt(Statement stmt) {
		if (null == session)
			return null;
		return session.execute(stmt);

	}

	//Follow markdown table format
	public static String resultSetToString(ResultSet rs) {
		StringBuilder sb = new StringBuilder();
		List<Row> list = rs.all();
		int titleFlag = 0;
		for (Row row : list) {
			ColumnDefinitions defs = row.getColumnDefinitions();
			int colSize = defs.size();

			if (titleFlag == 0) {
				for (int i = 0; i < colSize; ++i) {
					sb.append("| ");
					sb.append(defs.getName(i));
					sb.append("(");
					sb.append(defs.getType(i).getName());
					sb.append(") ");
				}
				sb.append("| \n");
				
				for (int i = 0; i < colSize; ++i) {
					sb.append("|------------");
				}
				sb.append("|\n");
				titleFlag = 1;
			}

			for (int i = 0; i < colSize; ++i) {
				sb.append("| ");
				DataType dataType = defs.getType(i);

				if (dataType == DataType.bigint() || dataType == DataType.decimal()) {
					sb.append(row.getLong(i));
				} else if (dataType == DataType.cint()) {
					sb.append(row.getInt(i));
				} else if (dataType == DataType.text() || dataType == DataType.varchar()) {
					sb.append(row.getString(i));
				} else if (dataType == DataType.uuid()) {
					sb.append(row.getUUID(i));
				} else if (dataType == DataType.inet()) {
					sb.append(row.getInet(i));
				} else if (dataType == DataType.timestamp()) {
					sb.append(row.getDate(i));
				} else {
					sb.append(row.getBytesUnsafe(i));

				}
				sb.append(" ");
			}
			sb.append("| \n");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		ConfigLoader cfgLoader = ConfigLoader.getInstance();
		try {
			cfgLoader.loadFromClassPath("jdbc.properties");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		CassandraConfig cfg = new CassandraConfig(cfgLoader.get("cassandra.nodes"),
				cfgLoader.getIntProperty("cassandra.port", 9042), cfgLoader.get("cassandra.username"),
				cfgLoader.get("cassandra.password"));
		CassandraConnection conn = new CassandraConnection(cfg);
		conn.connect();
		
		ResultSet rs = conn.executeSql("select keyspace_name, columnfamily_name from system.schema_columnfamilies");
		if (null != rs) {
			String strRet = resultSetToString(rs);
			System.out.println(strRet);
		}

		rs = conn.executeSql("select  * from system.local limit 10");
		if (null != rs) {
			String strRet = resultSetToString(rs);
			System.out.println(strRet);
		}
		conn.close();

	}

}
