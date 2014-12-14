package com.github.walterfan.sip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.Request;

import com.github.walterfan.util.RandomUtils;

public class SipRequestInfo {
	private SipFactory sipFactory = SipFactory.getInstance();
	private HeaderFactory headerFactory;
	private AddressFactory addressFactory;

	private String requestUri;
	
	private String toUri;
	
	private String fromUri;
	
	private String contactUri;
	
	private String priority;
	
	private int expireTime = -1;
	
	private String userAgent;
	
	private String sipMethod;
	
	private String authorization;
	
	private String callId = "";
	
	private int cseqNum = 0;
	
	public SipRequestInfo() throws PeerUnavailableException {
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		this.sipMethod = Request.REGISTER;
	}
	
	public SipRequestInfo(String sipMethod) throws PeerUnavailableException {
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		this.sipMethod = sipMethod;
	}
	
	public String getToUri() {
		return toUri;
	}
	public void setToUri(String toUri) {
		this.toUri = toUri;
	}
	public String getFromUri() {
		return fromUri;
	}
	public void setFromUri(String fromUri) {
		this.fromUri = fromUri;
	}
	public String getContactUri() {
		return contactUri;
	}
	public void setContactUri(String contactUri) {
		this.contactUri = contactUri;
	}
	
	public void setContactUri(String contactUri, String priority) {
		this.contactUri = contactUri;
		this.priority = priority;
	}
	
	public void setContactUri(String contactUri, String priority, int expires) {
		this.contactUri = contactUri;
		this.priority = priority;
		this.expireTime = expires;
	}
	
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public int getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getSipMethod() {
		return sipMethod;
	}
	public void setSipMethod(String sipMethod) {
		this.sipMethod = sipMethod;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	
	public SipURI getRequestUri() throws ParseException {
		SipURI requestURI = (SipURI)addressFactory.createURI(this.requestUri);		
		requestURI.setTransportParam("udp");
		return requestURI;
	}
	
	
	
	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}



	public int getCseqNum() {
		return cseqNum;
	}

	public void setCseqNum(int cseqNum) {
		this.cseqNum = cseqNum;
	}

	public ToHeader getToHeader() throws ParseException, InvalidArgumentException  {
		URI uri = addressFactory.createURI(this.toUri); 
		Address toAddress = addressFactory.createAddress(uri);
		ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
		return toHeader;
	}	

	public FromHeader getFromHeader() throws ParseException, InvalidArgumentException  {
		URI uri = addressFactory.createURI(this.fromUri); 
		Address fromAddress = addressFactory.createAddress(uri);
		FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, RandomUtils.getRandomLetters(10));
		return fromHeader;
	}	
	
	public ContactHeader getContactHeader() throws ParseException, InvalidArgumentException  {
		URI uri = addressFactory.createURI(this.contactUri); 
		Address contactAddress = addressFactory.createAddress(uri);
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		contactHeader.setParameter("q", this.priority);
		contactHeader.setExpires(this.expireTime);		
		return contactHeader;
	}	
	
	public AuthorizationHeader getAuthorizationHeader() throws ParseException {
		
		AuthorizationHeader authHeader = headerFactory.createAuthorizationHeader(this.authorization);
		return authHeader;
	}
	
	
	public UserAgentHeader getUserAgentHeader() throws ParseException {
		List<String> list = new ArrayList<String>(1);
		list.add(this.userAgent);
		UserAgentHeader uaHeader = headerFactory.createUserAgentHeader(list);
		return uaHeader;
	}
	
	public ExpiresHeader getExpiresHeader() throws InvalidArgumentException {
		return headerFactory.createExpiresHeader(this.expireTime);
	}

	public CallIdHeader getCallIdHeader() throws ParseException {
		return headerFactory.createCallIdHeader(callId);
	}
	
	public CSeqHeader getCSeqHeader() throws ParseException, InvalidArgumentException {
		return headerFactory.createCSeqHeader(this.cseqNum, this.sipMethod);
	}
}
