package com.vwo.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "changes",
        "weight"
})
public class Variation {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("changes")
    private Changes changes;
    @JsonProperty("weight")
    private double weight;
    private Integer startRangeVariation;
    private Integer endRangeVariation;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("changes")
    public Changes getChanges() {
        return changes;
    }

    @JsonProperty("changes")
    public void setChanges(Changes changes) {
        this.changes = changes;
    }

    @JsonProperty("weight")
    public double getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Integer getStartRangeVariation() {
        return startRangeVariation;
    }

    public void setStartRangeVariation(Integer startRangeVariation) {
        this.startRangeVariation = startRangeVariation;
    }

    public Integer getEndRangeVariation() {
        return endRangeVariation;
    }

    public void setEndRangeVariation(Integer endRangeVariation) {
        this.endRangeVariation = endRangeVariation;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}