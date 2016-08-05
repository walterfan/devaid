package com.github.walterfan.devaid.http;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.github.walterfan.util.http.CustomizedHttpRequest;
import com.github.walterfan.util.http.CustomizedHttpResponse;
import com.github.walterfan.util.swing.JTextAreaOutputStream;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;


public class HttpMediator extends SwingTool  {
	private class StartHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
		    
			SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	if(httpProxy.isStarted()) {
                		System.out.println("Already started");
                		return;
                	}
                	changeStdErrOut(true);
                	httpProxy = new HttpProxy("proxy-setting.xml");                	
                	httpProxy.startup();
                }
            });
		}
	}
	
	private class StopHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	if(!httpProxy.isStarted()) {
                		System.out.println("Already shutdowned");
                		return;
                	}
                	httpProxy.shutdown();                	
                	changeStdErrOut(false);
                }
            });
			
		}
	}
	
	private class ClearOutHandler implements ActionListener {		
		public void actionPerformed(ActionEvent event) {
			SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	txtLogs.setText("");
                }
            });
		}
	}
	
	private class ClearErrHandler implements ActionListener {		
		public void actionPerformed(ActionEvent event) {
			SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	txtErrs.setText("");
                }
            });
		}
	}
	
	private class SelectTreeHandler implements TreeSelectionListener {
		private HttpProxyTreePane cmdTreePane;
		
		public SelectTreeHandler(HttpProxyTreePane pane) {
			this.cmdTreePane = pane;
		}
		
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTreePane.getHttpCmdTree()
					.getLastSelectedPathComponent();

			if (node != null) {

				Object nodeInfo = node.getUserObject();
				if (node.isLeaf()) {
					CustomizedHttpRequest req = (CustomizedHttpRequest) nodeInfo;
					CustomizedHttpResponse resp = HttpMediator.this.httpProxy.getRequestMap().get(req);
					StringBuilder sb = new StringBuilder();
					sb.append("<request>\n");
					sb.append(req.toString());
					sb.append("\n</request>\n");
					sb.append("\n<response>\n");
					sb.append(resp.toString());
					sb.append("\n</response>\n");
					txtHint.setText(sb.toString());
				}
			}
		}
	}
	private static final long serialVersionUID = 1L;

	
	private JButton btnStart = new JButton("Start");
		
	private JButton btnStop = new JButton("Stop");
	
	private JButton btnClearOut = new JButton("Clear Output");
	
	private JButton btnClearErr = new JButton("Clear Error");
	
	private String xmlFile = "proxy-setting.xml";
	//private XTree  xmlTree;
	private HttpProxyTreePane proxyTreePane;
	private JTextField txtRemoteHost = new JTextField(20);
	private JTextField txtLocalPort = new JTextField(5);
	private JTextArea txtHint  = new JTextArea (20, 20);
	private JTextArea txtLogs  = new JTextArea (15, 50);
	private JTextArea txtErrs  = new JTextArea (5, 50);
	HttpProxy httpProxy = new HttpProxy();
	
	public HttpMediator(String title) {
		super(title);
	}
	
	public void init()  {
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<center><strong><u>Http Mediator Tool</u></strong></center><br/>");
		sb.append("&nbsp; It's a proxy and interceptor to http request.\n<br/>");
		sb.append("&nbsp; and it can return the real or fake http response to user.\n<br/>");
		sb.append("<ul>main features");
		sb.append("<li>proxy and record http request between client and server</li>");
		sb.append("<li>simulate http response as a web server to specfied http request</li>");
		sb.append("<li>user can specfy any response header , body as string regex match</li>");sb.append("</ul>");
		sb.append("&nbsp; You can modify configuration as below");
		sb.append("&nbsp; <ol><li> stop");
		sb.append("&nbsp; <li> edit ./etc/proxy-setting.xml");
		sb.append("&nbsp; <li> start");
		sb.append("</ol>");
		sb.append("Please contact walter.fan@gmail.com if you have any issue. ");
		sb.append("</ol></body></html>");
	    this.setAppHelp(sb.toString());
	    
		/*InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(this.xmlFile);
			String xmlContent= IOUtils.toString(is);
			this.xmlTree = new XTree(xmlContent);
		      // set up selection mode
			this.xmlTree.addTreeSelectionListener(new TreeSelectionListener()
		         {
		            public void valueChanged(TreeSelectionEvent event)
		            {
		               // the user selected a different node--update description
		               TreePath path = xmlTree.getSelectionPath();
		               if (path == null) return;
		               DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
		                     .getLastPathComponent();
		               if(selectedNode.isLeaf()) {
		            	   //selectedNode.getP
		            	   String description = selectedNode.getUserObject().toString();
		            	   hintArea.setText(description);
		               }
		            }
		         });
		      int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		      this.xmlTree.getSelectionModel().setSelectionMode(mode);
			
		} catch (Exception e) {
			SwingUtils.alert(e.getMessage());
		} finally {
			IOUtils.closeQuietly(is);
		}*/

		super.init();
		
	}


	public void initComponents() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(10, 10));
		
		JPanel cfgPane = new JPanel();
		cfgPane.setLayout(new BorderLayout());
		cfgPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		proxyTreePane = new HttpProxyTreePane("Http Proxy Items");
		try {
			this.httpProxy.loadConfig(xmlFile);
		} catch (Exception e) {
			SwingUtils.alert(e.getMessage());
		}
		proxyTreePane.setRequestMap(this.httpProxy.getRequestMap());
		proxyTreePane.setTreeListener(new SelectTreeHandler(proxyTreePane));
		proxyTreePane.init();
		JScrollPane proxyPane = new JScrollPane(proxyTreePane);
		cfgPane.add(proxyPane,BorderLayout.CENTER);
		
		JSplitPane leftPane = SwingUtils.createVSplitPane(cfgPane, 
				SwingUtils.createVTextComponentPane("Info:", txtHint), 320);
		
		

		this.txtLocalPort.setText(String.valueOf(this.httpProxy.getListenPort()));
		this.txtRemoteHost.setText(this.httpProxy.getTargetServer() + ":" + this.httpProxy.getTargetPort());
		
		JPanel infoPane = SwingUtils.createHorizontalPanel(
		SwingUtils.createTextComponentPane("listern on localhost ", txtLocalPort, DEFAULT_FONT,Color.white, false),
		SwingUtils.createTextComponentPane("to ", txtRemoteHost, DEFAULT_FONT,Color.white, false), null);

		JPanel logPane = SwingUtils.createTextComponentPane("Logs:", txtLogs, DEFAULT_FONT,DEFAULT_COLOR, true)	;		
		JPanel errPane = SwingUtils.createTextComponentPane("Errors:", txtErrs, DEFAULT_FONT,DEFAULT_COLOR, true);
		
				
		JSplitPane outPane = SwingUtils.createVSplitPane(logPane, errPane, 350);
		
		JPanel rightPane = new JPanel();
		rightPane.add(infoPane, BorderLayout.NORTH);
		rightPane.add(outPane, BorderLayout.CENTER);
		
		JSplitPane mainPane = SwingUtils.createHSplitPane(leftPane, outPane, 350);
		
		contentPane.add(infoPane, BorderLayout.SOUTH);
	    contentPane.add(mainPane, BorderLayout.CENTER);
	    
	    btnStart.setBackground(Color.ORANGE);
	    btnStop.setBackground(Color.ORANGE);
	    btnClearOut.setBackground(Color.ORANGE);
	    btnClearErr.setBackground(Color.ORANGE);
	    
	    btnStart.addActionListener(new StartHandler());	    
	    btnStop.addActionListener(new StopHandler());
	    btnClearOut.addActionListener(new ClearOutHandler());
	    btnClearErr.addActionListener(new ClearErrHandler());
	    
	    toolBar_.add(btnStart);
	    toolBar_.add(btnStop);
	    toolBar_.add(btnClearOut);
	    toolBar_.add(btnClearErr);
	    JMenuItem startItem = new JMenuItem("Start");
	    startItem.addActionListener(new StartHandler());
	    fileMenu.insert(startItem, 0);
	    
        JMenuItem stopItem = new JMenuItem("Stop");
        stopItem.addActionListener(new StartHandler());
        fileMenu.insert(stopItem, 1);
	}
	
	public void changeStdErrOut (boolean isChangeSystem) {
		if(isChangeSystem) {
			System.setOut (new PrintStream (new JTextAreaOutputStream (txtLogs)));
			System.setErr (new PrintStream (new JTextAreaOutputStream (txtErrs)));
        } else {
        	System.setOut (System.out);
			System.setErr (System.err);
        }
    }

	public static void main(String[] args) {
		HttpMediator hm = new HttpMediator("Http Mediator");
	
		try {
			hm.init();
			SwingUtils.run(hm, 800,600);
			//hm.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
