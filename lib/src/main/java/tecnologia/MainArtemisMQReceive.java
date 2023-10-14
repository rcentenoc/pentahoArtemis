package tecnologia;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class MainArtemisMQReceive {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            // Configurar la conexión a ActiveMQ Artemis
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            cf.setBrokerURL("tcp://localhost:61616");
            cf.setUser("admin");
            cf.setPassword("admin");

            context.addComponent("jms", org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge(cf));

            // Definir una ruta para recibir mensajes de la cola
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("jms:exampleMQ")
                        .process(exchange -> {
                            // Procesa el mensaje recibido
                            String mensaje = exchange.getIn().getBody(String.class);
                            System.out.println("Mensaje recibido: " + mensaje);
                        });
                }
            });


            context.start();
            Thread.sleep(Long.MAX_VALUE);

        } finally {
            context.stop();
        }
    }
}