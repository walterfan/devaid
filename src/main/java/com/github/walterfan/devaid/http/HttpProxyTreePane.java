package com.github.walterfan.devaid.http;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.github.walterfan.util.http.CustomizedHttpRequest;
import com.github.walterfan.util.http.CustomizedHttpResponse;



public class HttpProxyTreePane extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private class MyRenderer extends DefaultTreeCellRenderer {

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
				if (nodeInfo instanceof CustomizedHttpRequest) {
					CustomizedHttpRequest cmd = (CustomizedHttpRequest) nodeInfo;
					setText(cmd.getKeyword());
					setToolTipText("regex is " + cmd.isUseRegex());
				}
	        } 
	        

	        return this;
	    }

	}
	
	private TreeSelectionListener treeListener;
		
	private Map<CustomizedHttpRequest, CustomizedHttpResponse> httpCmdGroup;
	
	private JTree httpCmdtree;
	
	DefaultMutableTreeNode topNode;
	
	public HttpProxyTreePane(String title) {
		topNode = new DefaultMutableTreeNode(title);	    
	    
	    
	}
	
	public void setTreeListener(TreeSelectionListener listener) {
		this.treeListener = listener;
	}
	
	public void setRequestMap(Map<CustomizedHttpRequest, CustomizedHttpResponse> httpCmdGroup) {
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
	private void createNodes(DefaultMutableTreeNode top) {
	  
	    
	    for(Map.Entry<CustomizedHttpRequest, CustomizedHttpResponse> entry: httpCmdGroup.entrySet()) {
	    	DefaultMutableTreeNode category = new DefaultMutableTreeNode(entry.getKey());
		    top.add(category);
		    
	    }
	    
	}
	
	 
	public void valueChanged(TreeSelectionEvent e) {
		treeListener.valueChanged(e);
	}

}
