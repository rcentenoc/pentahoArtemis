package tecnologia;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MainArtemisMQSendTwo {

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

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Ingrese el mensaje JSON (o escriba 'exit' para salir): ");
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }
                
                String hms = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
                String json = input; // Utiliza la entrada del usuario como JSON
                // template.sendBody("jms:exampleMQ", json);
                template.sendBody("jms:queue:exampleMQ/exampleOne", json);
                System.out.println("Mensaje enviado: " + json);
            }
        } finally {
            context.stop();
        }
    }
}