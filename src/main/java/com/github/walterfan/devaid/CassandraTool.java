package com.github.walterfan.devaid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.github.walterfan.util.ByteUtil;
import com.github.walterfan.util.IKvStore;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.BinaryFileLoadHandler;
import com.github.walterfan.util.swing.FileSelectHandler;
import com.github.walterfan.util.swing.SpringUtilities;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;


public class CassandraTool extends SwingTool {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1900606129100906010L;

    private IKvStore kvStore;
    
    private volatile boolean isKvInited = false;
    
    private class RecentListSelectionListener implements ListSelectionListener {
        // This method is called each time the user changes the set of selected
        // items
        public void valueChanged(ListSelectionEvent evt) {
            // When the user release the mouse button and completes the
            // selection,
            // getValueIsAdjusting() becomes false
            if (!evt.getValueIsAdjusting()) {
                JList list = (JList) evt.getSource();

                // Get all selected items
                Object[] selected = list.getSelectedValues();

                /*
                 * // Iterate all selected items for (int i=0;
                 * i<selected.length; i++) { Object sel = selected[i]; }
                 */
                if (selected != null && selected.length > 0) {
                    txtKey.setText((String) selected[0]);
                }
            }
        }
    }

    
    private class GetHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                initKvStore();
                String key = StringUtils.trim(txtKey.getText());
                if(StringUtils.isEmpty(key)) {
                    SwingUtils.alert("please input a key");
                    return;
                }
                pushRecentList(key);
                Serializable ret = kvStore.get(key);
                if(null == ret) {
                    txtVal.setText("null");
                    return;
                }
                
                if(StringUtils.isNotBlank(txtFile.getText())) {
                    String fileName = StringUtils.trim(txtFile.getText());
                    File file = new File(fileName);
                    FileUtils.writeByteArrayToFile(file, ByteUtil.object2Bytes(ret));
                }
                
                txtVal.setText(ret.toString());
                //msgLine.setText("lastModified on " + DateFormatUtils.format(ret.getLastModifiedTime(),"yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e1) {                
               SwingUtils.alert(e1.getMessage());
            }


        }
    }

    private class SetHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                initKvStore();
                String key = StringUtils.trim(txtKey.getText());
                if(StringUtils.isEmpty(key)) {
                    SwingUtils.alert("please input a key");
                    return;
                }
                String fileName = StringUtils.trim(txtFile.getText());
                if(StringUtils.isEmpty(fileName)) {
                    SwingUtils.alert("Please input a binary file to set value");
                    return;
                }
                    
               File file = new File(fileName);
               byte[] bytes = FileUtils.readFileToByteArray(file);
               Serializable ret = ByteUtil.bytes2Object(bytes);
               //if(ret instanceof ITimingValue) { 
                   kvStore.set(key, ret); 
              // } else {
              //     SwingUtils.alert("it's not ITimingValue, save it as TimingValue");
              //     TimingValue tv = new TimingValue();
              //     tv.setValue(ret);
              //     kvStore.set(key, tv);
              // } 
            } catch (Exception e1) {                
               SwingUtils.alert(e1.getMessage());
            }


        }
    }

    private class RemoveHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                initKvStore();
                String key = StringUtils.trim(txtKey.getText());
                if(StringUtils.isEmpty(key)) {
                    SwingUtils.alert("please input a key");
                    return;
                }
                int answer = SwingUtils.yesOrNo("Are you sure", "Do you want to delete the key" + key + " really?");
                if(answer != 0 ) {
                    return;
                }
                kvStore.remove(key);
                SwingUtils.prompt("result", key + " is removed");
            } catch (Exception e1) {                
               SwingUtils.alert(e1.getMessage());
            }

 
        }
    }
    
    private class ReconfigHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                cleanKvStore();
                keySet.clear();
                DefaultListModel model = (DefaultListModel) keyList.getModel();
                while (model.getSize() > 0) {
                    model.remove(0);
                }
            } catch (Exception e1) {                
               SwingUtils.alert(e1.getMessage());
            }


        }
    }
    
    private Set<String> keySet= new HashSet<String>(10);
    
    private JList keyList = new JList(new DefaultListModel());
    
    JTextField txtHost = new JTextField(20);

    JTextField txtPort = new JTextField(20);
    
    JTextField txtToken = new JTextField(20);

    JTextField txtLocation = new JTextField(20);
    
    JTextArea txtKey = new JTextArea(2,20);
    
    JTextArea txtVal = new JTextArea(10,20);
    
    JTextArea txtFile = new JTextArea(2,20);
    
    private JButton btnGet = createOrangeButton("Get", "Get value by key", 24, 24);
    
    private JButton btnSet = createOrangeButton("Set", "Set value by key", 24, 24);
    
    private JButton btnRemove = createOrangeButton("Remove", "Remove value by key", 24, 24);
    
    private JButton btnConfig = createOrangeButton("Reset", "Change Config of KV", 24, 24);
    
    private JButton btnClear = createOrangeButton("Clear", "Get value by key", 24, 24);
    
    private JButton btnSelect = createButton("Select File", "Select file for Set method", 24, 24);
    
    private JButton btnLoad = createButton("Load File", "Load value from binary file", 24, 24);
    
    private JButton btnExit = createButton("Exit", "Get value by key", 24, 24);
    
    private JTextField msgLine = new JTextField(""); // Displays messages
    
    public CassandraTool(String title) {
        super(title);
    }
    
    private JPanel createForm(String[] labels, JComponent[] fields) {       
        int numPairs = labels.length;

        //Create and populate the panel.
        JPanel cfgPanel = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel lbl = new JLabel(labels[i], JLabel.TRAILING);
            cfgPanel.add(lbl);
            JComponent textField = fields[i];
            lbl.setLabelFor(textField);
            cfgPanel.add(textField);
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(cfgPanel,
                                        numPairs, 2, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad


        
        return cfgPanel;
    }
    
    private JPanel createRecentList() {
        keyList.setVisibleRowCount(5);
        keyList.ensureIndexIsVisible(2);
        keyList.setSelectedIndex(0);
        keyList.setFixedCellWidth(60);
        keyList.addListSelectionListener(new RecentListSelectionListener());
        keyList.setBackground(DEFAULT_COLOR);
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        JLabel lblTitle = new JLabel("Recent keys", SwingConstants.LEFT);
        JScrollPane listPane = new JScrollPane(keyList);
        pane.add(lblTitle, BorderLayout.NORTH);
        pane.add(listPane, BorderLayout.CENTER);
        return pane;
    }

    private void pushRecentList(String key) {
        DefaultListModel model = (DefaultListModel) keyList.getModel();
        boolean isNew = keySet.add(key);
        if (isNew) {
            model.add(model.getSize(), key);
        }
    }




    
    
    @Override
    public void initComponents() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        
        String[] labels = {"Host: ", "Port: ", "Token: ", "Location: "};
        JTextComponent[] fields = {txtHost, txtPort, txtToken , txtLocation};
        JPanel cfgPane = this.createForm(labels, fields);        
        JPanel recentPane = this.createRecentList();
        JSplitPane topLeftPane = SwingUtils.createVSplitPane(cfgPane, recentPane, 120);
        
        JPanel keyPane = SwingUtils.createVTextComponentPane("Key: ", txtKey);
        JPanel valPane = SwingUtils.createVTextComponentPane("Value: ", txtVal);
        JSplitPane kvPane = SwingUtils.createVSplitPane(keyPane, valPane, 120);
        JPanel filePane = SwingUtils.createVTextComponentPane("From/To File", txtFile);
        
        JPanel kvfPane = new JPanel();
        kvfPane.setLayout(new BorderLayout());
        kvfPane.add(kvPane, BorderLayout.CENTER);
        kvfPane.add(filePane, BorderLayout.SOUTH);
        //String[] kvLabels = {"Key: ", "Value: ", "From/To File"};
        //JComponent[] kvFields = {SwingUtils.createScrollPane(txtKey, SwingUtils.DEFAULT_FONT, SwingUtils.DEFAULT_COLOR),
         //       SwingUtils.createScrollPane(txtVal, SwingUtils.DEFAULT_FONT, SwingUtils.DEFAULT_COLOR), 
         //       SwingUtils.createScrollPane(txtFile, SwingUtils.DEFAULT_FONT, SwingUtils.DEFAULT_COLOR)};
        //JPanel kvPane = this.createForm(kvLabels, kvFields);
        
        JSplitPane topPane = SwingUtils.createHSplitPane(topLeftPane, kvfPane, 300);
        
        //JPanel filePane = SwingUtils.createHTextComponentPane(" from/to ", txtFile);
        

        this.btnGet.addActionListener(new GetHandler());
        this.btnSet.addActionListener(new SetHandler());
        this.btnRemove.addActionListener(new RemoveHandler());
        this.btnConfig.addActionListener(new ReconfigHandler());
        this.btnClear.addActionListener(ActionHandlerFactory.createClearHandler(txtVal));
        
        insertBtn2ToolbarAndMenu("Clear", btnClear);
        insertBtn2ToolbarAndMenu("Reconfig", btnConfig);
        insertBtn2ToolbarAndMenu("Remove", btnRemove);
        insertBtn2ToolbarAndMenu("Set", btnSet);
        JMenuItem getItem = insertBtn2ToolbarAndMenu("Get", btnGet);
        getItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0,
                false));
        
        //JPanel bottomPane = SwingUtils.createHorizontalPanel(new JComponent[]{
        //        btnGet, btnSet, btnRemove, btnConfig, btnClear});        
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BorderLayout());
        bottomPane.add(msgLine, BorderLayout.CENTER);

        
        btnSelect.addActionListener(new FileSelectHandler(this.txtFile, this));
        btnLoad.addActionListener(new BinaryFileLoadHandler(this.txtVal, this));
        btnExit.addActionListener(ActionHandlerFactory.createExitHandler());
        
        JPanel bottomLeftPanel = SwingUtils
                .createHorizontalPanel(new Component[] {
                        btnSelect, btnLoad});
        bottomPane.add(bottomLeftPanel, BorderLayout.WEST);
        bottomPane.add(btnExit, BorderLayout.EAST);
        
        mainPane.add(topPane, BorderLayout.CENTER);
        mainPane.add(bottomPane, BorderLayout.SOUTH);
        
        container.add(mainPane, BorderLayout.CENTER);
    }

    
    
    
    public void initKvStore() throws Exception {
        if(isKvInited) {
            return;
        }
        String host = this.txtHost.getText();
        int port = NumberUtils.toInt(this.txtPort.getText());
        String token = this.txtToken.getText();
        String location = this.txtLocation.getText();
        //this.kvStore = new CassandraStore(host, port, location, token);
        //this.kvStore.init();
        isKvInited = true;
    }
    
    public void cleanKvStore() throws Exception {
        if(null == this.kvStore) {
            return;
        }
        if(isKvInited) {
            this.kvStore.close();
            isKvInited = false;
        }
    }
    
    public void init() {
        super.init();
        String server = "10.224.55.35";
        int port = 12620;
        String token = "KCojQCRIREZJa2RoQCQpJkAjXnczaWhm";
        String location = "HF..TestGW";
        this.txtHost.setText(server);
        this.txtPort.setText(""+port);
        this.txtToken.setText(token);
        this.txtLocation.setText(location);
        
       
    }
    
    
    
    public void setKvStore(IKvStore kvStore) {
        this.kvStore = kvStore;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        CassandraTool kvTool = new CassandraTool("Cassandra Store Tool v1.0");
        kvTool.init();
        SwingUtils.run(kvTool,800,600);
    }

}
