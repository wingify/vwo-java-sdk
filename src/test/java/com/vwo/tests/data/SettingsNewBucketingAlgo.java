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

package com.vwo.tests.data;

import java.util.HashMap;

@SuppressWarnings("checkstyle:LineLength")
public class SettingsNewBucketingAlgo {
  // settings
  public static String SETTINGS_WITHOUT_SEED_WITHOUT_ISOB = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"name\":\"BUCKET_ALGO_WITHOUT_SEED\",\"key\":\"BUCKET_ALGO_WITHOUT_SEED\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"segments\":{}}],\"accountId\":888888,\"version\":1,\"isNB\":true}";
  public static String SETTINGS_WITH_SEED_WITHOUT_ISOB = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"isBucketingSeedEnabled\":true,\"name\":\"BUCKET_ALGO_WITH_SEED\",\"key\":\"BUCKET_ALGO_WITH_SEED\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"segments\":{}}],\"accountId\":888888,\"version\":1,\"isNB\":true}";
  public static String SETTINGS_WITH_ISNB_WITH_ISOB = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"isBucketingSeedEnabled\":true,\"name\":\"BUCKET_ALGO_WITH_SEED_WITH_isNB_WITH_isOB\",\"key\":\"BUCKET_ALGO_WITH_SEED_WITH_isNB_WITH_isOB\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"segments\":{},\"isOB\":true}],\"accountId\":888888,\"version\":1,\"isNB\":true}";
  public static String SETTINGS_WITH_ISNB_WITHOUT_ISOB = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"isBucketingSeedEnabled\":true,\"name\":\"BUCKET_ALGO_WITH_SEED_WITH_isNB_WITHOUT_isOB\",\"key\":\"BUCKET_ALGO_WITH_SEED_WITH_isNB_WITHOUT_isOB\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"segments\":{}}],\"accountId\":888888,\"version\":1,\"isNB\":true}";
  public static String SETTINGS_WITHOUT_SEED_WITH_ISNB_WITHOUT_ISOB = "{\"sdkKey\":\"someuniquestuff1234567\",\"campaigns\":[{\"goals\":[{\"identifier\":\"CUSTOM\",\"id\":213,\"type\":\"CUSTOM_GOAL\"}],\"variations\":[{\"id\":1,\"name\":\"Control\",\"changes\":{},\"weight\":50},{\"id\":2,\"name\":\"Variation-1\",\"changes\":{},\"weight\":50}],\"id\":231,\"percentTraffic\":100,\"name\":\"BUCKET_ALGO_WITHOUT_SEED_FLAG_WITH_isNB_WITHOUT_isOB\",\"key\":\"BUCKET_ALGO_WITHOUT_SEED_FLAG_WITH_isNB_WITHOUT_isOB\",\"status\":\"RUNNING\",\"type\":\"VISUAL_AB\",\"segments\":{}}],\"accountId\":888888,\"version\":1,\"isNB\":true}";
}