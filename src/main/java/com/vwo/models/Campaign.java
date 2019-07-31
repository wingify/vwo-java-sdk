package com.vwo.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "goals",
        "variations",
        "id",
        "percentTraffic",
        "key",
        "status",
        "type"
})
public class Campaign {

    @JsonProperty("goals")
    private List<Goal> goals = null;
    @JsonProperty("variations")
    private List<Variation> variations = null;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("percentTraffic")
    private Integer percentTraffic;
    @JsonProperty("key")
    private String key;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("goals")
    public List<Goal> getGoals() {
        return goals;
    }

    private double currentAllocationVariation = 0;


    @JsonProperty("goals")
    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    @JsonProperty("variations")
    public List<Variation> getVariations() {
        return variations;
    }

    @JsonProperty("variations")
    public void setVariations(List<Variation> variations) {
        this.variations = variations;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("percentTraffic")
    public Integer getPercentTraffic() {
        return percentTraffic;
    }

    @JsonProperty("percentTraffic")
    public void setPercentTraffic(Integer percentTraffic) {
        this.percentTraffic = percentTraffic;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public double getCurrentAllocationVariation() {
        return currentAllocationVariation;
    }

    public void setCurrentAllocationVariation(double currentAllocationVariation) {
        this.currentAllocationVariation = currentAllocationVariation;
    }

}