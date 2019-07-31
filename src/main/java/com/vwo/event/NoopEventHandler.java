package com.vwo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopEventHandler implements EventHandler {

    private  static final Logger LOGGER = LoggerFactory.getLogger(NoopEventHandler.class);

    @Override
    public void dispatchEvent(DispatchEvent event) throws Exception {
       LOGGER.debug("event.NoopEventHandler Dispatch event.Event Called with URL {} and Params {}",event.getHost().concat(event.getPath()),event.getRequestParams());
    }
}
