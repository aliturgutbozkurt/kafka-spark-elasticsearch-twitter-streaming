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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterStreamingKafkaProducer {

    static Logger logger = Logger.getLogger(TwitterStreamingKafkaProducer.class);



    private static KafkaProducer<String, String> producer;

    private static Client client;

    public static void run(String hashtag){

        String topic = hashtag.startsWith("#") ? hashtag.substring(1) : hashtag;

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("client.id", "TwitterStreamingPOC");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(properties);
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(Integer.MAX_VALUE);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Lists.newArrayList("twitterStreaming", hashtag.startsWith("#") ? hashtag : "#" + hashtag));
        Authentication auth;

        try (InputStream input = new FileInputStream("src/main/resources/twitter.properties")){
            Properties twitterProperties = new Properties();
            twitterProperties.load(input);
            String consumerKey = twitterProperties.getProperty("consumer.key");
            String consumerSecret = twitterProperties.getProperty("consumer.secret");
            String token = twitterProperties.getProperty("access.token");
            String secret = twitterProperties.getProperty("access.secret");
            auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        } catch (IOException e) {
            logger.error("Error while reading from twitter.properties");
            return;
        }
        client = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(endpoint).authentication(auth)
                .processor(new StringDelimitedProcessor(queue)).build();

        client.connect();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            try {
                producer.send(new ProducerRecord<>(topic, null, queue.take()));
            } catch (InterruptedException e) {
                logger.error("Error while producing message " + e.getMessage());
            }
        }
        producer.close();
        client.stop();
}
}