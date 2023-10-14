package parcial;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class AppINIDistrito {
	    public static void main(String[] args) throws Exception {
	        CamelContext context = new DefaultCamelContext();
	        try {
	            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
	            cf.setBrokerURL("tcp://localhost:61616");
	            cf.setUser("admin");
	            cf.setPassword("admin");

	            context.addComponent("jms", org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge(cf));

	            context.addRoutes(new RouteBuilder() {
	                @Override
	                public void configure() throws Exception {
	                    from("jms:queue:mqINIdistrito")
	                        .process(exchange -> {	  
	                            String mensaje = exchange.getIn().getBody(String.class);
	                            System.out.println("Mensaje recibido: \n" + mensaje);
	                        });
	                }
	            });

	            context.start();
	            System.out.println("Receptor de mensajes activo.");
	            Thread.sleep(Long.MAX_VALUE);

	        } finally {
	            context.stop();
	        }
	    }
	}
