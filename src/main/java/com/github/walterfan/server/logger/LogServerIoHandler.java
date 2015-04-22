package com.github.walterfan.server.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.github.walterfan.server.logger.message.LogMessage;
import com.github.walterfan.server.logger.message.ResultMessage;

public class LogServerIoHandler extends IoHandlerAdapter {
	private static Log logger = LogFactory.getLog(LogServerIoHandler.class);
	public static final String INDEX_KEY = LogServerIoHandler.class.getName()
			+ ".INDEX";

	public void sessionOpened(IoSession session) throws Exception {
		//set idle time to 60 seconds
        //session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
		session.setAttribute(INDEX_KEY, new Integer(0));

	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//SessionLog.warn(session, cause.getMessage(), cause);
		session.close(false);
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		LogMessage msg = (LogMessage)message;
		logger.info("get message: " + msg);
		ResultMessage rm = new ResultMessage();
		if(msg == null) {
			rm.setOkay(false);
		} else {
			rm.setOkay(true);
			rm.setSequence(msg.getSequence());
		}
		logger.info("send response: " + rm);
		session.write(rm);
	}
	
	public void sessionIdle(IoSession session, IdleStatus status) {
        //SessionLog.info(session, "Disconnecting the idle.");
        // disconnect an idle client
        session.close(false);
    }


}
