package com.vwo.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.URIConstants;
import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.httpclient.VWOHttpClient;
import com.vwo.logger.LoggerManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.util.Pair;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

/**
 * File Setting Utility Class to provide the settingsFile which helps in instantiating the VWO CLIENT INSTANCE.
 */
public class FileSettingUtils {

  private static final LoggerManager LOGGER = LoggerManager.getLogger(FileSettingUtils.class);

  /**
   * Fetches account settings from VWO server.
   *
   * @param accountID VWO application account-id.
   * @param sdkKey    Unique sdk-key provided to you inside VWO Application under the Apps section of server-side A/B Testing
   * @return JSON representation String representing the current state of campaign settings
   */
  public static String getSettingsFile(String accountID, String sdkKey) {
    if (accountID == null || accountID.isEmpty() || sdkKey == null || sdkKey.isEmpty()) {
      LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS.toString());
      return null;
    }

    LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.FETCHING_ACCOUNT_SETTINGS.value(new Pair<>("accountID", accountID)));

    VWOHttpClient vwoHttpClient = VWOHttpClient.Builder.newInstance().build();
    JsonNode jsonNode = null;

    try {
      URI uri = new URIBuilder()
              .setScheme("https")
              .setHost(URIConstants.BASE_URL.toString())
              .setPath(URIConstants.ACCOUNT_SETTINGS.toString())
              .setParameter("a", accountID)
              .setParameter("i", sdkKey)
              .setParameter("r", String.valueOf(Math.random()))
              .setParameter("platform", "server")
              .setParameter("api-verison", "2")
              .build();

      HttpGet httpRequest = new HttpGet(uri);
      httpRequest.setHeader("Content-Type", "application/json");
      httpRequest.setHeader("charset", "UTF-8");

      try (CloseableHttpResponse closeableHttpResponse = vwoHttpClient.execute(httpRequest)) {
        if (closeableHttpResponse != null) {
          try (InputStream content = closeableHttpResponse.getEntity().getContent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readValue(content, JsonNode.class);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
              LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.ACCOUNT_SETTINGS_NOT_FOUND.value(
                      new Pair<>("statusCode", String.valueOf(statusCode)),
                      new Pair<>("message", String.valueOf(jsonNode))
              ));
            }
          }
        }
      }
    } catch (URISyntaxException e) {
      LOGGER.error("Please check URI builder:", e);
    } catch (JsonParseException e) {
      LOGGER.error("Something went wrong:", e);
    } catch (JsonMappingException e) {
      LOGGER.error("Something went wrong:", e);
    } catch (IOException e) {
      LOGGER.error("Something went wrong:", e);
    }

    return String.valueOf(jsonNode);
  }
}
