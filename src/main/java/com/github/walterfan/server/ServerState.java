package com.github.walterfan.server;

public enum ServerState {
	FAILED(-1,"FAILED"),
	STOPPED(0,"STOPPED"),
	INITING(1,"STARTING"),
	INITED(2,"STARTING"),
	STARTING(3,"STARTING"),
	STARTED(4,"STARTED"),
	STOPPING(5,"STOPPING"),
	CLEANING(6,"CLEANING"),
	CLEANED(7,"CLEANED");
	
	int state;
    String name;

    ServerState(int state, String name) {
    	this.state = state;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}