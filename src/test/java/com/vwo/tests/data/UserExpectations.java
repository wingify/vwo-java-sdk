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

package com.vwo.tests.data;

import java.util.ArrayList;

public class UserExpectations {
  public static ArrayList<Variation> AB_TRAFFIC_50_WEIGHT_50_50 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_100_WEIGHT_50_50 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_100_WEIGHT_20_80 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_20_WEIGHT_10_90 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_100_WEIGHT_0_100 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_100_WEIGHT_33_33_33 = new ArrayList<>();
  public static ArrayList<Variation> AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_100_INVALID_VARIATIONS_10 = new ArrayList<>();
  public static ArrayList<Variation> AB_TRAFFIC_75_VARIATIONS_10 = new ArrayList<>();

  public static ArrayList<Variation> TRAFFIC_0 = new ArrayList<>();
  public static ArrayList<Variation> FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<Variation> FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<Variation> FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<Variation> FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40 = new ArrayList<>();

  public static ArrayList<FeatureEnabled> FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<FeatureEnabled> FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<FeatureEnabled> FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<FeatureEnabled> FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40 = new ArrayList<>();
  public static ArrayList<FeatureEnabled> FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40 = new ArrayList<>();

  public static ArrayList<Variation> ALL_VARIATION_2 = new ArrayList<>();

  static {
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Ashley", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Bill", "Control"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Chris", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Dominic", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Emma", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Faizan", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Gimmy", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Harry", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Ian", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("John", "Control"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("King", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Lisa", "Control"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Mona", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Nina", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Olivia", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Pete", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Queen", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Robert", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Tierra", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Una", "Control"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Varun", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Will", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Xin", "Variation-1"));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("You", null));
    AB_TRAFFIC_50_WEIGHT_50_50.add(getVariation("Zeba", "Variation-1"));
  }

  static {
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Ashley", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Bill", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Chris", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Dominic", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Emma", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Faizan", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Gimmy", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Harry", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Ian", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("John", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("King", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Lisa", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Mona", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Nina", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Olivia", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Pete", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Queen", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Robert", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Tierra", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Una", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Varun", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Will", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Xin", "Control"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("You", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_50_50.add(getVariation("Zeba", "Control"));
  }

  static {
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Ashley", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Bill", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Chris", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Dominic", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Emma", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Faizan", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Gimmy", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Harry", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Ian", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("John", "Control"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("King", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Lisa", "Control"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Mona", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Nina", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Olivia", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Pete", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Queen", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Robert", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Tierra", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Una", "Control"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Varun", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Will", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Xin", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("You", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_20_80.add(getVariation("Zeba", "Variation-1"));
  }

  static {
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Ashley", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Bill", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Chris", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Dominic", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Emma", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Faizan", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Gimmy", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Harry", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Ian", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("John", "Variation-1"));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("King", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Lisa", "Variation-1"));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Mona", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Nina", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Olivia", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Pete", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Queen", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Robert", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Tierra", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Una", "Variation-1"));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Varun", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Will", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Xin", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("You", null));
    AB_TRAFFIC_20_WEIGHT_10_90.add(getVariation("Zeba", null));
  }

  static {
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Ashley", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Bill", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Chris", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Dominic", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Emma", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Faizan", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Gimmy", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Harry", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Ian", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("John", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("King", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Lisa", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Mona", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Nina", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Olivia", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Pete", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Queen", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Robert", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Sarah", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Tierra", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Una", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Varun", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Will", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Xin", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("You", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_0_100.add(getVariation("Zeba", "Variation-1"));
  }

  static {
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Ashley", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Bill", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Chris", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Dominic", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Emma", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Faizan", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Gimmy", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Harry", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Ian", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("John", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("King", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Lisa", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Mona", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Nina", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Olivia", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Pete", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Queen", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Robert", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Tierra", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Una", "Control"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Varun", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Will", "Variation-2"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Xin", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("You", "Variation-1"));
    AB_TRAFFIC_100_WEIGHT_33_33_33.add(getVariation("Zeba", "Variation-1"));
  }

  static {
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Ashley", "Variation-9"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Bill", "Variation-8"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Chris", "Variation-7"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Dominic", "Variation-6"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Emma", "Variation-5"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Faizan", "Variation-4"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Gimmy", "Variation-3"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Harry", "Variation-2"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Ian", "Variation-1"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("John", "Variation-9"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("King", "Variation-8"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Lisa", "Variation-7"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Mona", "Variation-6"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Nina", "Variation-5"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Olivia", "Variation-4"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Pete", "Variation-3"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Queen", "Variation-2"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Robert", "Variation-1"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Sarah", "Variation-9"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Tierra", "Variation-8"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Una", "Variation-7"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Varun", "Variation-6"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Will", "Variation-5"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Xin", "Variation-4"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("You", "Variation-3"));
    AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10.add(getVariation("Zeba", "Variation-2"));
  }

  static {
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Ashley", "Variation-999"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Bill", "Variation-888"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Chris", "Variation-777"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Dominic", "Variation-666"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Emma", "Variation-555"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Faizan", "Variation-444"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Gimmy", "Variation-333"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Harry", "Variation-222"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Ian", "Variation-111"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("John", "Variation-999"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("King", "Variation-888"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Lisa", "Variation-777"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Mona", "Variation-666"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Nina", "Variation-555"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Olivia", "Variation-444"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Pete", "Variation-333"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Queen", "Variation-222"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Robert", "Variation-111"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Sarah", "Variation-999"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Tierra", "Variation-888"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Una", "Variation-777"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Varun", "Variation-666"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Will", "Variation-555"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Xin", "Variation-444"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("You", "Variation-333"));
    AB_TRAFFIC_100_INVALID_VARIATIONS_10.add(getVariation("Zeba", "Variation-222"));
  }

  static {
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Ashley", "Variation-6"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Bill", "Variation-3"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Chris", null));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Dominic", null));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Emma", "Variation-5"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Faizan", "Variation-5"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Gimmy", null));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Harry", "Variation-3"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Ian", "Variation-6"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("John", "Control"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("King", null));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Lisa", "Control"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Mona", "Variation-3"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Nina", "Variation-9"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Olivia", "Variation-4"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Pete", "Variation-8"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Queen", "Variation-7"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Robert", "Variation-3"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Sarah", "Control"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Tierra", "Variation-9"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Una", "Variation-2"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Varun", "Variation-6"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Will", null));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Xin", "Variation-5"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("You", "Variation-7"));
    AB_TRAFFIC_75_VARIATIONS_10.add(getVariation("Zeba", "Variation-4"));
  }

  static {
    TRAFFIC_0.add(getVariation("Ashley", null));
    TRAFFIC_0.add(getVariation("Bill", null));
    TRAFFIC_0.add(getVariation("Chris", null));
    TRAFFIC_0.add(getVariation("Dominic", null));
    TRAFFIC_0.add(getVariation("Emma", null));
    TRAFFIC_0.add(getVariation("Faizan", null));
    TRAFFIC_0.add(getVariation("Gimmy", null));
    TRAFFIC_0.add(getVariation("Harry", null));
    TRAFFIC_0.add(getVariation("Ian", null));
    TRAFFIC_0.add(getVariation("John", null));
    TRAFFIC_0.add(getVariation("King", null));
    TRAFFIC_0.add(getVariation("Lisa", null));
    TRAFFIC_0.add(getVariation("Mona", null));
    TRAFFIC_0.add(getVariation("Nina", null));
    TRAFFIC_0.add(getVariation("Olivia", null));
    TRAFFIC_0.add(getVariation("Pete", null));
    TRAFFIC_0.add(getVariation("Queen", null));
    TRAFFIC_0.add(getVariation("Robert", null));
    TRAFFIC_0.add(getVariation("Sarah", null));
    TRAFFIC_0.add(getVariation("Tierra", null));
    TRAFFIC_0.add(getVariation("Una", null));
    TRAFFIC_0.add(getVariation("Varun", null));
    TRAFFIC_0.add(getVariation("Will", null));
    TRAFFIC_0.add(getVariation("Xin", null));
    TRAFFIC_0.add(getVariation("You", null));
    TRAFFIC_0.add(getVariation("Zeba", null));
  }

  static {
    ALL_VARIATION_2.add(getVariation("Ashley", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Bill", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Chris", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Dominic", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Emma", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Faizan", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Gimmy", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Harry", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Ian", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("John", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("King", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Lisa", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Mona", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Nina", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Olivia", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Pete", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Queen", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Robert", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Sarah", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Tierra", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Una", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Varun", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Will", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Xin", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("You", "Variation-2"));
    ALL_VARIATION_2.add(getVariation("Zeba", "Variation-2"));
  }

  static {
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Ashley", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Bill", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Chris", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Dominic", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Emma", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Faizan", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Gimmy", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Harry", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Ian", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("John", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("King", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Lisa", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Mona", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Nina", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Olivia", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Pete", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Queen", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Robert", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Sarah", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Tierra", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Una", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Varun", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Will", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Xin", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("You", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getVariation("Zeba", null));
  }

  static {
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Ashley", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Bill", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Chris", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Dominic", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Emma", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Faizan", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Gimmy", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Harry", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Ian", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("John", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("King", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Lisa", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Mona", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Nina", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Olivia", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Pete", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Queen", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Robert", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Sarah", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Tierra", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Una", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Varun", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Will", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Xin", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("You", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getVariation("Zeba", "Variation-3"));
  }

  static {
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Ashley", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Bill", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Chris", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Dominic", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Emma", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Faizan", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Gimmy", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Harry", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Ian", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("John", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("King", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Lisa", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Mona", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Nina", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Olivia", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Pete", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Queen", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Robert", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Sarah", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Tierra", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Una", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Varun", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Will", null));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Xin", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("You", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getVariation("Zeba", "Variation-2"));
  }

  static {
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Ashley", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Bill", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Chris", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Dominic", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Emma", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Faizan", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Gimmy", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Harry", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Ian", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("John", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("King", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Lisa", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Mona", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Nina", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Olivia", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Pete", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Queen", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Robert", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Sarah", "Control"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Tierra", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Una", "Variation-1"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Varun", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Will", "Variation-3"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Xin", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("You", "Variation-2"));
    FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getVariation("Zeba", "Variation-2"));
  }

  static {
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ashley", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Bill", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Chris", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Dominic", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Emma", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Faizan", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Gimmy", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Harry", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ian", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("John", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("King", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Lisa", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Mona", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Nina", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Olivia", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Pete", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Queen", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Robert", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Sarah", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Tierra", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Una", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Varun", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Will", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Xin", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("You", false));
    FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40.add(getFeatureEnabled("Zeba", false));
  }

  static {
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ashley", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Bill", true));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Chris", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Dominic", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Emma", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Faizan", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Gimmy", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Harry", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ian", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("John", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("King", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Lisa", true));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Mona", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Nina", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Olivia", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Pete", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Queen", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Robert", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Sarah", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Tierra", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Una", true));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Varun", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Will", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Xin", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("You", false));
    FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40.add(getFeatureEnabled("Zeba", false));
  }

  static {
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ashley", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Bill", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Chris", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Dominic", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Emma", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Faizan", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Gimmy", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Harry", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ian", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("John", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("King", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Lisa", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Mona", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Nina", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Olivia", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Pete", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Queen", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Robert", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Sarah", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Tierra", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Una", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Varun", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Will", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Xin", true));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("You", false));
    FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40.add(getFeatureEnabled("Zeba", true));
  }

  static {
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ashley", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Bill", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Chris", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Dominic", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Emma", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Faizan", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Gimmy", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Harry", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ian", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("John", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("King", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Lisa", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Mona", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Nina", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Olivia", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Pete", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Queen", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Robert", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Sarah", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Tierra", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Una", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Varun", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Will", false));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Xin", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("You", true));
    FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40.add(getFeatureEnabled("Zeba", true));
  }

  static {
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ashley", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Bill", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Chris", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Dominic", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Emma", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Faizan", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Gimmy", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Harry", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Ian", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("John", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("King", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Lisa", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Mona", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Nina", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Olivia", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Pete", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Queen", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Robert", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Sarah", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Tierra", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Una", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Varun", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Will", false));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Xin", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("You", true));
    FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40.add(getFeatureEnabled("Zeba", true));
  }


  public static Object getVariableValueForVariation(String variation, String variableType, boolean isFeatureEnabled) {
    switch (variableType.toLowerCase()) {
      case "string":
        return getStringVariableValueForVariation(variation, isFeatureEnabled);
      case "integer":
        return getIntegerVariableValueForVariation(variation, isFeatureEnabled);
      default:
        return null;
    }
  }

  public static String getStringVariableValueForVariation(String variation, boolean isFeatureEnabled) {
    if (!isFeatureEnabled) {
      return "Control string";
    }

    switch (variation.toLowerCase()) {
      case "control":
        return "Control string";
       case "variation-1":
         return "Variation-1 string";
       case "variation-2":
         return "Variation-2 string";
       case "variation-3":
         return "Variation-3 string";
      default:
        return null;
    }
  }

  public static Integer getIntegerVariableValueForVariation(String variation, boolean isFeatureEnabled) {
    if (!isFeatureEnabled) {
      return 123;
    }

    switch (variation.toLowerCase()) {
      case "control":
        return 123;
      case "variation-1":
        return 456;
      case "variation-2":
        return 789;
      case "variation-3":
        return 100;
      default:
        return null;
    }
  }

  public static class Variation {
    String user;
    String variation;

    Variation(String user, String variation) {
      this.user = user;
      this.variation = variation;
    }

    public String getUser() {
      return this.user;
    }

    public String getVariation() {
      return this.variation;
    }
  }

  public static class FeatureEnabled {
    String user;
    boolean isFeatureEnabled;

    FeatureEnabled(String user, boolean isFeatureEnabled) {
      this.user = user;
      this.isFeatureEnabled = isFeatureEnabled;
    }

    public String getUser() {
      return this.user;
    }

    public boolean getIsFeatureEnabled() {
      return isFeatureEnabled;
    }
  }

  private static Variation getVariation (String user, String variation) {
    return new UserExpectations.Variation(user, variation);
  }

  private static FeatureEnabled getFeatureEnabled (String user, boolean isFeatureEnabled) {
    return new UserExpectations.FeatureEnabled(user, isFeatureEnabled);
  }
}
