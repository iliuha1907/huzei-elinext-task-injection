package com.elinext.taskinjection.test;

import com.elinext.taskinjection.exception.BindingNotFoundException;
import com.elinext.taskinjection.exception.ConstructorNotFoundException;
import com.elinext.taskinjection.exception.TooManyConstructorsException;
import com.elinext.taskinjection.injection.Injector;
import com.elinext.taskinjection.injection.InjectorImpl;
import com.elinext.taskinjection.injection.Provider;
import com.elinext.taskinjection.test.testdao.EventDAO;
import com.elinext.taskinjection.test.testdao.EventDAOImpl;
import com.elinext.taskinjection.test.testservice.EventService;
import com.elinext.taskinjection.test.testservice.EventServiceImpl;
import com.elinext.taskinjection.test.testservice.NoPassingConstructorService;
import com.elinext.taskinjection.test.testservice.TooManyConstructorsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InjectorImplTest {

    @Test
    void testExistingBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(EventDAO.class, EventDAOImpl.class);
        Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
        Assertions.assertNotNull(daoProvider);
        Assertions.assertNotNull(daoProvider.getInstance());
        Assertions.assertSame(EventDAOImpl.class, daoProvider.getInstance().getClass());
    }

    @Test
    void testCorrectSingletons() {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(EventDAO.class, EventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);
        Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
        Provider<EventService> serviceProvider = injector.getProvider(EventService.class);
        EventService eventService = serviceProvider.getInstance();
        EventDAO eventDao = daoProvider.getInstance();
        Assertions.assertNotNull(daoProvider);
        Assertions.assertNotNull(serviceProvider);
        Assertions.assertNotNull(eventDao);
        Assertions.assertNotNull(eventService);
        Assertions.assertSame(EventDAOImpl.class, eventDao.getClass());
        Assertions.assertSame(EventServiceImpl.class, eventService.getClass());
        Assertions.assertSame(eventDao, ((EventServiceImpl) eventService).getEventDao());
    }

    @Test
    void testCorrectPrototypes() {
        Injector injector = new InjectorImpl();
        injector.bind(EventDAO.class, EventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);
        Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
        Provider<EventService> serviceProvider = injector.getProvider(EventService.class);
        EventService eventService = serviceProvider.getInstance();
        EventDAO eventDao = daoProvider.getInstance();
        Assertions.assertNotNull(daoProvider);
        Assertions.assertNotNull(serviceProvider);
        Assertions.assertNotNull(eventDao);
        Assertions.assertNotNull(eventService);
        Assertions.assertSame(EventDAOImpl.class, eventDao.getClass());
        Assertions.assertSame(EventServiceImpl.class, eventService.getClass());
        Assertions.assertNotSame(eventDao, ((EventServiceImpl) eventService).getEventDao());
    }

    @Test
    void testNoBinding() {
        Injector injector = new InjectorImpl();
        Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
        Assertions.assertNull(daoProvider);
    }

    @Test
    void testNoBindingAtInject() {
        Injector injector = new InjectorImpl();
        injector.bind(EventService.class, EventServiceImpl.class);
        BindingNotFoundException thrown = Assertions.assertThrows(BindingNotFoundException.class,
                () -> injector.getProvider(EventService.class));
        Assertions.assertTrue(thrown.getMessage().contains("At injecting of class"
                + " com.elinext.taskinjection.test.testservice.EventServiceImpl no binding for interface"
                + " com.elinext.taskinjection.test.testdao.EventDAO found"));
    }

    @Test
    void testNoPassingConstructor() {
        Injector injector = new InjectorImpl();
        ConstructorNotFoundException thrown = Assertions.assertThrows(ConstructorNotFoundException.class,
                () -> injector.bind(EventService.class, NoPassingConstructorService.class));
        Assertions.assertTrue(thrown.getMessage().contains("Class should contain rather default or 1"
                + " annotated constructor!"));
    }

    @Test
    void testTooManyConstructors() {
        Injector injector = new InjectorImpl();
        TooManyConstructorsException thrown = Assertions.assertThrows(TooManyConstructorsException.class,
                () -> injector.bind(EventService.class, TooManyConstructorsService.class));
        Assertions.assertTrue(thrown.getMessage().contains("Class should not contain more than 1 annotated constructor!"));
    }
}
