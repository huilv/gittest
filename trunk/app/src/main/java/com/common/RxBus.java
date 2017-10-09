package com.common;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by hsg on 12/20/16.
 * http://www.jianshu.com/p/ca090f6e2fe2/
 */

public class RxBus {
    private static volatile RxBus singletonInstance;
    private final Subject<Object, Object> rxBus;

    public RxBus() {
        //PublishSubject只会把订阅发生的时间点之后来自原始Observable的数据发送给观察者
        this.rxBus = new SerializedSubject<>(PublishSubject.create());
    }

    // singleton
    public static RxBus getSingletonInstance() {
        RxBus tmpRxBus = singletonInstance;
        if (singletonInstance == null) {
            synchronized (RxBus.class) {
                tmpRxBus = singletonInstance;
                if (singletonInstance == null) {
                    tmpRxBus = new RxBus();
                    singletonInstance = tmpRxBus;
                }
            }
        }
        return tmpRxBus;
    }

    public void send(Object o) {
        this.rxBus.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> evenType) {
        return this.rxBus.ofType(evenType);
    }

    public boolean hasObservers() {
        return this.rxBus.hasObservers();
    }
}
