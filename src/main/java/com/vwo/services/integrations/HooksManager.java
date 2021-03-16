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

package com.vwo.services.integrations;

import java.util.Map;

public class HooksManager {
  private IntegrationEventListener integrations;

  public HooksManager(IntegrationEventListener integrationsCallback) {
    this.integrations = integrationsCallback;
  }

  public void execute(Map<String, Object> properties) {
    if (integrations != null) {
      integrations.onEvent(properties);
    }
  }
}
