package com.github.walterfan.util.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtils {
        private static InputStreamReader inputStream = new InputStreamReader(System.in);
        private static BufferedReader reader = new BufferedReader(inputStream);

        public int getNumberFromConsole() {
            int number = 0;
            try {
                number = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Enter a valid integer!!");
            }
            return number;
        }

        public String getStringFromConsole() {
            String strInput="";
            try {
                strInput = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strInput;
        }

        public static void sleepQueitly(long ms) {
    		try {
    			Thread.sleep(ms);
    		} catch (Exception ex) { 
    			// Ignore the exception
    		}
    	}

    	public static void closeQuietly(Closeable ca) {
    		try {
    			if (ca != null)
    				ca.close();
    		} catch (Exception ex) { 
    			// ignore the exception
    		}
    	}

    	public static String wait4Input(String prompt) {
    		System.out.print(prompt);
    		System.out.flush();
    		return wait4Input();
    	}

    	public static String wait4Input() {
    		InputStreamReader is = null;
    		String ret = null;
    		try {
    			is = new InputStreamReader(System.in);
    			BufferedReader in = new BufferedReader(is);
    			ret = in.readLine();
    		} catch (IOException e) {
    			System.err.println(e.getMessage());
    		} finally {
    			//need not close Sytem.in
    			//closeQuietly(is);
    		}
    		return ret;
    	}
}
