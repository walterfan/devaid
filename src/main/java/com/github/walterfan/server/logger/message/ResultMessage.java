package com.github.walterfan.server.logger.message;

import java.io.Serializable;

public class ResultMessage extends AbstractMessage implements Serializable {
	private static final long serialVersionUID = -6015374889032686045L;
	private boolean okay = false;
	
	public void setOkay(boolean okay) {
		this.okay = okay;
	}
	public boolean isOkay() {
		return okay;
	}
	
	public String toString() {
		return okay + ", sequence = " + this.getSequence();
	}

}