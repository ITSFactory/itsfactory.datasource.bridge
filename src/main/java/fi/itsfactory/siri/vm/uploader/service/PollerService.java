package fi.itsfactory.siri.vm.uploader.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.util.concurrent.AbstractScheduledService;

import fi.itsfactory.siri.vm.uploader.listener.ListenerResponse;
import fi.itsfactory.siri.vm.uploader.listener.ResponseListener;
import fi.itsfactory.siri.vm.uploader.request.Request;

public class PollerService extends AbstractScheduledService {
	
	private List<ResponseListener> listeners;
	private Request request;

	private static Logger logger = Logger.getLogger(PollerService.class.getName());
	
	public PollerService(Request request) {
		this.listeners = request.getListeners();
		this.request = request;
		
		logger.info("PollerService started up.");
	}
	
	@Override
	protected void runOneIteration() throws Exception {
		String response = null;
		try {
			long t1 = System.currentTimeMillis();
			response = new HttpConnector().fireRequest(request);
			long t2 = System.currentTimeMillis();
			if (response != null && listeners != null) {
				for (ResponseListener listener : listeners) {
					try {
						ListenerResponse listenerResponse = new ListenerResponse();
						listenerResponse.setResponse(response);
						listenerResponse.setResponseTime(t2-t1);
						listener.handleResponse(listenerResponse);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Listener error", e);
					}
				}
			}
			
		} catch (Exception e) {
			
			for (ResponseListener listener : listeners) {
				try {
					listener.handleException(e);
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Listener error", ex);
				}
			}
			
			logger.log(Level.WARNING, "Connection error", e);
		}
	}
	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedDelaySchedule(0, this.request.getInterval(), TimeUnit.MILLISECONDS);
	}
}
