package fi.itsfactory.siri.vm.uploader.listener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcachedListener implements ResponseListener{
	private MemcachedClient memcachedClient;
	private String cacheUrl;
	private String cacheKey;
	
	private static Logger logger = Logger.getLogger(MemcachedListener.class.getName());
	
	public MemcachedListener(String cacheUrl, String cacheKey) {
		memcachedClient = null;
		this.cacheUrl = cacheUrl;
		this.cacheKey = cacheKey;
	}
	@Override
	public void handleResponse(ListenerResponse response) {
		if(memcachedClient == null){
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(cacheUrl));
			try {
				memcachedClient = builder.build();
				logger.log(Level.INFO, "Memcached client built successfully.");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Memcached client build failed. Memcached server will not be updated!", e);
				memcachedClient = null;
			}				
		}
		try {
			memcachedClient.set(cacheKey, 0, response.getResponse());
			logger.log(Level.FINE, "Updated "+response.getResponse().length()+" characters to the memcached.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not update memcached.",e);
		}
	}

	@Override
	public void handleException(Exception exception) {
	}

}
