package com.elinext.taskinjection.injection;

public class ProviderImpl<T> implements Provider<T> {

    private final T instance;

    public ProviderImpl(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }
}
