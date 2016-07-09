package ws.cogito.magic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmqpProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmqpProducerApplication.class, args);
    }
}