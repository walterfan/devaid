package com.github.walterfan.sip;

public class SipMessageHandler implements MessageProcessor {

	@Override
	public void processMessage(String sender, String message) {
		System.out.println("processMessage: " + sender + "\n" + message);

	}

	@Override
	public void processError(String errorMessage) {
		System.out.println("processError: " + errorMessage);

	}

	@Override
	public void processInfo(String infoMessage) {
		System.out.println("processInfo: " + infoMessage);

	}

}
