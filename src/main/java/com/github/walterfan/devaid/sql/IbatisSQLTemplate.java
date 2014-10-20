package com.github.walterfan.devaid.sql;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.StatementType;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;


public class IbatisSQLTemplate extends SQLTemplate{
    
    private static Log logger = LogFactory.getLog(IbatisSQLTemplate.class);
    private SqlMapClient sqlMapper;

    public void loadFromStream(InputStream is) throws Exception {
        
        try {
            Map<String, SQLCommands> sqlsMap = super.getSqlMap();
            
            sqlMapper = SqlMapClientBuilder.buildSqlMapClient(is);

            SqlMapExecutorDelegate delegate = ((SqlMapClientImpl)sqlMapper).getDelegate();
            Iterator it = delegate.getMappedStatementNames();
            while(it.hasNext()) {
                String sqlID = (String)it.next();
                //logger.info("sqlID=" + sqlID);
                MappedStatement ms = delegate.getMappedStatement(sqlID);
                
                String resName =  ms.getResource();
                
                String sql = "";
                try {
                    sql = ms.getSql().getSql(new StatementScope(new SessionScope()), new Object());
                } catch(Exception e) {
                    logger.error(sqlID + " getSql error", e);
                    continue;
                }
                SQLCommands cmds = sqlsMap.get(resName);
                if (cmds == null) {
                    cmds = new SQLCommands();
                    cmds.setType(resName);
                    sqlsMap.put(resName, cmds);
                }
                SQLCommand cmd = new SQLCommand();
                cmd.setName(sqlID);
                cmd.setSql(StringUtils.trim(sql));
                cmds.addSQLCommand(cmd);
                logger.info("[" + resName + "] " + sqlID + " = " + sql);
                
            }            
        } finally {
            IOUtils.closeQuietly(is);
        }
    } 

    public String transStatementType(StatementType st) {
        if (st == st.UNKNOWN) {
            return "Unknown";
        } else if (st == st.SELECT) {
            return "Select";
        } else if (st == st.INSERT) {
            return "Insert";
        } else if (st == st.DELETE) {
            return "Delete";
        } else if (st == st.PROCEDURE) {
            return "Procedure";
        } else {
            return "Update";
        }
    }

    public static void main(String[] args) {
        // mapper.loadFromResource("com/googlecode/jwhat/exam/ibatis/SqlMapConfig.xml");
        String xml = "./src/main/webapp/WEB-INF/sqlmap-config.xml";
        
    	try {
            SQLTemplate template = new IbatisSQLTemplate();
            template.loadFromXml(xml);
            System.out.println("template=" + template);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
