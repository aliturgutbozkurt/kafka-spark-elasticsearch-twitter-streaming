package com.turkninja.bigdata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id_str")
    private String idStr;

    private String text;

    private String lang;

    private TwitterUser user;

    @JsonProperty("retweet_count")
    private int retweetCount;

    @JsonProperty("favorite_count")
    private int favoriteCount;
}
