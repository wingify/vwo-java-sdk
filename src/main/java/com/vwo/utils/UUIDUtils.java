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

import com.vwo.enums.LoggerMessagesEnums;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Took reference from StackOverflow (https://stackoverflow.com/) to:
 * Construct the type 5 UUIDs.
 * Author - Kiskae (https://stackoverflow.com/users/1452094/kiskae)
 * Source - https://stackoverflow.com/questions/40230276/how-to-make-a-type-5-uuid-in-java
 */
public class UUIDUtils {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");
  private static final UUID CONSTANT_NAMESPACE = UUIDUtils.nameUUIDFromNamespaceAndString(UUIDUtils.NAMESPACE_URL, "https://vwo.com");

  public static UUID nameUUIDFromNamespaceAndString(UUID namespace, String name) {
    return nameUUIDFromNamespaceAndBytes(namespace, Objects.requireNonNull(name, "name == null").getBytes(UTF8));
  }

  public static UUID nameUUIDFromNamespaceAndBytes(UUID namespace, byte[] name) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException nsae) {
      throw new InternalError("SHA-1 not supported");
    }
    md.update(toBytes(Objects.requireNonNull(namespace, "namespace is null")));
    md.update(Objects.requireNonNull(name, "name is null"));
    byte[] sha1Bytes = md.digest();
    sha1Bytes[6] &= 0x0f;  /* clear version        */
    sha1Bytes[6] |= 0x50;  /* set to version 5     */
    sha1Bytes[8] &= 0x3f;  /* clear variant        */
    sha1Bytes[8] |= 0x80;  /* set to IETF variant  */
    return fromBytes(sha1Bytes);
  }

  public static String getUUId(Integer account_id, String uId) {
    UUID accountUuid = UUIDUtils.nameUUIDFromNamespaceAndString(CONSTANT_NAMESPACE, account_id.toString());
    UUID userUuid = UUIDUtils.nameUUIDFromNamespaceAndString(accountUuid, uId);
    return userUuid.toString().replace("-", "").toUpperCase();
  }

  private static UUID fromBytes(byte[] data) {
    // Based on the private UUID(bytes[]) constructor
    long msb = 0;
    long lsb = 0;
    assert data.length >= 16;
    for (int i = 0; i < 8; i++) {
      msb = (msb << 8) | (data[i] & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      lsb = (lsb << 8) | (data[i] & 0xff);
    }
    return new UUID(msb, lsb);
  }

  private static byte[] toBytes(UUID uuid) {
    // inverted logic of fromBytes()
    byte[] out = new byte[16];
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();
    for (int i = 0; i < 8; i++) {
      out[i] = (byte) ((msb >> ((7 - i) * 8)) & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      out[i] = (byte) ((lsb >> ((15 - i) * 8)) & 0xff);
    }
    return out;
  }
}
