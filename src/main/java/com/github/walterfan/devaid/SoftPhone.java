/**
 * 
 */
package com.github.walterfan.devaid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.github.walterfan.util.ConfigLoader;
import com.github.walterfan.util.EncodeUtils;
import com.github.walterfan.util.GzipUtils;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;

/**
 * 
 * variable $var function: #length($var) #base64($var) #crc32($var) #gzip($var)
 * #base64(#gzip($var)))
 * 
 * 
 * @author walter
 * 
 */
public class SoftPhone extends SwingTool {

    private class DialingHandler implements ActionListener {

        public void actionPerformed(ActionEvent actionevent) {            
            String input = StringUtils.trim(txtIn.getText());
            if (StringUtils.isEmpty(input)) {
                SwingUtils.alert("Please input something.");
                return;
            }
            String tpl = StringUtils.trim(txtTpl.getText());
            String out = replace_dialing_string(tpl, input);
            txtOut.setText(out);
        }
    }

    private class RestoreHandler implements ActionListener {

        public void actionPerformed(ActionEvent actionevent) {
            String input = StringUtils.trim(txtOut.getText());
            if (StringUtils.isEmpty(input)) {
                SwingUtils.alert("Output is empty.");
                return;
            }
            String in = restore(input);
            txtIn.setText(in);
        }
    }
    
    private static String dialing_string = "";
    
    private static String dialing_numbers = "12345,12345789,3";

    private JButton btnClearIn = new JButton("clear input");

    private JButton btnClearOut = new JButton("clear output");
    
    private JButton btnCopy = new JButton("copy");

    private JButton btnAction = new JButton("Dial");

    private JButton btnRestore = new JButton("Bye");
    
    private JButton btnExit = new JButton("exit");

    private JTextArea txtIn = new JTextArea(6, 20);

    private JTextArea txtOut = new JTextArea(10, 20);

    private JTextArea txtTpl = new JTextArea(3, 20);

    public SoftPhone() {
        super();
    }

    public SoftPhone(String title) {
        super(title);
    }

    /**
     * 
     */
    @Override
    public void initComponents() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout(10, 10));
        txtTpl.setText(dialing_string);
        txtIn.setText(dialing_numbers);
        
        JSplitPane pane0 = SwingUtils.createVSplitPane(
                SwingUtils.createVTextComponentPane("Dialing String: ", txtTpl),
                SwingUtils.createVTextComponentPane("Dialing Number: ", txtIn), 100);

        JSplitPane pane = SwingUtils.createVSplitPane(pane0,
                SwingUtils.createVTextComponentPane("Output: ", txtOut), 250);

        container.add(pane, BorderLayout.CENTER);
        JPanel btnPane = SwingUtils.createHorizontalPanel(new Component[] {
                btnAction, btnRestore, btnCopy, btnClearIn, btnClearOut, btnExit });
        btnAction.addActionListener(new DialingHandler());
        btnRestore.addActionListener(new RestoreHandler());
        btnClearIn.addActionListener(ActionHandlerFactory
                .createClearHandler(txtIn));
        btnClearOut.addActionListener(ActionHandlerFactory
                .createClearHandler(txtOut));
        btnCopy.addActionListener(new ActionHandlerFactory.CopyActionHandler(
                txtOut));
        btnExit.addActionListener(ActionHandlerFactory.createExitHandler());

        container.add(btnPane, BorderLayout.SOUTH);

    }
    //input:12345,,,,,,***,,,12345789#,,3#***
    public int dialing(String line) throws InterruptedException {
    	for (int i = 0; i < line.length();i++) {
    		if (',' == line.charAt(i)) {
				TimeUnit.SECONDS.sleep(1);
    		}
    	}
    	return 0;
    }
    //input: [AccessNumber],,,,,,***,,,[MeetingKey]#,,[AttendeeID]#***
    //12345,12345789,3
    //output: 12345,,,,,,***,,,123456789#,,3#***
    public String replace_dialing_string(String line, String numbers) {
    	String[] arrTag = {"\\[AccessNumber\\]","\\[MeetingKey\\]","\\[AttendeeID\\]"};
    	String[] arrNum = numbers.split(",");
    	int cnt = arrNum.length;
    	for(int i=0; i<cnt; i++) {
    		line = line.replaceAll(arrTag[i], arrNum[i]);
    	}
    	
    	if(cnt==1)	{
    		line = line.replaceAll(arrTag[1], "");
    		line = line.replaceAll(arrTag[2], "");
    	}
    	if(cnt==2)	{
    		line = line.replaceAll(arrTag[2], "");
    	}
    	
        return line;
    }
    public String parse(String line, String content) {
        line = parse(line, content ,0);
        int posLeft = StringUtils.indexOf(line, "<![CDATA[");
        int posRight = StringUtils.lastIndexOf(line, "]]>");
        if(posLeft < 0 || posRight<0 || posLeft >= posRight) {
            return line;
        }
        content = StringUtils.substring(line, posLeft + 9, posRight);
        return parse(line, content ,1);
        
    }
    
    public String restore(String line) {
        int posLeft = StringUtils.indexOf(line, "gzip(");
        int posRight = StringUtils.lastIndexOf(line, ")");
        if(posLeft < 0 || posRight<0 || posLeft >= posRight) {
            return line;
        }
        StringBuilder sb = new StringBuilder();
        //sb.append(StringUtils.substring(line, 0, posLeft + 5));
        try {
            byte[] bytes = EncodeUtils.decodeBase64(StringUtils.substring(line, posLeft + 5, posRight).getBytes());
            sb.append(new String(GzipUtils.gunzipWithLen(bytes)));
        } catch (Exception e) {
            System.err.println("gunzip error: " + StringUtils.substring(line, posLeft + 5, posRight));
            sb.append(StringUtils.substring(line, posLeft + 5, posRight));
        }
        //sb.append(StringUtils.substring(line, posRight));
        return sb.toString();
    }
    
    public String parse(String line, String content, int round) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length();) {
            if ('$' == line.charAt(i)) {
                StringBuilder sbVar = new StringBuilder();
                for (int j = i + 1;; j++) {
                    char a = line.charAt(j);
                    if (Character.isLetter(a) || Character.isDigit(a)) {
                        sbVar.append(a);
                    } else {
                        i = j;
                        break;
                    }
                }
                String varName = sbVar.toString();
                if(round == 0) {
                    handleFunction(content, sb, varName); 
                } else if(round == 1) {
                    handleFunction1(content, sb, varName);
                }

            } else {
                sb.append(line.charAt(i));
                i++;
            } 
        }
        return sb.toString();
    }

    private void handleFunction(String content, StringBuilder sb, String varName) {
       /* if ("date".equals(varName)) {
            sb.append(DateFormatUtils.format(System.currentTimeMillis(),
                    "MM/dd/yyyy HH:mm:ss.SSS"));
        } else if ("pid".equals(varName)) {
            sb.append(SystemUtils.getPid());
        } else if ("base64Gzip".equals(varName)) {
            try {
                byte[] bytes = EncodeUtils.encodeBase64(GzipUtils
                        .gzipWithLen(content.getBytes()));
                System.out.println(content + "-->" + new String(bytes));
                sb.append(new String(bytes));
            } catch (Exception e) {
                sb.append("?" + varName + "?");
            }
        }  else {
            sb.append("$" + varName);
        }*/
    }

    private void handleFunction1(String content, StringBuilder sb, String varName) {
        if ("len".equals(varName)) {
            sb.append(StringUtils.length(content));
        } else if ("crc".equals(varName)) {
            CRC32 crc = new CRC32();
            crc.update(content.getBytes());
            sb.append(crc.getValue());
        } 
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SoftPhone txtTool = new SoftPhone();
        String cfgFilename = SoftPhone.class.getSimpleName() + ".properties";
        File cfgFile = new File(cfgFilename);

        System.out.println("load " + cfgFilename);
        ConfigLoader cfgLoader = ConfigLoader.getInstance();
        try {
            cfgLoader.loadFromClassPath(cfgFilename);
            dialing_string = cfgLoader.get("DialingString");
            System.out.println("DialingString= " + dialing_string);
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
            dialing_string = "";
        }

        if (args.length == 1) {
            System.out.println(txtTool.parse(dialing_string, args[0]));
            return;

        }
        SoftPhone aTool = new SoftPhone("Soft Phone v1.0");
        File readme = new File("./readme.txt");
        if (readme.exists()) {
            String help = "Ask Walter";
            try {
                help = FileUtils.readFileToString(readme);
            } catch (IOException e) {
                // ignore
            }
            if (StringUtils.isNotBlank(help)) {
                help = help.replace("\n", "<br/>");
            }
            aTool.setAppHelp(help);
        }
        aTool.init();
        SwingUtils.run(aTool, 800, 600);

    }

}
