package com.github.walterfan.server.logger;

import com.github.walterfan.server.logger.message.ResultMessage;

public interface ResultListener {
	void onResult(ResultMessage msg);
	void onException(Throwable throwable);
}
