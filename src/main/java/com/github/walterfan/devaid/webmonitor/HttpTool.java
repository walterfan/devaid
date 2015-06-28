/*
 * Copyright (C) 2010-2020  Walter Fan
 * walter.fan@gmail.com     Fan Ya Min
 * Please keep the declaration 
 */

package com.github.walterfan.devaid.webmonitor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.httpclient.Header;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.util.EncodeUtils;
import com.github.walterfan.util.FileUtil;
import com.github.walterfan.util.http.HttpClientWrapper;
import com.github.walterfan.util.http.HttpCommand;
import com.github.walterfan.util.http.HttpCommandConfig;
import com.github.walterfan.util.http.HttpCommandGroup;
import com.github.walterfan.util.http.HttpResponse;
import com.github.walterfan.util.http.HttpResult;
import com.github.walterfan.util.http.HttpUtil;
import com.github.walterfan.util.RegexUtils;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.SwingUtils;
import com.github.walterfan.util.swing.XMLFormatter;
import com.github.walterfan.util.swing.UnderlineHighlighter.UnderlineHighlightPainter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Reserve all rights
 * 
 * @author walter.fan@gmail.com
 * 
 */
public class HttpTool extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String version = "Http API Test Tool v1.3 (2010-02-12)";

    private static Log logger = LogFactory.getLog(HttpTool.class);

    static final int DEFAULT_WIDTH = 950;
    static final int DEFAULT_HEIGHT = 780;
    static final Color DEFAULT_COLOR = new Color(0x99, 0xFF, 0xCC);
    static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private String defaultCfgFile = "HttpAPI.xml";
    private String defaultCfgUrl = "http://10.224.55.21/down/tools/httptool/HttpAPI.xml";

    private LinkedList<HttpCommand> recentHttpCommands = new LinkedList<HttpCommand>();

    private HttpCommandConfig cmdCfgLoader = new HttpCommandConfig();
    private HttpCmdTreePane treePane = new HttpCmdTreePane("Http APIs");

    private JMenuBar menuBar;
    private JMenu cmdMenu = new JMenu("APIs");

    private JLabel labelUrl = new JLabel("URL: ", SwingConstants.LEFT);

    private JTextArea textAreaName = new JTextArea(2, 8);
    private JTextArea textAreaHint = new JTextArea(2, 8);
    private JTextArea textAreaRegex = new JTextArea(2, 40);
    private JCheckBox chkStatusLine = new JCheckBox("Output Status line");
    private JCheckBox chkRespHeader = new JCheckBox("Output Response Header");
    private JCheckBox chkFormat = new JCheckBox("Format Response automatically");

    // TODO by walter
    private JList httpCmdList = null;

    private JTextArea textAreaUrl = new JTextArea(2, 50);

    private JLabel labelHeader = new JLabel("Http Request Header: ",
            SwingConstants.LEFT);
    private JTextArea textAreaHeader = new JTextArea(2, 50);

    private JLabel labelRequest = new JLabel(
            "Http Request Content: (please select and urlencode value if it contains special chars, such as '=','/',etc. by right-click menu)",
            SwingConstants.LEFT);
    private JTextArea textAreaRequest = new JTextArea(6, 50);

    private JLabel labelResponse = new JLabel("Http Response: ",
            SwingConstants.LEFT);
    private JTextArea textAreaResponse = new JTextArea(6, 50);
    private  ButtonGroup rg = new ButtonGroup();
    private JRadioButton rb1 = new JRadioButton("Get", false),
            rb2 = new JRadioButton("Post", false), rb3 = new JRadioButton(
                    "Put", false), rb4 = new JRadioButton("Delete", false);

    private JTextField textPerfHint = new JTextField(10);

    // private HelpDialog helpDialog;
    String helpText = "<html><center><h2><i>How to use the tool</i></h2>"
            + "<div align='left'><ol>"
            + "<li> Load the Http API from configure file, <br>"
            + "select the command menu <br>" + "or fill the text area manually"
            + "<li> Execute the submit button" + "<li> Check the response."
            + "</ol></div>" + "</html>";

    String aboutText = "<html><div align='center'><ul align='left'>"
            + "<li><i>Http Test Tool v0.1 2/26/09</i>"
            + "<li><i>Http Test Tool v1.0 5/29/09</i></li>"
            + "<li><i>Http Test Tool v1.1 6/30/09</i></li>"
            + "<li><i>Http Test Tool v1.2 8/27/09</i></li>"
            + "<li><i>Http Test Tool v1.3 2/12/10</i> , improve context menu</li>"
            + "</ul>"
            + "By <a href='mailto:walter.fan@gmail.com'>Walter Fan</a>"
            + "</div></html>";

    HttpClientWrapper httpClient = new HttpClientWrapper();

    private FileFilter cfgFilter = new FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtil.getExtension(f);
            if (extension != null) {
                if (extension.equals("xml") || extension.equals("properties")) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        // The description of this filter
        public String getDescription() {
            return "Http Api Test Tool configuration File";
        }

    };

    private FileFilter xmlFilter = new FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtil.getExtension(f);
            if (extension != null) {
                if (extension.equals("xml")) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        // The description of this filter
        public String getDescription() {
            return "Http Command XML Files";
        }

    };

    private class SelectTreeHandler implements TreeSelectionListener {
        private HttpCmdTreePane cmdTreePane;

        public SelectTreeHandler(HttpCmdTreePane pane) {
            this.cmdTreePane = pane;
        }

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTreePane
                    .getHttpCmdTree().getLastSelectedPathComponent();

            if (node != null) {

                Object nodeInfo = node.getUserObject();
                if (node.isLeaf()) {
                    HttpCommand cmd = (HttpCommand) nodeInfo;
                    textAreaName.setText(cmd.getName());
                    
                    textAreaUrl.setText(cmd.getUrl());
                    textAreaHeader.setText(cmd.getHeader());
                    textAreaRequest.setText(cmd.getRequest());
                    textAreaResponse.setText("");
                    rg.clearSelection();
                    if ("post".equalsIgnoreCase(cmd.getMethod())) {
                        rb2.setSelected(true);
                    } else if ("put".equalsIgnoreCase(cmd.getMethod())) {
                        rb3.setSelected(true);
                      } else if ("delete".equalsIgnoreCase(cmd.getMethod())) {
                        rb4.setSelected(true);
                    }else {
                        rb1.setSelected(true);
                    }
                }
            }
        }
    }

    private class ExtractCredHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String retText = textAreaResponse.getText();
            if (StringUtils.isBlank(retText)) {
                alert("The response is empty");
                return;
            }
            // String retStr = ask("Extract Result",
            // TextUtils.wrapText(retText));
            String strPath = "/wbxapi/securityContext/cred/text()";
            HttpResult retHttp = new HttpResult(retText);
            String retStr = retHttp.getNodeText(strPath);
            prompt("Wapi credential", "cred = " + retStr);
            textAreaHint.setText(retStr);
        }
    }

    private class ReplaceCredHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String reqText = textAreaRequest.getText();
            String urlText = textAreaUrl.getText();

            String credText = textAreaHint.getText();
            if (StringUtils.isBlank(credText)) {
                alert("Please extract and save cred firstly");
                return;
            }

            if (StringUtils.isBlank(reqText) && StringUtils.isBlank(urlText)) {
                alert("The url and request is empty");
                return;
            }

            String credStr = "[cred]";
            String reqStr = StringUtils.replace(reqText, credStr, credText);
            String urlStr = StringUtils.replace(urlText, credStr, credText);
            textAreaRequest.setText(reqStr);
            textAreaUrl.setText(urlStr);
        }
    }

    private class ExtractTokenHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String retText = textAreaResponse.getText();
            if (StringUtils.isBlank(retText)) {
                alert("The response is empty");
                return;
            }
            // String retStr = ask("Extract Result",
            // TextUtils.wrapText(retText));
            String strPath = "/soapenv:Envelope/soapenv:Body/ns:authenticationResponse/ns:return/ax22:params/ax22:token";
            HttpResult retHttp = new HttpResult(retText);
            String retStr = retHttp.getTextWithNS(strPath);
            prompt("SLIM Token", "token = " + retStr);
            textAreaHint.setText(retStr);
        }
    }

    private class ReplaceTokenHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String reqText = textAreaRequest.getText();

            String credText = textAreaHint.getText();
            if (StringUtils.isBlank(credText)) {
                alert("Please extract and save token firstly");
                return;
            }

            if (StringUtils.isBlank(reqText)) {
                alert("The request is empty");
                return;
            }

            String credStr = "[token]";
            String reqStr = StringUtils.replace(reqText, credStr, credText);
            textAreaRequest.setText(reqStr);
        }
    }

    /*
     * private class MyKeyListener extends KeyAdapter { public void
     * keyPressed(KeyEvent evt) {
     * 
     * int keyCode = evt.getKeyCode(); //System.out.println("keyCode=" +
     * keyCode); if (keyCode == KeyEvent.VK_F5) { submitHttpCommand(); } } }
     */

    private class ConfigMenuItemHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            JFileChooser c = new JFileChooser("./etc");
            c.setFileFilter(cfgFilter);
            int rVal = c.showOpenDialog(HttpTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                File file = c.getSelectedFile();
                cmdCfgLoader.clear();
                try {
                    cmdCfgLoader.load(file.getAbsolutePath());
                } catch (Exception e) {
                    logger.error("load APIs error: ", e);
                    SwingUtils.alert(e.getMessage());
                }
                // TODO:
                HttpTool.this.removeCmdMenuItems(cmdMenu);
                cmdMenu.repaint();
                HttpTool.this.addCmdMenuItems(cmdMenu);
                HttpTool.this.treePane.setHttpCmdGroup(cmdCfgLoader
                        .getHttpCmdsMap());
                HttpTool.this.treePane.update();
                HttpTool.this.createTreePanel(HttpTool.this.getContentPane());
                HttpTool.this.treePane.repaint();
            }
        }
    }

    public HttpTool() {
        this(version);
    }

    public HttpTool(String title) {
        super(title);

        try {
            cmdCfgLoader.load(defaultCfgFile, this.getClass().getClassLoader());
        } catch (Exception e) {
            printErrorLog(defaultCfgFile + " load error: ", e);
            try {
                URL url = new URL(this.defaultCfgUrl);
                cmdCfgLoader.load(url);
            } catch (Exception e1) {
                printErrorLog(defaultCfgUrl + " load error: ", e);
            }
        }
        arrange();
    }

    public void alert(String message) {
        JOptionPane.showMessageDialog(null, message, "Alert",
                JOptionPane.WARNING_MESSAGE);
    }

    public String ask(String title, String message) {
        return JOptionPane.showInputDialog(null,
                StringUtils.substring(message, 0, 1024), title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void prompt(String title, String message) {
        JOptionPane.showMessageDialog(null,
                StringUtils.substring(message, 0, 1024), title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private class SubmitButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            submitHttpCommand();
        }
    }

    private class ClearButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            // textAreaUrl.setText("");
            // textAreaHeader.setText("");
            // textAreaRequest.setText("");
            textAreaResponse.setText("");
        }
    }

    private class TodoHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            prompt("Todo feature",
                    "The feature is not finished yet, please look forward to.");
            return;
        }
    }

    private class ValidationHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String strResponse = StringUtils.trim(textAreaResponse.getText());
            if (StringUtils.isBlank(strResponse)) {
                alert("The resonse is blank.");
            }
            String pattern = textAreaRegex.getText();
            if (RegexUtils.isMatched(strResponse, pattern)) {
                prompt("", "validation success");
            } else {
                alert("validation failed '" + strResponse + "' of '" + pattern
                        + "'");
            }
        }
    }

    private class LoadButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser c = new JFileChooser("./log");
            c.setFileFilter(xmlFilter);
            int rVal = c.showOpenDialog(HttpTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {

                File file = c.getSelectedFile();
                XStream xs = new XStream(new DomDriver());
                xs.alias("HttpCommand", HttpCommand.class);
                xs.useAttributeFor(HttpCommand.class, "name");
                xs.useAttributeFor(HttpCommand.class, "method");

                HttpCommand cmd = new HttpCommand();
                try {
                    xs.fromXML(new FileInputStream(file), cmd);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                textAreaUrl.setText(cmd.getUrl());
                textAreaHeader.setText(cmd.getHeader());
                textAreaRequest.setText(cmd.getRequest());
                textAreaResponse.setText(cmd.getResponse());

                if ("post".equalsIgnoreCase(cmd.getMethod())) {
                    rb2.setSelected(true);
                } else if ("put".equalsIgnoreCase(cmd.getMethod())) {
                    rb3.setSelected(true);
                } else if ("delete".equalsIgnoreCase(cmd.getMethod())) {
                    rb4.setSelected(true);
                }else {
                    rb1.setSelected(true);
                }
            }
        }
    }

    private class SaveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser c = new JFileChooser("./log");
            c.setFileFilter(xmlFilter);
            int rVal = c.showSaveDialog(HttpTool.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                // File file = c.getSelectedFile();
                String strHost = StringUtils.trim(textAreaUrl.getText());
                String strHeader = StringUtils.trim(textAreaHeader.getText());
                String strRequest = StringUtils.trim(textAreaRequest.getText());
                String strResponse = StringUtils.trim(textAreaResponse
                        .getText());

                HttpCommand cmd = new HttpCommand();
                cmd.setHeader(strHeader);
                cmd.setUrl(strHost);
                cmd.setRequest(strRequest);
                cmd.setResponse(strResponse);

                XStream xs = new XStream();
                xs.alias("HttpCommand", HttpCommand.class);
                xs.useAttributeFor(HttpCommand.class, "name");
                xs.useAttributeFor(HttpCommand.class, "method");
                // Write to a file in the file system
                FileOutputStream fs = null;
                try {
                    String filename = c.getSelectedFile().getAbsolutePath();
                    fs = new FileOutputStream(filename);
                    xs.toXML(cmd, fs);
                    prompt("Save command", "saved " + filename);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(fs);
                }
            }
        }
    }

    private class MenuCommandHandler implements ActionListener {
        private String grpName;
        private String cmdName;

        public MenuCommandHandler(String grpName, String cmdName) {
            this.grpName = grpName;
            this.cmdName = cmdName;
        }

        public void actionPerformed(ActionEvent event) {
            HttpCommand cmd = cmdCfgLoader.getHttpCommand(grpName, cmdName);
            String strUrl = cmd.getUrl();
            if (StringUtils.isBlank(strUrl)) {
                alert("Please add the relative configure for the item(url,request).");
                return;
            }
            textAreaName.setText(cmd.getName());
            // textAreaUrl.setToolTipText(cmd.getName());
            textAreaUrl.setText(cmd.getUrl());
            textAreaHeader.setText(cmd.getHeader());
            textAreaRequest.setText(cmd.getRequest());
            textAreaResponse.setText("");

            if ("post".equalsIgnoreCase(cmd.getMethod())) {
                rb2.setSelected(true);
            } else if ("put".equalsIgnoreCase(cmd.getMethod())) {
                rb3.setSelected(true);
            } else if ("delete".equalsIgnoreCase(cmd.getMethod())) {
                rb4.setSelected(true);
            }else {
                rb1.setSelected(true);
            }
        }
    }

    private static void printErrorLog(String logContent, Exception e) {
        logger.error(logContent);
        // e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.debug(sw.toString());
    }

    private static void printLog(String logContent) {
        logger.info(logContent);

    }

    public void arrange() {

        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // Set up the content pane.

        Container contentPane = getContentPane();

        JPanel topPanel = createTopPanel();
        // create right tree pane
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(createComponentsPane(), BorderLayout.CENTER);
        contentPane.add(createBottomPane(), BorderLayout.SOUTH);
        createTreePanel(contentPane);

        this.setJMenuBar(createJMenuBar());
        // setVisible(true);
        // SwingUtils.moveToCenter(this);
    }

    private void createTreePanel(Container contentPane) {
        treePane.setHttpCmdGroup(cmdCfgLoader.getHttpCmdsMap());
        treePane.setTreeListener(new SelectTreeHandler(treePane));
        treePane.init();
        JScrollPane rightPane = new JScrollPane(treePane);
        contentPane.add(rightPane, BorderLayout.EAST);
    }

    private JPanel createTopPanel() {

        JPanel topPane = new JPanel();
        topPane.setBorder(BorderFactory.createEtchedBorder());
        topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));

        // Box hBox = Box.createHorizontalBox();
        topPane.add(createToolBar());

        JScrollPane nameTextPane = SwingUtils.createScrollPane(
                this.textAreaName, DEFAULT_FONT, null);
        topPane.add(Box.createHorizontalStrut(10));
        this.textAreaName.setText("Http API Name");
        topPane.add(nameTextPane);

        JScrollPane hintTextPane = SwingUtils.createScrollPane(
                this.textAreaHint, DEFAULT_FONT, null);
        topPane.add(Box.createHorizontalStrut(10));
        this.textAreaHint.setText("Http API Hint");
        topPane.add(hintTextPane);
        return topPane;

    }

    private JSplitPane createtSplitPanel(JPanel topPanel, JPanel bottomPanel) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                topPanel, bottomPanel);
        splitPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        splitPane.setOneTouchExpandable(true);
        return splitPane;
    }

    public JPanel createComponentsPane() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel urlPane = new JPanel();
        urlPane.setLayout(new BorderLayout());
        urlPane.add(labelUrl, BorderLayout.NORTH);
        urlPane.add(SwingUtils.createScrollPane(textAreaUrl, DEFAULT_FONT,
                DEFAULT_COLOR), BorderLayout.CENTER);

        JPanel headerPane = new JPanel();
        headerPane.setLayout(new BorderLayout());
        headerPane.add(labelHeader, BorderLayout.NORTH);
        headerPane.add(SwingUtils.createScrollPane(textAreaHeader,
                DEFAULT_FONT, DEFAULT_COLOR), BorderLayout.CENTER);

        JSplitPane pane0 = createtSplitPanel(urlPane, headerPane);

        JPanel topPane = new JPanel();
        topPane.setLayout(new BorderLayout());
        topPane.add(labelRequest, BorderLayout.NORTH);
        topPane.add(SwingUtils.createScrollPane(textAreaRequest, DEFAULT_FONT,
                DEFAULT_COLOR), BorderLayout.CENTER);

        JPanel bottomPane = new JPanel();
        Box hBox = Box.createHorizontalBox();
        hBox.add(labelResponse);
        hBox.add(chkStatusLine);
        hBox.add(chkRespHeader);
        hBox.add(chkFormat);

        /*
         * JButton btnClear = new JButton("clear");
         * btnClear.addActionListener(new ClearButtonHandler());
         * btnClear.setMaximumSize(new Dimension(80,18)); hBox.add(btnClear);
         */

        bottomPane.setLayout(new BorderLayout());
        bottomPane.add(hBox, BorderLayout.NORTH);
        bottomPane.add(SwingUtils.createScrollPane(textAreaResponse,
                DEFAULT_FONT, DEFAULT_COLOR), BorderLayout.CENTER);

        JSplitPane pane1 = createtSplitPanel(topPane, bottomPane);

        createContextMenu();

        // mainPanel.add(new JLabel("   "),BorderLayout.WEST);
        mainPanel.add(pane0, BorderLayout.NORTH);
        mainPanel.add(pane1, BorderLayout.CENTER);
        // mainPanel.add(new JLabel("   "),BorderLayout.EAST);
        // pane.add(mainPanel, layout);
        return mainPanel;

    }

    private void createContextMenu() {
        // create popup menu when click right menu
        final JPopupMenu contextMenu = SwingUtils
                .createStdEditPopupMenu(new JTextComponent[] {
                        textAreaHint, textAreaUrl, textAreaHeader,
                        textAreaRequest, textAreaResponse });
        contextMenu.addSeparator();

        final JMenuItem formatMenuItem = new JMenuItem("Format XML", 'f');
        formatMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Component c = contextMenu.getInvoker();

                if (c instanceof JTextComponent) {
                    JTextComponent txtBox = ((JTextComponent) c);
                    String fullTxt = txtBox.getText();
                    try {
                        txtBox.setText(XMLFormatter.format(fullTxt));
                    } catch (Exception ex) {
                        // ignore, not fomat it
                    }
                }
            }
        });
        contextMenu.add(formatMenuItem);
        // TODO: refactor to remove duplicated codes
        /* text fields popup menu: "URLEncode" */
        final JMenuItem urlenMenuItem = new JMenuItem("URLEncode", 'e');
        urlenMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Component c = contextMenu.getInvoker();

                if (c instanceof JTextComponent) {
                    JTextComponent txtBox = ((JTextComponent) c);
                    String selectTxt = txtBox.getSelectedText();

                    if (StringUtils.isBlank(selectTxt)) {
                        return;
                    }
                    String encTxt = EncodeUtils.urlEncode(selectTxt);
                    String fullTxt = txtBox.getText();
                    int beginPos = txtBox.getSelectionStart();
                    int endPos = txtBox.getSelectionEnd();

                    String retTxt = StringUtils.left(fullTxt, beginPos)
                            + encTxt
                            + StringUtils.right(fullTxt, fullTxt.length()
                                    - endPos);
                    txtBox.setText(retTxt);

                    Highlighter hilite = txtBox.getHighlighter();

                    SwingUtils.removeHighlights(txtBox);

                    try {
                        hilite.addHighlight(beginPos,
                                beginPos + encTxt.length(),
                                new UnderlineHighlightPainter(Color.red));
                    } catch (BadLocationException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        contextMenu.add(urlenMenuItem);

        final JMenuItem urldeMenuItem = new JMenuItem("URLDecode", 'd');
        urldeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Component c = contextMenu.getInvoker();

                if (c instanceof JTextComponent) {
                    JTextComponent txtBox = ((JTextComponent) c);
                    String selectTxt = txtBox.getSelectedText();

                    if (StringUtils.isBlank(selectTxt)) {
                        return;
                    }
                    String decTxt = EncodeUtils.urlDecode(selectTxt);
                    String fullTxt = txtBox.getText();
                    int beginPos = txtBox.getSelectionStart();
                    int endPos = txtBox.getSelectionEnd();

                    String retTxt = StringUtils.left(fullTxt, beginPos)
                            + decTxt
                            + StringUtils.right(fullTxt, fullTxt.length()
                                    - endPos);
                    txtBox.setText(retTxt);

                    Highlighter hilite = txtBox.getHighlighter();

                    SwingUtils.removeHighlights(txtBox);

                    try {
                        hilite.addHighlight(beginPos,
                                beginPos + decTxt.length(),
                                new UnderlineHighlightPainter(Color.blue));
                    } catch (BadLocationException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        contextMenu.add(urldeMenuItem);
    }

    private JPanel createBottomPane() {
        JPanel bottomPanel = new JPanel();
        Box hBox = Box.createHorizontalBox();
        JButton btnSubmit = createToolBarButton("mail-send_48", "submit",
                "Submit the http request", "submit");
        btnSubmit.addActionListener(new SubmitButtonHandler());
        hBox.add(btnSubmit);

        
        rg.add(rb1);
        rb1.setSelected(true);
        rg.add(rb2);
        rg.add(rb3);
        rg.add(rb4);

        hBox.add(rb1);
        hBox.add(rb2);
        hBox.add(rb3);
        hBox.add(rb4);

        hBox.add(Box.createHorizontalStrut(10));
        hBox.add(textPerfHint);
        textPerfHint.setText("Duration: ");
        textPerfHint.setEditable(false);
        textPerfHint.setBorder(new EtchedBorder());
        textPerfHint.setBackground(Color.WHITE);// new Color(0x99,0xFF,0xCC)

        hBox.add(Box.createHorizontalStrut(10));

        JScrollPane hintTextPane = SwingUtils.createScrollPane(
                this.textAreaRegex, DEFAULT_FONT, null);
        textAreaRegex.setText("Http API Response Validation Regex");
        hBox.add(hintTextPane);
        // hBox.add(Box.createHorizontalStrut(10));
        // JLabel lab0 = new JLabel("Hint: ", SwingConstants.LEFT);
        // hBox.add(lab0);
        JButton btnValidate = createToolBarButton("accepted_48", "validate",
                "Validate Http Response", "validate");
        // JButton btnValidate = new JButton("Validation");
        hBox.add(btnValidate);
        btnValidate.addActionListener(new ValidationHandler());

        bottomPanel.add(hBox);
        return bottomPanel;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Http tool box");

        JButton btnSubmit = createToolBarButton("mail-send_48", "submit",
                "Submit the http request", "submit");
        btnSubmit.addActionListener(new SubmitButtonHandler());
        toolBar.add(btnSubmit);

        JButton btnOpen = createToolBarButton("folder_48", "open",
                "Open Http API XML File", "open");
        btnOpen.addActionListener(new ConfigMenuItemHandler());
        toolBar.add(btnOpen);

        JButton btnExCred = createToolBarButton("box_upload_48", "extract",
                "extract cred", "extract");
        btnExCred.addActionListener(new ExtractCredHandler());
        toolBar.add(btnExCred);

        JButton btnReCred = createToolBarButton("box_download_48", "replace",
                "replace cred", "replace");
        btnReCred.addActionListener(new ReplaceCredHandler());
        toolBar.add(btnReCred);

        JButton button1 = createToolBarButton("arrow_left_48", "pervious",
                "Back to previous Http command", "previous");
        button1.addActionListener(new TodoHandler());
        toolBar.add(button1);

        JButton button2 = createToolBarButton("arrow_right_48", "next",
                "Forward to next Http Command", "next");
        button2.addActionListener(new TodoHandler());
        toolBar.add(button2);

        JButton btnLoad = createToolBarButton("open_48", "open",
                "Open Http Command File", "open");
        btnLoad.addActionListener(new LoadButtonHandler());
        toolBar.add(btnLoad);

        JButton btnSave = createToolBarButton("floppy_disk_48", "save",
                "Save Http Command File", "save");
        btnSave.addActionListener(new SaveButtonHandler());
        toolBar.add(btnSave);

        JButton btnReset = createToolBarButton("clear_48", "clear",
                "Clear output", "clear");
        btnReset.addActionListener(new ClearButtonHandler());
        toolBar.add(btnReset);

        JButton btnExit = createToolBarButton("exit_48", "exit", "Exit", "exit");
        btnExit.addActionListener(ActionHandlerFactory.createExitHandler());
        toolBar.add(btnExit);

        toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        return toolBar;
    }

    private JButton createToolBarButton(String imageName, String actionCommand,
            String toolTipText, String altText) {
        // Look for the image.
        String imgLocation = "icons/" + imageName + ".png";
        URL imageURL = Thread.currentThread().getContextClassLoader()
                .getResource(imgLocation); // HttpTool.class.getResource(imgLocation);

        // Create and initialize the button.
        JButton button = new JButton();
        // button.setActionCommand(actionCommand);

        button.setToolTipText(toolTipText);

        if (imageURL != null) { // image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else { // no image found
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        }
        button.setSize(24, 24);
        return button;
    }

    public JMenuBar createJMenuBar() {

        menuBar = new JMenuBar();
        JMenu fileMenu = createFileMenu();

        menuBar.add(fileMenu);
        menuBar.add(cmdMenu);
        addCmdMenuItems(cmdMenu);

        JMenu helpMenu = createHelpMenu();
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Usage");
        // menuBar.add(fileMenu);

        // ConfigMenuItemHandler
        JMenuItem cfgItem = new JMenuItem("Load APIs");
        cfgItem.addActionListener(new ConfigMenuItemHandler());
        cfgItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));

        fileMenu.add(cfgItem);

        fileMenu.addSeparator();
        JMenuItem executeItem = new JMenuItem("Execute Command");
        executeItem.addActionListener(new SubmitButtonHandler());
        executeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0,
                false));
        fileMenu.add(executeItem);

        JMenuItem loadItem = new JMenuItem("Load Command");
        loadItem.addActionListener(new LoadButtonHandler());
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                ActionEvent.CTRL_MASK));
        fileMenu.add(loadItem);

        JMenuItem saveItem = new JMenuItem("Save Command");
        saveItem.addActionListener(new SaveButtonHandler());
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        // extract cred
        JMenuItem credItem = new JMenuItem("Extract WAPI Cred");
        credItem.addActionListener(new ExtractCredHandler());
        credItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                ActionEvent.CTRL_MASK));
        fileMenu.add(credItem);

        // replace cred
        JMenuItem credItem1 = new JMenuItem("Replace WAPI Cred");
        credItem1.addActionListener(new ReplaceCredHandler());
        fileMenu.add(credItem1);
        credItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.CTRL_MASK));

        fileMenu.addSeparator();
        // extract token
        JMenuItem tokenItem = new JMenuItem("Extract SLIM Token");
        tokenItem.addActionListener(new ExtractTokenHandler());
        tokenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                ActionEvent.ALT_MASK));
        fileMenu.add(tokenItem);

        // replace token
        JMenuItem tokenItem1 = new JMenuItem("Replace SLIM Token");
        tokenItem1.addActionListener(new ReplaceTokenHandler());
        tokenItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.ALT_MASK));
        fileMenu.add(tokenItem1);
        fileMenu.addSeparator();
        // The Exit item exits the program

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(ActionHandlerFactory.createExitHandler());
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.ALT_MASK));

        fileMenu.add(exitItem);
        return fileMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        // menuBar.add(helpMenu);

        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(ActionHandlerFactory.createAboutHandler(
                this, "Help of " + version, helpText, 320, 240));
        helpMenu.add(helpItem);

        // The About item shows the About dialog

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(ActionHandlerFactory.createAboutHandler(
                this, "About the " + version, aboutText, 320, 240));
        helpMenu.add(aboutItem);

        JMenuItem optionItem = new JMenuItem("Option");
        optionItem.addActionListener(ActionHandlerFactory.createAboutHandler(
                this, "Perferences",
                "Do you want output http response header? todo...", 320, 180));
        helpMenu.add(optionItem);

        return helpMenu;

    }

    /*
     * private void addCmdMenu(JMenuBar menuBar) { menuBar.add(cmdMenu);
     * addCmdMenuItems(cmdMenu);
     * 
     * }
     */
    private void addCmdMenuItems(JMenu cmdMenu) {
        Map<String, HttpCommandGroup> map = cmdCfgLoader.getHttpCmdsMap();
        for (Map.Entry<String, HttpCommandGroup> entry : map.entrySet()) {
            String grpName = StringUtils.trim(entry.getKey());
            JMenu subMenu = new JMenu(grpName);
            cmdMenu.add(subMenu);

            HttpCommandGroup grp = entry.getValue();
            java.util.List<String> cmdList = new ArrayList<String>(20);
            cmdList.addAll(grp.keySet());
            Collections.sort(cmdList);
            for (String strCmd : cmdList) {
                // addCmdMenuItem(subMenu, grpName, StringUtils.trim(strCmd));
                JMenuItem menuItem = new JMenuItem(strCmd);
                menuItem.addActionListener(new MenuCommandHandler(grpName,
                        strCmd));
                subMenu.add(menuItem);
            }
        }
    }

    private void removeCmdMenuItems(JMenu cmdMenu) {
        cmdMenu.removeAll();
    }

    public static void usage() {
        System.out.println("HttpTool httpMethod httpCommandGroup shttpCommand");
        System.out.println("Such as : HttpTool post SlimLogin");
    }

    private void executeHttpCommands(HttpCommandGroup grp) {
        for (HttpCommand cmd : grp.values()) {
            try {
                printLog("---request:" + cmd.getName());
                HttpResponse resp = this.executeHttpCommand(cmd);
                printLog("---response:" + resp);
            } catch (IOException e) {
                printErrorLog("executeHttpCommands error: ", e);
            }
        }
    }

    public void run(String[] args) {
        HttpCommand cmd = null;
        if (args.length == 1) {
            String grpName = args[0];
            HttpCommandGroup grp = cmdCfgLoader.getHttpCommandGroup(grpName);
            executeHttpCommands(grp);
            return;
        } else if (args.length == 2) {
            String grpName = args[0];
            String cmdName = args[1];
            cmd = cmdCfgLoader.getHttpCommand(grpName, cmdName);
        } else if (args.length == 3) {
            String method = args[0];
            String url = args[1];
            String request = args[1];
            cmd = new HttpCommand(method, url, request);
        }

        if (null == cmd) {
            usage();
            return;
        }

        cmd.parseUrl();
        printLog("---request:" + cmd.getRequest());

        try {
            HttpResponse resp = this.executeHttpCommand(cmd);
            printLog("---response:" + resp);
        } catch (IOException e) {
            printErrorLog("error: ", e);
        }

    }

    private void submitHttpCommand() {
        String strHost = StringUtils.trim(textAreaUrl.getText());
        String strHeader = StringUtils.trim(textAreaHeader.getText());
        String strRequest = StringUtils.trim(textAreaRequest.getText());

        if (StringUtils.isBlank(strHost)) {
            alert("Please input Host.");
            return;
        }
        if (StringUtils.isBlank(strRequest)) {
            if (StringUtils.contains(strHost, '?')) {
                int pos = strHost.indexOf('?');
                strRequest = StringUtils.substring(strHost, pos + 1);
                strHost = StringUtils.substring(strHost, 0, pos);
                // alert("strHost=" + strHost + "strRequest=" + strRequest);
            }
        }
        printLog("request:\n" + strRequest);
        long startTime = System.currentTimeMillis();
        try {
            HttpResponse response = executeHttpCommand(new HttpCommand(strHost,
                    strHeader, strRequest, getMethodName()));
            if (response != null) {
                String strResp = response.getBody();
                StringBuilder sb = new StringBuilder();
                if (chkStatusLine.isSelected()) {
                    sb.append("statusLine = " + response.getStatusLine() + "\n");
                    sb.append("\n\n");
                }

                if (this.chkRespHeader.isSelected()) {
                    Header[] headers = response.getHeaders();

                    for (Header aHead : headers) {
                        sb.append(aHead.getName() + " = " + aHead.getValue()
                                + "\n");
                    }
                    sb.append("\n\n");
                    
                    
                } 
                sb.append(strResp);
                textAreaResponse.setText(sb.toString());
                //textAreaResponse.setText(strResp);
                printLog("response:\n" + sb.toString());
                

                if (this.chkFormat.isSelected()) {
                    try {
                        textAreaResponse.setText(XMLFormatter
                                .format(textAreaResponse.getText()));
                    } catch (Exception exp) {
                        // ignore it, not format
                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                textPerfHint.setText("duration:" + duration + " ms");
                // TODO:for history
                /*
                 * HttpCommand aCmd = new HttpCommand();
                 * aCmd.setHeader(strHeader); aCmd.setRequest(strRequest);
                 * aCmd.setResponse(response.getBody());
                 * recentHttpCommands.push(aCmd);
                 */
            }
        } catch (IOException e) {
            printErrorLog("Http Request error: ", e);
            textAreaResponse.setText("Error: " + e.getMessage());
        }
    }

    private String getMethodName() {
        if (rb1.isSelected()) {
            return ("get");
        } else if (rb2.isSelected()) {// ---POST method
            return ("post");
        } else if (rb3.isSelected()) {// ---PUT method
            return ("put");
        } else if (rb4.isSelected()) {// ---DELETE method
            return ("delete");
        } else {
            return "";
        }
    }

    private HttpResponse executeHttpCommand(HttpCommand hcmd)
            throws IOException {
        HttpResponse response = null;
        printLog("### executeHttpCommand: " + hcmd);
        // ---GET method
        if ("get".equalsIgnoreCase(hcmd.getMethod())) {

            response = httpClient.doGet(hcmd.getUrl(),
                    HttpUtil.getMapFromParameters(hcmd.getRequest()),
                    HttpUtil.getMapFromParameters(hcmd.getHeader()));
        } else if ("post".equalsIgnoreCase(hcmd.getMethod())) {// ---POST method
            if (StringUtils.contains(hcmd.getHeader(), "SOAPAction")) {
                printLog("SOAP post: " + hcmd.getUrl());
                response = httpClient.doSoapPost(hcmd.getUrl(),
                        hcmd.getRequest());
                printLog("SOAP response: " + response);
            } else {
                printLog("post: " + hcmd.getUrl());
                response = httpClient.doPost(hcmd.getUrl(),
                        HttpUtil.getMapFromParameters(hcmd.getRequest()),
                        HttpUtil.getMapFromParameters(hcmd.getHeader()));
            }

        } else if ("put".equalsIgnoreCase(hcmd.getMethod())) {// ---PUT method
            printLog("put: " + hcmd.getUrl());
            response = httpClient.doPut(hcmd.getUrl(), hcmd.getRequest());
        } else if ("delete".equalsIgnoreCase(hcmd.getMethod())) {// ---DELETE
                                                                 // method
            printLog("delete: " + hcmd.getUrl());
            response = httpClient.doDelete(hcmd.getUrl(), hcmd.getRequest());
        }
        return response;
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        // SwingUtils.setFont(new Font("Arial",Font.PLAIN,13));
        if (args.length > 0) {
            HttpTool ht = new HttpTool();
            ht.run(args);
        } else {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    HttpTool ht = new HttpTool();
                    SwingUtils.run(ht, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                }
            });
        }
    }
}
