package com.github.walterfan.util.swing;

import java.awt.Component;
import java.io.File;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

import com.github.walterfan.util.ByteUtil;


public class BinaryFileLoadHandler extends FileLoadHandler {
    public BinaryFileLoadHandler(JTextArea txtArea, Component component) {
        super(txtArea,component);
    }
    
    protected String readFileToString(File srcFile) throws Exception {
        byte[] bytes = FileUtils.readFileToByteArray(srcFile);
        Object obj = ByteUtil.bytes2Object(bytes);
        if(null == obj) {
            return "null";
        }
        return obj.toString();
    }
}
