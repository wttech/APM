package com.cognifide.apm.core.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LabelUtils {

  public static String capitalize(Object item) {
    return capitalize(item.toString());
  }

  public static String capitalize(String label) {
    return Arrays.stream(label.replace('_', ' ').split(" "))
        .map(StringUtils::capitalize)
        .collect(Collectors.joining(" "));
  }

}
