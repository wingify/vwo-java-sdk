/**
 * Copyright 2019-2020 Wingify Software Pvt. Ltd.
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

package com.vwo.services.storage;

import java.util.Map;

public interface UserStorage {

  String userId = "userId";
  String campaignKey = "campaignKey";
  String variationKey = "variationName";
  String goalIdentifier = "goalIdentifier";

  Map<String, String> get(String userId, String campaignKey) throws Exception;

  void set(Map<String, String> userData) throws Exception;
}
