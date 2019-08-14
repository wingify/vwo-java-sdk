package com.vwo.httpclient;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class VWOHttpClient implements Closeable {
  /**
   * Closes this stream and releases any system resources associated
   * with it. If the stream is already closed then invoking this
   * method has no effect.
   *
   * <p>As noted in {@link AutoCloseable#close()}, cases where the
   * close may fail require careful attention. It is strongly advised
   * to relinquish the underlying resources and to internally
   * <em>mark</em> the {@code Closeable} as closed, prior to throwing
   * the {@code IOException}.
   *
   * @throws IOException if an I/O error occurs
   */

  private final CloseableHttpClient threadSafeHttpClient;

  private VWOHttpClient(Builder builder) {
    this.threadSafeHttpClient = builder.threadSafeHttpClient;
  }

  @Override
  public void close() throws IOException {
    this.threadSafeHttpClient.close();
  }

  public CloseableHttpResponse execute(final HttpUriRequest httpRequest) throws IOException {
    return threadSafeHttpClient.execute(httpRequest);
  }

  public <T> T execute(final HttpUriRequest httpUriRequest, final ResponseHandler<T> responseHandler) throws IOException {
    return threadSafeHttpClient.execute(httpUriRequest, responseHandler);
  }


  public static class Builder {
    private CloseableHttpClient threadSafeHttpClient;
    private int maxTotalConections = 200;
    private int maxPerRoute = 20;
    private int validateAfterInactivity = 5000;

    public Builder withThreadSafeHttpClient(CloseableHttpClient threadSafeHttpClient) {
      this.threadSafeHttpClient = threadSafeHttpClient;
      return this;
    }

    public Builder withMaxTotalConections(int maxTotalConections) {
      this.maxTotalConections = maxTotalConections;
      return this;
    }

    public Builder withMaxPerRoute(int maxPerRoute) {
      this.maxPerRoute = maxPerRoute;
      return this;
    }

    public Builder withValidateAfterInactivity(int validateAfterInactivity) {
      this.validateAfterInactivity = validateAfterInactivity;
      return this;
    }

    public static Builder newInstance() {
      return new Builder();
    }

    private Builder() {
    }

    public VWOHttpClient build() {
      PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
      poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
      poolingHttpClientConnectionManager.setMaxTotal(maxTotalConections);
      poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
      poolingHttpClientConnectionManager.setValidateAfterInactivity(validateAfterInactivity);


      //build the client
      this.threadSafeHttpClient = HttpClients.custom()
              .setConnectionManager(poolingHttpClientConnectionManager)
              .disableCookieManagement()
              .build();

      return new VWOHttpClient(this);
    }
  }

}
