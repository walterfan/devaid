package com.github.walterfan.util.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.security.Security;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketClient {

	public static void main(String args[]) throws Exception {

	    String urlString = (args.length == 1) ? 
	      args[0] : "https://www.google.com";
	    URL url = new URL(urlString);

	    Security.addProvider(
	      new com.sun.net.ssl.internal.ssl.Provider());

	    SSLSocketFactory factory = 
	      (SSLSocketFactory)SSLSocketFactory.getDefault();
	    SSLSocket socket = 
	      (SSLSocket)factory.createSocket(url.getHost(), 443);

	    PrintWriter out = new PrintWriter(
	        new OutputStreamWriter(
	          socket.getOutputStream()));
	    out.println("GET " + urlString + " HTTP/1.1");
	    out.println();
	    out.flush();

	    BufferedReader in = new BufferedReader(
	      new InputStreamReader(
	      socket.getInputStream()));

	    String line;

	    while ((line = in.readLine()) != null) {
	      System.out.println(line);
	    }

	    out.close();
	    in.close();
	  }

}
