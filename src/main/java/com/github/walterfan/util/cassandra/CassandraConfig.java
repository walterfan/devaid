package com.github.walterfan.util.cassandra;


import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;

public class CassandraConfig {
    private String nodes;
    private int port;
    private String username;
    private String password;
    
    private PoolingOptions poolingOptions;
    private ReconnectionPolicy reconnectPolicy;
    private RetryPolicy retryPolicy = DefaultRetryPolicy.INSTANCE;
    private LoadBalancingPolicy loadBalancingPolicy;
    private boolean isJmxEnabled = false;
    
	public CassandraConfig(String nodes, int port, String username, String password) {
		super();
		this.nodes = nodes;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public String getNodes() {
		return nodes;
	}
	public void setNodes(String nodes) {
		this.nodes = nodes;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public PoolingOptions getPoolingOptions() {
		return poolingOptions;
	}
	public void setPoolingOptions(PoolingOptions poolingOptions) {
		this.poolingOptions = poolingOptions;
	}
	public ReconnectionPolicy getReconnectPolicy() {
		return reconnectPolicy;
	}
	public void setReconnectPolicy(ReconnectionPolicy reconnectPolicy) {
		this.reconnectPolicy = reconnectPolicy;
	}
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}
	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}
	public LoadBalancingPolicy getLoadBalancingPolicy() {
		return loadBalancingPolicy;
	}
	public void setLoadBalancingPolicy(LoadBalancingPolicy loadBalancingPolicy) {
		this.loadBalancingPolicy = loadBalancingPolicy;
	}

	public boolean isJmxEnabled() {
		return isJmxEnabled;
	}

	public void setJmxEnabled(boolean isJmxEnabled) {
		this.isJmxEnabled = isJmxEnabled;
	}
    
    
}
