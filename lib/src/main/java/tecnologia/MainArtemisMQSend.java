package tecnologia;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MainArtemisMQSend {

	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();
        try {
        	ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            cf.setBrokerURL("tcp://localhost:61616");
            cf.setUser("admin");
            cf.setPassword("admin");
            JmsComponent jms = new JmsComponent();
            jms.setConnectionFactory(cf);
            jms.setPreserveMessageQos(true);
            context.addComponent("jms", jms);
            String hms = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String json = "{\"nombre\":\"Luciana "+hms+"\",\"email\":\"PUCP"+hms
            		+"@gmail.com\",\"codigo\":\""+hms+"\"}";
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            template.sendBody("jms:exampleMQ", json);
            
        } finally {
            context.stop();
        }
	}
}
