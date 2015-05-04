package com.github.walterfan.service.signal;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.github.walterfan.service.AbstractService;
import com.github.walterfan.service.IService;

public class SignalService extends AbstractService implements IService {

	private int nPort = 9999;
	private IoHandlerAdapter msgHandler;
	private IoAcceptor acceptor = new NioSocketAcceptor();
	
	@Override
	protected void onStart() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onInit() throws Exception {
   
        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec", 
        		new ProtocolCodecFilter( 
        				new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        acceptor.setHandler( msgHandler );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(nPort) );
   
		
	}

	@Override
	protected void onClean() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
