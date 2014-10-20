package com.github.walterfan.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author walter
 *
 */
public class SystemUtils {
	private static Log logger = LogFactory.getLog(SystemUtils.class);
	
	public static boolean setFileRight(String filename, String right) throws IOException {
        boolean bRet = false;
        if(StringUtils.containsIgnoreCase(System.getProperty("os.name"),
                                           "windows")) {
            return true;
        }
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("chmod " + right + " " + filename);
            bRet = true;                 
        } finally {
            if(proc != null) {
                proc.destroy();
            }
        }
        return bRet;
    }
	
	public static long getFreeSpace(String path) {
		String os = System.getProperty("os.name");
		if (StringUtils.contains(os, "Windows")) {
			return getFreeSpaceOnWindows(path);
		} else {
			return getFreeSpaceOnLinux(path);
		}
	}
	
	public static long getFreeSpaceOnWindows(String dirName) {				
		Process process = null;		
		try {		
			String command = "cmd.exe /c dir " + dirName;

			process = Runtime.getRuntime().exec(command);
			if (process == null) {
				logger.error("Not found " + command);
				return -1;
			}
			// read the output of the dir command
			// only the last line is of interest
			BufferedReader in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			String freeSpace = null;
			while ((line = in.readLine()) != null) {
				freeSpace = line;
			}
			if (freeSpace == null) {
				return -1;
			}
			// remove dots & commas & leading and trailing whitespace
			freeSpace = freeSpace.trim();
			freeSpace = freeSpace.replaceAll("\\.", "");
			freeSpace = freeSpace.replaceAll(",", "");
			String[] items = freeSpace.split(" ");
			// the first valid numeric value in items after(!) index 0
			// is probably the free disk space
			int index = 1;
			while (index < items.length) {
				try {
					long bytes = Long.parseLong(items[index++]);
					return bytes;
				} catch (NumberFormatException nfe) {
				}
			}
			return -1;
		} catch (Exception e) {
			logger.error(e);
			return -1;
		} finally {
			if(process != null) {
				process.destroy();
			}
		}
	}
	
	public static long getFreeSpaceOnLinux(String path) {
		long bytesFree = -1;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("df " + path);
			InputStream reader = new BufferedInputStream(
					process.getInputStream());
			
			StringBuilder sb = new StringBuilder();
			for (;;) {
				int charCount = reader.read();
				if (charCount == -1)
					break;
				sb.append((char) charCount);
			}
			String outputText = sb.toString();
			//reader.close();

			// parse the output text for the bytes free info
			StringTokenizer tokenizer = new StringTokenizer(outputText, "\n");
			tokenizer.nextToken();
			if (tokenizer.hasMoreTokens()) {
				String line = tokenizer.nextToken();
				StringTokenizer tokenizerSecond = new StringTokenizer(line, " ");
				if (tokenizerSecond.countTokens() >= 4) {
					tokenizerSecond.nextToken();
					tokenizerSecond.nextToken();
					tokenizerSecond.nextToken();
					bytesFree = Long.parseLong(tokenizerSecond.nextToken());					
				}				
				return bytesFree*1024;				
			}

		} catch (IOException e) {
			logger.error(e);
			
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return -1;
	} 
	
	public static void main(String[] args) {
		System.out.println(FileUtils.byteCountToDisplaySize(getFreeSpace(".")));
	}
}
