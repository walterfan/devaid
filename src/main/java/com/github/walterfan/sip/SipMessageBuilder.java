package com.github.walterfan.sip;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;

import com.github.walterfan.util.RandomUtils;


public class SipMessageBuilder {
	
	private HeaderFactory headerFactory;
	
	private AddressFactory addressFactory;	

	private MessageFactory messageFactory;
	
	public SipMessageBuilder(HeaderFactory headerFactory, AddressFactory addressFactory,   MessageFactory messageFactory) {
		this.addressFactory = addressFactory;
		this.headerFactory = headerFactory;
		this.messageFactory = messageFactory;
	}
	
	
	public ToHeader createToHeader(String toSipAddress, String toUser, String toDisplayName)  throws ParseException {
        SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(toDisplayName);
        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
        return toHeader;
        
        
	}
	
	public ToHeader createToHeader(String to)  throws ParseException {
		String username = null;
		String address = null;
		int pos0 = to.indexOf(":");
		int pos1 = to.indexOf("@");
		
		if(pos1 > 0) {
			username = to.substring(pos0 + 1, pos1);
			address = to.substring(pos1 + 1);
		} else {
			address = to.substring(pos0 + 1);
		}
	
		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		//toNameAddress.setDisplayName(username);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);		
		return toHeader;
    
    
	}

	public FromHeader createFromHeader(String from)  throws ParseException {
		String username = null;
		String address = null;
		int pos0 = from.indexOf(":");
		int pos1 = from.indexOf("@");
		
		if(pos1 > 0) {
			username = from.substring(pos0 + 1, pos1);
			address = from.substring(pos1 + 1);
		} else {
			address = from.substring(pos0 + 1);
		}
		
		SipURI fromAddress = addressFactory.createSipURI(username, address);
		Address fromNameAddress = addressFactory.createAddress(fromAddress);
		//toNameAddress.setDisplayName(username);
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, RandomUtils.getRandomLetters(10));
		return fromHeader;
	}
    
	
	public FromHeader createFromHeader(String fromName, String fromSipAddress, String fromDisplayName, String tag) throws ParseException {
        SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
        Address fromNameAddress = addressFactory.createAddress(fromAddress);
        
        fromNameAddress.setDisplayName(fromDisplayName);
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, tag);
        return fromHeader;
	}

	public ContactHeader createContactHeader(String contact, String qstr, int expires) throws ParseException, InvalidArgumentException  {
		URI uri = addressFactory.createURI(contact); 
		Address contactAddress = addressFactory.createAddress(uri);
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		contactHeader.setParameter("q", qstr);
		contactHeader.setExpires(expires);		
		return contactHeader;
	}
	
	public ContactHeader createContactHeader(String contact) throws ParseException  {
		String username = null;
		String address = null;
		int pos0 = contact.indexOf(":");
		int pos1 = contact.indexOf("@");
		
		
		if(pos1 > 0) {
			username = contact.substring(pos0 + 1, pos1);
			address = contact.substring(pos1 + 1);
		} else {
			address = contact.substring(pos0 + 1);
		}
		
		SipURI contactUri = addressFactory.createSipURI(username, address);
		Address contactAddress = addressFactory.createAddress(contactUri);
		
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		return contactHeader;
	}
	
	public AddressFactory getAddressFactory() {
		return addressFactory;
	}


	public void setAddressFactory(AddressFactory addressFactory) {
		this.addressFactory = addressFactory;
	}


	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}


	public void setHeaderFactory(HeaderFactory headerFactory) {
		this.headerFactory = headerFactory;
	}


	public MessageFactory getMessageFactory() {
		return messageFactory;
	}


	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	
}
