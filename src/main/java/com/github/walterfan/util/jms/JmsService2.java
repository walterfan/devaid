package com.github.walterfan.util.jms;

import java.net.URI;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.service.IService;


/**
 * @author walter
 *
 */
public class JmsService2 implements IService {
    private static Log logger = LogFactory.getLog(JmsService2.class);

    private BrokerService broker;

    public JmsService2() {

    }

    public String getName() {
        return "JmsService";
    }

    public BrokerService getBrokerService() {
        return this.broker;
    }

    public void init() throws Exception {
        broker = BrokerFactory.createBroker(new URI("xbean:activemq.xml"));
    }

    public void clean() throws Exception {

    }

    public String toString() {
        return broker.getSystemUsage().getStoreUsage().toString();
    }

    public boolean isStarted() {
        return broker.isStarted();
    }

    public void start() throws Exception {
        broker.start();
        broker.waitUntilStarted();

    }

    public void stop() throws Exception {
        broker.stop();
        broker.waitUntilStopped();
    }

    public static void main(String[] args) throws Exception {
        JmsService2 svc = new JmsService2();
        svc.init();
        svc.start();
        Thread.sleep(360000);
        svc.stop();
        svc.clean();
    }
}
