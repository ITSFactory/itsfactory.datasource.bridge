package fi.itsfactory.siri.vm.uploader.request;

import java.util.Map;

public interface Request {
	public String getAddress();
	public Map<String, String> getParameters();
	
	public String getPayload();
	public long getInterval();
}
