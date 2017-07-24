package com.jolokia;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;

import javax.jms.*;
import java.util.Enumeration;
import java.util.Random;

@SpringBootApplication
public class JolokiaSpringApplication implements CommandLineRunner{

	@Bean
	public JmsTemplate jmsTemplate(){
		JmsTemplate jmsTemplate=new JmsTemplate();
		jmsTemplate.setConnectionFactory(amqConnectionFactory());
		jmsTemplate.setSessionTransacted(true);
		jmsTemplate.setDeliveryPersistent(true);
		jmsTemplate.setDefaultDestination(new ActiveMQQueue("NotificationTopic"));
		jmsTemplate.setDefaultDestinationName("NotificationTopic");
		return jmsTemplate;
	}

	@Bean
	public ActiveMQConnectionFactory amqConnectionFactory(){
		ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://localhost:61616");
		connectionFactory.setUserName("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setTrustAllPackages(true);
		return connectionFactory;
	}
	public static void main(String[] args) {
		SpringApplication.run(JolokiaSpringApplication.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {

		JmsTemplate jmsTemplate=jmsTemplate();
		jmsTemplate().send(session -> session.createTextMessage("MY Message"));
		Notification notification=new Notification();
		notification.setEventType("Test");
		notification.setLoanNumber(String.valueOf(new Random().nextInt()));
		jmsTemplate().send(session -> session.createObjectMessage(notification));
		int count = jmsTemplate.browse("NotificationTopic", new BrowserCallback<Integer>() {
			public Integer doInJms(final Session session, final QueueBrowser browser) throws JMSException {
				Enumeration enumeration = browser.getEnumeration();
				int counter = 0;
				while (enumeration.hasMoreElements()) {
					Message msg = (Message) enumeration.nextElement();
					if(msg instanceof ActiveMQObjectMessage){
						ActiveMQObjectMessage objectMessage=(ActiveMQObjectMessage)msg;
						System.out.println("Message:"+msg);
						System.out.println("Content:"+objectMessage.getObject());
					}
					else{
						System.out.println(String.format("\tFound : %s", msg));
					}

					counter += 1;
				}
				return counter;
			}
		});

		System.out.println(String.format("\tThere are %s messages", count));

	}
}
