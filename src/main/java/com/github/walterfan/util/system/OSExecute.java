package com.github.walterfan.util.system;
//: net/mindview/util/OSExecute.java
//Run an operating system command
//and send the output to the console.
//From Think in java 4th
import java.io.*;

public class OSExecute {

    public static void command(String command) {
        boolean err = false;
        Process process = null;
        try {
            process = new ProcessBuilder(command.split(" ")).start();
            BufferedReader results = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = results.readLine()) != null)
                System.out.println(s);
            BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            // Report errors and return nonzero value
            // to calling process if there are problems:
            while ((s = errors.readLine()) != null) {
                System.err.println(s);
                err = true;
            }
        } catch (Exception e) {
            // Compensate for Windows 2000, which throws an
            // exception for the default command line:
            if (!command.startsWith("CMD /C"))
                command("CMD /C " + command);
            else
                throw new RuntimeException(e);
        } finally {
        	if(process!=null) {
        		process.destroy();
        	}
        }
        if (err)
            throw new RuntimeException("Errors executing " + command);
    }
    
    public static void main(String[] args) throws InterruptedException {
    	String cmd = "date";
    	int nTimes = 1;
    	int interval = 3000;
    	if(args.length > 0) {
    		cmd = args[0];
    	}
    	if(args.length > 1) {
    		nTimes = Integer.parseInt(args[1]);
    	}
    	if(args.length > 2) {
    		interval = Integer.parseInt(args[2]);
    	}
    	for (int i = 0; i < nTimes; i++) {
    		command(cmd); 
    		if(i >= nTimes -1) {
    			break;
    		}
    		Thread.sleep(interval);
		}    	
    }
    
} ///:~

