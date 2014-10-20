package com.github.walterfan.util.jms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.service.IService;



/**
 * @author walter
 * 
 */
public class JmsService implements IService {
    private static Log logger = LogFactory.getLog(JmsService.class);
    private boolean useJmx = true;
    private boolean persistent = true;
    private String dataDir = "./ext/activemq-data";
    private String brokerUrl = "tcp://localhost:61616";
    private String brokerName = "JmsService";
    private BrokerService broker = new BrokerService();

    private String userName;
    private String password;
    private String group = "users";

    public String getName() {
        return this.brokerName;
    }

    public BrokerService getBrokerService() {
        return this.broker;
    }

    /**
     * @param brokerUrl
     */
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * @param flag
     */
    public void setUseJmx(boolean flag) {
        this.useJmx = flag;
    }

    public void setPersistent(boolean flag) {
        this.persistent = flag;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void init() throws Exception {
        broker.setBrokerName(StringUtils.trim(this.brokerName));
        broker.setPersistent(this.persistent);
        broker.setUseJmx(this.useJmx);
        if (persistent) {
            // use shared file system file lock as the master evidence
            PersistenceAdapter pa = new KahaDBPersistenceAdapter();
            pa.setDirectory(new File(dataDir));
            broker.setPersistenceAdapter(pa);
        }
        broker.setUseShutdownHook(true);
        // Add plugin
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return;
        }
        AuthenticationUser user = new AuthenticationUser(userName, password,
                group);
        List<AuthenticationUser> list = new ArrayList<AuthenticationUser>(1);
        list.add(user);
        SimpleAuthenticationPlugin plugin = new SimpleAuthenticationPlugin(list);
        broker.setPlugins(new BrokerPlugin[] { plugin });
        logger.debug("userName=" + userName + ", password=" + password);

    }

    public int getQueueCount(Destination dest) {
        //this.broker.getDestination(dest);
        return 0;
    }
    
    public void clean() throws Exception {


    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("brokerName: " + this.brokerName);
        sb.append(", brokerUrl: " + this.brokerUrl);
        sb.append("\n group: " + this.group);
        sb.append(", userName: " + this.userName);
        sb.append(", password: " + this.password);

        sb.append("\n persistent: " + this.persistent);
        sb.append(", useJmx: " + this.useJmx);
        sb.append("\nstoreUsage: "
                + broker.getSystemUsage().getStoreUsage().toString());
        return sb.toString();
    }

    public boolean isStarted() {
        return broker.isStarted();
    }

    public void start() throws Exception {
        broker.addConnector(this.brokerUrl);
        broker.start();
        broker.waitUntilStarted();

    }

    public void stop() throws Exception {
        broker.stop();
        broker.waitUntilStopped();
    }

    public static void main(String[] args) throws Exception {
        JmsService svc = new JmsService();
        svc.init();
        svc.start();
        Thread.sleep(1000);
        System.out.println(svc.toString());
        Thread.sleep(360000);
        svc.stop();
        svc.clean();
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }
    
    
}
