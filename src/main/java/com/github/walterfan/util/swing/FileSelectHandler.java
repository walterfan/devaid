package com.github.walterfan.util.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;

public class FileSelectHandler implements ActionListener {
    final JTextComponent txtArea_;
    final Component component_;

    public FileSelectHandler(JTextComponent txtArea, Component component) {
        txtArea_ = txtArea;
        component_ = component;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser c = null;
        if (StringUtils.isNotEmpty(getFilePath())) {
            c = new JFileChooser(getFilePath());
        } else {
            c = new JFileChooser(".");
        }
        int rVal = c.showOpenDialog(component_);
        if (rVal == JFileChooser.APPROVE_OPTION) {

            File srcFile = c.getSelectedFile();
            if (srcFile.exists()) {
                txtArea_.setText(srcFile.getPath());
                saveFilePath(srcFile.getAbsolutePath());
            }
        }
    }

    public String getFilePath() {
        Preferences pref = Preferences.userRoot().node(
                component_.getClass().getName());
        String lastPath = pref.get("lastPath", "");
        return lastPath;
    }

    public void saveFilePath(String path) {
        Preferences pref = Preferences.userRoot().node(
                component_.getClass().getName());
        pref.put("lastPath", path);

    }
}