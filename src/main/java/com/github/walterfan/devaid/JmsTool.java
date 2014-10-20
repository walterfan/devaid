package com.github.walterfan.devaid;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.github.walterfan.util.JmsUtils;
import com.github.walterfan.util.jms.JmsJmxClient;
import com.github.walterfan.util.jms.JmsSender;
import com.github.walterfan.util.jms.JmsService;
import com.github.walterfan.util.jms.MessageReceiver;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;

public class JmsTool extends SwingTool implements MessageListener {

    private static final long serialVersionUID = 1L;

    private class JmsListener implements MessageListener, ExceptionListener {

        public void onMessage(Message arg0) {
            ObjectMessage objMsg = (ObjectMessage) arg0;
            // JmsTool.this.txtAreaOut.append("\nreceived message");
            try {
                printInTextArea(JmsTool.this.txtAreaReceive, "received: "
                        + objMsg.getObject());
                if ("error".equals(objMsg.getObject() + "")) {
                    throw new UncategorizedJmsException(new RuntimeException(
                            "got error message"));
                }
            } catch (JMSException e) {
                SwingUtils.alert(e.getMessage());
            }
        }

        public void onException(JMSException jmsexception) {
            printInTextArea(JmsTool.this.txtAreaReceive, "error: "
                    + jmsexception.getMessage());

        }

    }

    private class JmsToolConfig {
        String brokerUrl = "tcp://localhost:61616";
        String brokerName = "JmsTool";
        String dataFolder = "./log/jms";
        String defaultTopic;
        String defaultQueue;
        boolean useJmx = true;
        boolean persistent = true;

        @Override
        public String toString() {
            return "brokerName=" + brokerName + ", brokerUrl=" + brokerUrl
                    + ", dataFolder=" + dataFolder + ", defaultQueue="
                    + defaultQueue + ", defaultTopic=" + defaultTopic
                    + ", persistent=" + persistent + ", useJmx=" + useJmx + "]";
        }

    }

    private class SendHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String msg = StringUtils.trim(JmsTool.this.txtAreaSend.getText());
            if (StringUtils.isEmpty(msg)) {
                SwingUtils.alert("Please fill the message to send");
                return;
            }
            JmsSender jmsSender = new JmsSender();
            String userName = txtUserName.getText();
            String password = txtPassword.getText();
            if (StringUtils.isNotBlank(userName)
                    && StringUtils.isNotBlank(password)) {
                jmsSender.setUserName(userName);
                jmsSender.setPassword(password);
            }

            try {
                jmsSender.setConnectionUrl(JmsTool.this.txtAreaUrl.getText());
                Destination dest = new org.apache.activemq.command.ActiveMQQueue(
                        JmsTool.this.txtAreaDest.getText());
                jmsSender.setDestination(dest);
                jmsSender.init();
                jmsSender.start();
                printInTextArea(JmsTool.this.txtAreaOut, "send message: " + msg);
                jmsSender.send(msg);
                printInTextArea(JmsTool.this.txtAreaOut, "sent message. ");
                jmsSender.stop();
                // jmsSender.clean();
            } catch (Exception e) {
                printInTextArea(txtAreaOut, "Error:\n" + e.getCause());
                e.printStackTrace();
            }
        }

    }

    private class ReceiveHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            MessageReceiver jmsReceiver = new MessageReceiver();

            String url = StringUtils.trim(JmsTool.this.txtAreaUrl.getText());
            String dest = StringUtils.trim(JmsTool.this.txtAreaDest.getText());
            String userName = txtUserName.getText();
            String password = txtPassword.getText();

            PooledConnectionFactory factory = JmsUtils
                    .createPooledConnectionFactory(url, userName, password);

            DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
            jmsContainer.setConnectionFactory(factory);

            JmsListener listener = new JmsListener();
            jmsContainer.setDestination(new ActiveMQQueue(dest));
            jmsContainer.setMessageListener(jmsReceiver);
            jmsContainer.setExceptionListener(jmsReceiver);
            jmsContainer.setTransactionTimeout(15);
            jmsContainer.setSessionTransacted(true);
            jmsContainer.setReceiveTimeout(5000);
            jmsContainer.setCacheLevelName("CACHE_NONE");
            jmsContainer.setAutoStartup(false);
            jmsContainer.setConcurrentConsumers(1);
            jmsContainer.setMaxConcurrentConsumers(1);

            jmsReceiver.setContainer(jmsContainer);
            jmsReceiver.setMessageListener(listener);

            try {
                jmsReceiver.init();
                jmsReceiver.start();
                printInTextArea(JmsTool.this.txtAreaOut,
                        "\nstart to receive message for 3 seconds... ");

                Thread.sleep(3000);
                jmsReceiver.stop();
                jmsReceiver.clean();
                printInTextArea(JmsTool.this.txtAreaOut,
                        "\nstopped message receiver");
            } catch (Exception e) {
                SwingUtils.alert(e.getMessage());
            }

        }

    }

    private class StartHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String userName = txtUserName.getText();
                    String password = txtPassword.getText();
                    String brokerName = txtAreaBroker.getText();
                    String brokerUrl = txtAreaUrl.getText();
                    if (StringUtils.isNotBlank(userName)
                            && StringUtils.isNotBlank(password)) {
                        jmsService.setUserName(userName);
                        jmsService.setPassword(password);
                    }
                    try {
                        jmsService.setBrokerName(brokerName);
                        jmsService.setBrokerUrl(brokerUrl);
                        jmsService.setDataDir(jmsCfg.dataFolder);
                        jmsService.setUseJmx(jmsCfg.useJmx);
                        jmsService.setPersistent(jmsCfg.persistent);
                        jmsService.init();
                        jmsService.start();
                        if(jmsService.isStarted()) {
                            printInTextArea(JmsTool.this.txtAreaOut,
                                "JmsService started " + jmsService.getBrokerName() + " by "
                                        + jmsService.getBrokerUrl() + "\n");
                       }
                    } catch (Exception e) {
                        SwingUtils.alert(e.getMessage());
                    }
                }
            });
        }
    }

    private class StopHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        jmsService.stop();
                        printInTextArea(
                                JmsTool.this.txtAreaOut,
                                jmsService.getName() + ": "
                                        + jmsService.isStarted());
                    } catch (Exception e) {
                        SwingUtils.alert(e.getMessage());
                    }
                }
            });
        }
    }

    private class InfoHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JmsJmxClient jmxClient = new JmsJmxClient();
            // "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
            try {
                jmxClient.connect();
                List<QueueViewMBean> aList = jmxClient
                        .getQueueViewMBeans(txtAreaBroker.getText());
                if (CollectionUtils.isEmpty(aList)) {
                    statusBar_.setText("There is no any queue");
                    return;
                }
                for (QueueViewMBean vb : aList) {
                    if (null == vb) {
                        continue;
                    }
                    statusBar_.setText("enqueue:" + vb.getEnqueueCount()
                            + ", dequeue:" + vb.getEnqueueCount());
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtils.alert("getQueueViewMBeans error:" + e);
            } finally {
                try {
                    jmxClient.close();
                } catch (Exception e1) {
                    SwingUtils.alert(e1.getMessage());
                }
            }
        }
    }

    private class ClearOutHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    txtAreaOut.setText("");
                }
            });
        }
    }
    
    private JmsToolConfig jmsCfg = new JmsToolConfig();

    private JButton btnStart = new JButton("Start");

    private JButton btnStop = new JButton("Stop");

    private JButton btnSend = new JButton("Send");

    private JButton btnReceive = new JButton("Receive");

    private JButton btnClearOut = new JButton("Clear Log");

    private JButton btnClearMsg = new JButton("Clear Msg");
    // rows, cols
    private JTextArea txtAreaDest = new JTextArea("WalterTestQueue", 2, 20);

    private JTextArea txtAreaSend = new JTextArea(5, 20);

    private JTextArea txtAreaReceive = new JTextArea(5, 20);

    private JTextArea txtAreaOut = new JTextArea(10, 20);

    private JTextArea txtAreaBroker = new JTextArea("TestJmsService", 2, 20);
    
    private JTextArea txtAreaUrl = new JTextArea("tcp://localhost:61616", 2, 20);

    private JTextField txtUserName = new JTextField("walter", 20);

    private JTextField txtPassword = new JTextField("pass", 20);

    private JButton btnInfo = new JButton("Information");

    private JmsService jmsService = new JmsService();

    /*
     * private JmsSender<String> jmsSender;
     * 
     * private JmsReceiver jmsReceiver = new JmsReceiver();
     */

    public JmsTool(String title) {
        super(title);
    }

    public void initComponents() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        JPanel pane = new JPanel();

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        // 3 columns and 3 rows
        double[] columnSize = { 400, 360 };
        double[] rowSize = { 100, 50, 50, 50, 200, 20};

        TableLayout layout = new TableLayout(columnSize, rowSize);
        layout.setHGap(3);
        pane.setLayout(layout);

        pane.add(SwingUtils.createVTextComponentPane("Connection Url: ",
                this.txtAreaUrl),    "0, 0, F, F");
        pane.add(SwingUtils.createVTextComponentPane("Broker Name: ",
                this.txtAreaBroker), "0, 1, F, F");
        pane.add(SwingUtils.createVTextComponentPane("User name: ",
                this.txtUserName), "0, 2, L, F");
        pane.add(SwingUtils.createVTextComponentPane("Password: ",
                this.txtPassword), "0, 3, L, F");
        pane.add(SwingUtils.createVTextComponentPane("Log: ", this.txtAreaOut),
                                   "0, 4, 0, 4, F, F");

        pane.add(SwingUtils.createVTextComponentPane("Destintion: ",
                this.txtAreaDest), "1, 0, f, f");
        pane.add(
                SwingUtils.createVTextComponentPane("Send: ", this.txtAreaSend),
                "1, 1, 1, 2, F, F");
        pane.add(SwingUtils.createVTextComponentPane("Receive: ",
                this.txtAreaReceive), "1, 3, 1, 4, F, F");
        pane.add(statusBar_, "0,5,1,5,F,F");
        statusBar_.setText("You can use it as publisher, consumer and broker");
        
        btnStart.addActionListener(new StartHandler());
        btnStop.addActionListener(new StopHandler());
        btnClearOut.addActionListener(ActionHandlerFactory.createClearHandler(this.txtAreaOut));
        btnClearMsg.addActionListener(ActionHandlerFactory.createClearHandler(this.txtAreaReceive));
        btnSend.addActionListener(new SendHandler());
        btnReceive.addActionListener(new ReceiveHandler());
        btnInfo.addActionListener(new InfoHandler());
        
        this.insertBtn2ToolbarAndMenu("information", btnInfo);
        this.insertBtn2ToolbarAndMenu("clear message", btnClearMsg);
        this.insertBtn2ToolbarAndMenu("clear output", btnClearOut);
        this.insertBtn2ToolbarAndMenu("receive", btnReceive);
        this.insertBtn2ToolbarAndMenu("send", btnSend);
        this.insertBtn2ToolbarAndMenu("stop", btnStop);
        this.insertBtn2ToolbarAndMenu("start", btnStart);

        container.add(pane, BorderLayout.CENTER);
    }

    public void onMessage(Message arg0) {
        this.txtAreaReceive.setText(arg0.toString());

    }
    
    public static void main(String[] args) {
        JmsTool tool = new JmsTool("JMS Tool v0.1");
        tool.init();
        SwingUtils.run(tool, 800, 600);
    }


}
