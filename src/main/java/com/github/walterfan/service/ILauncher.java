package com.github.walterfan.service;

import org.apache.commons.cli.Options;

public interface ILauncher {

	void init()  throws Exception;

	void executeCmd(Options options, String[] args)
			throws Exception;

	void defaultCmd() throws Exception;

	void otherCmd() throws Exception;

	void printUsage(Options options);
}