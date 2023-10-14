package parcial;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AppINIDepartamento {

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
                    from("jms:queue:artemis")
                        .process(exchange -> {
                            String message = exchange.getIn().getBody(String.class);
                            System.out.println("Mensaje recibido: \n" + message);
                            
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode jsonNode = objectMapper.readValue(message, ObjectNode.class);       
                            jsonNode = sortJsonFields(jsonNode);
                            message = objectMapper.writeValueAsString(jsonNode);
                            
                            boolean isChachapoyas = message.contains("\"DISTRITO\":\"CHACHAPOYAS\"");
                            String targetQueue = isChachapoyas ? "mqINIprovincia1" : "mqINIprovincia2";
                            if (isChachapoyas) {
                                message = message.substring(0, message.length() - 1) + ",\"EMISOR\":\"AppINIDepartamento\"}";
                            }
                            exchange.getIn().setHeader("targetQueue", targetQueue);
                            exchange.getIn().setBody(message);
                        })
                        .toD("jms:queue:${header.targetQueue}");
                }
            });

            context.start();
            System.out.println("Receptor de mensajes activo.");
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            context.stop();
        }
    }
    private static ObjectNode sortJsonFields(ObjectNode jsonNode) {
        ObjectNode sortedJson = jsonNode.objectNode();
        sortedJson.set("EXPEDIENTE", jsonNode.get("EXPEDIENTE"));
        sortedJson.set("PRESTADOR", jsonNode.get("PRESTADOR"));
        sortedJson.set("RESOLUCION", jsonNode.get("RESOLUCION"));
        sortedJson.set("ASPECTO", jsonNode.get("ASPECTO"));
        sortedJson.set("PLAZO_IMP", jsonNode.get("PLAZO_IMP"));
        sortedJson.set("NUM_MED_CORREC_IMP", jsonNode.get("NUM_MED_CORREC_IMP"));
        sortedJson.set("DEPARTAMENTO", jsonNode.get("DEPARTAMENTO"));
        sortedJson.set("PROVINCIA", jsonNode.get("PROVINCIA"));
        sortedJson.set("DISTRITO", jsonNode.get("DISTRITO"));
        return sortedJson;
    }
}
