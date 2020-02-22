package com.github.walterfan.sip;

//refer to http://www.oracle.com/technetwork/java/introduction-jain-sip-090386.html
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.util.RandomUtils;

public class SipUserAgent implements SipListener {

	private static final Log logger = LogFactory.getLog(SipUserAgent.class);
	
	public static final int WAIT_SECONDS = 5;

	private Map<String, Integer> _expectResultMap = new LinkedHashMap<String, Integer>(10);      
	
	private Map<String, Integer> _actualResultMap = new LinkedHashMap<String, Integer>(10);	
	
	private MessageProcessor messageProcessor;

	private SipStack sipStack;

	private SipFactory sipFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipProvider sipProvider;

	/** Here we initialize the SIP stack. */
	public SipUserAgent(String ip, int port)
			throws PeerUnavailableException, TransportNotSupportedException,
			InvalidArgumentException, ObjectInUseException,
			TooManyListenersException {

		//setUsername(username);
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "UserAgent");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);

		// DEBUGGING: Information will go to files
		// textclient.log and textclientdebug.log
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "16");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",	this.getClass().getSimpleName() + ".txt");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",	this.getClass().getSimpleName() + ".log");

		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		//addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();
		
		//sipMsgBuilder = new SipMessageBuilder(headerFactory, addressFactory, messageFactory);

		ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
		ListeningPoint udp = sipStack.createListeningPoint(port, "udp");

		sipProvider = sipStack.createSipProvider(tcp);
		sipProvider.addSipListener(this);
		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
	}
	
	

	public String sendRequest(SipRequestInfo sipReq) throws ParseException,
		InvalidArgumentException, SipException {
		ToHeader toHeader = sipReq.getToHeader();		
		FromHeader fromHeader = sipReq.getFromHeader();
		SipURI requestURI = sipReq.getRequestUri();
		List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(),
				getPort(), "udp", RandomUtils.getRandomLetters(10));
		viaHeaders.add(viaHeader);
		
		
		CallIdHeader callIdHeader = null;
		
		if(sipReq.getCallId().isEmpty())
			callIdHeader = sipProvider.getNewCallId();
		else 
			callIdHeader = sipReq.getCallIdHeader();
		
		CSeqHeader cSeqHeader = null;
		if(sipReq.getCseqNum() == 0)
			cSeqHeader = headerFactory.createCSeqHeader(1, sipReq.getSipMethod());
		else 
			cSeqHeader = sipReq.getCSeqHeader(); 
		
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

		Request request = messageFactory.createRequest(requestURI,
				sipReq.getSipMethod(), callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);

		ContactHeader contactHeader = sipReq.getContactHeader();
		request.addHeader(contactHeader);
		
		if(sipReq.getExpireTime() >= 0) {
			request.addHeader(sipReq.getExpiresHeader());
		}
		
		AuthorizationHeader authHeader = sipReq.getAuthorizationHeader();
		request.addHeader(authHeader);
		
		request.addHeader(sipReq.getUserAgentHeader());
		
        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
        byte[] contents = {};
        request.setContent(contents, contentTypeHeader);

		sipProvider.sendRequest(request);
		return callIdHeader + "_" + cSeqHeader;
		
	}
	

	/** This method is called by the SIP stack when a response arrives. */
	public void processResponse(ResponseEvent evt) {
		Response response = evt.getResponse();
        //ClientTransaction tid = evt.getClientTransaction();
        
        CallIdHeader callid = (CallIdHeader)response.getHeader(CallIdHeader.NAME);
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        FromHeader fromHeader =  (FromHeader) response.getHeader(FromHeader.NAME);
        
        logger.info("Response received : Status Code = "
                + response.getStatusCode() + ", " + callid + "_" + cseq + " from " + fromHeader);
        
        /*if (tid == null) {
            System.out.println("Stray response -- dropping ");
            return;
        }
        System.out.println("transaction state is " + tid.getState());
        System.out.println("Dialog = " + tid.getDialog());
        System.out.println("Dialog State is " + tid.getDialog().getState());
		*/
		int status = response.getStatusCode();

		_actualResultMap.put(callid + "_" + cseq, status);
		
		if ((status >= 200) && (status < 300)) { // Success!
			messageProcessor.processInfo("-- received sip response: " + status);
			return;
		}

		messageProcessor.processError("Previous message not sent: " + status);
		
		
	}

	/**
	 * This method is called by the SIP stack when a new request arrives.
	 */
	public void processRequest(RequestEvent evt) {
		Request req = evt.getRequest();

		String method = req.getMethod();
		if (!method.equals("MESSAGE")) { // bad request type.
			messageProcessor.processError("Bad request type: " + method);
			return;
		}

		FromHeader from = (FromHeader) req.getHeader("From");
		messageProcessor.processMessage(from.getAddress().toString(),
				new String(req.getRawContent()));
		Response response = null;
		try { // Reply with OK
			response = messageFactory.createResponse(200, req);
			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			toHeader.setTag("888"); // This is mandatory as per the spec.
			ServerTransaction st = sipProvider.getNewServerTransaction(req);
			st.sendResponse(response);
		} catch (Throwable e) {
			e.printStackTrace();
			messageProcessor.processError("Can't send OK reply.");
		}
	}

	/**
	 * This method is called by the SIP stack when there's no answer to a
	 * message. Note that this is treated differently from an error message.
	 */
	public void processTimeout(TimeoutEvent evt) {
		messageProcessor
				.processError("Previous message not sent: " + "timeout");
	}

	/**
	 * This method is called by the SIP stack when there's an asynchronous
	 * message transmission error.
	 */
	public void processIOException(IOExceptionEvent evt) {
		messageProcessor.processError("Previous message not sent: "
				+ "I/O Exception");
	}

	/**
	 * This method is called by the SIP stack when a dialog (session) ends.
	 */
	public void processDialogTerminated(DialogTerminatedEvent evt) {
	}

	/**
	 * This method is called by the SIP stack when a transaction ends.
	 */
	public void processTransactionTerminated(TransactionTerminatedEvent evt) {
	}

	public String getHost() {
		int port = sipProvider.getListeningPoint().getPort();
		String host = sipStack.getIPAddress();
		return host;
	}

	public int getPort() {
		int port = sipProvider.getListeningPoint().getPort();
		return port;
	}


	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(MessageProcessor newMessageProcessor) {
		messageProcessor = newMessageProcessor;
	}

	

	public void waitResult(int seconds) throws InterruptedException {
		logger.info("------------------- wait response -------------------- ");
		TimeUnit.SECONDS.sleep(seconds);
		
				
	}
	
	public void stop() {
		logger.info("------------------- stop sip stack -------------------- ");
		this.sipStack.stop();
	}
	
	public void addExpection(String seq, int status) {
		this._expectResultMap.put(seq, status);
	}
	
	
	public int checkResult() {
		System.out.println("------------------- result ---------------------------- ");
		int errCnt = 0;
		for(Map.Entry<String, Integer> entry: _expectResultMap.entrySet()) {
			String seqNum = entry.getKey();
			int expectedStatus = entry.getValue();
			Integer theStatus = this._actualResultMap.get(seqNum);
			int actualStatus = theStatus == null? 0: theStatus ;
			//if ((status >= 200) && (status < 300)) 
			//	errCnt ++;
			if(expectedStatus != actualStatus) {
				errCnt ++;
				System.err.println(entry.getKey() + " request , return " + actualStatus + ", should be " + expectedStatus);
			}
			else {
				System.out.println(entry.getKey() + " request , return " + actualStatus + " correctly. ");
			}
		}
		return errCnt;
	}


	public String sendRegisterRequest(String accesscode,  String srcAddr, String destAddr, String domain) throws ParseException, InvalidArgumentException, SipException {	
		SipRequestInfo sipReq = new SipRequestInfo();
		
		sipReq.setRequestUri("sip:" + destAddr);
		sipReq.setToUri("sip:" + accesscode + "@" + domain);
		sipReq.setFromUri("sip:" + accesscode + "@" + domain);
		sipReq.setContactUri("sip:" + srcAddr, "1", 1800);
		sipReq.setUserAgent("TAS");


		sipReq.setAuthorization("");
		return sendRequest(sipReq);
	}


	public static void main(String args[]) {

		int nPort = args.length == 0 ? 5068 : Integer.parseInt(args[0]);
		try
        {
			String ip = InetAddress.getLocalHost().getHostAddress();
			SipUserAgent ua = new SipUserAgent(ip, nPort);
			//sendTestRegister(ua);
        } 
		catch (Throwable e)
        {
            System.out.println("Problem initializing the SIP stack.");
            e.printStackTrace();
            System.exit(-1);
        }

    }

	private static void sendTestRegister(SipUserAgent ua) throws Exception {

		ua.setMessageProcessor(new SipMessageHandler());
		//user name, src addr, dest addr, domain
		String seq = ua.sendRegisterRequest("140723211", "10.140.202.83:5068", "10.224.77.47:5060", "walterfan.github.com");
		ua.addExpection(seq, 200);
		ua.waitResult(WAIT_SECONDS);
		int errCnt = ua.checkResult();
		System.out.println("failed case: " + errCnt);
	}

	//todo: build sip request
}
