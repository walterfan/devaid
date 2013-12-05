package com.github.walterfan.devaid.sql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.github.walterfan.util.ClassPathHacker;
import com.github.walterfan.util.ConfigLoader;
import com.github.walterfan.util.StringUtil;
import com.github.walterfan.util.db.DbConfig;
import com.github.walterfan.util.io.MyFilter;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;
import com.github.walterfan.util.TextTransfer;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author walter wrote on 07/11/09 based ResultSetTableModel
 **/

public class SQLTool extends SwingTool {

    private static final long serialVersionUID = -5757194742469820513L;

    private static final String NAME_VER = "SQL Tool v1.0";
    private static final Color DEFAULT_COLOR = new Color(0x99, 0xFF, 0xCC);

    private MyFilter sqlFileFilter = new MyFilter(new String[] {
        "sql" }, "sql");

    private MyFilter csvFileFilter = new MyFilter(new String[] {
        "csv" }, "csv");

    private MyFilter xmlFileFilter = new MyFilter(new String[] {
        "xml" }, "xml");

    private ConfigLoader cfgLoader = ConfigLoader.getInstance();

    private class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {

            int keyCode = evt.getKeyCode();
            // System.out.println("keyCode=" + keyCode);
            if (keyCode == KeyEvent.VK_F5) {
                executeSql();
            } else if (keyCode == KeyEvent.VK_F6) {
                executeSqlBlock();
            }
        }
    }

    private class ResetHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            txtSQL.setText("");
            while (sqlTab.getColumnCount() > 0) {
                sqlTab.removeColumn(sqlTab.getColumnModel().getColumn(0));
            }

            setSql.clear();
            DefaultListModel model = (DefaultListModel) sqlList.getModel();
            while (model.getSize() > 0) {
                model.remove(0);
            }

            modelFactory.close();
        }
    }

    private class CloseHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            modelFactory.close();
            msgline.setText("Closed connection.");
        }
    }

    private class ExitHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (modelFactory != null) {
                modelFactory.close();
            }
            System.exit(0);
        }
    }

    private class ExecuteScriptHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // CallableStatement cstmt = (CallableStatement)
            // conn.prepareCall(sqlblock);
            executeSqlBlock();
        }
    }

    private class CommitHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            executeAndDisplayResults("commit");
        }
    }

    private class RollbackHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            executeAndDisplayResults("rollback");
        }
    }

    private class LoadSqlTplHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser("./etc");
            c.setFileFilter(xmlFileFilter);
            int rVal = c.showOpenDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                File file = c.getSelectedFile();
                curSqlTplName = file.getAbsolutePath();

                sqlTemplate = null;
                sqlTemplate = new SQLTemplate();
                try {
                    SQLTool.this.createSqlMenuAndTree(curSqlTplName);
                    sqlMenu.repaint();
                    SwingUtils.prompt("Load SQL", "loaded " + curSqlTplName);

                    int fsize = recentSqlFiles.size();
                    if (fsize == 0) {
                        fileMenu.addSeparator();
                    } else if (fsize > 4) {
                        recentSqlFiles.removeFirst();
                    }
                    recentSqlFiles.addLast(curSqlTplName);
                    JMenuItem tplItem = new JMenuItem(curSqlTplName);
                    fileMenu.add(tplItem);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            }
        }

    }

    private class LoadIbatisCfgHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String classpath = cfgLoader.getProperty("iBatisClassPath");
            if (StringUtils.isNotBlank(classpath)) {
                try {
                    ClassPathHacker.addFile(new File(classpath));
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
            JFileChooser c = null;
            String filename = cfgLoader.getProperty("iBatisSqlTemplate");
            if (StringUtils.isNotBlank(filename)) {
                String dir = FilenameUtils.getPath(filename);
                c = new JFileChooser(dir);
            } else {
                c = new JFileChooser("./etc");
            }
            c.setFileFilter(xmlFileFilter);
            int rVal = c.showOpenDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                File file = c.getSelectedFile();
                curSqlTplName = file.getAbsolutePath();

                sqlTemplate = new IbatisSQLTemplate();
                try {
                    SQLTool.this.createSqlMenuAndTree(curSqlTplName);
                    sqlMenu.repaint();
                    SwingUtils.prompt("Load SQL", "loaded " + curSqlTplName);

                    // if(!recentIbatisFiles.isEmpty()) {
                    int fsize = recentIbatisFiles.size();
                    if (fsize == 0) {
                        fileMenu.addSeparator();
                    } else if (fsize > 4) {
                        recentIbatisFiles.removeFirst();
                    }
                    recentIbatisFiles.addLast(curSqlTplName);
                    JMenuItem tplItem = new JMenuItem(curSqlTplName);
                    fileMenu.add(tplItem);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            }
        }

    }

    private class ReloadSqlTplHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String filename = SQLTool.this.curSqlTplName;
            try {
                SQLTool.this.createSqlMenuAndTree(filename);
                sqlMenu.repaint();
                SwingUtils.prompt("Reload SQL", "reloaded " + filename);
            } catch (Exception e1) {
                e1.printStackTrace();
                SwingUtils.alert(e1.getMessage());
            }

        }

    }

    private class LoadHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser("./log");
            c.setFileFilter(sqlFileFilter);
            int rVal = c.showOpenDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                File file = c.getSelectedFile();
                try {
                    java.util.List<String> aList = FileUtils.readLines(file);

                    DefaultListModel model = (DefaultListModel) sqlList
                            .getModel();
                    for (String aSql : aList) {
                        String sql = com.github.walterfan.util.StringUtil
                                .trimTrailingCharacter(aSql, ';');
                        boolean isNew = SQLTool.this.setSql.add(sql);
                        if (isNew) {
                            model.add(model.getSize(), sql);
                        }
                    }
                    SwingUtils.prompt("Load SQL", "loaded " + aList.size()
                            + " SQL.");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            }
        }
    }

    private class AddPathHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser(cfgLoader.getProperty(
                    "classpath", "/"));
            // c.setFileFilter(sqlFileFilter);
            c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            c.setDialogTitle("Select path into classpath");
            int rVal = c.showOpenDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                File file = c.getSelectedFile();
                try {
                    ClassPathHacker.addFile(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            }
        }
    }

    private class ExportHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TableModel model = sqlTab.getModel();
            if (model.getRowCount() == 0) {
                SwingUtils.alert("There is no data.");
                return;
            }

            JFileChooser c = new JFileChooser("./log");
            c.setFileFilter(csvFileFilter);
            int rVal = c.showSaveDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                FileOutputStream fs = null;
                try {
                    String filename = c.getSelectedFile().getAbsolutePath();
                    fs = new FileOutputStream(filename);

                    String resultType = (String) resultTypeList
                            .getSelectedItem();
                    if ("CSV".equals(resultType)) {
                        saveTableModelAsCsv(fs, model);
                    } else {
                        saveTableModelAsSql(fs, model);
                    }

                    SwingUtils.prompt("Saved CSV", "saved " + filename);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                } catch (IOException e2) {
                    e2.printStackTrace();
                    SwingUtils.alert(e2.getMessage());
                } finally {
                    IOUtils.closeQuietly(fs);
                }
            }

        }
    }

    private class CopyHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TableModel model = sqlTab.getModel();
            if (model.getRowCount() == 0) {
                SwingUtils.alert("There is no data.");
                return;
            }

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            try {
                String resultType = (String) resultTypeList.getSelectedItem();
                if ("CSV".equals(resultType)) {
                    saveTableModelAsCsv(bs, model);
                } else {
                    saveTableModelAsSql(bs, model);
                }

            } catch (IOException e2) {
                e2.printStackTrace();
                SwingUtils.alert(e2.getMessage());
            }

            SwingUtils.prompt("Copied to Clipboard", "Copied to Clipboard");

            TextTransfer trans = new TextTransfer();
            trans.setClipboardContents(bs.toString());

        }
    }

    private class SaveHandler implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser c = new JFileChooser("./log");
            c.setFileFilter(sqlFileFilter);
            int rVal = c.showSaveDialog(SQLTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                FileOutputStream fs = null;
                try {
                    String filename = c.getSelectedFile().getAbsolutePath();
                    fs = new FileOutputStream(filename);
                    if (setSql.isEmpty()
                            && StringUtils.isNotBlank(txtSQL.getText())) {
                        fs.write(txtSQL.getText().getBytes());
                        fs.write(";\n".getBytes());
                    } else {
                        for (String sql : setSql) {
                            fs.write(sql.getBytes());
                            fs.write(";\n".getBytes());
                        }
                    }

                    SwingUtils.prompt("Saved SQL", "saved " + filename);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                } catch (IOException e2) {
                    e2.printStackTrace();
                    SwingUtils.alert(e2.getMessage());
                } finally {
                    IOUtils.closeQuietly(fs);
                }
            }

        }
    }

    private class SqlMenuHandler implements ActionListener {
        private String sqlType;
        private String sqlName;

        public SqlMenuHandler(String sqlType, String sqlName) {
            this.sqlType = sqlType;
            this.sqlName = sqlName;
        }

        public void actionPerformed(ActionEvent event) {
            SQLCommand cmd = sqlTemplate.getSQLCommand(sqlType, sqlName);
            txtSQL.setText(cmd.getSql());
        }
    }

    private class SubmitHandler implements ActionListener {

        // This method is invoked when the user hits ENTER in the field
        public void actionPerformed(ActionEvent e) {
            executeSql();

        }
    }

    private class DbListHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = (String) dbList.getSelectedItem();
            // SwingUtils.alert(name);
            JdbcConfig cfg = jdbcFactory.get(name);
            if (cfg == null) {
                return;
            }
            txtDriver.setText(cfg.getDriverClass());
            // txtDbType.setHorizontalAlignment(JTextField.LEFT);
            txtDbType.setText(cfg.getType());
            txtUrl.setText(cfg.getUrl());
            txtUsername.setText(cfg.getUserName());
            txtPassword.setText(cfg.getPassword());

        }
    }

    private class SQLListSelectionListener implements ListSelectionListener {
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
                    txtSQL.setText((String) selected[0]);
                }
            }
        }
    }

    private class SelectTreeHandler implements TreeSelectionListener {
        private SQLCmdTreePane cmdTreePane;

        public SelectTreeHandler(SQLCmdTreePane pane) {
            this.cmdTreePane = pane;
        }

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTreePane
                    .getHttpCmdTree().getLastSelectedPathComponent();

            if (node != null) {

                Object nodeInfo = node.getUserObject();
                if (node.isLeaf()) {
                    SQLCommand cmd = (SQLCommand) nodeInfo;
                    txtSQL.setText(cmd.getSql());

                }
            }
        }
    };    

    private JdbcConfigFactory jdbcFactory = null;

    private DbConfig currentDbCfg = null;

    private SQLTemplate sqlTemplate = new SQLTemplate();

    private Set<String> setSql = new HashSet<String>(10);

    private int defaultIdx = 0;

    private String defaultSql = "select sysdate from dual";

    private String curSqlTplName = "SQLTemplate.xml";

    private LinkedList<String> recentSqlFiles = new LinkedList<String>();

    private LinkedList<String> recentIbatisFiles = new LinkedList<String>();

    private JMenu fileMenu = new JMenu("Usage");

    private JMenu sqlMenu = new JMenu("SQL");

    private JList sqlList = null;

    private JMenuBar menuBar = new JMenuBar();

    private JPanel sqlPanel = new JPanel(new BorderLayout());

    private JScrollPane sqlTreePane;

    ResultSetTableModelFactory modelFactory; // A factory to obtain our table
                                             // data

    JComboBox dbList = null;

    JTextField txtDriver = new JTextField();

    JTextField txtDbType = new JTextField();

    JTextField txtUsername = new JTextField();

    JPasswordField txtPassword = new JPasswordField();

    JTextArea txtUrl = new JTextArea();

    JButton btnSubmit = new JButton("execute");
    JButton btnReset = new JButton("clear");
    JButton btnSave = new JButton("save");
    JButton btnCommit = new JButton("commit");
    JButton btnRollback = new JButton("rollback");
    JButton btnLoad = new JButton("load");
    JButton btnClose = new JButton("close");
    JButton btnCopy = new JButton("copy");
    JButton btnExport = new JButton("export");
    JButton btnExit = new JButton("exit");

    private JComboBox resultTypeList;
    // Create the Swing components we'll be using
    JTextArea txtSQL = new JTextArea(4, 240); // Lets the user enter a query

    JTable sqlTab = new JTable() {
    	public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);
            tip = "" + this.getModel().getValueAt(rowIndex,realColumnIndex) ;
            tip = "<html>" +  StringUtil.nl2br(StringEscapeUtils.escapeHtml(tip)) + "</html>";
            return tip;
        }
    	
    	/*public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == wordWrapColumnIndex ) {
                return wordWrapRenderer;
            }
            else {
                return super.getCellRenderer(row, column);
            }
        }*/
    };

    JTextArea msgline = new JTextArea(); // Displays messages

    public SQLTool() {
        this(NAME_VER);
    }

    public SQLTool(String title) {
        this(title, new ResultSetTableModelFactory());
    }

    /**
     * This constructor method creates a simple GUI and hooks up an event
     * listener that updates the table when the user enters a new query.
     **/
    public SQLTool(String title, ResultSetTableModelFactory factory) {
        super(title); // Set window title
        this.modelFactory = factory;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(true);
        addKeyListener(new MyKeyListener());

        loadConfig();
        createDbList();
        createResultTypeList();

        // Place the components within this window
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel cfgPanel = createCfgPanel();
        //JPanel btnPanel = createButtonPanel();
        JPanel sqlPanel = createSqlPanel();

        JSplitPane splitPane = createtSplitPanel(cfgPanel, sqlPanel);
        splitPane.setPreferredSize(new Dimension(this.getWidth(), 140));

        JScrollPane sqlTabPane = new JScrollPane(sqlTab, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        sqlTabPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        sqlTab.setBackground(DEFAULT_COLOR);
        sqlTab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JSplitPane topPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                splitPane, sqlTabPane);
        topPane.setOneTouchExpandable(true);
        topPane.setDividerLocation(300);

        // contentPane.add(splitPane, BorderLayout.NORTH);
        contentPane.add(topPane, BorderLayout.CENTER);

        // add popup menu of right click
        JPopupMenu rightMenu = SwingUtils
                .createStdEditPopupMenu(new JTextComponent[] {
                    txtSQL });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        msgline.setRows(2);
        JScrollPane msgPane = new JScrollPane(msgline);
        bottomPanel.add(msgPane, BorderLayout.CENTER);

        btnCopy.addActionListener(new CopyHandler());
        btnExport.addActionListener(new ExportHandler());
        btnExit.addActionListener(new ExitHandler());

        JPanel bottomLeftPanel = SwingUtils
                .createHorizontalPanel(new Component[] {
                        btnCopy, btnExport, resultTypeList });
        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);
        bottomPanel.add(btnExit, BorderLayout.EAST);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        // Now hook up the JTextField so that when the user types a query
        // and hits ENTER, the query results get displayed in the JTable

        super.init();
    }

    private void createResultTypeList() {
        Vector<String> vec = new Vector<String>();
        vec.add("CSV");
        vec.add("SQL");
        resultTypeList = new JComboBox(vec);
    }

    private void loadConfig() {

        try {
            cfgLoader.loadFromClassPath("SQLToolConfig.properties", this
                    .getClass().getClassLoader());
            defaultSql = cfgLoader.getProperty("defaultSql", defaultSql);
            defaultIdx = NumberUtils.toInt(cfgLoader
                    .getProperty("defaultIndex"));
            curSqlTplName = cfgLoader.getProperty("defaultSqlTemplate",
                    curSqlTplName);
            // ibatisSqlTemplate = cfgLoader.getProperty("ibatisSqlTemplate");

            jdbcFactory = JdbcConfigFactory.deserialize();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            jdbcFactory = JdbcConfigFactory.createJdbcConfigFactory();
        }

    }

    private JPanel createSqlPanel() {
        // JSplitPane splitPane = createtSplitPanel(cfgPanel, sqlPanel);
        // splitPane.setPreferredSize(new Dimension(this.getWidth(),140))

        sqlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        txtSQL.setText(defaultSql);
        txtSQL.setLineWrap(true);
        txtSQL.setBackground(DEFAULT_COLOR);
        //txtSQL.setPreferredSize(new Dimension(240, this.getHeight()));
        
        sqlList = new JList(new DefaultListModel());
        sqlList.setVisibleRowCount(5);
        sqlList.ensureIndexIsVisible(2);
        sqlList.setSelectedIndex(0);
        sqlList.setFixedCellWidth(60);
        sqlList.addListSelectionListener(new SQLListSelectionListener());
        sqlList.setBackground(DEFAULT_COLOR);

        return sqlPanel;
    }

    private void createDbList() {
        java.util.List<JdbcConfig> list = jdbcFactory.getConfigList();
        Vector<String> vec = new Vector<String>(list.size());
        for (int i = 0; i < list.size(); i++) {
            JdbcConfig cfg = list.get(i);
            if (cfg == null) {
                continue;
            }
            if (i == defaultIdx) {

                txtDriver.setText(cfg.getDriverClass());
                txtDbType.setText(cfg.getType());
                txtUrl.setText(cfg.getUrl());
                txtUsername.setText(cfg.getUserName());
                txtPassword.setText(cfg.getPassword());
            }
            vec.add(cfg.getName());
        }
        JComboBox cfgList = new JComboBox(vec);
        cfgList.setEditable(true);
        if (vec.size() > 0)
            cfgList.setSelectedIndex(defaultIdx);
        cfgList.addActionListener(new DbListHandler());

        dbList = cfgList;
    }

    @Override
    public void initComponents() {
        JSplitPane leftPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                new JScrollPane(txtSQL), new JScrollPane(sqlList));
        leftPane.setOneTouchExpandable(true);
        leftPane.setDividerLocation(400);

        JSplitPane topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                leftPane, sqlTreePane);
        topPane.setOneTouchExpandable(true);
        topPane.setDividerLocation(500);
        sqlPanel.add(topPane, BorderLayout.CENTER);

    }

    public void initTopMenu(JMenuBar aMenuBar) {
        this.setJMenuBar(menuBar);
        aMenuBar.add(fileMenu);

        // ConfigMenuItemHandler
        JMenuItem cfgItem = new JMenuItem("Load SQL Template");
        cfgItem.addActionListener(new LoadSqlTplHandler());
        fileMenu.add(cfgItem);

        JMenuItem ibatisItem = new JMenuItem("Load ibatis sqlmap Config");
        ibatisItem.addActionListener(new LoadIbatisCfgHandler());
        fileMenu.add(ibatisItem);

        JMenuItem pathItem = new JMenuItem("Add Class Path");
        pathItem.addActionListener(new AddPathHandler());
        fileMenu.add(pathItem);

        JMenuItem reloadItem = new JMenuItem("Reload current Template");
        reloadItem.addActionListener(new ReloadSqlTplHandler());
        fileMenu.add(reloadItem);
        fileMenu.addSeparator();

        JMenuItem executeItem = new JMenuItem("Execute SQL");
        executeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0,
                false));
        executeItem.addActionListener(new SubmitHandler());
        fileMenu.add(executeItem);

        JMenuItem scriptItem = new JMenuItem("Execute SQL as script");
        scriptItem.addActionListener(new ExecuteScriptHandler());
        scriptItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0,
                false));
        fileMenu.add(scriptItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ExitHandler());
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.ALT_MASK, false));
        fileMenu.add(exitItem);

        createSqlMenuAndTree(this.curSqlTplName);
        aMenuBar.add(sqlMenu);

        JMenu helpMenu = new JMenu("Help");
        aMenuBar.add(helpMenu);
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(ActionHandlerFactory.createAboutHandler(
                this, "Help of SQL Tool v1.0", " Just like PHP database tool ",
                240, 100));

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(ActionHandlerFactory.createAboutHandler(
                this, "About SQL Tool v1.0",
                " Wrote by Walter Fan, updated on 07/11/09 ", 320, 100));

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

    }

    private void createSqlMenuAndTree(String xmlTpl) {
        sqlMenu.removeAll();
        try {
            sqlTemplate.clear();
            sqlTemplate.loadFromResource(xmlTpl);
            Map<String, SQLCommands> sqlMap = sqlTemplate.getSqlMap();

            for (Map.Entry<String, SQLCommands> entry : sqlMap.entrySet()) {
                String sqlType = entry.getKey();
                JMenu sqlSubMenu = new JMenu(sqlType);
                sqlMenu.add(sqlSubMenu);
                Collection<SQLCommand> sqls = entry.getValue().getSqlMap()
                        .values();
                Object[] arrSqls = sqls.toArray();
                Arrays.sort(arrSqls);
                for (Object cmd : arrSqls) {
                    SQLCommand sqlCmd = (SQLCommand) cmd;
                    JMenuItem cmdMenuItem = new JMenuItem(sqlCmd.getName());
                    sqlSubMenu.add(cmdMenuItem);
                    cmdMenuItem.addActionListener(new SqlMenuHandler(sqlType,
                            sqlCmd.getName()));
                }
            }

            // --- create right tree pane ----
            SQLCmdTreePane treePane = new SQLCmdTreePane("Frequently-used SQLs");
            treePane.setSqlCmdGroup(sqlMap);
            treePane.setTreeListener(new SelectTreeHandler(treePane));
            treePane.init();
            sqlTreePane = new JScrollPane(treePane);

        } catch (Exception e) {
        	//allow load xml error
            //SwingUtils.alert("Load XML error: " + e.getMessage());
        	e.printStackTrace();

        }

    }

    private void saveTableModelAsCsv(OutputStream os, TableModel model)
            throws IOException {

        for (int i = 0; i < model.getColumnCount(); i++) {
            if (i > 0) {
                os.write(",".getBytes());
            }
            os.write((model.getColumnName(i)).getBytes());
        }
        os.write("\n".getBytes());
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                if (value == null) {
                    value = "null";
                }
                if (j > 0) {
                    os.write(",".getBytes());
                }
                os.write((value.toString()).getBytes());
            }
            os.write("\n".getBytes());
        }
    }

    private void saveTableModelAsSql(OutputStream os, TableModel model)
            throws IOException {

        os.write("insert into tableName (".getBytes());
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (i > 0) {
                os.write(",".getBytes());
            }
            os.write((model.getColumnName(i)).getBytes());
        }
        os.write(")\n values (".getBytes());
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                if (value == null) {
                    value = "null";
                }
                if (j > 0) {
                    os.write(",".getBytes());
                }
                os.write((value.toString()).getBytes());
            }
            os.write(")\n".getBytes());
        }
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1, 6));
        btnSave.addActionListener(new SaveHandler());
        insertBtn2ToolbarAndMenu("save", btnSave);

        btnLoad.addActionListener(new LoadHandler());
        insertBtn2ToolbarAndMenu("load", btnLoad);

        // btnPanel.add(btnClose);
        insertBtn2ToolbarAndMenu("close", btnClose);
        btnClose.setToolTipText("close connection");
        btnClose.addActionListener(new CloseHandler());

        // btnPanel.add(btnRollback);
        insertBtn2ToolbarAndMenu("rollback", btnRollback);
        btnRollback.setToolTipText("Rollback the SQL operation");
        btnRollback.addActionListener(new RollbackHandler());

        // btnPanel.add(btnCommit);
        insertBtn2ToolbarAndMenu("commit", btnCommit);
        btnCommit.setToolTipText("commit the SQL operation");
        btnCommit.addActionListener(new CommitHandler());

        // btnPanel.add(btnReset);
        insertBtn2ToolbarAndMenu("clear", btnReset);
        btnReset.setToolTipText("Clear SQL and the history");
        btnReset.addActionListener(new ResetHandler());

        insertBtn2ToolbarAndMenu("execute", btnSubmit);
        // btnPanel.add(btnSubmit);
        btnSubmit.setToolTipText("Execute SQL");
        btnSubmit.addActionListener(new SubmitHandler());
        return btnPanel;
    }

    private JPanel createCfgPanel() {
        JPanel cfgPanel = new JPanel();
        cfgPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        Box cfgBox = Box.createVerticalBox();
        Dimension labelDim = new Dimension(80, 20);

        JPanel panel0 = new JPanel(new BorderLayout());

        JLabel label0 = new JLabel("Database: ", JLabel.RIGHT);
        label0.setPreferredSize(labelDim);

        panel0.add(label0, BorderLayout.WEST);
        panel0.add(dbList, BorderLayout.CENTER);

        Dimension dim = new Dimension(30, 20);
        txtDbType.setMinimumSize(dim);
        txtDbType.setHorizontalAlignment(JTextField.LEFT);
        panel0.add(txtDbType, BorderLayout.EAST);

        cfgBox.add(Box.createVerticalGlue());
        cfgBox.add(panel0);

        JPanel panel1 = new JPanel(new BorderLayout());
        JLabel label1 = new JLabel("Driver: ", JLabel.RIGHT);
        label1.setPreferredSize(labelDim);
        panel1.add(label1, BorderLayout.WEST);
        panel1.add(txtDriver, BorderLayout.CENTER);

        cfgBox.add(Box.createVerticalGlue());
        cfgBox.add(panel1);

        JPanel panel2 = new JPanel(new BorderLayout());
        JLabel label2 = new JLabel("URL: ", JLabel.RIGHT);
        label2.setPreferredSize(labelDim);
        panel2.add(label2, BorderLayout.WEST);

        cfgBox.add(Box.createVerticalGlue());
        txtUrl.setRows(2);
        txtUrl.setLineWrap(true);

        panel2.add(new JScrollPane(txtUrl), BorderLayout.CENTER);
        cfgBox.add(panel2);

        cfgBox.add(Box.createVerticalGlue());

        JPanel panel3 = new JPanel(new BorderLayout());
        JLabel label3 = new JLabel("Username: ", JLabel.RIGHT);
        label3.setPreferredSize(labelDim);
        panel3.add(label3, BorderLayout.WEST);
        panel3.add(txtUsername, BorderLayout.CENTER);

        cfgBox.add(panel3);

        cfgBox.add(Box.createVerticalGlue());
        JPanel panel4 = new JPanel(new BorderLayout());
        JLabel label4 = new JLabel("Password: ", JLabel.RIGHT);
        label4.setPreferredSize(labelDim);
        panel4.add(label4, BorderLayout.WEST);
        panel4.add(txtPassword, BorderLayout.CENTER);

        cfgBox.add(panel4);
        cfgPanel.setLayout(new BorderLayout());
        // cfgPanel.add(labelBox,BorderLayout.WEST);
        cfgPanel.add(cfgBox, BorderLayout.CENTER);

        return cfgPanel;
    }

    private JSplitPane createtSplitPanel(JPanel cfgPanel, JPanel sqlPanel) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                cfgPanel, sqlPanel);

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.5);

        // Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(100, 50);
        // cfgPanel.setMinimumSize(minimumSize);
        sqlPanel.setMinimumSize(minimumSize);
        // Provide a preferred size for the split pane.
        splitPane.setPreferredSize(new Dimension(400, 100));
        return splitPane;
    }

    /**
     * This method uses the supplied SQL query string, and the
     * ResultSetTableModelFactory object to create a TableModel that holds the
     * results of the database query. It passes that TableModel to the JTable
     * component for display.
     **/
    public void executeAndDisplayResults(final String sql) {
        // It may take a while to get the results, so give the user some
        // immediate feedback that their query was accepted.
        if (StringUtils.isBlank(sql)) {
            SwingUtils.alert("Please input an SQL at least.");
            return;
        }
        msgline.setText("Contacting database...");

        // In order to allow the feedback message to be displayed, we don't
        // run the query directly, but instead place it on the event queue
        // to be run after all pending events and redisplays are done.
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    // This is the crux of it all. Use the factory object
                    // to obtain a TableModel object for the query results
                    // and display that model in the JTable component.
                    long beginTime = System.currentTimeMillis();
                    TableModel model = modelFactory.execute4TableModel(sql);
                    sqlTab.setModel(model);
                    sqlTab.setColumnSelectionAllowed(true);
                    sqlTab.setRowSelectionAllowed(true);
                    
                    int colCnt = model.getColumnCount();
                    StringBuilder colSb = new StringBuilder();
                    for(int i=0;i<colCnt;i++) {
                        colSb.append(model.getColumnName(i));
                        colSb.append(", ");
                    }
                    
                    // We're done, so clear the feedback message
                    msgline.setText("Row count: " + model.getRowCount()
                            + ", column count: " + colCnt
                            + ", columns: " + colSb.toString()
                            + "Duration(ms): "
                            + (System.currentTimeMillis() - beginTime));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // If something goes wrong, clear the message line
                    msgline.setText(ex.getMessage());
                    // Then display the error in a dialog box
                    JOptionPane.showMessageDialog(SQLTool.this, new String[] {
                            // Display a 2-line message
                            ex.getClass().getName() + ": ", ex.getMessage() });
                }
            }
        });
    }

    private void executeSqlBlock() {

        try {
            checkDbConfig();
        } catch (ClassNotFoundException e) {
            SwingUtils.alert(e.getMessage());
            return;
        }

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    String sql = txtSQL.getText();
                    if (StringUtils.isBlank(sql)) {
                        SwingUtils.alert("Please input an SQL at least.");
                        return;
                    }
                    long beginTime = System.currentTimeMillis();
                    TableModel model = modelFactory.executeSqlBlock(sql);
                    sqlTab.setModel(model);
                    sqlTab.setColumnSelectionAllowed(true);
                    sqlTab.setRowSelectionAllowed(true);
                    // We're done, so clear the feedback message
                    msgline.setText("Row count: " + model.getRowCount()
                            + ", duration(ms): "
                            + (System.currentTimeMillis() - beginTime));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // If something goes wrong, clear the message line
                    msgline.setText(ex.getMessage());
                    // Then display the error in a dialog box
                    JOptionPane.showMessageDialog(SQLTool.this, new String[] {
                            ex.getClass().getName() + ": ", ex.getMessage() });
                }
            }
        });
    }

    private void executeSql() {

        String sql = txtSQL.getSelectedText();

        if (StringUtils.isBlank(sql)) {
            sql = txtSQL.getText();
        }

        try {
            checkDbConfig();
        } catch (ClassNotFoundException e) {
            SwingUtils.alert(e.getMessage());
            return;
        }

        msgline.setText("Execute " + sql);

        executeAndDisplayResults(sql);

        pushSql(sql);
    }

    private void checkDbConfig() throws ClassNotFoundException {
        DbConfig thisCfg = new DbConfig((String) txtDriver.getText(),
                txtUrl.getText(), txtUsername.getText(), txtPassword.getText());
        // msgline.setText("checkDbConfig " + thisCfg);
        if (currentDbCfg == null) {
            currentDbCfg = thisCfg;
            modelFactory.setDbCfg(currentDbCfg);
        } else if (!currentDbCfg.equals(thisCfg)) {
            currentDbCfg = thisCfg;
            modelFactory.setDbCfg(currentDbCfg);
            modelFactory.close();

        }
    }

    private void pushSql(String sql) {
        DefaultListModel model = (DefaultListModel) sqlList.getModel();
        boolean isNew = setSql.add(sql);
        if (isNew) {
            model.add(model.getSize(), sql);
        }
    }

    /**
     * This simple main method tests the class. It expects four command-line
     * arguments: the driver classname, the database URL, the username, and the
     * password
     **/
    public static void main(String args[]) throws Exception {
        // Create the factory object that holds the database connection using
        // the data specified on the command line
        ResultSetTableModelFactory factory = null;
        if (args.length == 0) {
            factory = new ResultSetTableModelFactory();
        } else {
            factory = new ResultSetTableModelFactory(args[0], args[1], args[2],
                    args[3]);
        }
        SwingUtils.setFont(new Font("Dialog", Font.PLAIN, 13));
        // Create a QueryFrame component that uses the factory object.
        SQLTool qf = new SQLTool(NAME_VER, factory);
        // Set the size of the QueryFrame, then pop it up
        SwingUtils.run(qf, 900, 700);

    }
}
