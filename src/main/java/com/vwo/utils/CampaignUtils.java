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

package com.vwo.utils;

import com.vwo.enums.CampaignEnums;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.Settings;
import com.vwo.models.Variation;
import com.vwo.services.settings.SettingsFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampaignUtils {

  public static Variation getVariationObjectFromCampaign(Campaign campaign, String variationName) {
    if (variationName != null) {
      for (Variation variation : campaign.getVariations()) {
        if (variation.getName().equalsIgnoreCase(variationName)) {
          return variation;
        }
      }
    }
    return null;
  }

  public static double getVariationsTotalWeight(List<Variation> variations) {
    double totalWeight = 0;

    for (Variation variation: variations) {
      totalWeight += variation.getWeight();
    }

    return totalWeight;
  }

  /**
   * Rationalize or scale the variations wrt variation weight.
   *
   * @param variations - List of variations
   */
  public static void rationalizeVariationsWeights(List<Variation> variations) {
    double totalWeight = CampaignUtils.getVariationsTotalWeight(variations);
    if (totalWeight == 0) {
      variations.forEach(variation -> variation.setWeight(100 / variations.size()));
    } else {
      variations.forEach(variation -> variation.setWeight((variation.getWeight() / totalWeight) * 100));
    }
  }

  /**
   * Filter campaigns from the given list matching the given goal identifier.
   * @param goalIdentifier Goal identifier name
   * @param campaigns Campaigns List
   *
   * @return List of campaigns matching goal identifier.
   */
  public static ArrayList<Campaign> getCampaignFromGoalIdentifier(String goalIdentifier, List<Campaign> campaigns) {
    if (campaigns != null) {
      ArrayList<Campaign> campaignList = new ArrayList<>();
      for (Campaign campaign : campaigns) {
        List<Goal> goalList = campaign.getGoals();
        for (Goal goal : goalList) {
          if (goal.getIdentifier().equals(goalIdentifier) && campaign.getStatus().equals(CampaignEnums.STATUS.RUNNING.value())) {
            campaignList.add(campaign);
          }
        }
      }
      return campaignList;
    }
    return null;
  }

  /**
   * Check if the campaign is a part of mutually exclusive group.
   *
   * @param settings      - Settings instance
   * @param campaignId    - campaign id
   * @return group id and name for the campaign.
   */
  public static Map<String, Object> isPartOfGroup(Settings settings, int campaignId) {
    Map<String, Object> groupDetails = new HashMap<String, Object>();
    if (settings.getCampaignGroups() != null && settings.getCampaignGroups().containsKey(String.valueOf(campaignId))) {
      int groupId = settings.getCampaignGroups().get(String.valueOf(campaignId));
      groupDetails.put("groupId", groupId);
      groupDetails.put("groupName", settings.getGroups().get(String.valueOf(groupId)).getName());
      return groupDetails;
    }
    return groupDetails;
  }

  /**
   * Get the list of campaigns on the basis of their id.
   *
   * @param settings    - Settings instance
   * @param groupId     - group id
   * @return list of campaigns
   */
  public static ArrayList<Campaign> getGroupCampaigns(Settings settings, int groupId) {
    ArrayList<Campaign> campaignList = new ArrayList<>();
    if (settings.getGroups().containsKey(String.valueOf(groupId))) {
      for (int campaignId: settings.getGroups().get(String.valueOf(groupId)).getCampaigns()) {
        Campaign campaign = getCampaignBasedOnId(settings, campaignId);
        if (campaign != null) {
          campaignList.add(campaign);
        }
      }
    }
    return campaignList;
  }

  /**
   * Get the campaign on the basis of campaign id.
   *
   * @param settings      - Settings instance
   * @param campaignId    - Campaign id
   * @return Campaign object.
   */
  private static Campaign getCampaignBasedOnId(Settings settings, int campaignId) {
    Campaign campaign = null;
    for (Campaign eachCampaign: settings.getCampaigns()) {
      if (eachCampaign.getId() == campaignId) {
        campaign = eachCampaign;
        break;
      }
    }
    return campaign;
  }

  /**
   * Allocate range to each campaign in the list.
   *
   * @param campaigns list of campaigns.
   */
  public static void setCampaignRange(List<Campaign> campaigns) {
    double allocatedRange = 0;

    for (Campaign campaign : campaigns) {
      Double stepFactor = SettingsFileUtil.getVariationBucketRange(campaign.getWeight());
      if (stepFactor != null && stepFactor != -1) {
        campaign.setStartRange((int) allocatedRange + 1);
        allocatedRange = (Math.ceil(allocatedRange + stepFactor));
        campaign.setEndRange((int) allocatedRange);
      } else {
        campaign.setStartRange(-1);
        campaign.setEndRange(-1);
      }
    }
  }

}
