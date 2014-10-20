package com.github.walterfan.devaid.http;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.github.walterfan.util.http.HttpCommand;
import com.github.walterfan.util.http.HttpCommandConfig;
import com.github.walterfan.util.http.HttpCommandGroup;
import com.github.walterfan.util.swing.SwingUtils;

public class HttpCmdTreePane extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	
	class MyRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		public MyRenderer() {
	    }

	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
	        if (leaf ) {
	       	
	        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
	        	Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof HttpCommand) {
					HttpCommand cmd = (HttpCommand) nodeInfo;
					setText(cmd.getName());
					setToolTipText(cmd.getRequest());
				}
	        } 
	        

	        return this;
	    }

	}
	
	private TreeSelectionListener treeListener;
		
	private Map<String, HttpCommandGroup> httpCmdGroup;
	
	private JTree httpCmdtree;
	
	DefaultMutableTreeNode topNode;
	
	public HttpCmdTreePane(String title) {
		topNode = new DefaultMutableTreeNode(title);	    
	    
	    
	}
	
	public void setTreeListener(TreeSelectionListener listener) {
		this.treeListener = listener;
	}
	
	public void setHttpCmdGroup(Map<String, HttpCommandGroup> httpCmdGroup) {
		this.httpCmdGroup = httpCmdGroup;
	}
	
	public JTree getHttpCmdTree() {
		return this.httpCmdtree;
	}
	
	public void init() {
		createNodes(topNode);
		httpCmdtree = new JTree(topNode);
	    httpCmdtree.addTreeSelectionListener(this.treeListener);
	    httpCmdtree.setCellRenderer(new MyRenderer());

	    JScrollPane treeView = new JScrollPane(httpCmdtree);
	    this.add(treeView, BorderLayout.CENTER);
	}
	
	public void update() {
		this.topNode.removeAllChildren();
		createNodes(topNode);
		this.validate();
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
	    for(Map.Entry<String, HttpCommandGroup> entry: httpCmdGroup.entrySet()) {
	    	DefaultMutableTreeNode category = new DefaultMutableTreeNode(entry.getKey());
		    top.add(category);
		    HttpCommandGroup group = entry.getValue();
		    List<HttpCommand> list = new ArrayList<HttpCommand>(group.size());
		    list.addAll(group.values());
		    Collections.sort(list);
		    for(HttpCommand cmd : list) {
		    	DefaultMutableTreeNode apiNode = new DefaultMutableTreeNode(cmd);
		    	category.add(apiNode);
		    }
	    }
	    
	}
	
	 
	public void valueChanged(TreeSelectionEvent e) {
		treeListener.valueChanged(e);
	}

	
	public static void main(String[] args) throws Exception {
		
		
		final HttpCmdTreePane cmdTree = new HttpCmdTreePane("Http APIs");
		TreeSelectionListener listener = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree.getHttpCmdTree()
						.getLastSelectedPathComponent();

				if (node != null) {

					Object nodeInfo = node.getUserObject();
					if (node.isLeaf()) {
						HttpCommand cmd = (HttpCommand) nodeInfo;
						System.out.println("select API: " + cmd.toString());
						SwingUtils.prompt("select API", cmd.toString());
					}
				}
			}
		};
		cmdTree.setTreeListener(listener);
		HttpCommandConfig factory = new HttpCommandConfig();
		factory.load("./etc/" + HttpCommandConfig.HTTP_CMD_XML);
		cmdTree.setHttpCmdGroup(factory.getHttpCmdsMap());
		cmdTree.init();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(cmdTree, BorderLayout.CENTER);
		SwingUtils.run(frame, 240, 480);
	}
}
