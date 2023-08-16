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

package com.vwo.tests.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.vwo.VWO;
import com.vwo.enums.APIEnums;
import com.vwo.enums.EventArchEnums;
import com.vwo.models.response.BatchEventData;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Goal;
import com.vwo.models.response.Variation;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.services.settings.SettingFile;
import com.vwo.services.settings.SettingsFileUtil;
import com.vwo.tests.data.Settings;
import org.junit.jupiter.api.Test;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class RequestBuilderTests {

  @Test
  public void trackUserPayloadTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();

    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackUserPayload(settings, "Ashley", 20, 3, null, null);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, EventArchEnums.VWO_VARIATION_SHOWN.toString(), payload, new HashMap<String, Integer>() {{
      put("_l", 1);
      put("cl", 1);
      put("ll", 1);
    }}, null, null);

    assertNotNull(queryParams.getQueryParams().get("a"));
    assertNotNull(queryParams.getQueryParams().get("en"));
    assertNotNull(queryParams.getQueryParams().get("eTime"));
    assertNotNull(queryParams.getQueryParams().get("random"));
    assertNotNull(queryParams.getQueryParams().get("env"));
    assertNotNull(queryParams.getQueryParams().get("_l"));
    assertNotNull(queryParams.getQueryParams().get("cl"));
    assertNotNull(queryParams.getQueryParams().get("ll"));

    assertTrue(queryParams.getQueryParams().get("a") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("en") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("eTime") instanceof  Long);
    assertTrue(queryParams.getQueryParams().get("random") instanceof  Double);
    assertTrue(queryParams.getQueryParams().get("env") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("_l") instanceof  Integer);
    assertTrue(queryParams.getQueryParams().get("cl") instanceof  Integer);
    assertTrue(queryParams.getQueryParams().get("ll") instanceof  Integer);

    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());

    assertTrue(node.has("d") && node.get("d").getNodeType() == JsonNodeType.OBJECT);
    assertTrue(node.get("d").has("msgId") && node.get("d").get("msgId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("visId") && node.get("d").get("visId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("sessionId") && node.get("d").get("sessionId").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").has("visitor") && node.get("d").get("visitor").getNodeType() == JsonNodeType.OBJECT);

    //assertTrue(node.get("d").get("visitor").get("props").has("vwo_fs_environment") && node.get("d").get("visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);

    assertTrue(node.get("d").get("event").has("name") && node.get("d").get("event").get("name").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").has("time") && node.get("d").get("event").get("time").getNodeType() == JsonNodeType.NUMBER);

    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkName") && node.get("d").get("event").get("props").get("vwo_sdkName").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkVersion") && node.get("d").get("event").get("props").get("vwo_sdkVersion").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("id") && node.get("d").get("event").get("props").get("id").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").get("event").get("props").has("variation") && node.get("d").get("event").get("props").get("variation").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").get("event").get("props").has("isFirst") && node.get("d").get("event").get("props").get("isFirst").getNodeType() == JsonNodeType.NUMBER);
    //assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("vwo_fs_environment") && node.get("d").get("event").get("props").get("$visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
  }

  @Test
  public void trackGoalPayloadTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();

    Map<String, Integer> metricMap = new HashMap<String, Integer>() {{
      put("20", 20);
      put("10", 30);
      put("50", 40);
    }};
    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackGoalPayload(settings, "Ashley", metricMap, "goalIdentifier", 300, new HashSet<String>(){{add("revenue");}},new HashMap<>(), null, null);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, "goalIdentifier", payload, null, null, null);

    assertNotNull(queryParams.getQueryParams().get("a"));
    assertNotNull(queryParams.getQueryParams().get("en"));
    assertNotNull(queryParams.getQueryParams().get("eTime"));
    assertNotNull(queryParams.getQueryParams().get("random"));
    assertNotNull(queryParams.getQueryParams().get("env"));

    assertTrue(queryParams.getQueryParams().get("a") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("en") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("eTime") instanceof  Long);
    assertTrue(queryParams.getQueryParams().get("random") instanceof  Double);
    assertTrue(queryParams.getQueryParams().get("env") instanceof  String);

    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());

    assertTrue(node.has("d") && node.get("d").getNodeType() == JsonNodeType.OBJECT);
    assertTrue(node.get("d").has("msgId") && node.get("d").get("msgId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("visId") && node.get("d").get("visId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("sessionId") && node.get("d").get("sessionId").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").has("visitor") && node.get("d").get("visitor").getNodeType() == JsonNodeType.OBJECT);

    assertTrue(node.get("d").get("visitor").get("props").has("vwo_fs_environment") && node.get("d").get("visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);

    assertTrue(node.get("d").get("event").has("name") && node.get("d").get("event").get("name").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").has("time") && node.get("d").get("event").get("time").getNodeType() == JsonNodeType.NUMBER);

    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkName") && node.get("d").get("event").get("props").get("vwo_sdkName").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkVersion") && node.get("d").get("event").get("props").get("vwo_sdkVersion").getNodeType() == JsonNodeType.STRING);
    //assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("vwo_fs_environment") && node.get("d").get("event").get("props").get("$visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
    //assertTrue(node.get("d").get("event").get("props").get("vwoMeta").has("revenue") && node.get("d").get("event").get("props").get("vwoMeta").get("revenue") .getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").get("event").get("props").get("vwoMeta").get("metric").has("id_20") && node.get("d").get("event").get("props").get("vwoMeta").get("metric").get("id_20").getNodeType() == JsonNodeType.ARRAY);
    assertTrue(node.get("d").get("event").get("props").has("customEvent") && node.get("d").get("event").get("props").get("customEvent") .getNodeType() == JsonNodeType.BOOLEAN);
  }

  @Test
  public void pushPayloadTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();

    Map<String, Object> payload = HttpRequestBuilder.getEventArchPushPayload(settings, "Ashley",  new HashMap<String, String>() {{put("tagKey", "TagValue");}});
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, EventArchEnums.VWO_SYN_VISITOR_PROP.toString(), payload, null, null, null);

    assertNotNull(queryParams.getQueryParams().get("a"));
    assertNotNull(queryParams.getQueryParams().get("en"));
    assertNotNull(queryParams.getQueryParams().get("eTime"));
    assertNotNull(queryParams.getQueryParams().get("random"));
    assertNotNull(queryParams.getQueryParams().get("env"));

    assertTrue(queryParams.getQueryParams().get("a") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("en") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("eTime") instanceof  Long);
    assertTrue(queryParams.getQueryParams().get("random") instanceof  Double);
    assertTrue(queryParams.getQueryParams().get("env") instanceof  String);

    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());

    assertTrue(node.has("d") && node.get("d").getNodeType() == JsonNodeType.OBJECT);
    assertTrue(node.get("d").has("msgId") && node.get("d").get("msgId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("visId") && node.get("d").get("visId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("sessionId") && node.get("d").get("sessionId").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").has("visitor") && node.get("d").get("visitor").getNodeType() == JsonNodeType.OBJECT);

    assertTrue(node.get("d").get("visitor").get("props").has("vwo_fs_environment") && node.get("d").get("visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("visitor").get("props").has("tagKey") && node.get("d").get("visitor").get("props").get("tagKey").getNodeType() == JsonNodeType.STRING);

    assertTrue(node.get("d").get("event").has("name") && node.get("d").get("event").get("name").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").has("time") && node.get("d").get("event").get("time").getNodeType() == JsonNodeType.NUMBER);

    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkName") && node.get("d").get("event").get("props").get("vwo_sdkName").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkVersion") && node.get("d").get("event").get("props").get("vwo_sdkVersion").getNodeType() == JsonNodeType.STRING);
    //assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("vwo_fs_environment") && node.get("d").get("event").get("props").get("$visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
    //assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("tagKey") && node.get("d").get("event").get("props").get("$visitor").get("props").get("tagKey").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("customEvent") && node.get("d").get("event").get("props").get("customEvent") .getNodeType() == JsonNodeType.BOOLEAN);
  }

  @Test
  public void pushPayloadWithMultipleCDTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();

    Map<String, Object> payload = HttpRequestBuilder.getEventArchPushPayload(settings, "Ashley",  new HashMap<String, String>() {{
      put("string", "string");
      put("int", "20");
      put("double", "20.34");
      put("boolean", "false");
    }});
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, EventArchEnums.VWO_SYN_VISITOR_PROP.toString(), payload, null, null, null);

    assertNotNull(queryParams.getQueryParams().get("a"));
    assertNotNull(queryParams.getQueryParams().get("en"));
    assertNotNull(queryParams.getQueryParams().get("eTime"));
    assertNotNull(queryParams.getQueryParams().get("random"));
    assertNotNull(queryParams.getQueryParams().get("env"));

    assertTrue(queryParams.getQueryParams().get("a") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("en") instanceof  String);
    assertTrue(queryParams.getQueryParams().get("eTime") instanceof  Long);
    assertTrue(queryParams.getQueryParams().get("random") instanceof  Double);
    assertTrue(queryParams.getQueryParams().get("env") instanceof  String);

    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());

    assertTrue(node.has("d") && node.get("d").getNodeType() == JsonNodeType.OBJECT);
    assertTrue(node.get("d").has("msgId") && node.get("d").get("msgId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("visId") && node.get("d").get("visId").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").has("sessionId") && node.get("d").get("sessionId").getNodeType() == JsonNodeType.NUMBER);
    assertTrue(node.get("d").has("visitor") && node.get("d").get("visitor").getNodeType() == JsonNodeType.OBJECT);

    assertTrue(node.get("d").get("visitor").get("props").has("vwo_fs_environment") && node.get("d").get("visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("visitor").get("props").has("string") && node.get("d").get("visitor").get("props").get("string").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("visitor").get("props").has("int") && node.get("d").get("visitor").get("props").get("int").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("visitor").get("props").has("double") && node.get("d").get("visitor").get("props").get("double").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("visitor").get("props").has("boolean") && node.get("d").get("visitor").get("props").get("double").getNodeType() == JsonNodeType.STRING);
    assertFalse(node.get("d").get("visitor").get("props").has("tagKey"));

    assertTrue(node.get("d").get("event").has("name") && node.get("d").get("event").get("name").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").has("time") && node.get("d").get("event").get("time").getNodeType() == JsonNodeType.NUMBER);

    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkName") && node.get("d").get("event").get("props").get("vwo_sdkName").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("vwo_sdkVersion") && node.get("d").get("event").get("props").get("vwo_sdkVersion").getNodeType() == JsonNodeType.STRING);
   // assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("vwo_fs_environment") && node.get("d").get("event").get("props").get("$visitor").get("props").get("vwo_fs_environment").getNodeType() == JsonNodeType.STRING);
   // assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("string") && node.get("d").get("event").get("props").get("$visitor").get("props").get("string").getNodeType() == JsonNodeType.STRING);
   // assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("int") && node.get("d").get("event").get("props").get("$visitor").get("props").get("int").getNodeType() == JsonNodeType.STRING);
   // assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("double") && node.get("d").get("event").get("props").get("$visitor").get("props").get("double").getNodeType() == JsonNodeType.STRING);
   // assertTrue(node.get("d").get("event").get("props").get("$visitor").get("props").has("boolean") && node.get("d").get("event").get("props").get("$visitor").get("props").get("boolean").getNodeType() == JsonNodeType.STRING);
    assertTrue(node.get("d").get("event").get("props").has("customEvent") && node.get("d").get("event").get("props").get("customEvent") .getNodeType() == JsonNodeType.BOOLEAN);
  }

  @Test
  public void locationDataTests() throws Exception {
    BatchEventData batchEventData = new BatchEventData();
    batchEventData.setEventsPerRequest(50);
    VWO vwoInstance = VWO.launch(Settings.LOCATION_DATA).withBatchEvents(batchEventData).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    Goal goal = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getGoals().get(0);
    int accountId = vwoInstance.getSettingFile().getSettings().getAccountId();
    String apiKey = vwoInstance.getSettingFile().getSettings().getSdkKey();
    Map<String, Integer> usageStats = new HashMap<String, Integer>();
    usageStats.put("cl", 1);
    usageStats.put("ll", 1);

    try {
      HttpParams httpParams = HttpRequestBuilder.getUserParams(vwoInstance.getSettingFile(), campaign, "userId", variation, usageStats, null, null);
      assertTrue(httpParams.getUrl().contains("eu"));

      httpParams = HttpRequestBuilder.getGoalParams(vwoInstance.getSettingFile(), campaign, "userId", goal, variation, null, null, null);
      assertTrue(httpParams.getUrl().contains("eu"));

      httpParams = HttpRequestBuilder.getCustomDimensionParams(vwoInstance.getSettingFile(), "tagKey", "tagValue", "userId");
      assertTrue(httpParams.getUrl().contains("eu"));

      vwoInstance.activate(campaign.getKey(), "userId");
      httpParams = HttpRequestBuilder.getBatchEventPostCallParams(String.valueOf(accountId), apiKey, vwoInstance.getBatchEventQueue().getBatchQueue(), usageStats);
      assertTrue(httpParams.getUrl().contains("eu"));

      Map<String, Object> trackUserPayload = HttpRequestBuilder.getEventArchTrackUserPayload(vwoInstance.getSettingFile(), "userId", campaign.getId(), variation.getId(), null, null);
      httpParams = HttpRequestBuilder.getEventArchQueryParams(vwoInstance.getSettingFile(), "eventName", trackUserPayload,  usageStats, null, null);
      assertTrue(httpParams.getUrl().contains("eu"));
    } catch (Exception e) {
      assertFalse(false);
    }
  }

  @Test
  public void requestHeaderForTrackGoalInEventArchWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings
        .AB_TRAFFIC_50_WEIGHT_50_50).build();
    String userAgent = "user_agent";
    String userIP = "user_IP";
    boolean isHeaderUserAgent = false, isHeaderIP = false;

    Map<String, Integer> metricMap = new HashMap<String, Integer>() {{
      put("20", 20);
      put("10", 30);
      put("50", 40);
    }};
    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackGoalPayload(settings,
      "Ashley", metricMap, "goalIdentifier", 300, new HashSet<String>(){{add("revenue");}},
      new HashMap<>(), userAgent, userIP);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, "goalIdentifier",
        payload, null, userAgent, userIP);
    
    // verify headers
    Header[] headers = queryParams.getHeaders();
    assertNotNull(headers);
    assertEquals(headers.length, 3);
    for (int x = 0; x < headers.length; x++) {
        // verify user agent
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_USERAGENT.value())) {
          isHeaderUserAgent = headers[x].getValue().equals(userAgent);
        }

        // verify IP
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_IP.value())) {
          isHeaderIP = headers[x].getValue().equals(userIP);
        }
    }
    assertEquals(isHeaderUserAgent, true);
    assertEquals(isHeaderIP, true);
  }

  @Test
  public void payloadForTrackGoalInEventArchWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings
        .AB_TRAFFIC_50_WEIGHT_50_50).build();
    String userAgent = "user_agent", userIP = "user_IP";

    Map<String, Integer> metricMap = new HashMap<String, Integer>() {{
      put("20", 20);
      put("10", 30);
      put("50", 40);
    }};
    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackGoalPayload(settings,
      "Ashley", metricMap, "goalIdentifier", 300, new HashSet<String>(){{add("revenue");}},
      new HashMap<>(), userAgent, userIP);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, "goalIdentifier",
        payload, null, userAgent, userIP);
    
    // verify params
    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());
    assertNotNull(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()));
    assertTrue(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()).getNodeType() == JsonNodeType.STRING);
    assertEquals(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()).asText(), userAgent);
    assertNotNull(node.get("d").get(APIEnums.VISITOR.IP.value()));
    assertTrue(node.get("d").get(APIEnums.VISITOR.IP.value()).getNodeType() == JsonNodeType.STRING);
    assertEquals(node.get("d").get(APIEnums.VISITOR.IP.value()).asText(), userIP);
  }

  @Test
  public void requestHeaderForTrackUserInEventArchWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    String clientUserAgent = "user_agent", userIPAddress = "visitor_ip";
    boolean isHeaderUserAgent = false, isHeaderIP = false;

    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackUserPayload(settings,
      "Ashley", campaign.getId(), variation.getId(), clientUserAgent, userIPAddress);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, "CUSTOM",
        payload, null, clientUserAgent, userIPAddress);
    
    // verify headers
    Header[] headers = queryParams.getHeaders();
    assertNotNull(headers);
    assertEquals(headers.length, 3);
    for (int x = 0; x < headers.length; x++) {
        // verify user agent
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_USERAGENT.value())) {
          isHeaderUserAgent = headers[x].getValue().equals(clientUserAgent);
        }

        // verify IP
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_IP.value())) {
          isHeaderIP = headers[x].getValue().equals(userIPAddress);
        }
    }
    assertEquals(isHeaderUserAgent, true);
    assertEquals(isHeaderIP, true);
  }

  @Test
  public void payloadForTrackUserInEventArchWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings
        .AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0)
        .getVariations().get(0);
    String clientUserAgent = "user_agent";
    String userIPAddress = "visitor_ip";

    Map<String, Object> payload = HttpRequestBuilder.getEventArchTrackUserPayload(settings, 
        "Ashley", campaign.getId(), variation.getId(), clientUserAgent, userIPAddress);
    HttpParams queryParams = HttpRequestBuilder.getEventArchQueryParams(settings, "CUSTOM",
    payload, null, clientUserAgent, userIPAddress);
    
    // verify params
    JsonNode node = new ObjectMapper().readTree(queryParams.getBody());
    assertNotNull(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()));
    assertTrue(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()).getNodeType() == JsonNodeType.STRING);
    assertEquals(node.get("d").get(APIEnums.VISITOR.USERAGENT.value()).asText(), clientUserAgent);
    assertNotNull(node.get("d").get(APIEnums.VISITOR.IP.value()));
    assertTrue(node.get("d").get(APIEnums.VISITOR.IP.value()).getNodeType() == JsonNodeType.STRING);
    assertEquals(node.get("d").get(APIEnums.VISITOR.IP.value()).asText(), userIPAddress);
  }

  @Test
  public void requestHeaderForTrackGoalWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Goal goal = campaign.getGoals().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    String clientUserAgent = "user_agent";
    String userIPAddress = "user_IP";
    boolean isHeaderUserAgent = false;
    boolean isHeaderIP = false;
    
    HttpParams queryParams = HttpRequestBuilder.getGoalParams(settings, campaign, "Ashley", goal, variation, 300, clientUserAgent, userIPAddress);
    
    // verify headers
    Header[] headers = queryParams.getHeaders();
    assertNotNull(headers);
    assertEquals(headers.length, 3);
    for (int x = 0; x < headers.length; x++) {
        // verify user agent
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_USERAGENT.value())) {
          isHeaderUserAgent = headers[x].getValue().equals(clientUserAgent);
        }

        // verify IP
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_IP.value())) {
          isHeaderIP = headers[x].getValue().equals(userIPAddress);
        }
    }
    assertEquals(isHeaderUserAgent, true);
    assertEquals(isHeaderIP, true);
  }

  @Test
  public void payloadForTrackGoalWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Goal goal = campaign.getGoals().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    String clientUserAgent = "user_agent";
    String userIPAddress = "user_IP";
	    
    HttpParams httpParams = HttpRequestBuilder.getGoalParams(settings, campaign, "Ashley", goal, variation, 300, clientUserAgent, userIPAddress);
    
    // verify params
    Map<String, Object> queryParams = httpParams.getQueryParams();
    assertNotNull(queryParams.get(APIEnums.VISITOR.USERAGENT.value()));
    assertEquals(queryParams.get(APIEnums.VISITOR.USERAGENT.value()), clientUserAgent);
    assertNotNull(queryParams.get(APIEnums.VISITOR.IP.value()));
    assertEquals(queryParams.get(APIEnums.VISITOR.IP.value()), userIPAddress);
  }

  @Test
  public void requestHeaderForTrackUserWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    Map<String, Integer> usageStats = new HashMap<String, Integer>();
    usageStats.put("cl", 1);
    usageStats.put("ll", 1);
    String clientUserAgent = "user_agent";
    String userIPAddress = "user_IP";
    boolean isHeaderUserAgent = false;
    boolean isHeaderIP = false;
    
    HttpParams queryParams = HttpRequestBuilder.getUserParams(settings, campaign, "Ashley", variation, usageStats, clientUserAgent, userIPAddress);
    
    // verify headers
    Header[] headers = queryParams.getHeaders();
    assertNotNull(headers);
    assertEquals(headers.length, 3);
    for (int x = 0; x < headers.length; x++) {
        // verify user agent
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_USERAGENT.value())) {
          isHeaderUserAgent = headers[x].getValue().equals(clientUserAgent);
        }

        // verify IP
        if (headers[x].getName().equalsIgnoreCase(APIEnums.VISITOR.CUSTOMHEADER_IP.value())) {
          isHeaderIP = headers[x].getValue().equals(userIPAddress);
        }
    }
    assertEquals(isHeaderUserAgent, true);
    assertEquals(isHeaderIP, true);
  }

  @Test
  public void payloadForTrackUserWithUAAndIPTest() throws Exception {
    SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Goal goal = campaign.getGoals().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    Map<String, Integer> usageStats = new HashMap<String, Integer>();
    usageStats.put("cl", 1);
    usageStats.put("ll", 1);
    String clientUserAgent = "user_agent";
    String userIPAddress = "user_IP";
    
    HttpParams httpParams = HttpRequestBuilder.getUserParams(settings, campaign, "Ashley", variation, usageStats, clientUserAgent, userIPAddress);
    
    // verify params
    Map<String, Object> queryParams = httpParams.getQueryParams();
    assertNotNull(queryParams.get(APIEnums.VISITOR.USERAGENT.value()));
    assertEquals(queryParams.get(APIEnums.VISITOR.USERAGENT.value()), clientUserAgent);
    assertNotNull(queryParams.get(APIEnums.VISITOR.IP.value()));
    assertEquals(queryParams.get(APIEnums.VISITOR.IP.value()), userIPAddress);
  }

  @Test
  public void payloadForBatchingTrackUserWithUAAndIPTest() throws Exception {
	SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    BatchEventData batchEventData = new BatchEventData();
    batchEventData.setEventsPerRequest(50);
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).withBatchEvents(batchEventData).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    String clientUserAgent = "user_agent", userIPAddress = "visitor_ip";

    Map<String, Object> payload = HttpRequestBuilder.getBatchEventForTrackingUser(settings, campaign, "Ashley", variation, clientUserAgent, userIPAddress);

    // verify params for tracking user
    assertNotNull(payload.keySet().contains(APIEnums.VISITOR.USERAGENT.value()));
    assertEquals(payload.get(APIEnums.VISITOR.USERAGENT.value()), clientUserAgent);
    assertNotNull(payload.keySet().contains(APIEnums.VISITOR.IP.value()));
    assertEquals(payload.get(APIEnums.VISITOR.IP.value()), userIPAddress);
  }
  
  @Test
  public void payloadForBatchingTrackGoalWithUAAndIPTest() throws Exception {
	SettingFile settings = SettingsFileUtil.Builder.getInstance(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    BatchEventData batchEventData = new BatchEventData();
    batchEventData.setEventsPerRequest(50);
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).withBatchEvents(batchEventData).build();
    Campaign campaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0);
    Goal goal = campaign.getGoals().get(0);
    Variation variation = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getVariations().get(0);
    String clientUserAgent = "user_agent", userIPAddress = "visitor_ip";

    Map<String, Object> payload = HttpRequestBuilder.getBatchEventForTrackingGoal(settings, campaign, "Ashley", goal, variation, 300, new HashMap<>(), clientUserAgent, userIPAddress);

    // verify params for tracking user
    assertNotNull(payload.keySet().contains(APIEnums.VISITOR.USERAGENT.value()));
    assertEquals(payload.get(APIEnums.VISITOR.USERAGENT.value()), clientUserAgent);
    assertNotNull(payload.keySet().contains(APIEnums.VISITOR.IP.value()));
    assertEquals(payload.get(APIEnums.VISITOR.IP.value()), userIPAddress);
  }
}
