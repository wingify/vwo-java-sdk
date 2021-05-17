/**
 * Copyright 2019-2021 Wingify Software Pvt. Ltd.
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

package com.vwo.tests.data;

@SuppressWarnings("checkstyle:LineLength")
public class Settings {

  public static String AB_TRAFFIC_50_WEIGHT_50_50 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":230,\"percentTraffic\":50,\"key\":\"AB_TRAFFIC_50_WEIGHT_50_50\", \"name\": \"AB_TRAFFIC_50_WEIGHT_50_50\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_100_WEIGHT_50_50 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"abcd\",\"id\":1,\"type\":\"REVENUE_TRACKING\"},{\"identifier\":\"CUSTOM\",\"id\":214,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_50_50\", \"name\": \"AB_TRAFFIC_100_WEIGHT_50_50\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_100_WEIGHT_20_80 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":215,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":20},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":80}],\"id\":232,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_20_80\", \"name\": \"AB_TRAFFIC_100_WEIGHT_20_80\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_20_WEIGHT_10_90 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":216,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":10},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":90}],\"id\":233,\"percentTraffic\":20,\"key\":\"AB_TRAFFIC_20_WEIGHT_10_90\", \"name\": \"AB_TRAFFIC_20_WEIGHT_10_90\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_100_WEIGHT_0_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":217,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":0},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":100}],\"id\":234,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_0_100\", \"name\": \"AB_TRAFFIC_100_WEIGHT_0_100\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_100_WEIGHT_33_33_33 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":218,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":33.3333},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":33.3333},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":33.3333}],\"id\":235,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"name\": \"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_NOT_RUNNING_TRAFFIC_100_WEIGHT_33_33_33 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":218,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":33.3333},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":33.3333},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":33.3333}],\"id\":235,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"name\": \"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"status\":\"PAUSED\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_100_VARIATIONS_10 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":231,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":10},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":10},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":10},{\"id\":4,\"name\":\"Variation-3\",\"changes\":{},\"weight\":10},{\"id\":5,\"name\":\"Variation-4\",\"changes\":{},\"weight\":10},{\"id\":6,\"name\":\"Variation-5\",\"changes\":{},\"weight\":10},{\"id\":7,\"name\":\"Variation-6\",\"changes\":{},\"weight\":10},{\"id\":8,\"name\":\"Variation-7\",\"changes\":{},\"weight\":10},{\"id\":9,\"name\":\"Variation-8\",\"changes\":{},\"weight\":10},{\"id\":10,\"name\":\"Variation-9\",\"changes\":{},\"weight\":10}],\"id\":260,\"percentTraffic\":100,\"key\":\"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"name\": \"AB_TRAFFIC_100_WEIGHT_33_33_33\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_TRAFFIC_75_VARIATIONS_10 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":231,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":10},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":10},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":10},{\"id\":4,\"name\":\"Variation-3\",\"changes\":{},\"weight\":10},{\"id\":5,\"name\":\"Variation-4\",\"changes\":{},\"weight\":10},{\"id\":6,\"name\":\"Variation-5\",\"changes\":{},\"weight\":10},{\"id\":7,\"name\":\"Variation-6\",\"changes\":{},\"weight\":10},{\"id\":8,\"name\":\"Variation-7\",\"changes\":{},\"weight\":10},{\"id\":9,\"name\":\"Variation-8\",\"changes\":{},\"weight\":10},{\"id\":10,\"name\":\"Variation-9\",\"changes\":{},\"weight\":10}],\"id\":260,\"percentTraffic\":75,\"key\":\"ten_variations\", \"name\": \"ten_variations\", \"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":60781,\"version\":1}";
  public static String AB_AND_FT_TRAFFIC_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"track1\",\"id\":1,\"type\":\"CUSTOM_GOAL\"},{\"identifier\":\"track2\",\"id\":2,\"type\":\"CUSTOM_GOAL\"},{\"identifier\":\"track3\",\"id\":3,\"type\":\"REVENUE_TRACKING\"},{\"identifier\":\"track4\",\"id\":4,\"type\":\"REVENUE_TRACKING\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"key\":\"DEV_TEST_2\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"},{\"goals\":[{\"identifier\":\"track1\",\"id\":5,\"type\":\"CUSTOM_GOAL\"},{\"identifier\":\"track2\",\"id\":6,\"type\":\"REVENUE_TRACKING\"},{\"identifier\":\"track3\",\"id\":7,\"type\":\"CUSTOM_GOAL\"},{\"identifier\":\"track4\",\"id\":8,\"type\":\"REVENUE_TRACKING\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":false}],\"id\":22,\"percentTraffic\":100,\"key\":\"FT_T_100_W_10_20_30_40\", \"name\": \"FT_T_100_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":60781,\"version\":1}";

  public static String FEATURE_ROLLOUT_TRAFFIC_0 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"this_is_a_string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":123.456},{\"id\":2,\"key\":\"BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":true}],\"id\":29,\"percentTraffic\":0,\"key\":\"FR_T_0_W_100\", \"name\": \"FR_T_0_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_ROLLOUT_TRAFFIC_25 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"this_is_a_string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":123.456},{\"id\":2,\"key\":\"BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":true}],\"id\":29,\"percentTraffic\":25,\"key\":\"FR_T_25_W_100\", \"name\": \"FR_T_25_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_ROLLOUT_TRAFFIC_50 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"this_is_a_string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":123.456},{\"id\":2,\"key\":\"BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":true}],\"id\":29,\"percentTraffic\":50,\"key\":\"FR_T_50_W_100\", \"name\": \"FR_T_50_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_ROLLOUT_TRAFFIC_75 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"this_is_a_string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":123.456},{\"id\":2,\"key\":\"BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":true}],\"id\":29,\"percentTraffic\":75,\"key\":\"FR_T_75_W_100\", \"name\": \"FR_T_75_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_ROLLOUT_TRAFFIC_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"this_is_a_string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":123.456},{\"id\":2,\"key\":\"BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":true}],\"id\":29,\"percentTraffic\":100,\"key\":\"FR_T_100_W_100\", \"name\": \"FR_T_100_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_ROLLOUT_INCORRECT_VARIABLE_TYPE = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":100}],\"variables\":[{\"id\":1,\"key\":\"INVALID_STRING_VARIABLE\",\"type\":\"string\",\"value\":false},{\"id\":2,\"key\":\"INVALID_INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123.4},{\"id\":1,\"key\":\"INVALID_FLOAT_VARIABLE\",\"type\":\"double\",\"value\":121},{\"id\":2,\"key\":\"INVALID_BOOLEAN_VARIABLE\",\"type\":\"boolean\",\"value\":\"asd\"}],\"id\":29,\"percentTraffic\":100,\"key\":\"FR_T_100_W_100\", \"name\": \"FR_T_100_W_100\", \"status\":\"RUNNING\",\"type\":\"FEATURE_ROLLOUT\"}],\"accountId\":123456,\"version\":2}";

  public static String FEATURE_TEST_TRAFFIC_0 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":true}],\"id\":22,\"percentTraffic\":0,\"key\":\"FT_T_0_W_10_20_30_40\", \"name\": \"FT_T_0_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_TEST_TRAFFIC_25 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":true}],\"id\":22,\"percentTraffic\":25,\"key\":\"FT_T_25_W_10_20_30_40\", \"name\": \"FT_T_25_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_TEST_TRAFFIC_50 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":true}],\"id\":22,\"percentTraffic\":50,\"key\":\"FT_T_50_W_10_20_30_40\", \"name\": \"FT_T_50_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_TEST_TRAFFIC_75 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":true}],\"id\":22,\"percentTraffic\":75,\"key\":\"FT_T_75_W_10_20_30_40\", \"name\": \"FT_T_75_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_TEST_TRAFFIC_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":false}],\"id\":22,\"percentTraffic\":100,\"key\":\"FT_T_100_W_10_20_30_40\", \"name\": \"FT_T_100_W_10_20_30_40\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";
  public static String FEATURE_TEST_TRAFFIC_100_DISABLED = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":false},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":false},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":false}],\"id\":22,\"percentTraffic\":100,\"key\":\"FT_T_100_W_10_20_30_40_IFEF\", \"name\": \"FT_T_100_W_10_20_30_40_IFEF\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\"}],\"accountId\":123456,\"version\":2}";

  public static String PRE_SEGMENT_AB_TEST_TRAFFIC_100 = "{\"sdkKey\":\"some_unique_key\",\"campaigns\":[{\"percentTraffic\":100,\"goals\":[{\"identifier\":\"ddd\",\"id\":453,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":174,\"segments\":{\"and\":[{\"or\":[{\"custom_variable\":{\"a\":\"wildcard(*123*)\"}}]},{\"or\":[{\"custom_variable\":{\"hello\":\"regex(world)\"}}]}]},\"key\":\"T_100_W_50_50_WS\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\"}],\"accountId\":88888888,\"version\":1}";
  public static String PRE_SEGMENT_FEATURE_TEST_TRAFFIC_75 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"FEATURE_TEST_GOAL\",\"id\":203,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":\"1\",\"name\":\"Control\",\"weight\":10,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}],\"isFeatureEnabled\":false},{\"id\":\"2\",\"name\":\"Variation-1\",\"weight\":20,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}],\"isFeatureEnabled\":true},{\"id\":\"3\",\"name\":\"Variation-2\",\"weight\":30,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}],\"isFeatureEnabled\":true},{\"id\":\"4\",\"name\":\"Variation-3\",\"weight\":40,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100}],\"isFeatureEnabled\":true}],\"id\":22,\"percentTraffic\":75,\"key\":\"FT_T_75_W_10_20_30_40_WS\", \"name\": \"FT_T_75_W_10_20_30_40_WS\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\",\"segments\":{\"and\":[{\"or\":[{\"custom_variable\":{\"a\":\"wildcard(*123*)\"}}]},{\"or\":[{\"custom_variable\":{\"hello\":\"regex(world)\"}}]}]}}],\"accountId\":123456,\"version\":2}";

  public static String USER_WHITELISTING_AB_TEST_TRAFFIC_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":218,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":33.3333,\"segments\":{\"or\":[{\"custom_variable\":{\"safari\":\"true\"}}]}},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":33.3333,\"segments\":{\"or\":[{\"custom_variable\":{\"browser\":\"wildcard(chrome*)\"}}]}},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":33.3333,\"segments\":{\"or\":[{\"custom_variable\":{\"chrome\":\"false\"}}]}}],\"id\":235,\"percentTraffic\":100,\"key\":\"T_100_W_33_33_33_WS_WW\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"isForcedVariationEnabled\":true,\"segments\":{\"or\":[{\"custom_variable\":{\"eq\":\"something\"}}]}}],\"accountId\":88888888,\"version\":1}";
  public static String USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100 = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":218,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":10,\"segments\":{\"or\":[{\"custom_variable\":{\"safari\":\"true\"}}]},\"isFeatureEnabled\":false,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Control string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":123}]},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":20,\"segments\":{\"or\":[{\"custom_variable\":{\"browser\":\"wildcard(chrome*)\"}}]},\"isFeatureEnabled\":true,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-1 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":456}]},{\"id\":3,\"name\":\"Variation-2\",\"changes\":{},\"weight\":30,\"segments\":{\"or\":[{\"custom_variable\":{\"chrome\":\"false\"}}]},\"isFeatureEnabled\":false,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-2 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":789}]},{\"id\":3,\"name\":\"Variation-3\",\"changes\":{},\"weight\":40,\"segments\":{\"or\":[{\"custom_variable\":{\"browser\":\"regex(chrome)\"}}]},\"isFeatureEnabled\":false,\"variables\":[{\"id\":1,\"key\":\"STRING_VARIABLE\",\"type\":\"string\",\"value\":\"Variation-3 string\"},{\"id\":2,\"key\":\"INTEGER_VARIABLE\",\"type\":\"integer\",\"value\":100},{\"id\":1,\"key\":\"FLOAT_VARIABLE\",\"type\":\"double\",\"value\":2.2}]}],\"id\":235,\"percentTraffic\":100,\"key\":\"FT_100_W_33_33_33_WS_WW\", \"name\": \"FT_100_W_33_33_33_WS_WW\", \"status\":\"RUNNING\",\"type\":\"FEATURE_TEST\",\"isForcedVariationEnabled\":true,\"segments\":{\"or\":[{\"custom_variable\":{\"eq\":\"something\"}}]}}],\"accountId\":88888888,\"version\":1}";
}

