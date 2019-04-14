package com.turkninja.bigdata.producer;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterStreamingKafkaProducer{

    static Logger logger = Logger.getLogger(TwitterStreamingKafkaProducer.class);

    private static final String topic = "bigdata-twitter";

    public static void run(String consumerKey, String consumerSecret,
                           String token, String secret) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("client.id", "TwitterStreamingPOC");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        endpoint.trackTerms(Lists.newArrayList("twitterStreaming", "#bigdata"));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        Client client = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(endpoint).authentication(auth)
                .processor(new StringDelimitedProcessor(queue)).build();

        client.connect();

        for (int msgRead = 0; msgRead < 10000; msgRead++) {
            try {
                producer.send(new ProducerRecord<String, String>(topic, null, queue.take()));
            } catch (InterruptedException e) {
                logger.error("Error while sending record to Kafka");
            }
        }

        producer.close();
        client.stop();
    }
}