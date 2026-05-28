package day1.microservices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.stereotype.Service;

// ==========================================
// 1. SOAP Configuration Class
// ==========================================
@Configuration
class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Bind the dummy request class directly to JAXB instead of using non-existent generated package path
        marshaller.setClassesToBeBound(LegacyMeterSoapClient.GetMeterStatusRequest.class);
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri("http://legacy-system.energy/ws/meters");
        return template;
    }
}

// ==========================================
// 2. SOAP Service Client Implementation
// ==========================================
@Service
public class LegacyMeterSoapClient {

    private final WebServiceTemplate webServiceTemplate;

    public LegacyMeterSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Sends a request to a legacy SOAP Web Service to retrieve meter status.
     * WebServiceTemplate handles the marshalling of Java objects to XML and unmarshalling the response.
     */
    public Object getMeterStatus(String meterId) {
        // Dummy generated class representations
        GetMeterStatusRequest request = new GetMeterStatusRequest();
        request.setMeterId(meterId);

        // Execute request via SOAP envelope over HTTP
        return webServiceTemplate.marshalSendAndReceive(request);
    }

    // Dummy inner classes simulating JAXB generated classes from a WSDL schema
    public static class GetMeterStatusRequest {
        private String meterId;

        public String getMeterId() { return meterId; }
        public void setMeterId(String meterId) { this.meterId = meterId; }
    }
}
