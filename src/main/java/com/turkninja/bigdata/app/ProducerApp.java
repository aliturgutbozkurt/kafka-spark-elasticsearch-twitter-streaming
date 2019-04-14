package com.turkninja.bigdata.app;

import com.turkninja.bigdata.producer.TwitterStreamingKafkaProducer;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProducerApp {

    static Logger logger = Logger.getLogger(ProducerApp.class);

    public static void main(String[] args) {
        try (InputStream input = new FileInputStream("src/main/resources/twitter.properties")) {

            Properties properties = new Properties();
            properties.load(input);

            String consumerKey = properties.getProperty("consumer.key");
            String consumerSecret = properties.getProperty("consumer.secret");
            String token = properties.getProperty("access.token");
            String secret = properties.getProperty("access.secret");
            TwitterStreamingKafkaProducer.run(consumerKey, consumerSecret, token, secret);
        } catch (IOException ex) {
            logger.error("Error while reading property file");
        }
    }
}
