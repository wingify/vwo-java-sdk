/**
 * Copyright 2019-2022 Wingify Software Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vwo.services.http;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClient implements Closeable {
  /**
   * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Closeable.html.
   * Closes this stream and releases any system resources associated
   * with it. If the stream is already closed then invoking this
   * method has no effect.
   *
   * As noted in {@link AutoCloseable#close()}, cases where the
   * close may fail require careful attention. It is strongly advised
   * to relinquish the underlying resources and to internally
   * <em>mark</em> the {@code Closeable} as closed, prior to throwing
   * the {@code IOException}.
   *
   * @throws IOException if an I/O error occurs
   */

  private int MAX_CONNECTIONS = 200;
  private int MAX_CONNECTIONS_PER_ROUTE = 20;

  private final CloseableHttpClient httpClient;

  public HttpClient() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(MAX_CONNECTIONS);
    connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

    this.httpClient = HttpClients.custom().setConnectionManager(connectionManager).disableCookieManagement().build();
  }

  @Override
  public void close() throws IOException {
    this.httpClient.close();
  }

  public CloseableHttpResponse execute(final HttpUriRequest httpRequest) throws IOException {
    return httpClient.execute(httpRequest);
  }

  public <T> T send(final HttpUriRequest httpRequest, final ResponseHandler<T> responseHandler) throws IOException {
    return httpClient.execute(httpRequest, responseHandler);
  }
}
