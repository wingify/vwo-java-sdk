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

package com.vwo.utils;

import com.vwo.services.storage.Storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StorageUtils {
  private static String delimeter = "_vwo_";

  public static boolean isValidUserStorageMap(Map<String, String> map) {
    return map.containsKey(Storage.User.userId)
            && map.containsKey(Storage.User.campaignKey)
            && map.containsKey(Storage.User.variationKey);
  }

  /**
   * convert a ArrayList into a string.
   *
   * @param arrayList Arraylist to be converted
   * @return converted string value
   */
  public static String arrayToString(ArrayList<String> arrayList) {
    if (arrayList.size() == 0) {
      return null;
    }

    String goalListString = "";
    for (String goalIdentifier : arrayList) {
      goalListString += goalIdentifier + StorageUtils.delimeter;
    }
    return goalListString;
  }

  /**
   * convert a String into a Array List.
   *
   * @param goalListString String to be converted
   * @return converted Arraylist object
   */
  public static ArrayList<String> stringToArray(String goalListString) {
    if (goalListString == null || goalListString.isEmpty()) {
      return new ArrayList<>();
    }
    ArrayList<String> goalListArray = new ArrayList<>(Arrays.asList(goalListString.split(StorageUtils.delimeter)));
    return goalListArray;
  }

}
