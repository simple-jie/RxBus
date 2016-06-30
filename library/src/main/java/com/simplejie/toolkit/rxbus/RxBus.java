package com.simplejie.toolkit.rxbus;


import com.simplejie.toolkit.rxbus.entity.Event;
import com.simplejie.toolkit.rxbus.entity.SubscriberMethod;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Xingbo.Jie on 29/6/16.
 */
public class RxBus {
    private static RxBus _default;

    private final Subject bus;

    private ReentrantLock lock;

    private HashMap<Object, SubscriberMethod> subscriber2SubscriberMethod;

    public RxBus() {
        bus = PublishSubject.create().toSerialized();
        lock = new ReentrantLock();
        subscriber2SubscriberMethod = new HashMap<>();
    }

    public static RxBus getDefault() {
        if (_default == null) {
            synchronized (RxBus.class) {
                if (_default == null) {
                    _default = new RxBus();
                }
            }
        }

        return _default;
    }

    public void register(Object object) {
        lock.lock();

        try {
            if (!subscriber2SubscriberMethod.containsKey(object)) {
                SubscriberMethod subscriberMethod = SubscriberMethod.create(object);
                if (subscriberMethod != null) {
                    subscriberMethod.register(this.bus);
                    subscriber2SubscriberMethod.put(object, subscriberMethod);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void unRegister(Object object) {
        lock.lock();
        try {
            SubscriberMethod subscriberMethod = subscriber2SubscriberMethod.remove(object);
            if (subscriberMethod != null) {
                subscriberMethod.unRegister();
            }
        } finally {
            lock.unlock();
        }
    }

    public void pubishArray(int event, Object... objects) {
        bus.onNext(new Event(event, objects));
    }

    public void pubish(int event, Object object) {
        bus.onNext(new Event(event, object));
    }
}
