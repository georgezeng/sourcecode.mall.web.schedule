package com.sourcecode.malls.schedule.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSchedule {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void run() {
		try {
			execute();
		} catch (Throwable t) {
			logger.error("[Schedule Error]: " + t.getMessage(), t);
		}
	}

	protected abstract void execute() throws Exception;
}
