package com.github.walterfan.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GzipUtils {
    private static final int INT_BYTE_LEN = 4;
    public static final int BUFFER = 4096;
    public static final String CHARSET = "ISO-8859-1";

    public static void gzip(InputStream is, OutputStream os) throws IOException {

        GZIPOutputStream gos = null;

        try {
            gos = new GZIPOutputStream(os);

            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }

            gos.finish();
            gos.flush();
        } finally {
            closeQuietly(gos);
        }
    }

    public static void gunzip(InputStream is, OutputStream os)
            throws IOException {

        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }

        } finally {
            closeQuietly(gis);
        }

    }

    public static byte[] gzipObject(Serializable data) throws IOException {
        ByteArrayOutputStream baOut = null;
        GZIPOutputStream gzOut = null;
        ObjectOutputStream objOut = null;
        try {
            baOut = new ByteArrayOutputStream();
            gzOut = new GZIPOutputStream(baOut);
            objOut = new ObjectOutputStream(gzOut);
            objOut.writeObject(data);
            gzOut.finish();
            gzOut.flush();
            return baOut.toByteArray();
        } finally {
            closeQuietly(objOut);
            closeQuietly(gzOut);
            closeQuietly(baOut);
        }

    }

    public static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] gzipWithLen(byte[] bytes) throws IOException {
        ByteArrayOutputStream baOut = null;
        GZIPOutputStream gzOut = null;
        try {
            baOut = new ByteArrayOutputStream();
            baOut.write(EncodeUtils.int2ByteArray(bytes.length), 0, 4);
            gzOut = new GZIPOutputStream(baOut);
            gzOut.write(bytes);
            gzOut.finish();
            gzOut.flush();
            return baOut.toByteArray();
        } finally {
            closeQuietly(gzOut);
            closeQuietly(baOut);
        }
    }
    
   
    public static byte[] gunzipWithLen(byte[] bytes) throws IOException {

        ByteArrayInputStream baIn = null;
        GZIPInputStream gzIn = null;
        try {
            baIn = new ByteArrayInputStream(bytes);
            byte arrLen[] = new byte[4];
            int cnt = baIn.read(arrLen, 0, INT_BYTE_LEN);
            if (cnt != INT_BYTE_LEN) {
                return null;
            }
            int nLen = EncodeUtils.byteArray2Int(arrLen);
            if (nLen == 0) {
                return null;
            }
            // System.out.println("length="+ nLen);
            gzIn = new GZIPInputStream(baIn);
            byte data[] = new byte[nLen];
            int nTotalSize = 0;
            while (nTotalSize < nLen) {
                int nSize = gzIn.read(data, nTotalSize, nLen - nTotalSize);
                if (nSize < 0) {
                    break;
                }
                nTotalSize += nSize;
            }

            if (nTotalSize != nLen) {
                // System.err.println(nTotalSize +"!="+ nLen);
                return null;
            }
            return data;
        } finally {
            closeQuietly(gzIn);
            closeQuietly(baIn);

        }
    }

    public static byte[] gzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream baOut = null;
        GZIPOutputStream gzOut = null;
        try {
            baOut = new ByteArrayOutputStream();
            gzOut = new GZIPOutputStream(baOut);
            gzOut.write(bytes);
            gzOut.finish();
            gzOut.flush();
            return baOut.toByteArray();
        } finally {
            closeQuietly(gzOut);
            closeQuietly(baOut);
        }
    }

    public static byte[] gunzip(byte[] bytes) throws IOException {

        ByteArrayOutputStream baOut = null;
        ByteArrayInputStream baIn = null;
        GZIPInputStream gzIn = null;
        try {
            baOut = new ByteArrayOutputStream();
            baIn = new ByteArrayInputStream(bytes);
            gzIn = new GZIPInputStream(baIn);
            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = gzIn.read(data, 0, BUFFER)) != -1) {
                baOut.write(data, 0, count);
            }

            return baOut.toByteArray();
        } finally {
            closeQuietly(gzIn);
            closeQuietly(baIn);
            closeQuietly(baOut);
        }
    }

    public static Serializable gunzipObject(byte[] bytes) throws IOException,
            ClassNotFoundException {

        ByteArrayInputStream baIn = null;
        GZIPInputStream gzIn = null;
        ObjectInputStream objIn = null;
        try {
            baIn = new ByteArrayInputStream(bytes);
            gzIn = new GZIPInputStream(baIn);
            objIn = new ObjectInputStream(gzIn);
            return (Serializable) objIn.readObject();
        } finally {
            closeQuietly(objIn);
            closeQuietly(gzIn);
            closeQuietly(baIn);
        }

    }

}
