package parcial;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppINIProvincia1 {

	    public static void main(String[] args) throws Exception {
	        CamelContext context = new DefaultCamelContext();

	        try {
	            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
	            cf.setBrokerURL("tcp://localhost:61616");
	            cf.setUser("admin");
	            cf.setPassword("admin");
	            JmsComponent jms = new JmsComponent();
	            jms.setConnectionFactory(cf);
	            context.addComponent("jms", jms);

	            context.addRoutes(new RouteBuilder() {
	                @Override
	                public void configure() throws Exception {
	                    from("jms:queue:mqINIprovincia1")
	                        .process(exchange -> {
	                            String message = exchange.getIn().getBody(String.class);
	                            System.out.println("Mensaje recibido: \n" + message);	  
	                            	             
	                            int numMediciones = parseNumeroMediciones(message);	                        
	                            for (int i = 0; i < numMediciones; i++) {
	                            	message = message.replace("\"EMISOR\":\"AppINIDepartamento\"", "\"EMISOR\":\"AppINIProvincia1\"");
	                                message = message.substring(0, message.length() - 1) + ",\"msgCorrelativo\":"+(i+1)+"}";
	                                message = message.substring(0, message.length() - 1) + ",\"msgTotal\":"+numMediciones+"}";
	                                message = message;
	                                exchange.getIn().setBody(message);
	                                System.out.println("Enviando mensaje " + (i + 1) + ": \n" + message);
	                            }
	                            
	                        })
	                        .to("jms:queue:mqINIdistrito");
	                }
	            });

	            context.start();
	            System.out.println("Receptor de mensajes activo.");
	            Thread.sleep(Long.MAX_VALUE);
	        } finally {
	            context.stop();
	        }
	    }
	    
	    private static int parseNumeroMediciones(String message) {
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode jsonNode = objectMapper.readTree(message);

	            if (jsonNode.has("NUM_MED_CORREC_IMP")) {
	                int numMediciones = jsonNode.get("NUM_MED_CORREC_IMP").asInt();
	                return numMediciones;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return 1;
	    }

	}