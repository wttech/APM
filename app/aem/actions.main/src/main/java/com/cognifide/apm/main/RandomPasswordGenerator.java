/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.main;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class RandomPasswordGenerator {

  private static final int MAX_BITS = 130;

  private static final int RADIX = 32;

  private static final SecureRandom random = new SecureRandom();

  private RandomPasswordGenerator() {
    // intentionally empty
  }

  public static String getRandomPassword() {
    return new BigInteger(MAX_BITS, random).toString(RADIX);
  }

}
