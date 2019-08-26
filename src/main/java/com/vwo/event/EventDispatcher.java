package com.vwo.event;

import static java.lang.Thread.MIN_PRIORITY;

import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.httpclient.VWOHttpClient;
import com.vwo.logger.LoggerManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;


public class EventDispatcher implements EventHandler, AutoCloseable {


  public static final int DEFAULT_CORE_POOL_SIZE = 2;
  public static final int DEFAULT_MAX_POOL_SIZE = 200;
  public static final int DEFAULT_EVENT_QUEUE = 10000;

  private static final LoggerManager LOGGER = LoggerManager.getLogger(EventDispatcher.class);

  private static final EventResponseHandler EVENT_RESPONSE_HANDLER = new EventResponseHandler();

  private final VWOHttpClient vwoHttpClient;
  private final ExecutorService executorService;

  private final long closeTimeout;
  private final TimeUnit closeTimeoutUnit;


  ThreadFactory eventDispatcherThreadFactory(final String name) {
    return new ThreadFactory() {
      @Override
      public Thread newThread(final Runnable r) {
        return new Thread(new Runnable() {
          @Override
          public void run() {
            Thread.currentThread().setPriority(MIN_PRIORITY);
            r.run();
          }
        }, name);
      }
    };
  }


  private EventDispatcher(int eventQueueSize,
                          int corePoolSize,
                          int maxPoolSize,
                          long closeTimeout,
                          TimeUnit closeTimeoutUnit) {

    eventQueueSize = checkNotNull("eventQueueSize", eventQueueSize, DEFAULT_EVENT_QUEUE);
    corePoolSize = checkNotNull("corePoolSize", corePoolSize, DEFAULT_CORE_POOL_SIZE);
    maxPoolSize = checkNotNull("maxPoolSize", maxPoolSize, DEFAULT_MAX_POOL_SIZE);

    this.vwoHttpClient = VWOHttpClient.Builder.newInstance().build();

    this.executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(eventQueueSize),
            eventDispatcherThreadFactory("event"));

    this.closeTimeout = closeTimeout;
    this.closeTimeoutUnit = closeTimeoutUnit;
  }

  private int checkNotNull(String name, int input, int fallback) {
    if (input <= 0) {
      LOGGER.warn("Invalid value {} for {} . Setting to Default value {}", input, name, fallback);
      return fallback;
    }

    return input;
  }

  @Override
  public void dispatchEvent(DispatchEvent dispatchEvent) {
    try {
      executorService.execute(new HttpEventHandler(dispatchEvent));
    } catch (RejectedExecutionException e) {
      LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.UNABLE_TO_DISPATCH_EVENT.value(), e);
    }
  }

  public void shutdownHook(long timeout, TimeUnit unit) {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(timeout, unit)) {
        int unprocessedCount = executorService.shutdownNow().size();
        if (!executorService.awaitTermination(timeout, unit)) {
          LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CLOSE_EXECUTOR_SERVICE.value());
        }
      }
    } catch (InterruptedException ie) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    } finally {
      try {
        vwoHttpClient.close();
      } catch (IOException e) {
        LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CLOSE_HTTP_CONNECTION.value(), e);
      }
    }
  }

  @Override
  public void close() {
    shutdownHook(closeTimeout, closeTimeoutUnit);
  }

  private class HttpEventHandler implements Runnable {

    private final DispatchEvent dispatchEvent;

    HttpEventHandler(DispatchEvent dispatchEvent) {
      this.dispatchEvent = dispatchEvent;
    }

    @Override
    public void run() {
      try {
        HttpRequestBase request;
        if (dispatchEvent.getRequestMethod() == DispatchEvent.RequestMethod.GET) {
          request = getRequest(dispatchEvent);
          vwoHttpClient.execute(request, EVENT_RESPONSE_HANDLER);

          LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.EVENT_HTTP_EXECUTION.value(new Pair<>("url", request.getURI().toString())));
        } else {
          //TO DO
        }

      } catch (IOException e) {
        LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.EVENT_DISPATCHER_EXCEPTION.value(), e);
      } catch (URISyntaxException e) {
        LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.URI_PARSER_EXCEPTION.value(), e);
      }
    }

    private HttpGet getRequest(DispatchEvent event) throws URISyntaxException {
      URIBuilder builder = new URIBuilder();
      builder.setScheme("https")
              .setHost(event.getHost())
              .setPath(event.getPath());

      for (Map.Entry<String, Object> param : event.getRequestParams().entrySet()) {
        if (param.getValue() != null) {
          builder.addParameter(param.getKey(), param.getValue().toString());
        }
      }
      return new HttpGet(builder.build());
    }
  }


  private static final class EventResponseHandler implements ResponseHandler<Void> {

    @Override
    public Void handleResponse(HttpResponse response) throws IOException {
      int status = response.getStatusLine().getStatusCode();
      if (status >= 200 && status < 300) {
        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.HTTP_RESPONSE.value(new Pair<>("response", response.getStatusLine().toString())));
        return null;
      } else {
        throw new ClientProtocolException("Unexpected response : " + status);
      }
    }
  }


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    int eventQueueSize = DEFAULT_EVENT_QUEUE;
    int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private long closeTimeout = Long.MAX_VALUE;
    private TimeUnit closeTimeoutUnit = TimeUnit.MILLISECONDS;


    public Builder withEventQueueSize(int eventQueueSize) {
      if (eventQueueSize <= 0) {
        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.INVALID_EVENT_QUEUE_SIZE.value(new Pair<>("eventQueueSize", String.valueOf(this.eventQueueSize))));
        return this;
      }
      this.eventQueueSize = eventQueueSize;
      return this;
    }

    public Builder withCorePoolSize(int corePoolSize) {
      if (corePoolSize <= 0) {
        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.INVALID_EVENT_POOL_SIZE.value(new Pair<>("corePoolSize", String.valueOf(this.corePoolSize))));
        return this;
      }

      this.corePoolSize = corePoolSize;
      return this;
    }

    public Builder withMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
      return this;
    }

    public Builder withCloseTimeout(long closeTimeout, TimeUnit unit) {
      this.closeTimeout = closeTimeout;
      this.closeTimeoutUnit = unit;
      return this;
    }

    public EventDispatcher build() {
      return new EventDispatcher(
              eventQueueSize,
              corePoolSize,
              maxPoolSize,
              closeTimeout,
              closeTimeoutUnit
      );
    }
  }
}
