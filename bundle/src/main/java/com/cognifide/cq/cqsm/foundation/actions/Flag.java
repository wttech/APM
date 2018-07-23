package com.cognifide.cq.cqsm.foundation.actions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Flag {

  public static final String IF_EXISTS = "if_exists";
  public static final String IF_NOT_EXISTS = "if_not_exists";

  public static boolean isIfExists(String flag) {
    return IF_EXISTS.equalsIgnoreCase(flag);
  }

  public static boolean isIfNotExists(String flag) {
    return IF_NOT_EXISTS.equalsIgnoreCase(flag);
  }
}
