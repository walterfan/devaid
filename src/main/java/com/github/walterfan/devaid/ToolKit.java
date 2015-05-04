package com.github.walterfan.devaid;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.walterfan.devaid.http.HttpTool;
import com.github.walterfan.devaid.sql.SQLTool;
import com.github.walterfan.util.swing.SwingUtils;


/**
 * @author walter
 * 
 */
public class ToolKit extends JFrame implements ChangeListener {

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;

    private List<JFrame> frameList = new ArrayList<JFrame>(10);

    public ToolKit(String title) {
        super(title);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.addChangeListener(this);

        this.getContentPane().add(tabbedPane);

    }

    public void addFrame(JFrame f) {
        if (frameList.isEmpty()) {
            this.setJMenuBar(f.getJMenuBar());
        }
        frameList.add(f);
        f.setVisible(false);
        tabbedPane.addTab(f.getTitle(), f.getContentPane());
    }

    public void stateChanged(ChangeEvent e) {
        Object selected = tabbedPane.getSelectedComponent();

        for (JFrame frame : frameList) {
            if (frame.getContentPane() == selected) {
                setJMenuBar(frame.getJMenuBar());
                break;
            }
        }

    }

    public static void start(String[] args) {
  
        ToolKit frameContainer = new ToolKit(
                "Development Aid Tool Kits v1.3 - Walter Fan Ya Min");

        frameContainer.addFrame(new HttpTool("Http Tool"));
        
        SQLTool sqlTool = new SQLTool("SQL Tool");
        sqlTool.init();
        frameContainer.addFrame(sqlTool);
        
        RegexTool regexTool = new RegexTool("Regular Expression Tool");
        regexTool.init();
        frameContainer.addFrame(regexTool);
        
        EncodeTool encodeTool = new EncodeTool("Encode Tool");
        encodeTool.init();
        frameContainer.addFrame(encodeTool);

        CassandraTool kvTool = new CassandraTool("Cassandra Tool v1.0");
        kvTool.init();
        frameContainer.addFrame(kvTool);
        
        JmsTool jmsTool = new JmsTool("JMS Tool v1.0");
        jmsTool.init();
        frameContainer.addFrame(jmsTool);

        
        JFrame tzTool = new TimeZoneConverter();
		frameContainer.addFrame(tzTool);
		
        SwingUtils.run(frameContainer, 1000, 700);
    }
    
    public static void main( String[] args )
    {
       ToolKit.start(args);
    }
    
    
}
