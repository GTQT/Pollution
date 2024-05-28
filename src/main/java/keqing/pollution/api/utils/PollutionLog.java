package keqing.pollution.api.utils;

import org.apache.logging.log4j.Logger;

public class PollutionLog {

	public static Logger logger;

	public PollutionLog() {
	}

	public static void init(Logger modLogger) {
		logger = modLogger;
	}

}
