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
	private final int RETRY_STAGE_MAX = 18;
	private final int RETRY_STAGE_INCREMENT = 5;
	
	private List<ResponseListener> listeners;
	private Request request;
	private int retryStage;

	private static Logger logger = Logger.getLogger(PollerService.class.getName());
	
	public PollerService(Request request, List<ResponseListener> listeners) {
		this.listeners = listeners;
		this.request = request;
		this.retryStage = 0;
		
		logger.info("PollerService started up.");
	}
	
	@Override
	protected void runOneIteration() throws Exception {
		long interval = retryStage * RETRY_STAGE_INCREMENT * 1000;
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
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
			
			retryStage = 0;
		} catch (Exception e) {
			if(retryStage < RETRY_STAGE_MAX){
				retryStage++;
			}
			
			for (ResponseListener listener : listeners) {
				try {
					listener.handleException(e);
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Listener error", ex);
				}
			}
			
			logger.log(Level.WARNING, "Connection error, retry stage "+retryStage+", interval: "+interval+" ms", e);
		}
	}
	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(0, this.request.getInterval(), TimeUnit.MILLISECONDS);
	}
}
