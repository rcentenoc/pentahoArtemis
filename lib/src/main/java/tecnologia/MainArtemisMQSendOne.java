package tecnologia;

import java.util.Scanner;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MainArtemisMQSendOne {

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

            ProducerTemplate template = context.createProducerTemplate();
            context.start();

            // Leer el mensaje JSON desde la consola
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el mensaje JSON: ");
            String json = scanner.nextLine();

            // Envía el mensaje JSON a la cola
            template.sendBody("jms:queue:exampleMQ/exampleOne", json);

        } finally {
            context.stop();
        }
    }
}

// 
//     {"nombre": "Ejemplo","email": "ejemplo@gmail.com","codigo": "12345"}
// 