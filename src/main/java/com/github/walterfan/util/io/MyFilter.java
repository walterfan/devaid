package com.github.walterfan.util.io;

import java.io.File;
import javax.swing.filechooser.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;


public class MyFilter extends FileFilter {

    private String[] arrSuffix;
    
    private String desc;
    
    public MyFilter(String[] arrSuffix, String desc) {
        this.arrSuffix = arrSuffix;
        this.desc = desc;
    }
    
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FilenameUtils.getExtension(f.getName());
        if (extension != null) {
            for (int i = 0; i < arrSuffix.length; i++) {
                if(StringUtils.endsWithIgnoreCase(extension,arrSuffix[i])) {
                    return true;
                }    
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return desc;
    }
}

