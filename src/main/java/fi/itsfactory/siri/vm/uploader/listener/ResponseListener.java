package fi.itsfactory.siri.vm.uploader.listener;

public interface ResponseListener {
	public void handleResponse(ListenerResponse response);	
	public void handleException(Exception exception);	
}
