package com.github.walterfan.util.jms;

import javax.jms.ExceptionListener;
import javax.jms.MessageListener;

import com.github.walterfan.service.IService;

public interface IJmsReceiver extends MessageListener, ExceptionListener, IService {

}
