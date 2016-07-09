package ws.cogito.magic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="amqp")
public final class AmqpProducerProperties {

	//connections
	private String host;
	private String vHost;
	private String userName;
	private String userPassword;
	
	//exchanges
	private String directExchange;		
	
	//queues
	private String priorityQueue;
	
	//bindings
	private String priorityQueueBinding;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getvHost() {
		return vHost;
	}
	public void setvHost(String vHost) {
		this.vHost = vHost;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getDirectExchange() {
		return directExchange;
	}
	public void setDirectExchange(String directExchange) {
		this.directExchange = directExchange;
	}
	public String getPriorityQueue() {
		return priorityQueue;
	}
	public void setPriorityQueue(String priorityQueue) {
		this.priorityQueue = priorityQueue;
	}
	public String getPriorityQueueBinding() {
		return priorityQueueBinding;
	}
	public void setPriorityQueueBinding(String priorityQueueBinding) {
		this.priorityQueueBinding = priorityQueueBinding;
	}
}