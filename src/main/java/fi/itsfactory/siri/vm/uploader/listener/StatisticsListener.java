package fi.itsfactory.siri.vm.uploader.listener;

import java.util.logging.Logger;


public class StatisticsListener implements ResponseListener {
	private static Logger logger = Logger.getLogger(StatisticsListener.class.getName());
	@Override
	public void handleResponse(ListenerResponse response) {
		logger.fine("Received response with " + response.getResponse().length() + " characters in "
				+ (response.getResponseTime().longValue()) + " ms.");
	}

	@Override
	public void handleException(Exception exception) {
	}

}
