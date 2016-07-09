package ws.cogito.magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ws.cogito.magic.messaging.PriorityMessageListener;



@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(AmqpProducerProperties.class)
public class AmqpDeclarationsConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpDeclarationsConfiguration.class);
	
	@Autowired
	@Qualifier("amqpProducerProperties")
	AmqpProducerProperties properties;
	
    @Bean
    public ConnectionFactory connectionFactory() {
    	
    	CachingConnectionFactory connectionFactory = null;
    	
    	logger.info("Configuring Connection Factory");
    	
        connectionFactory = new CachingConnectionFactory(properties.getHost());
        connectionFactory.setUsername(properties.getUserName());
        connectionFactory.setPassword(properties.getUserPassword());
        connectionFactory.setVirtualHost(properties.getvHost());
    	
    	return connectionFactory;
    }
    
	/**
	 * In order for all the other configurations to take effect there must
	 * be at least one decleration by the rabbitAdmin
	 * @param connectionFactory
	 * @return RabbitAdmin
	 */
    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
    	
    	logger.info("Kicking off Declarations");
    	
    	RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    	
    	/* 
    	 * *********************IMPORTANT********************************
    	 * 
    	 * None of the other declarations take effect unless at least one 
    	 * declaration is executed by RabbitAdmin.
    	 * 
    	 * *********************IMPORTANT******************************** 
    	 */
    	rabbitAdmin.declareExchange(directExchange());
    	
        return new RabbitAdmin(connectionFactory);
    }
    
    @Bean
    RabbitTemplate rabbitTemplate() {
    	
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    	
    	return rabbitTemplate;
    }    
    
	@Bean
	DirectExchange directExchange() {
		return new DirectExchange(properties.getDirectExchange());
	}
    
    /* PRIORITY ORDERS CONFIGURATION */
    
	@Bean
	Queue priorityQueue() {
		return new Queue(properties.getPriorityQueue(), true);
	}

	@Bean
	Binding priorityBinding(Queue priorityQueue, DirectExchange directExchange) {
		return BindingBuilder.bind(priorityQueue).to(directExchange).
				with(properties.getPriorityQueueBinding());
	}
 
    @Bean
    MessageListenerAdapter priorityMessageListener() throws Exception {
    	
    	PriorityMessageListener listener = new PriorityMessageListener();
    	listener.setRabbitTemplate(rabbitTemplate());

        return new MessageListenerAdapter(listener) {{
            setDefaultListenerMethod("onMessage");
        }};
    }
    
	@Bean
	SimpleMessageListenerContainer ordersContainer(ConnectionFactory connectionFactory, 
			MessageListenerAdapter priorityMessageListener) {
		
		SimpleMessageListenerContainer priorityContainer = 
				new SimpleMessageListenerContainer();
		
		priorityContainer.setConnectionFactory(connectionFactory);
		priorityContainer.setQueueNames(properties.getPriorityQueue());
		priorityContainer.setMessageListener(priorityMessageListener);
		
		return priorityContainer;
	}
}