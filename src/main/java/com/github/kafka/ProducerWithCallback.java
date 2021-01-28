package com.github.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerWithCallback {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ProducerWithCallback.class);


        String bootstrapServers = "127.0.0.1:9092";

        //create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //create a producer record
        for(int i=0;i<10;i++) {
            ProducerRecord record = new ProducerRecord("first_topic", "hello-world");

            //send data - asynchronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //execute every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        //record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing", e);
                    }
                }
            });
        }
        //flush data(because it is asyn)
        producer.flush();
    }
}
