package com.github.walterfan.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {

    private static Log logger = LogFactory.getLog(FileUtil.class);

    static public boolean isFileExists(String filename) {
        File f = new File(filename);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param is
     *            InputStream/OutputStream
     */
    public static void close(Closeable is) {
        if (is != null) {
            return;
        }
        try {
            is.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static String getCurrentPath() {
        File f = new File("./");
        return f.getAbsolutePath();
    }
 
    public static void replaceFile(File srcFile, File destFile, String srcStr, String destStr) throws IOException {
        byte[] bytes = readFromFile(srcFile);
        String oldText = new String(bytes);
        String newText = oldText.replaceAll(srcStr, destStr);
        writeToFile(destFile, newText.getBytes());
    }

    public static byte[] readFromFile(File file) throws IOException {

        FileInputStream in = new FileInputStream(file);

        try {
            DataInputStream din = new DataInputStream(in);
            byte[] buf = new byte[in.available()];
            din.readFully(buf);
            return buf;
        } finally {
            close(in);
        }
    }
    
    public static byte[] readFromFile(String filename) throws IOException {
        return readFromFile(new File(filename));
    }

    public static InputStream readFromClassPath(String name) throws IOException {
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(name);
    }
    
    public static void writeToFile(String name, byte[] bytes) throws IOException {
        writeToFile(new File(name), bytes);
    }

    public static void writeToFile(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);

        try {
            out.write(bytes, 0, bytes.length);
            out.flush();
        } finally {
            close(out);
        }
    }
    public static void createFile(String filename, String content) throws IOException {
        File file = new File(filename);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.close();
    }

    public static void createFile(String filename, String content, boolean ignoreIfExitst) throws IOException {
        File file = new File(filename);
        if (ignoreIfExitst && file.exists()) {
            return;
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.close();
    }

    public static boolean isFileExist(String filename) {
        File file = new File(filename);

        if (file.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public static void createDir(String dir, boolean ignoreIfExitst) throws IOException {
        File file = new File(dir);

        if (ignoreIfExitst && file.exists()) {
            return;
        }

        if (file.mkdir() == false) {
            throw new IOException("Cannot create the directory = " + dir);
        }
    }

    public static void createDirs(String dir, boolean ignoreIfExitst) throws IOException {
        File file = new File(dir);

        if (ignoreIfExitst && file.exists()) {
            return;
        }

        if (file.mkdirs() == false) {
            throw new IOException("Cannot create directories = " + dir);
        }
    }

    public static void deleteFile(String filename) throws IOException {
        File file = new File(filename);

        if (file.isDirectory()) {
            throw new IOException("IOException -> BadInputException: not a file.");
        }
        if (file.exists() == false) {
            throw new IOException("IOException -> BadInputException: file is not exist.");
        }
        if (file.delete() == false) {
            throw new IOException("Cannot delete file. filename = " + filename);
        }
    }

    public static void deleteDir(File dir) throws IOException {
        if (dir.isFile())
            throw new IOException("IOException -> BadInputException: not a directory.");
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    file.delete();
                } else {
                    deleteDir(file);
                }
            }
        }// if
        dir.delete();
    }

    public static long getDirLength(File dir) throws IOException {
        if (dir.isFile())
            throw new IOException("BadInputException: not a directory.");
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                long length = 0;
                if (file.isFile()) {
                    length = file.length();
                } else {
                    length = getDirLength(file);
                }
                size += length;
            }// for
        }// if
        return size;
    }

    public static long getDirLength_onDisk(File dir) throws IOException {
        if (dir.isFile())
            throw new IOException("BadInputException: not a directory.");
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                long length = 0;
                if (file.isFile()) {
                    length = file.length();
                } else {
                    length = getDirLength_onDisk(file);
                }
                double mod = Math.ceil(((double) length) / 512);
                if (mod == 0)
                    mod = 1;
                length = ((long) mod) * 512;
                size += length;
            }
        }// if
        return size;
    }
    


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        byte[] block = new byte[512];
        while (true) {
            int readLength = inputStream.read(block);
            if (readLength == -1)
                break;// end of file
            byteArrayOutputStream.write(block, 0, readLength);
        }
        byte[] retValue = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return retValue;
    }

    public static String getFileName(String fullFilePath) {
        if (fullFilePath == null) {
            return "";
        }
        int index1 = fullFilePath.lastIndexOf('/');
        int index2 = fullFilePath.lastIndexOf('\\');

        // index is the maximum value of index1 and index2
        int index = (index1 > index2) ? index1 : index2;
        if (index == -1) {
            // not found the path separator
            return fullFilePath;
        }
        String fileName = fullFilePath.substring(index + 1);
        return fileName;
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static boolean isDir(String dirName) {
        try {
            File aFile = new File(dirName);
            if (aFile.isDirectory()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return false;
        }
    }

    public static void dirList(String folder, ArrayList files) {
        File dirpath = new File(folder);
        String[] dirlist;
        dirlist = dirpath.list();
        for (int i = 0; i < dirlist.length; i++) {
            dirlist[i] = folder + "/" + dirlist[i];
            files.add(dirlist[i]);
            if (isDir(dirlist[i])) {
                dirList(dirlist[i], files);
            }
        }
    }

    public static void zip(String zipFileName, String inputFile) throws Exception {
        zip(zipFileName, new File(inputFile));
    }

    public static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        logger.debug("zip done");
        out.close();
    }

    public static void unzip(String zipFileName, String outputDirectory) throws Exception {
        ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
        ZipEntry z;
        while ((z = in.getNextEntry()) != null) {
            logger.debug("unziping " + z.getName());
            if (z.isDirectory()) {
                String name = z.getName();
                name = name.substring(0, name.length() - 1);
                File f = new File(outputDirectory + File.separator + name);
                f.mkdir();
                logger.debug("mkdir " + outputDirectory + File.separator + name);
            } else {
                File f = new File(outputDirectory + File.separator + z.getName());
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                int b;
                while ((b = in.read()) != -1)
                    out.write(b);
                out.close();
            }
        }

        in.close();
    }

    public static void zip(ZipOutputStream out, File f, String base) throws Exception {
        logger.debug("Zipping  " + f.getName());
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            // out.putNextEntry(new ZipEntry(base + "/"));
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            // out.putNextEntry(new ZipEntry(base));
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1)
                out.write(b);
            in.close();
        }

    }

    public static void main(String[] args) {
        File f = new File("./");
        System.out.println(f.getPath());
        System.out.println(f.getAbsolutePath());
    }
}
