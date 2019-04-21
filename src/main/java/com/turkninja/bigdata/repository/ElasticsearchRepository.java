package com.turkninja.bigdata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkninja.bigdata.model.Tweet;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticsearchRepository {


    private Client client;

    public ElasticsearchRepository(){
        try {
            client = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "turkninja").put("node.name", "node-1").build())
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public void save(String tweetRawData, String index, String key){

        ObjectMapper mapper = new ObjectMapper();

        try {
            Tweet tweet = mapper.readValue(tweetRawData, Tweet.class);
            IndexResponse response = client.prepareIndex(index, key)
                    .setSource( mapper.writeValueAsBytes(tweet), XContentType.JSON)
                            .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
