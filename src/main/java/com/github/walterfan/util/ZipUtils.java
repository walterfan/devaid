package com.github.walterfan.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;


public class ZipUtils {
    private static final int INT_BYTE_LEN = 4;
    /** The Constant BUFFER. */
    public static final int BUF_LEN = 1024;

    public static byte[] zip(byte[] input) {
        Deflater compressor = new Deflater();
         compressor.setLevel(Deflater.BEST_COMPRESSION);

         // Give the compressor the data to compress
         compressor.setInput(input);
         compressor.finish();

         // Create an expandable byte array to hold the compressed data.
         // You cannot use an array that's the same size as the orginal because
         // there is no guarantee that the compressed data will be smaller than
         // the uncompressed data.
         ByteArrayOutputStream bos = null;
         try {
             bos = new ByteArrayOutputStream(input.length);
    
             // Compress the data
             byte[] buf = new byte[BUF_LEN];
             while (!compressor.finished()) {
                 int count = compressor.deflate(buf);
                 bos.write(buf, 0, count);
             }
             
             
            
    
             // Get the compressed data
             byte[] compressedData = bos.toByteArray();
            return compressedData;
         } finally {
            IOUtils.closeQuietly(bos);
         }
    }
    
    public static byte[] unzip(byte[] compressedData) throws DataFormatException  {
     // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream(compressedData.length);
    
            // Decompress the data
            byte[] buf = new byte[BUF_LEN];
            while (!decompressor.finished()) {
                
                    int count = decompressor.inflate(buf);
                    bos.write(buf, 0, count);
                
            }
            
            // Get the decompressed data
            return bos.toByteArray();
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }
    
    /**
     * Unzip.
     *
     * @param bytes byte array
     * @return byte array
     * @throws IOException exception
     */
    public static byte[] unzipWithLen(byte[] bytes) throws IOException {
        ByteArrayInputStream baIs = null;
        InflaterInputStream zIs = null;
        try {
            baIs = new ByteArrayInputStream(bytes);
            byte[] arrLen = new byte[4];
            int cnt = baIs.read(arrLen, 0, INT_BYTE_LEN);
            if (cnt != INT_BYTE_LEN) {
                return null;
            }
            int nTotalSize = EncodeUtils.byteArray2Int(arrLen);
            if (nTotalSize == 0) {
                return null;
            }
            
            zIs = new InflaterInputStream(baIs);
            byte[] data = new byte[nTotalSize];
            int nReadSize = 0;
            while (nReadSize < nTotalSize) {
                int nSize = zIs.read(data, nReadSize, nTotalSize - nReadSize);
                if (nSize < 0) {
                    break;
                }
                nReadSize += nSize;
            }
            if (nReadSize != nTotalSize) {
                return null;
            }
            
            return data;
        } finally {
            IOUtils.closeQuietly(zIs);
            IOUtils.closeQuietly(baIs);
        }
    }

    public static byte[] zipWithLen(byte[] bytes) throws IOException {
        ByteArrayOutputStream baOut = null;
        DeflaterOutputStream zipOut = null;

        try {
            baOut = new ByteArrayOutputStream();
            baOut.write(EncodeUtils.int2ByteArray(bytes.length), 0, 4);
            zipOut = new DeflaterOutputStream(baOut);                        
            zipOut.write(bytes);
            zipOut.finish();
            zipOut.flush();
            return baOut.toByteArray();
        } finally {
            IOUtils.closeQuietly(zipOut);
            IOUtils.closeQuietly(baOut);
        }
    }
    
    
}
