package fi.itsfactory.siri.vm.uploader;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import fi.itsfactory.siri.vm.uploader.listener.ResponseListener;
import fi.itsfactory.siri.vm.uploader.request.Request;
import fi.itsfactory.siri.vm.uploader.service.PollerService;

public class UploaderServer {
	private Request request;
	
	private PollerService repeater;	
	private List<ResponseListener> listeners;
	private static Logger logger = Logger.getLogger(UploaderServer.class.getName());
	
	public UploaderServer(Request request) {
		this.request = request;
		repeater = null;
	}
	
	public void start(){
		repeater = new PollerService(request, listeners);
		repeater.startAsync();
		repeater.awaitRunning();
		
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
		if(repeater != null){
			logger.info("Stopping the service...");
			repeater.stopAsync();
			repeater.awaitTerminated();
			logger.info("Service stopped.");
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
