package ws.cogito.magic.messaging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;

import com.jayway.jsonpath.JsonPath;

public class PriorityMessageListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(PriorityMessageListener.class);

	private static final String trackingId = "trackingId";

	private RabbitTemplate rabbitTemplate;

	@Override
	public void onMessage(Message message) {
		
		logger.info("Processing message...");
		
		//Response Properties
		MessageProperties properties = new MessageProperties();
		properties.setContentType(MediaType.APPLICATION_JSON.toString());
		properties.setContentEncoding(StandardCharsets.UTF_8.name());
		
		//process the tracking ID
		processTrackingId(message, properties);
		
		//return the Correlation ID if present
		processCorrelationId(message, properties);

		//create and return the response message
		String body = createResponseBody(message);
		
		Message responseMessage = new Message(body.getBytes(), properties);
		
		logger.info("Preparing to Reply To: " + message.getMessageProperties().getReplyTo());
		
		//THIS IS KEY: Routing Key = ReplyTo
		rabbitTemplate.send(message.getMessageProperties().getReplyTo(), responseMessage);
		
	    logger.info("Processed Message and Replied");
	}
	
	private String createResponseBody (Message message) {
		
		String response = "{\"processed\": ";

		try {
		
			String json = new String(message.getBody(), StandardCharsets.UTF_8.name());
			
			Integer order = JsonPath.read(json,"$.order");
			
			response = response + "\""+ order.intValue() + "\"}";
		
		} catch (UnsupportedEncodingException e) {
			
			logger.error("Unable to parse message body /n" + e.getMessage());
		}
		
		return response;
	}
	
	private void processCorrelationId (Message message, MessageProperties properties) {
	
		if (message.getMessageProperties().getCorrelationId() != null) {
			
			try {
			
				String correlationId = new String(message.getMessageProperties().
						getCorrelationId(), "UTF-8");
				
				logger.info("Message Correlation ID: " + correlationId);
			
			} catch (UnsupportedEncodingException e) {
				logger.error("Unable to process correlation identifer \n" + e.getMessage());
			}
			
			//add the correlation to the reply message properties
			properties.setCorrelationId(message.getMessageProperties().getCorrelationId());
		}
	}
	
	private void processTrackingId (Message message, MessageProperties properties) {
		
		String tid = null;
		
		//log tracking id if it exists
		if (message.getMessageProperties().getHeaders().containsKey(trackingId)) {
			
			tid = (String)message.getMessageProperties().getHeaders().get(trackingId);
			
			logger.info("Existing Tracking ID: " + trackingId + "=" + tid + " ");
		
		} else {
			
			//create new tracking id
			tid = UUID.randomUUID().toString();
			
			logger.info("Generated Tracking ID: " + trackingId + "=" + tid + " ");
		}
		
		//return the tracking id to the caller
		properties.getHeaders().put(trackingId, tid);
	}
	
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
}