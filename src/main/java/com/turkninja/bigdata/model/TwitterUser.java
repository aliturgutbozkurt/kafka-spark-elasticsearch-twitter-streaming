package com.turkninja.bigdata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUser {

    @JsonProperty("id_str")
    private String idStr;

    private String name;

    @JsonProperty("screen_name")
    private String screenName;

    private String location;

    @JsonProperty("friends_count")
    private int friendsCount;

    @JsonProperty("followers_count")
    private int followersCount;
}
