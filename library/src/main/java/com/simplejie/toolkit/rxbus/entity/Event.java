package com.simplejie.toolkit.rxbus.entity;

/**
 * Created by Xingbo.Jie on 29/6/16.
 */
public class Event {
    public Event(int id, Object args) {
        this.id = id;
        this.args = args;
    }
    int id;
    Object args;
}
