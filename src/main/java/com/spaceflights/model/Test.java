package com.spaceflights.model;

public class Test {
    private int id;
    private String topic;

    public Test() {}

    public Test(int id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    public Test(String topic) {
        this(0, topic);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    @Override
    public String toString() {
        return String.format("Test{id=%d, topic='%s'}", id, topic);
    }
}
