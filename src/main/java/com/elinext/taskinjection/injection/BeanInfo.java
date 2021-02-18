package com.elinext.taskinjection.injection;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class BeanInfo<T> {

    private final Constructor<T> constructor;
    private final boolean isSingleton;

    public BeanInfo(Constructor<T> constructor, boolean isSingleton) {
        this.constructor = constructor;
        this.isSingleton = isSingleton;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanInfo<?> beanInfo = (BeanInfo<?>) o;
        return isSingleton == beanInfo.isSingleton && Objects.equals(constructor, beanInfo.constructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, isSingleton);
    }
}
