package com.github.walterfan.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
/**
 * @author sun.erick
 * modified by walter.fan, add addFolder
 */
public class ClassPathFinder {
    private static final Class[] parameters = new Class[] { URL.class };

    private static class JarFileFilter implements FileFilter {

        public boolean accept(File f) {
            if (f == null) {
                return false;
            }

            if (!f.isFile()) {
                return false;
            }

            String fileName = f.getName();
            return fileName.contains("jar");
        }
    }

    private static class JarFileComparator implements Comparator<File> {

        /**
         * File will order by last modified time desc.
         * 
         * @param f1
         * @param f2
         * @return 1 - Okay, -1 - need revise, 0 - equal
         */
        public int compare(File f1, File f2) {
            if (f1 == null || f2 == null) {
                return 0;
            }

            long t1 = f1.lastModified();
            long t2 = f2.lastModified();
            if (t1 > t2) {
                return -1;
            } else if (t1 < t2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static List<File> getDirFiles(String pathName) throws IOException {
        if (pathName == null) {
            throw new IOException("Path name is null!");
        }
        File fParent = new File(pathName);
        if (!fParent.exists()) {
            throw new IOException("Directory is not exist!");
        }
        if (!fParent.isDirectory()) {
            throw new IOException("Path name is not a directory!");
        }
        
        List<File> resultList = new LinkedList<File>();
        File[] fileList = fParent.listFiles(new JarFileFilter());
        for (File fChild : fileList) {
            resultList.add(fChild);
        }
        
        Collections.sort(resultList, new JarFileComparator());
        return resultList;
    }
    
    public static void addFolder(String path) {
        try {
            List<File> fileList = getDirFiles(path);
            for(File file: fileList) {
                addFile(file);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void addFile(String file) throws IOException {
        File f = new File(file);
        addFile(f);
    }

    public static void addFile(File f) throws IOException {
        addURL(f.toURL());
    }

    public static void addURL(URL u) throws IOException {

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }// end try catch

    }

    public static void main(String[] args) throws Exception {
        try {
            addFolder("D:/source/cvsfiles/server/WMAMS/webapp/dist");

            Class<?> cls = Class.forName("cn.fanyamin.util.NetUtils");
            Method method = cls.getDeclaredMethod("getLocalhostIP");
            System.out.println(method.invoke(cls));

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

    }

}
