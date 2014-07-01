package fi.itsfactory.siri.vm.uploader.service;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import fi.itsfactory.siri.vm.uploader.request.Request;

public class HttpConnector {
	
	public String fireRequest(Request request) throws ClientProtocolException, IOException {
		String response = null;
		
		RequestConfig defaultRequestConfig = RequestConfig.custom()
	            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
	            .setConnectTimeout(10000)
	            .setSocketTimeout(10000)
	            .setConnectionRequestTimeout(10000)
	            .setStaleConnectionCheckEnabled(true)
	            .build();
		
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        
		try{
	        HttpPost post = new HttpPost(request.getAddress());		
			if(request.getParameters() != null){
				for(Entry<String, String> entry : request.getParameters().entrySet()) {
				    post.setHeader(entry.getKey(), entry.getValue());
				}
			}		
			post.setEntity(new StringEntity(request.getPayload()));
			
			CloseableHttpResponse httpResponse = client.execute(post);
			
			try{
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					response = IOUtils.toString(entity.getContent());
					EntityUtils.consume(entity);
				}
			}finally{
				if(httpResponse != null){
					httpResponse.close();			
				}
			}
		}finally{
			if(client != null){
				client.close();
			}
		}
		
		return response;			
	}
}
