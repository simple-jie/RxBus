package com.simplejie.toolkit.rxbus.entity;

import com.simplejie.toolkit.rxbus.PostingThread;
import com.simplejie.toolkit.rxbus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * Created by Xingbo.Jie on 29/6/16.
 */
public class SubscriberMethod {

    private LinkedList<MethodWrap> methodWraps;
    private Object subscriber;

    private SubscriberMethod(Object object) {
        methodWraps = new LinkedList<>();
        subscriber = object;
    }

    public static SubscriberMethod create(Object object) {
        if (object == null)
            return null;

        SubscriberMethod result = null;

        Class<?> objectClass = object.getClass();
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null && subscribe.eventId().length > 0) { //the method is a subscribe method and the id be set
                Class[] types = method.getParameterTypes();
                if (types != null && types.length == 2 && (int.class.equals(types[0]) || Integer.class.equals(types[0]))) { //only two parameter be accepted
                    if (result == null)
                        result = new SubscriberMethod(object);

                    result.addMethod(method, types[1], subscribe.eventId(), subscribe.thread());
                }
            }
        }

        return result;
    }

    public void register(Subject bus) {
        for (MethodWrap methodWrap : methodWraps) {
            for (int event : methodWrap.eventIds) {
                Subscription subscription = subscribe(postThread(toObservable(bus, event, methodWrap.parameterType), methodWrap.thread), methodWrap.method, event);
                methodWrap.subscriptions.add(subscription);
            }
        }
    }

    Subscription subscribe(Observable observable, final Method method, final int event) {
        return observable.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                try {
                    method.invoke(subscriber, event, o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Observable toObservable(Subject bus, final int eventId, final Class eventType) {
        return bus.ofType(Event.class)
                .filter(new Func1<Event, Boolean>() {
                    @Override
                    public Boolean call(Event event) {
                        return eventId == event.id && eventType.isInstance(event.args);
                    }
                })
                .map(new Func1<Event, Object>() {
                    @Override
                    public Object call(Event o) {
                        return o.args;
                    }
                })
                .cast(eventType);
    }

    Observable postThread(Observable observable, PostingThread thread) {
        switch (thread) {
            case COMPUTATION:
                return observable.observeOn(Schedulers.computation());
            case IO:
                return observable.observeOn(Schedulers.io());
            case CURRENT:
                return observable.observeOn(Schedulers.immediate());
            case MAIN:
                return observable.observeOn(AndroidSchedulers.mainThread());
            case NEW_THEAD:
                return observable.observeOn(Schedulers.newThread());
        }

        return observable;
    }

    public void unRegister() {
        for (MethodWrap methodWrap : methodWraps) {
            for (Subscription subscription : methodWrap.subscriptions) {
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        }
    }

    private void addMethod(Method method, Class parameterType, int[] eventIds, PostingThread thread) {
        methodWraps.add(new MethodWrap(method, parameterType, eventIds, thread));
    }

    private class MethodWrap {
        MethodWrap(Method method, Class parameterType, int[] eventIds, PostingThread thread) {
            this.method = method;
            this.parameterType = parameterType;
            this.eventIds = eventIds;
            this.thread = thread;
            this.subscriptions = new LinkedList<>();
        }

        Method method;
        Class parameterType;
        int[] eventIds;
        PostingThread thread;
        LinkedList<Subscription> subscriptions;
    }
}
