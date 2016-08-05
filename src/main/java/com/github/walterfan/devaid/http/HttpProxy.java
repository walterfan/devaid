package com.github.walterfan.devaid.http;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.lang.reflect.Array;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

import static java.lang.System.* ;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.github.walterfan.util.RegexUtils;
import com.github.walterfan.util.http.CustomizedHttpRequest;
import com.github.walterfan.util.http.CustomizedHttpResponse;
import com.github.walterfan.util.http.HttpUtil;
import com.github.walterfan.util.io.ConsoleUtils;

//import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;


/**
 * 
 * Walter Fan modified on 2009-11-30 --> mailto: walter.fan@gmail.com
 * version 1.3
 */
public class HttpProxy implements Runnable {

	public static final int DEFAULT_PORT = 8000;

	public static boolean isSSL = false;
	
	private int debugLevel = 0;
	
	private int timeoutSec = 20;
	
	private boolean filterFlag = true;
	
	private boolean isStarted = false;
	
	private ServerSocket localSocket = null;

	private int listenPort = DEFAULT_PORT;

	private String targetServer = "10.224.55.21";

	private int targetPort = 80;

    private volatile boolean appRunning = false;
	    
    protected Thread mainThread = new Thread(this);
	    
	protected ExecutorService execService = Executors.newCachedThreadPool();

	private Map<CustomizedHttpRequest, CustomizedHttpResponse> requestMap 
		= new TreeMap<CustomizedHttpRequest, CustomizedHttpResponse>();

	public boolean useFilter() {
		return this.filterFlag;
	}
	
/*	public boolean useRegex() {
		return this.regexFlag;
	}
*/	
	public void setFilterFlag(boolean filterFlag) {
		this.filterFlag = filterFlag;
	}
	
/*	public void setRegexFlag(boolean regexFlag) {
		this.regexFlag = regexFlag;
	}
*/	
	
	public void loadConfig(String filename) throws IOException,
			DocumentException {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);

		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element root = document.getRootElement();
			Iterator<Element> it = root.elementIterator();

			while (it.hasNext()) {
				Element nodeL1 = it.next();
				String tagNameL1 = nodeL1.getName();
				if ("listenPort".equals(tagNameL1)) {
					this.listenPort = NumberUtils.toInt(nodeL1.getText());
					continue;
				} else if ("targetServer".equals(tagNameL1)) {
					this.targetServer = nodeL1.getText();
					continue;
				} else if ("targetPort".equals(tagNameL1)) {
					this.targetPort = NumberUtils.toInt(nodeL1.getText());
					continue;
				} else if ("useFilter".equals(tagNameL1)) {
					this.filterFlag = "true".equalsIgnoreCase(nodeL1.getText());
					continue;
				} /*else if ("useRegex".equals(tagNameL1)) {
					this.regexFlag = "true".equalsIgnoreCase(nodeL1.getText());
					continue;
				}*/	else if ("timeoutSec".equals(tagNameL1)) {
					this.timeoutSec = NumberUtils.toInt(nodeL1.getText());
					continue;
				} else if ("debugLevel".equals(tagNameL1)) {
					this.debugLevel = NumberUtils.toInt(nodeL1.getText());
					continue;
				} else if ("proxy-item".equals(tagNameL1)) {
					loadProxySetting(nodeL1);
				}

			}
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	public Map<CustomizedHttpRequest, CustomizedHttpResponse> getRequestMap() {
		return requestMap;
	}
	
	public String toString() {
	    StringBuilder sb =  new StringBuilder("");
	    
		sb.append("HttpProxy on " + listenPort + " to " + 
		          targetServer + ":" + targetPort + ", filterFlag=" + filterFlag + "\n");
		for(Map.Entry<CustomizedHttpRequest, CustomizedHttpResponse> entry
				: requestMap.entrySet()) {
		    sb.append("* " + entry.getKey() + " => " + entry.getValue() + "\n" );
		}		
		return sb.toString();

	}

	public int getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}

	private void loadProxySetting(Element nodeL1) {
		CustomizedHttpRequest hq = new CustomizedHttpRequest();
		CustomizedHttpResponse hr = new CustomizedHttpResponse();
		
		String content = null;
		
		Iterator it0 = nodeL1.elementIterator();
		while (it0.hasNext()) {
			Element nodeL2 = (Element) it0.next();

			if ("request".equals(nodeL2.getName())) {
				Iterator it1 = nodeL2.elementIterator();

				while (it1.hasNext()) {
					Element nodeL3 = (Element) it1.next();

					if ("keyword".equals(nodeL3.getName())) {
						hq.setKeyword(StringUtils.trim(nodeL3.getText()));
						if("true".equals(nodeL3.attributeValue("useRegex"))) {
							hq.setUseRegex(true);
						}
					} else if ("variable".equals(nodeL3.getName())) {
						hq.addVariable(StringUtils.trim(nodeL3.getText()));
					}
				}

			} else if ("response".equals(nodeL2.getName())) {
				Iterator it1 = nodeL2.elementIterator();

				while (it1.hasNext()) {
					Element nodeL3 = (Element) it1.next();
					if ("content".equals(nodeL3.getName())) {
						String xmlText = nodeL3.getText();
						if(StringUtils.isNotBlank(xmlText)) {
						    String[] lines = xmlText.split("\n");
						    StringBuilder sb = new StringBuilder("");
						    for(String line:lines) {
						        sb.append(StringUtils.trim(line));
						    }
						    content = sb.toString();
						}
					} else if ("header".equals(nodeL3.getName())) {
						Iterator it4 = nodeL3.elementIterator();
						while (it4.hasNext()) {
							Element nodeL4 = (Element) it4.next();
							if ("contentType".equals(nodeL4.getName())) {
								hr.setContentType(nodeL4.getText());
							} else if("statusCode".equals(nodeL4.getName())){
								int statusCode = NumberUtils.toInt(nodeL4.getText());
								if(statusCode>0) {
									hr.setStatusCode(statusCode);
								}
							} else if("field".equals(nodeL4.getName())){
								hr.addField(nodeL4.getText());
							}
						}
					}
				}
			}
		}
		if(StringUtils.isBlank(hq.getKeyword())
				|| StringUtils.isBlank(content)) {
			System.err.println("blank");
			return;
		} 
		
		hr.setBody(StringUtils.trim(content));
		requestMap.put(hq, hr);
	}

	public void startup() {
		if(isStarted) {
			return;
		}
		this.appRunning = true;
		this.mainThread.start();
		isStarted = true;
	}
	
	public void shutdown() {
		if(!isStarted) {
			return;
		}
		this.appRunning = false;
		this.mainThread.interrupt();
		
		execService.shutdownNow();
		try {
			if(null!=this.localSocket) {
				this.localSocket.close();
			}
			this.mainThread.join();
			isStarted = false;
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("shutdowned.");
	}
	
	public void waiting() throws InterruptedException {
		this.mainThread.join();
		ConsoleUtils.wait4Input("stop?");
		this.appRunning = false;
	}

	public static void main(String args[]) {
		int theListenPort = 8000;
		String theTargetServer = "10.224.38.71";
		int theTargetPort = 80;
		System.out.println("HttpProxy version: 1.2, author: Walter Fan (hava not support https)");

		HttpProxy aProxy = null;
		if (args.length == 1) {
            aProxy = new HttpProxy(args[0]);
        } else if (args.length == 3) {
			// get the command-line parameters
			theListenPort = Integer.parseInt(args[0]);			
			theTargetServer = args[1];
			theTargetPort = Integer.parseInt(args[2]);
			aProxy = new HttpProxy();
			aProxy.setListenPort(theListenPort);
			aProxy.setTargetServer(theTargetServer);			
			aProxy.setTargetPort(theTargetPort);
		} else {		
			aProxy = new HttpProxy("proxy-setting.xml");
		}	
		//HttpProxy.isSSL = true;
		out.println("* Starting HttpProxy. Press CTRL-C to end ...");
		//out.println(aProxy.toString());
		//aProxy.setDebugLevel(1);
		try {			
			aProxy.startup();
			aProxy.waiting();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println("byebye!");
	}

	public HttpProxy() {
          
    }
	
	public HttpProxy(String filename) {
		try {
			loadConfig(filename);
		} catch (IOException e) {
			System.err.println("loadConfig error: " + e.getMessage());
		} catch (DocumentException e) {
			System.err.println("loadConfig error: " + e.getMessage());
		}
	}

	

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public void setTargetServer(String targetServer) {
		this.targetServer = targetServer;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	/*
	 * get the port that we're supposed to be listening on
	 */
	public int getListenPort() {
		return listenPort;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public String getTargetServer() {
		return targetServer;
	}

	/*
	 * return whether or not the socket is currently open
	 */
	public boolean isRunning() {
		if (localSocket == null)
			return false;
		else
			return true;
	}

	

	public void run() {
		
		//super.addDaemonShutdownHook();
		
		try {
			// create a server socket, and loop forever listening for
			// client connections
			localSocket = new ServerSocket(listenPort);
			out.println("* Started  " + toString());

			while (appRunning) {
				Socket client = localSocket.accept();
				//System.out.println("accept: " + client.getRemoteSocketAddress());
				ProxyWorker worker = new ProxyWorker(client, this);

				worker.setTimeoutSec(timeoutSec);
				//worker.start();				
				execService.execute(worker);
			}
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("run error: " + e.getMessage());
		} finally {
			try {
				localSocket.close();
				} catch (Exception e) {
				System.err.println("close error" + e.getMessage());
			}
		}
	}

	public String info() {
		return "listen on " + listenPort + " to " + targetServer + targetPort;
	}

	public String usage() {
		return "HttpProxy listenPort targetServer targetPort";
	}
	
	public static Socket createSocket(String ip, int port) throws IOException, UnknownHostException {
		Socket serverSocket = null;
		if(isSSL) {
			serverSocket = SSLSocketFactory.getDefault().
		     createSocket(ip, port);
		} else {
			serverSocket = new Socket(ip, port);
		}
		return serverSocket;
	}
	
	public static ServerSocket createServerSocket(int port) throws IOException, UnknownHostException {
		ServerSocket serverSocket= null;
		if(isSSL) {
			ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
			serverSocket = ssocketFactory.createServerSocket(port);
		} else {
			serverSocket = new ServerSocket(port);
		}
		return serverSocket;
	}
}

/*
 * The ProxyThread will take an HTTP request from the client socket and send it
 * to either the server that the client is trying to contact, or another proxy
 * server
 */
class ProxyWorker implements Runnable {

	private Socket clientSocket;

	private HttpProxy proxy;

	public static final int DEFAULT_TIMEOUT_MS = 15000;

	private int socketTimeout = DEFAULT_TIMEOUT_MS;

	public ProxyWorker(Socket s, HttpProxy proxy) {
		clientSocket = s;
		this.proxy = proxy;
	}

	public void setTimeoutSec(int timeout) {
		// assume that the user will pass the timeout value
		// in seconds (because that's just more intuitive)
		socketTimeout = timeout * 1000;
	}

	public void run() {

		BufferedInputStream clientIn = null;
		BufferedOutputStream clientOut = null;

		BufferedInputStream serverIn = null;
		BufferedOutputStream serverOut = null;
		
		try {
			clientIn = new BufferedInputStream(clientSocket.getInputStream());
			clientOut = new BufferedOutputStream(clientSocket.getOutputStream());

			// the socket to the remote server
			Socket serverSocket = null;

			// other variables
			byte[] requestBytes = null;
			int requestLength = 0;

			StringBuilder host = new StringBuilder("");
			
			requestBytes = transferHttpDataFromClient(clientIn, host);
			requestLength = Array.getLength(requestBytes);
			
			String strRequest = new String(requestBytes);
			if(proxy.getDebugLevel() > 0) {
				System.out.println("<request>");
				System.out.println(strRequest.trim());
				System.out.println("</request>");
			}							

			CustomizedHttpResponse fakeResp = null;
			if(proxy.useFilter()) {
				fakeResp = getFakeResponse(strRequest);				
			}

			//boolean waitFlag = true;
			if (null != fakeResp) {
				byte[] contentBytes = fakeResp.getBody().getBytes();
				byte[] headerBytes = HttpUtil.makeHttpHeader(fakeResp.getStatusCode(), 
						fakeResp.getContentType(),
						contentBytes.length + 32, fakeResp.getFields());
				int headerLen = headerBytes.length;
				int contentLen = contentBytes.length;
				//clientOut.write(headerBytes, 0, headerBytes.length);
				//clientOut.write(fakeResp.getHeader().getBytes(), 0, fakeResp.getHeader().getBytes().length);
				byte[] allBytes = new byte[headerLen + contentLen]; 
				System.arraycopy(headerBytes, 0, allBytes, 0, headerLen);
				System.arraycopy(contentBytes, 0, allBytes, headerLen, contentLen);
				transferHttpDataFromServer(new ByteArrayInputStream(allBytes),clientOut);
			} else {
				try {
					serverSocket = HttpProxy.createSocket(proxy.getTargetServer(), proxy.getTargetPort());
				} catch (IOException ioe) {
					String errMsg = "HTTP/1.0 500\nContent Type: text/plain\n\n"
							+ "Error connecting to the server:\n" + ioe + "\n";
					clientOut.write(errMsg.getBytes(), 0, errMsg.length());
					return;
				}
				
				serverSocket.setSoTimeout(socketTimeout);
				serverIn = new BufferedInputStream(serverSocket.getInputStream());
				serverOut = new BufferedOutputStream(serverSocket.getOutputStream());

				// send the request out
				serverOut.write(requestBytes, 0, requestLength);
				serverOut.flush();
				
				transferHttpDataFromServer(serverIn, clientOut);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(clientIn);
			IOUtils.closeQuietly(clientOut);

			IOUtils.closeQuietly(serverIn);
			IOUtils.closeQuietly(serverOut);

			try {
				clientSocket.close();
			} catch (IOException e) {
				// ignore
			}
		}

	}



	private byte[] transferHttpDataFromClient(InputStream in, StringBuilder sb) {
		// get the HTTP data from an InputStream, and return it as
		// a byte array, and also return the Host entry in the header,
		// if it's specified -- note that we have to use a StringBuffer
		// for the 'host' variable, because a String won't return any
		// information when it's used as a parameter like that
		
		// There, use ByteArrayOutputStream as output stream
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		transferHttpData(in, bs, sb, true);
		return bs.toByteArray();
	}

	private int transferHttpDataFromServer(InputStream in, OutputStream out) {
		StringBuilder sb = new StringBuilder("");
		return transferHttpData(in, out, sb, false);
	}

	private int transferHttpData(InputStream is, OutputStream os,
			StringBuilder host,boolean isFromClient) {
		// get the HTTP data from an InputStream, and send it to
		// the designated OutputStream
		
		StringBuilder header = new StringBuilder("");
		String data = "";
		int responseCode = 200;
		int contentLength = 0;
		int pos = -1;
		int byteCount = 0;

		try {
			// get the first line of the header, so we know the response code
			data = readLine(is);
			if (data != null) {
				header.append(data + "\r\n");
				pos = data.indexOf(" ");
				if ((data.toLowerCase().startsWith("http")) && (pos >= 0)
						&& (data.indexOf(" ", pos + 1) >= 0)) {
					String rcString = data.substring(pos + 1, data.indexOf(" ",
							pos + 1));
					try {
						responseCode = Integer.parseInt(rcString);
					} catch (Exception e) {
						System.err.println("number format error: " + e.getMessage());
					}
				}
			}
			
			// get the rest of the header info
			while ((data = readLine(is)) != null) {
				
				// the header ends at the first blank line
				if (data.length() == 0)
					break;
				header.append(data + "\r\n");

				// check for the Host header
				pos = data.toLowerCase().indexOf("host:");
				if (pos >= 0) {
					host.setLength(0);
					host.append(data.substring(pos + 5).trim());
				}

				// check for the Content-Length header
				pos = data.toLowerCase().indexOf("content-length:");
				if (pos >= 0) {
					contentLength = Integer.parseInt(data.substring(pos + 15)
							.trim());
				}
			}
				
			// add a blank line to terminate the header info
			header.append("\r\n");
			// convert the header to a byte array, and write it to our stream
			os.write(header.toString().getBytes(), 0, header.length());
			
			
			if(proxy.getDebugLevel() > 0) {
				if(!isFromClient) {					
			    	System.out.println("<response><header>");
			    	System.out.println(header.toString().trim());
			    	System.out.println("</header>");
			    }
			    
			}
			
			// if the header indicated that this was not a 200 response,
			// just return what we've got if there is no Content-Length,
			// because we may not be getting anything else
			if ((responseCode != 200) && (contentLength == 0)) {
				os.flush();
				return header.length();
			}

			// get the body, if any; we try to use the Content-Length header to
			// determine how much data we're supposed to be getting, because
			// sometimes the client/server won't disconnect after sending us
			// information...
			boolean waitForDisconnect = false;
			if (contentLength == 0 && !isFromClient) {
				System.out.println("wait server to close connection, contentLength=" + contentLength);
				waitForDisconnect = true;
			} 
			
			if (contentLength > 0 || waitForDisconnect) {
				try {
					byte[] buf = new byte[4096];
					int bytesIn = 0;
					StringBuilder sb1 = new StringBuilder();
					while (byteCount < contentLength || waitForDisconnect) {
						
						bytesIn = is.read(buf);						
						if(bytesIn < 0) {
							break;
						}
							
						os.write(buf, 0, bytesIn);
						if(proxy.getDebugLevel() > 0 && !isFromClient) {
							sb1.append(new String(buf));
			            }
						
						byteCount += bytesIn;
					}//end of while
					if(proxy.getDebugLevel() > 0 && !isFromClient) {
						System.out.println("<body>");
						System.out.println(sb1.toString().trim());
						System.out.println("</body></response>");
		            }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// flush the OutputStream and return
		try {
			os.flush();
		} catch (Exception e) {
		}
		return (header.length() + byteCount);
	}

	private String readLine(InputStream in) {
		// reads a line of text from an InputStream
		StringBuffer data = new StringBuffer("");
		int c;

		try {
			// if we have nothing to read, just return null
			if (in.markSupported()) {
				in.mark(1);
			}
			if (in.read() == -1) {
				return null;
			} else {
				if (in.markSupported()) {
					in.reset();
				}
			}
			while ((c = in.read()) >= 0) {
				// check for an end-of-line character
				if ((c == 0) || (c == 10) || (c == 13))
					break;
				else
					data.append((char) c);
			}

			// deal with the case where the end-of-line terminator is \r\n
			if (c == 13 && in.markSupported()) {
				in.mark(1);
				if (in.read() != 10)
					in.reset();
			}
		} catch (Exception e) {
			System.err.println("read error: " + e.getMessage());
		}

		// and return what we have
		return data.toString();
	}

	public CustomizedHttpResponse getFakeResponse(String text) {	
		
		for(Map.Entry<CustomizedHttpRequest,CustomizedHttpResponse> entry :proxy.getRequestMap().entrySet())	{
			boolean ret = false;
			CustomizedHttpRequest hq = entry.getKey();			
			
			System.out.println("<!--Check keyword " + entry.getKey().getKeyword() + "-->");
			try {
				if(hq.isUseRegex()) {
					ret = RegexUtils.isMatched(text, hq.getKeyword());
				} else {
					ret = StringUtils.contains(text, hq.getKeyword());
				}
			} catch(StackOverflowError e) {
				System.err.println("Please increase stack size: " + e);
			} catch(Throwable e) {
				System.err.println("regex error: " + e);
			}
			if (ret) {
				if(proxy.getDebugLevel() > 0) {
					System.out.println("<!-- Eureka! the request contains \"" + entry.getKey() + "\". -->");
				}
				if(hq.hasVariable()) {
					Iterator<String> it = hq.iterator();
					while(it.hasNext()) {
						String var = it.next();
						String val = HttpUtil.getValueByName(text, var);
						if(StringUtils.isEmpty(val)) {
							continue;
						}
						System.out.println("<!-- replace %" + var + "% with "+ val +" -->");

						try {
							CustomizedHttpResponse hr = (CustomizedHttpResponse)entry.getValue().clone();
							String body = StringUtils.replace(hr.getBody(), "%"+var+"%", val);
							hr.setBody(body);
							return hr;
						} catch (CloneNotSupportedException e) {
							System.err.println("regex error: " + e);
						}
						
					}
				}
				return  entry.getValue();
			}
		}
		return null;
	}
	
	
	

}
