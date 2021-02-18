package com.elinext.taskinjection.test.testservice;

import com.elinext.taskinjection.injection.Inject;
import com.elinext.taskinjection.test.testdao.EventDAO;

public class EventServiceImpl implements EventService {

    private final EventDAO eventDao;

    @Inject
    public EventServiceImpl(EventDAO eventDao) {
        this.eventDao = eventDao;
    }

    public EventDAO getEventDao() {
        return eventDao;
    }
}
