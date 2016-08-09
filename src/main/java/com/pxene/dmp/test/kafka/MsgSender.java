package com.pxene.dmp.test.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MsgSender {

	public static void main(String[] args) {
		String topic = "flume";
		
		Properties props = new Properties();
		props.put("bootstrap.servers", "dmp04:9092,dmp05:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        producer.send(new ProducerRecord<String, String>(topic, "key", "value"));
        producer.close();
	}
}
