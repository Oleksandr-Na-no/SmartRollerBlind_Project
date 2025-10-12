package com.example.smart_roller_blind;

public class Publish {
    private String topic;
    private String payload;
    private Object_model object_model;

    public Publish(String topic, String payload, Object_model object_model) {
        this.topic = topic;
        this.payload = payload;
        this.object_model = object_model;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Object_model getObject_model() {
        return object_model;
    }

    public void setObject_model(Object_model object_model) {
        this.object_model = object_model;
    }
}
