package com.github.walterfan.devaid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.zip.CRC32;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.github.walterfan.util.EncodeUtils;
import com.github.walterfan.util.Encryptor;
import com.github.walterfan.util.ParamUtils;
import com.github.walterfan.util.RandomUtils;
import com.github.walterfan.util.TimeZoneConv;
import com.github.walterfan.util.XmlUtils;
import com.github.walterfan.util.ZipUtils;
import com.github.walterfan.util.swing.ActionHandlerFactory;
import com.github.walterfan.util.swing.SwingTool;
import com.github.walterfan.util.swing.SwingUtils;




interface ConvertHandler {
    String convert(String text) throws Exception;
}

interface EncodeHandler extends ConvertHandler {
    String convert(String text) throws Exception;
    String decode(String text) throws Exception;
}

/**
 * @author walter
 *
 */
public class EncodeTool extends SwingTool {

	private static final long serialVersionUID = 1L;
	private static final Color DEFAULT_COLOR = new Color(0x99,0xFF,0xCC);
    public static int TEXT_SIZE = 8;
	
    private class EncryptHandler implements EncodeHandler {

        public String convert(String text) throws Exception {
            String key = StringUtils.trim(txtKey.getText());
            String algorithm = (String)algorithmList.getSelectedItem() ;
            String spec = buildAlgorithmSpec(algorithm);
            Encryptor enc = new Encryptor(spec);
            return EncodeUtils.byte2Hex((enc.encode(text.getBytes(), key.getBytes())));
            
        }
        
        public String decode(String text) throws Exception {
        	String key = StringUtils.trim(txtKey.getText());
            String mode = (String)modeList.getSelectedItem();
            String padding = (String)paddingList.getSelectedItem();
            String algorithm = "AES" ;
            if(!StringUtils.contains(mode, "select")) {
            	algorithm = algorithm + "/" + mode;
            }
            if(!StringUtils.contains(mode, "select")) {
            	algorithm = algorithm + "/" + padding;
            }
            Encryptor enc = new Encryptor(algorithm);
            return new String(enc.decode(EncodeUtils.hex2Byte(text), key.getBytes()));
        }
    }
    
    private class BtnEncodeHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String encode = (String)algorithmList.getSelectedItem();
            ConvertHandler handler = handlerMap.get(encode);
            if(handler != null) {
                try {
                    txtOutput.setText(handler.convert(txtInput.getText()));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            } else {
                SwingUtils.alert("Have not support " + encode);
            }
                
        }
    }

    private class BtnDecodeHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String encode = (String)algorithmList.getSelectedItem();
            ConvertHandler handler = handlerMap.get(encode);
            if(handler != null) {
                if(!EncodeHandler.class.isInstance(handler)) {
                    SwingUtils.alert("Do not support decode");
                    return;
                }
                try {
                    txtInput.setText(((EncodeHandler)handler).decode(txtOutput.getText()));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    SwingUtils.alert(e1.getMessage());
                }
            } else {
                SwingUtils.alert("Have not support " + encode);
            }
                
        }
    }
    
    private class ClearHandler implements ActionListener {

        JTextArea txtArea = null;
        
        public ClearHandler(JTextArea txtArea) {
            this.txtArea = txtArea;
        }
        
        public void actionPerformed(ActionEvent event) {
            txtArea.setText("");           
        }
    }
    
    private class GenKeyHandler implements ActionListener {

    	private JTextArea textArea;
    	public GenKeyHandler(JTextArea textArea) {
    		this.textArea = textArea;
    	}
        public void actionPerformed(ActionEvent event) {
        	String ctype = (String)charsList.getSelectedItem();
    		int count = NumberUtils.toInt(txtCharsCount.getText());
    		String ret = RandomUtils.getRandomChars(getChars(ctype), count);
    		textArea.setText(ret);
        }
    }
    
    private class MakeKeyHandler implements ActionListener {

    	private JTextArea textArea;
    	public MakeKeyHandler(JTextArea textArea) {
    		this.textArea = textArea;
    	}
        public void actionPerformed(ActionEvent event) {
        	String algorithm = (String)algorithmList.getSelectedItem() ;
        	String spec = buildAlgorithmSpec(algorithm);
        	Encryptor enc = new Encryptor(spec);
        	try {
				textArea.setText(new String((enc.makeKey())));
			} catch (NoSuchAlgorithmException e) {
				SwingUtils.alert(e.getMessage());
			}
        }
    }
    
    private class LoadHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	JFileChooser c = null;
        	if(StringUtils.isNotEmpty(getFilePath())) {
        		c = new JFileChooser(getFilePath());
        	} else {
        		c = new JFileChooser(".");
        	}
            int rVal = c.showOpenDialog(EncodeTool.this);
            if(rVal==JFileChooser.APPROVE_OPTION) {
                
                File srcFile = c.getSelectedFile();
                saveFilePath(srcFile.getAbsolutePath());
                try {
                	String content = FileUtils.readFileToString(srcFile, "GBK");
                	txtInput.setText(content);
				} catch (IOException e1) {
					SwingUtils.alert(e1.getMessage());
				}
            }
        }
    }
    
    private class SaveHandler implements ActionListener {
	    public void actionPerformed(ActionEvent event) {
	        JFileChooser c = new JFileChooser("./log");
	        int rVal = c.showSaveDialog(EncodeTool.this);
	        if(rVal==JFileChooser.APPROVE_OPTION) {
	        	File targetFile = c.getSelectedFile();
	        	String content = txtOutput.getText();
	        	if(StringUtils.isBlank(content)) {
	        		SwingUtils.alert("The output text area is empty.");
	        	}
	        	try {
					FileUtils.writeStringToFile(targetFile, content, "UTF-8");
				} catch (IOException e) {
					SwingUtils.alert(e.getMessage());
				}
	        }
        }
	}
       
	private static final Font displayFont = new Font("Arial", Font.PLAIN, 18);

	private JTextArea txtInput = new JTextArea(3,28);
	
	private JTextArea txtOutput = new JTextArea(3,28);
	
	private JTextArea txtKey = new JTextArea(3,10);
	
	private JButton btnEncode = new JButton("encode =>");
	
	private JButton btnDecode = new JButton("<= decode");
	
	private JButton btnGenKey = new JButton("generate key");
	
	private JButton btnMakeKey = new JButton("make key");
	
	private JButton btnMakeInput = new JButton("<- generate input");
	
	private JButton btnMakeOutput = new JButton("generate output ->");
	
	private JButton btnAbout = new JButton("about");
	
	private JButton btnResetInput = new JButton("<- clear input");
	
	private JButton btnExit = new JButton("exit");
	
	private JButton btnResetOutput = new JButton("clear output ->");
	
	private JButton btnResetKey = new JButton("clear key");
    
	private JComboBox algorithmList;
	
	private JComboBox modeList;
	
	private JComboBox paddingList;
	
	private JComboBox charsList ;
	
	private JTextField txtCharsCount = new JTextField("16",5);
	
	private Map<String, ConvertHandler> handlerMap 
	    = new TreeMap<String, ConvertHandler>();
	
	public EncodeTool() {
		this("Encode tool v2.0");
	}
	
	public EncodeTool(String title) {
		super(title);
		initiateHandlerMap();
		initiateAlgorithmList();
		initiateModeList();
		initiatePaddingList();
		initiateCharsList();
		arrange();
		this.txtInput.setBackground(DEFAULT_COLOR);
		this.txtOutput.setBackground(DEFAULT_COLOR);
		this.txtKey.setBackground(DEFAULT_COLOR);
		super.init();
	}

	public String getFilePath() {
		Preferences pref = Preferences.userRoot().node("/cn/fanyamin");
		String lastPath = pref.get("lastPath", "");
		return lastPath;
	}
	
	public void saveFilePath(String path) {
		Preferences pref = Preferences.userRoot().node("/cn/fanyamin");
		pref.put("lastPath", path);
		
	}
	
    public void initiateHandlerMap() {
        handlerMap.put("Base64", new EncodeHandler() {
            public String convert(String text) throws Exception {
                return new String(EncodeUtils.encodeBase64(text.getBytes()));
            }

            public String decode(String text) throws Exception {
                return new String(EncodeUtils.decodeBase64(text.getBytes()));
            }
        });
        
        handlerMap.put("Hex-base64", new EncodeHandler() {
            public String convert(String text) throws Exception {
                     return new String(Base64.encodeBase64(EncodeUtils.hex2Byte(text)));
            }

            public String decode(String text) throws Exception {
                 return new String(EncodeUtils.byte2Hex(Base64.decodeBase64(text.getBytes())));
            }
        });
        handlerMap.put("URL", new EncodeHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.urlEncode(text);
            }

            public String decode(String text) throws Exception {
                return EncodeUtils.urlDecode(text);
            }
        });

        handlerMap.put("native2ascii", new EncodeHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.native2ascii(text);
            }

            public String decode(String text) throws Exception {
                return EncodeUtils.ascii2native(text);
            }
        });

        handlerMap.put("Html", new EncodeHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.htmlEncode(text);
            }

            public String decode(String text) throws Exception {
                return EncodeUtils.htmlDecode(text);
            }
        });

        handlerMap.put("Timestamp-String", new EncodeHandler() {
            public String convert(String text) throws Exception {
                java.util.Date now = new java.util.Date(Long.valueOf(text));
                return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
                        .format(now);
            }

            public String decode(String text) throws Exception {
                java.util.Date now = new SimpleDateFormat(
                        TimeZoneConv.DATE_FMT_ISO).parse(text);
                return String.valueOf(now.getTime());
            }
        });

        handlerMap.put("ParameterBase64", new EncodeHandler() {
            public String convert(String text) throws Exception {
                return ParamUtils.encode(text.trim());
            }

            public String decode(String text) throws Exception {
                return ParamUtils.decode(text.trim());
            }
        });
        
        handlerMap.put("XmlParameterBase64", new EncodeHandler() {
            public String convert(String text) throws Exception {
            	return XmlUtils.encode(text.trim());
            }

            public String decode(String text) throws Exception {
            	return XmlUtils.decode(text.trim());
            }
        });

        handlerMap.put("zip-base64", new EncodeHandler() {
            public String convert(String text) throws Exception {
                     return new String(Base64.encodeBase64(ZipUtils.zip(text.getBytes())));
            }

            public String decode(String text) throws Exception {
                 return new String(ZipUtils.unzip(Base64.decodeBase64(text.getBytes())));
            }
        });

        handlerMap.put("zip-base64-with-len", new EncodeHandler() {
            public String convert(String text) throws Exception {
                     return new String(Base64.encodeBase64(ZipUtils.zipWithLen(text.getBytes())));
            }

            public String decode(String text) throws Exception {
                 return new String(ZipUtils.unzipWithLen(Base64.decodeBase64(text.getBytes())));
            }
        });
        
        handlerMap.put("SQL", new ConvertHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.sqlEncode(text);
            }
        });

        handlerMap.put("MD2", new ConvertHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.byte2Hex(EncodeUtils.md2(text));
            }
        });

        handlerMap.put("MD5", new ConvertHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.byte2Hex(EncodeUtils.md5(text));
            }
        });

        handlerMap.put("SHA1", new ConvertHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.byte2Hex(EncodeUtils.sha1(text));
            }
        });

        handlerMap.put("SHA2", new ConvertHandler() {
            public String convert(String text) throws Exception {
                return EncodeUtils.byte2Hex(EncodeUtils.sha2(text));
            }
        });

        handlerMap.put("10-2", new EncodeHandler() {
            public String convert(String text) throws Exception {
                long num = NumberUtils.toLong(text);
                return Long.toBinaryString(num);
            }

            public String decode(String text) throws Exception {
                return "" + Long.valueOf(text, 2);
            }
        });

        handlerMap.put("10-16", new EncodeHandler() {
            public String convert(String text) throws Exception {
                long num = NumberUtils.toLong(text);
                return Long.toHexString(num);
            }

            public String decode(String text) throws Exception {
                return "" + Long.valueOf(text, 16);
            }
        });

        handlerMap.put("CRC32", new ConvertHandler() {
            public String convert(String text) throws Exception {
                CRC32 crc = new CRC32();
                crc.update(text.getBytes());
                return "" + crc.getValue();
            }
        });

        handlerMap.put("AES", new EncryptHandler());
        // handlerMap.put("DES", new EncryptHandler());
        // handlerMap.put("DESede", new EncryptHandler());
        // handlerMap.put("Blowfish", new EncryptHandler());

    }

	private void arrange() {

		JPanel mainPane =  new JPanel(new BorderLayout());	
		this.setLayout(new BorderLayout());
		
		txtInput.setLineWrap(true);
		txtOutput.setLineWrap(true);
		JScrollPane left = new JScrollPane(txtInput);
		JScrollPane right = new JScrollPane(txtOutput);
		SwingUtils.createStdEditPopupMenu(new JTextComponent[]{txtInput, txtOutput});
		
		JPanel center = new JPanel();
		GridLayout g = new GridLayout(10,1);
		g.setVgap(3);
		center.setLayout(g);
		center.add(this.algorithmList);
		
		center.add(modeList);

		center.add(paddingList);
		
		btnEncode.setToolTipText("Encode");
        center.add(btnEncode);
        btnEncode.addActionListener(new BtnEncodeHandler());
        
        center.add(this.btnMakeInput);
        btnMakeInput.addActionListener(new GenKeyHandler(txtInput));
        
        center.add(this.btnResetInput);
        btnResetInput.addActionListener(new ClearHandler(txtInput)); 
        
        center.add(new JLabel(""));
        
        
        btnDecode.setToolTipText("Decode");
        center.add(btnDecode);
        btnDecode.addActionListener(new BtnDecodeHandler());
               
        center.add(this.btnMakeOutput);
        btnMakeOutput.addActionListener(new GenKeyHandler(txtOutput));
        
        center.add(this.btnResetOutput);
        btnResetOutput.addActionListener(new ClearHandler(txtOutput)); 
        
        
        Box topBox = Box.createHorizontalBox();
		topBox.setBorder(new EmptyBorder(5, 5, 5, 5));
		topBox.add(left);
		topBox.add(center);
		topBox.add(right);
		
		JPanel bottomPane = new JPanel(new GridLayout(1,1));
		bottomPane.setBorder(new EmptyBorder(5, 5, 5, 5));	
		
		Box keyBox = arrangeButtons();
		
		JPanel rightPane = new JPanel(new BorderLayout());
		
		rightPane.add(keyBox, BorderLayout.NORTH);
		rightPane.add(new JScrollPane(this.txtKey), BorderLayout.CENTER);

		bottomPane.add(rightPane);
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPane.add(new JLabel("  Encode and Encrypt"), BorderLayout.NORTH);
		
		JSplitPane spiltPane = SwingUtils.createVSplitPane(topBox, bottomPane, 400);
		
		mainPane.add(spiltPane, BorderLayout.CENTER);
		//mainPane.add(bottomPane, BorderLayout.SOUTH);
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		createMenu(this);
	}

	private Box arrangeButtons() {
		Box hBox = Box.createHorizontalBox();
		
		hBox.add(Box.createHorizontalGlue());
		hBox.add(new JLabel(" Key(as hex): ", JLabel.LEFT));

		hBox.add(charsList);

		hBox.add(new JLabel(" count: ", JLabel.LEFT));
		hBox.add(txtCharsCount);
		//hBox.add(new JLabel(" bytes ", JLabel.LEFT));
		
		hBox.add(Box.createHorizontalGlue());
		hBox.add(this.btnGenKey);
		btnGenKey.setToolTipText("generate key by random");
		btnGenKey.addActionListener(new GenKeyHandler(txtKey));
		
//		hBox.add(this.btnMakeKey);
//		btnMakeKey.setToolTipText("make key by KeyGenerator as specified algorithm");
//		btnMakeKey.addActionListener(new MakeKeyHandler(txtKey));
		
		hBox.add(this.btnResetKey);
        btnResetKey.addActionListener(new ClearHandler(txtKey));
        
        hBox.add(this.btnAbout);
		btnAbout.addActionListener(ActionHandlerFactory.createAboutHandler(
				this, "About Encoding Tool v1.0", " Wrote by Walter Fan, updated on 07/11/09 ", 320, 100));
		
		hBox.add(this.btnExit);
		btnExit.addActionListener(ActionHandlerFactory.createExitHandler());
		hBox.add(Box.createHorizontalGlue());
		
		return hBox;
	}


	private void initiateModeList() {
		Vector<String> vec = new Vector<String>();
		vec.add("-- select mode --");
		vec.add("None");
		vec.add("CBC");
		vec.add("CFB");
		vec.add("ECB");
		vec.add("OFB");
		vec.add("CBC");
		vec.add("PCBC");
		modeList = new JComboBox(vec);
		modeList.setFont(displayFont);
		modeList.setEditable(true);
	}
	
	private void initiatePaddingList() {
		Vector<String> vec = new Vector<String>();
		vec.add("-- select padding --");
		vec.add("NoPadding");
		vec.add("ISO10126Padding");
		vec.add("PKCS5Padding");
		vec.add("SSL3Padding");
		paddingList = new JComboBox(vec);
		paddingList.setFont(displayFont);
		paddingList.setEditable(true);
	}
	
	
	private void initiateAlgorithmList() {
	    Vector<String> vec = new Vector<String>();
		for(Map.Entry<String, ConvertHandler> entry: this.handlerMap.entrySet()) {
		    vec.add(entry.getKey());  
		}
        //Collections.sort(vec);
		/*vec.add("AES encrypt");
		vec.add("AES decrypt");
		vec.add("DES encrypt");
		vec.add("DES decrypt");
		vec.add("CRC16");
		vec.add("CRC32");
		vec.add("RSA");
		vec.add("DSA");*/
		       
		algorithmList = new JComboBox(vec);
		algorithmList.setFont(displayFont);
		algorithmList.setEditable(true);
		
	}

	private void initiateCharsList() {
		Vector<String> vec = new Vector<String>();
		vec.add("-- select chars --");
		vec.add("Numbers");
		vec.add("Letters");
		vec.add("Hex numbers");
		vec.add("ASCII chars");
		charsList = new JComboBox(vec);
		charsList.setEditable(true);
	}
	
	public static String getChars(String ctype) {
		if("-- select chars --".equals(ctype)) {
			return RandomUtils.ASCIICHARS;
		} else if ("Numbers".equals(ctype)){
    		return RandomUtils.NUMBERS;
    	} else if ("Letters".equals(ctype)){
    		return RandomUtils.LETTERS;
    	} else if ("Hex numbers".equals(ctype)){
    		return RandomUtils.HEXS;
    	} else if ("ASCII chars".equals(ctype)){
    		return RandomUtils.CHARS;
    	} else {
    		return "";
    	}
    }
	
	
	public void createMenu(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);		
		JMenu fileMenu = new JMenu("Usage");
		menuBar.add(fileMenu);
		
		//ConfigMenuItemHandler
		JMenuItem loadItem = new JMenuItem("Load File");
        loadItem.addActionListener(new LoadHandler());
        fileMenu.add(loadItem);
        loadItem.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_L,InputEvent.CTRL_MASK, false));
        
        JMenuItem saveItem = new JMenuItem("Save File");
        saveItem.addActionListener(new SaveHandler());
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        saveItem.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK, false));
        
        
        JMenuItem executeItem = new JMenuItem("Convert");
        executeItem.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_F5,0, false));
        executeItem.addActionListener(new BtnEncodeHandler());
        fileMenu.add(executeItem);
                     
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(ActionHandlerFactory.createExitHandler());
        exitItem.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK, false));
        fileMenu.add(exitItem);
        
        
        JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(ActionHandlerFactory.createAboutHandler(
				this, "Help of Encode Tool v1.0", " Encode and Decode text ", 240, 100));

        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(ActionHandlerFactory.createAboutHandler(
				this, "About Encode Tool v1.0", " Wrote by Walter Fan, updated on 07/11/09 ", 320, 100));

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);
		
	}
	private String buildAlgorithmSpec(String algorithm) {
		String mode = (String)modeList.getSelectedItem();
		String padding = (String)paddingList.getSelectedItem();
		
		String spec = algorithm;
		if(!StringUtils.contains(mode, "select")) {
			spec = spec + "/" + mode;
		}
		if(!StringUtils.contains(padding, "select")) {
			spec = spec + "/" + padding;
		}
		SwingUtils.alert(spec);
		return spec;
	}

	public void initComponents() {
		//todo
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtils.run(new EncodeTool("Encoding tool v1.0"), 800, 600);

	}

}
