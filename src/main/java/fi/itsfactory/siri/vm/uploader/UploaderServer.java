package fi.itsfactory.siri.vm.uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import fi.itsfactory.siri.vm.uploader.listener.ResponseListener;
import fi.itsfactory.siri.vm.uploader.request.Request;
import fi.itsfactory.siri.vm.uploader.service.PollerService;

public class UploaderServer {
	private List<Request> requests;
	
	private List<PollerService> repeaters;	
	private List<ResponseListener> listeners;
	private static Logger logger = Logger.getLogger(UploaderServer.class.getName());
	
	public UploaderServer(List<Request> requests) {
		this.requests = requests;
		repeaters = new ArrayList<PollerService>();
	}
	
	public void start(){
	    for(Request request : this.requests){
	        PollerService repeater = new PollerService(request);
	        repeater.startAsync();
	        repeater.awaitRunning();
	        
	        repeaters.add(repeater);
	    }
	    
		logger.info("Main thread starting.");
		while(true){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public void stop(){
		if(this.repeaters != null){
    	    for(PollerService repeater : this.repeaters){
    	        if(repeater != null){
    	            logger.info("Stopping the service...");
    	            repeater.stopAsync();
    	            repeater.awaitTerminated();
    	            logger.info("Service stopped.");
    	        }		    
    		}
		}
	}
	
	public List<ResponseListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<ResponseListener> listeners) {
		this.listeners = listeners;
	}

	public static void main(String[] args) {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("uploadserver-main.xml");
		final UploaderServer server = (UploaderServer) context.getBean(UploaderServer.class);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	server.stop();
		    	context.close();
		    }
		});
		
		server.start();
	}
}
