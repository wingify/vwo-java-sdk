package com.vwo.event;

public interface EventHandler {

  void dispatchEvent(DispatchEvent event) throws Exception;
}
