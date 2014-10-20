package com.github.walterfan.util.http;


import com.sun.net.ssl.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DummySSLSocketFactory
 *
 * @author Eugen Kuleshov
 */
public class DummySSLSocketFactory extends SSLSocketFactory {
  private SSLSocketFactory factory;
  private static Log logger = LogFactory.getLog(DummySSLSocketFactory.class);
  
  public DummySSLSocketFactory() {
    logger.info( "DummySocketFactory instantiated");
    try {
      @SuppressWarnings("deprecation")
	SSLContext sslcontext = SSLContext.getInstance( "TLS");
      sslcontext.init( null,
                       // new KeyManager[] { new DummyKeyManager()},
                       new TrustManager[] { new DummyTrustManager()},
                       new java.security.SecureRandom());
      factory = ( SSLSocketFactory) sslcontext.getSocketFactory();

    } catch( Exception ex) {
      ex.printStackTrace();
    }
  }

  public static SocketFactory getDefault() {
    logger.info( "DummySocketFactory.getDefault()");
    return new DummySSLSocketFactory();
  }

  public Socket createSocket( Socket socket, String s, int i, boolean flag)
      throws IOException {	
    logger.info( "DummySocketFactory.createSocket()");
    new RuntimeException("createSocket test").printStackTrace();
    return factory.createSocket( socket, s, i, flag);
  }

  public Socket createSocket( InetAddress inaddr, int i,
                              InetAddress inaddr1, int j) throws IOException {
    logger.info( "DummySocketFactory.createSocket()");
    return factory.createSocket( inaddr, i, inaddr1, j);
  }

  public Socket createSocket( InetAddress inaddr, int i)
      throws IOException {
    logger.info( "DummySocketFactory.createSocket()");
    return factory.createSocket( inaddr, i);
  }

  public Socket createSocket( String s, int i, InetAddress inaddr, int j)
      throws IOException {
    logger.info( "DummySocketFactory.createSocket()");
    return factory.createSocket( s, i, inaddr, j);
  }

  public Socket createSocket( String s, int i) throws IOException {
    logger.info( "DummySocketFactory.createSocket()");
    return factory.createSocket( s, i);
  }

  public String[] getDefaultCipherSuites() {
    logger.info( "DummySocketFactory.getDefaultCipherSuites()");
    return factory.getSupportedCipherSuites();
  }

  public String[] getSupportedCipherSuites() {
    logger.info( "DummySocketFactory.getSupportedCipherSuites()");
    return factory.getSupportedCipherSuites();
  }
}

