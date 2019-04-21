package com.turkninja.bigdata.app;

import com.turkninja.bigdata.producer.TwitterStreamingKafkaProducer;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProducerApp {

    public static void main(String[] args) {
        TwitterStreamingKafkaProducer.run("istanbul");
    }
}
