package com.vwo.event;

import com.vwo.logger.LoggerManager;

public class NoopEventHandler implements EventHandler {

    private static final LoggerManager LOGGER = LoggerManager.getLogger(NoopEventHandler.class);


    @Override
    public void dispatchEvent(DispatchEvent event) throws Exception {
       LOGGER.debug("event.NoopEventHandler Dispatch event.Event Called with URL {} and Params {}",event.getHost().concat(event.getPath()),event.getRequestParams());
    }
}
