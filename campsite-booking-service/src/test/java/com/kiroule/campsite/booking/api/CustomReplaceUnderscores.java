package com.kiroule.campsite.booking.api;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator;

public class CustomReplaceUnderscores extends DisplayNameGenerator.ReplaceUnderscores {

  @Override
  public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
    String methodName = testMethod.getName().replace("__", ", ").replace("_", " ");

    if (nonNull(testMethod.getAnnotation(DisplayNamePrefix.class))) {
      methodName =
          format("%s, %s", testMethod.getAnnotation(DisplayNamePrefix.class).value(), methodName);
    }
    return methodName;
  }
}
