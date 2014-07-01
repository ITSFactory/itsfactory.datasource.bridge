package fi.itsfactory.siri.vm.uploader.request;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import uk.org.siri.siri.GeneralMessageRequestStructure;
import uk.org.siri.siri.InfoChannelRefStructure;
import uk.org.siri.siri.ParticipantRefStructure;
import uk.org.siri.siri.ServiceRequest;
import uk.org.siri.siri.Siri;
import fi.itsfactory.siri.vm.uploader.listener.ResponseListener;

public class GeneralMessageRequest implements Request{
	private String address;
	private String authorization;
	private String requestorRef;
	private Long interval;
	private String payload;
	private Map<String, String> payloadParameters;
	
	private List<ResponseListener> listeners;
	
	private static Logger logger = Logger.getLogger(GeneralMessageRequest.class.getName());
		
	public GeneralMessageRequest(String address, String authorization, Long interval, String requestorRef) {
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
            ServiceRequest sr = new ServiceRequest();
		    
		    GeneralMessageRequestStructure gmrs = new GeneralMessageRequestStructure();
		    gmrs.setVersion("1.3");
		    
		    InfoChannelRefStructure icrsErrors = new InfoChannelRefStructure();
		    icrsErrors.setValue("errors");
            gmrs.getInfoChannelRef().add(icrsErrors);
		    
            InfoChannelRefStructure icrsWarnings = new InfoChannelRefStructure();
            icrsWarnings.setValue("warnings");
            gmrs.getInfoChannelRef().add(icrsWarnings);
            
            InfoChannelRefStructure icrsMessages = new InfoChannelRefStructure();
            icrsMessages.setValue("messagaes");            
		    gmrs.getInfoChannelRef().add(icrsMessages);
		    
		    GregorianCalendar gregorianCalendar = new GregorianCalendar();
		    DatatypeFactory datatypeFactory;
            try {
                datatypeFactory = DatatypeFactory.newInstance();
                XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
                sr.setRequestTimestamp(now);
                gmrs.setRequestTimestamp(now);
            } catch (DatatypeConfigurationException e) {
                logger.log(Level.SEVERE, "Cannot create GeneralMessageRequest", e);
            }
		    
            ParticipantRefStructure prs = new ParticipantRefStructure();
            prs.setValue(requestorRef);     
            sr.setRequestorRef(prs);

            sr.getGeneralMessageRequest().add(gmrs);
			
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

    public List<ResponseListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ResponseListener> listeners) {
        this.listeners = listeners;
    }
}
