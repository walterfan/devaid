package com.github.walterfan.util.swing;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
/*
#define repository wide threshold

#define root logger
log4j.rootLogger=DEBUG,A1

#define Appenders
log4j.appender.A1=com.omniscient.log4jcontrib.swingappender.SwingAppender
log4j.appender.A1.layout=org.apache.log4j.PatternLayout
log4j.appender.A1.layout.ConversionPattern=%r [%t] %p %c %x %m%n
*/
/**
 * @author kalpak
 * 
 */
public class SwingAppender extends AppenderSkeleton {

	/** The appender swing UI. */
	private Logable logAppender;

	public SwingAppender() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent
	 * )
	 */
	public Logable getLogAppender() {
		return logAppender;
	}

	public void setLogAppender(Logable logAppender) {
		this.logAppender = logAppender;
	}



	/*
	 * ﻿ * (non-Javadoc) ﻿ * ﻿ * @see org.apache.log4j.Appender#close() ﻿
	 */
	public void close() {
		// Opportunity for the appender ui to do any cleanup.
		/*
		 * logAppender.close(); logAppender = null;
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Performs checks to make sure the appender ui is still alive.
	 * 
	 * @return
	 */
	private boolean performChecks() {
		return !closed && layout != null;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (!performChecks()) {
			return;
		}
		String logOutput = this.layout.format(event);
		logAppender.writeLog(logOutput);

		if (layout.ignoresThrowable()) {
			String[] lines = event.getThrowableStrRep();
			if (lines != null) {
				int len = lines.length;
				for (int i = 0; i < len; i++) {
					logAppender.writeLog(lines[i]);
					logAppender.writeLog(Layout.LINE_SEP);
				}
			}
		}
	}
}
