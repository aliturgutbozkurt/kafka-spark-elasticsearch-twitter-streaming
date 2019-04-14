package com.turkninja.bigdata.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TwitterStreamingSparkConsumer {

    static Logger logger = Logger.getLogger(TwitterStreamingSparkConsumer.class);

    private static Map<String, Object> kafkaParams = new HashMap<>();

    private static Collection<String> topics = Arrays.asList("bigdata-twitter");

    static PairFunction<ConsumerRecord<String, String>, String, String> pairFunction = new  PairFunction<ConsumerRecord<String, String>, String, String>(){
        @Override
        public Tuple2<String, String> call(ConsumerRecord<String, String> record) throws Exception {
            return new Tuple2<>(record.key(), record.value());
        }
    };

    static {
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("group.id", "TwitterStreamingPOC");
    }


    public static void consume() {
        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("TwitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));

        final JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );

        JavaPairDStream<String, String> jPairDStream =  stream.mapToPair(pairFunction);

        jPairDStream.foreachRDD(jPairRDD -> {
            jPairRDD.foreach(rdd -> {
                System.out.println("value= "+rdd._2());
            });
        });

        streamingContext.start();
        try {
            streamingContext.awaitTermination();
        } catch (InterruptedException e) {
            logger.error("Error while streaming context await termination");
        }

    }
}
