package com.vwo.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.URIConstants;

import com.vwo.httpclient.VWOHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * File Setting Utility Class to provide the settingsFile which helps in instantiating the VWO CLIENT INSTANCE
 */
public class FileSettingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSettingUtils.class);

    /**
     *
     * @param accountID   VWO application account-id.
     * @param sdkKey Unique sdk-key provided to you inside VWO Application under the Apps section of server-side A/B Testing
     * @return JSON representation String representing the current state of campaign settings
     */
    public static String getSetting(String accountID, String sdkKey) {

        if(accountID!=null && !accountID.isEmpty() && sdkKey!=null && !sdkKey.isEmpty()){
           LOGGER.debug("SDK key and account settings are valid.");
        }

        CloseableHttpResponse closeableHttpResponse = null;
        VWOHttpClient vwoHttpClient = VWOHttpClient.Builder.newInstance().build();
        InputStream content = null;
        JsonNode jsonNode = null;

        String path = "https://" + URIConstants.BASE_URL.toString() + URIConstants.ACCOUNT_SETTINGS.toString()
                + "?a=" + accountID + "&i=" + sdkKey + "&r=" + Math.random() + "&platform=server&api-version=2";

        URI uri = null;
        try {
            uri = new URIBuilder()
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
            httpRequest.setHeader("Content-Type", "application/javascript");
            httpRequest.setHeader("charset", "UTF-8");

            closeableHttpResponse = vwoHttpClient.execute(httpRequest);

            if (closeableHttpResponse != null) {
                content = closeableHttpResponse.getEntity().getContent();
                ObjectMapper objectMapper = new ObjectMapper();
                jsonNode = objectMapper.readValue(content, JsonNode.class);
            }
        } catch (URISyntaxException e) {
           LOGGER.error("Please check URI builder:", e);
        } catch (JsonParseException e) {
            LOGGER.error("Something went wrong:", e);
        } catch (JsonMappingException e) {
            LOGGER.error("Something went wrong:", e);
        } catch (IOException e) {
            LOGGER.error("Something went wrong:", e);
        } finally {
            try {
                content.close();
                closeableHttpResponse.close();
            } catch (IOException e) {
               LOGGER.error("ERROR in fetching Setting File",e);
            }
        }

        return String.valueOf(jsonNode);
    }
}
