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

package com.vwo.utils;

import com.vwo.models.Campaign;
import com.vwo.models.Variation;

import java.util.List;

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
}
