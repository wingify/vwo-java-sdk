package com.vwo;

import com.vwo.models.Campaign;
import com.vwo.models.Variation;

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
}
