package com.elinext.taskinjection.test.testservice;

import com.elinext.taskinjection.injection.Inject;
import com.elinext.taskinjection.test.testdao.EventDAO;

public class TooManyConstructorsService implements EventService {

    @Inject
    public TooManyConstructorsService(EventDAO eventDAO) {
    }

    @Inject
    public TooManyConstructorsService(NoPassingConstructorService service) {
    }
}
