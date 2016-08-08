package com.pxene.dmp.test.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

public class SendMsg {

	public static void main(String[] args) {
		String topic = "flume";
		
		Properties props = new Properties();
		props.put("metadata.broker.list", "dmp04:9092,dmp05:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        
	}
}
