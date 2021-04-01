package com.kiroule.campsite.booking.api;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator;

public class CustomReplaceUnderscoresDisplayNameGenerator
    extends DisplayNameGenerator.ReplaceUnderscores {

  @Override
  public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
    String methodName = testMethod.getName()
        .replace("__", ", ").replace("_", " ");

    if (testMethod.getAnnotation(DisplayNamePrefix.class) != null) {
      methodName = String
          .format("%s, %s", testMethod.getAnnotation(DisplayNamePrefix.class).value(), methodName);
    }
    return methodName;
  }
}
