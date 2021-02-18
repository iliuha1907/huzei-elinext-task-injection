package com.elinext.taskinjection.injection;

import com.elinext.taskinjection.exception.BindingNotFoundException;
import com.elinext.taskinjection.exception.ConstructorNotFoundException;
import com.elinext.taskinjection.exception.IncorrectInitializationException;
import com.elinext.taskinjection.exception.TooManyConstructorsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectorImpl implements Injector {

    private final Map<Class<?>, BeanInfo<?>> bindings;
    private final Map<Class<?>, Object> singletonInstances;

    public InjectorImpl() {
        bindings = new ConcurrentHashMap<>();
        singletonInstances = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T> Provider<T> getProvider(Class<T> type) {
        T instance = (T) singletonInstances.get(type);
        if (instance == null) {
            BeanInfo<T> beanInfo = (BeanInfo<T>) bindings.get(type);
            if (beanInfo == null) {
                return null;
            }
            Constructor<T> constructor = beanInfo.getConstructor();
            instance = createInstance(constructor);
            if (beanInfo.isSingleton()) {
                singletonInstances.put(type, instance);
            }
        }
        return new ProviderImpl<>(instance);
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        bindings.putIfAbsent(intf, new BeanInfo<>(findAppropriateConstructor(impl), false));
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        bindings.putIfAbsent(intf, new BeanInfo<>(findAppropriateConstructor(impl), true));
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findAppropriateConstructor(Class<T> type) {
        Constructor<T> defaultConstructor = null;
        Constructor<T> annotatedConstructor = null;
        Constructor<T>[] constructors = (Constructor<T>[]) type.getConstructors();
        for (Constructor<T> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                if (annotatedConstructor == null) {
                    annotatedConstructor = constructor;
                } else {
                    throw new TooManyConstructorsException("Class should not contain more than 1 annotated constructor!");
                }
            } else if (defaultConstructor == null && constructor.getParameters().length == 0) {
                defaultConstructor = constructor;
            }
        }
        if (defaultConstructor == null && annotatedConstructor == null) {
            throw new ConstructorNotFoundException("Class should contain rather default or 1 annotated constructor!");
        }
        return annotatedConstructor == null ? defaultConstructor : annotatedConstructor;
    }

    private <T> T createInstance(Constructor<T> constructor) {
        Class<?> parentType = constructor.getDeclaringClass();
        Parameter[] arguments = constructor.getParameters();
        List<Object> argumentInstances = new ArrayList<>();
        for (Parameter argument : arguments) {
            Class<?> argumentType = argument.getType();
            Object argumentInstance = singletonInstances.get(argumentType);
            if (argumentInstance == null) {
                BeanInfo<?> argumentBeanInfo = bindings.get(argumentType);
                if (argumentBeanInfo == null) {
                    throw new BindingNotFoundException("At injecting of " + parentType + " no binding for " + argumentType
                            + " found");
                }
                Constructor<?> argumentConstructor = argumentBeanInfo.getConstructor();
                argumentInstance = createInstance(argumentConstructor);
                if (argumentBeanInfo.isSingleton()) {
                    singletonInstances.put(argumentType, argumentInstance);
                }
            }
            argumentInstances.add(argumentInstance);
        }
        try {
            return constructor.newInstance(argumentInstances.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IncorrectInitializationException("Error at initialization of " + parentType, ex);
        }
    }
}
