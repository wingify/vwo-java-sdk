/**
 * Copyright 2019 Wingify Software Pvt. Ltd.
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

package com.vwo.logger;

import com.vwo.VWO;

public abstract class VWOLogger {
  public static String level;

  public VWOLogger() {
    this.level = VWO.Enums.LOGGER_LEVEL.ERROR.value();
  }

  public VWOLogger(String level) {
    this.level = level;
  }

  public abstract void trace(String var1, Object... var2);

  public abstract void debug(String var1, Object... var2);

  public abstract void info(String var1, Object... var2);

  public abstract void warn(String var1, Object... var2);

  public abstract void error(String var1, Object... var2);
}
