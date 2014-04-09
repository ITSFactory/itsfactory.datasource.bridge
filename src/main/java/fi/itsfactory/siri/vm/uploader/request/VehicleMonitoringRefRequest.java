package fi.itsfactory.siri.vm.uploader.request;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import uk.org.siri.siri.ParticipantRefStructure;
import uk.org.siri.siri.ServiceRequest;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.VehicleMonitoringRefStructure;
import uk.org.siri.siri.VehicleMonitoringRequestStructure;

public class VehicleMonitoringRefRequest implements Request{
	private String address;
	private String authorization;
	private String requestorRef;
	private Long interval;
	private String payload;
	private Map<String, String> payloadParameters;
	
	private String vehicleMonitoringRef;
	
	private static Logger logger = Logger.getLogger(VehicleMonitoringRefRequest.class.getName());
		
	public VehicleMonitoringRefRequest(String address, String authorization, Long interval,String requestorRef) {
		this.address = address;
		this.authorization = authorization;
		this.requestorRef = requestorRef;
		this.interval = interval;
		
		payload = null;
		payloadParameters = new HashMap<String, String>();
		payloadParameters.put("Authorization", authorization);
		payloadParameters.put("Accept-Charset", "utf-8");
	}
	
	public String getPayload() {
		if(payload == null){
			payload = createPayload();
		}
		return payload;
	}

	public String getAddress() {
		return address;
	}
	
	public String getAuthorization() {
		return authorization;
	}

	public long getInterval() {
		return interval;			
	}

	private String createPayload(){
		if(payload == null){
			VehicleMonitoringRefStructure vmrs = new VehicleMonitoringRefStructure();
			if(vehicleMonitoringRef != null){
				vmrs.setValue(vehicleMonitoringRef);			
			}else{
				vmrs.setValue("VEHICLES_ALL");
			}
			
			VehicleMonitoringRequestStructure vmr = new VehicleMonitoringRequestStructure();
			vmr.setVersion("1.3");
			vmr.setVehicleMonitoringRef(vmrs);
			
			ParticipantRefStructure prs = new ParticipantRefStructure();
			prs.setValue(requestorRef);
			
			ServiceRequest sr = new ServiceRequest();
			sr.getVehicleMonitoringRequest().add(vmr);
			sr.setRequestorRef(prs);
			
			Siri siri = new Siri();
			siri.setServiceRequest(sr);
			
			StringWriter writer;
			try {
				JAXBContext context = JAXBContext.newInstance(Siri.class);
				
				Marshaller marshaller = context.createMarshaller();
				
				writer = new StringWriter();
				marshaller.marshal(siri, writer);
				
				payload = writer.toString();
			} catch (JAXBException e) {
				logger.log(Level.SEVERE, "Cannot parse request", e);
				payload = null;
			}
		}
		return payload;		
	}

	@Override
	public Map<String, String> getParameters() {
		return payloadParameters;
	}

	public String getVehicleMonitoringRef() {
		return vehicleMonitoringRef;
	}

	public void setVehicleMonitoringRef(String vehicleMonitoringRef) {
		this.vehicleMonitoringRef = vehicleMonitoringRef;
	}	
}
