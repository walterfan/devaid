package com.github.walterfan.devaid.sql;

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


public class SQLCmdTreePane extends JPanel implements TreeSelectionListener {
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
				if (nodeInfo instanceof SQLCommand) {
					SQLCommand cmd = (SQLCommand) nodeInfo;
					setText(cmd.getName());
					setToolTipText(cmd.getSql());					
				}
	        } 
	        

	        return this;
	    }

	}
	
	private TreeSelectionListener treeListener;
		
	private Map<String, SQLCommands> sqlCmdGroup;

	private JTree sqlCmdtree;
	
	DefaultMutableTreeNode topNode;
	
	public SQLCmdTreePane(String title) {
		topNode = new DefaultMutableTreeNode(title);	    
	    
	    
	}
	
	public void setTreeListener(TreeSelectionListener listener) {
		this.treeListener = listener;
	}
	
	public void setSqlCmdGroup(Map<String, SQLCommands> httpCmdGroup) {
		this.sqlCmdGroup = httpCmdGroup;
	}
	
	public JTree getHttpCmdTree() {
		return this.sqlCmdtree;
	}
	
	public void init() {
		createNodes(topNode);
		sqlCmdtree = new JTree(topNode);
	    sqlCmdtree.addTreeSelectionListener(this.treeListener);
	    sqlCmdtree.setCellRenderer(new MyRenderer());

	    JScrollPane treeView = new JScrollPane(sqlCmdtree);
	    this.add(treeView, BorderLayout.CENTER);
	}
	private void createNodes(DefaultMutableTreeNode top) {
	  
	    
	    for(Map.Entry<String, SQLCommands> entry: sqlCmdGroup.entrySet()) {
	    	DefaultMutableTreeNode category = new DefaultMutableTreeNode(entry.getKey());
		    top.add(category);
		    SQLCommands group = entry.getValue();
		    for(Map.Entry<String, SQLCommand> entry0 : group) {
		    	DefaultMutableTreeNode apiNode = new DefaultMutableTreeNode(entry0.getValue());
		    	category.add(apiNode);
		    }
	    }
	    
	}
	
	 
	public void valueChanged(TreeSelectionEvent e) {
		treeListener.valueChanged(e);
	}

	
	/*public static void main(String[] args) throws Exception {
		
		
		final SQLCmdTreePane cmdTree = new SQLCmdTreePane("Http APIs");
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
	}*/
}
