package com.turkninja.bigdata.app;

import com.turkninja.bigdata.consumer.TwitterStreamingSparkConsumer;

public class ConsumerApp {

    public static void main(String[] args) {
        TwitterStreamingSparkConsumer.consume();
    }
}
