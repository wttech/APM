package com.cognifide.apm.core.utils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class LogUtils {

  private static final String slackURL = "https://hooks.slack.com/services/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";

  private static String getInstanceName() {
    return ManagementFactory.getRuntimeMXBean().getName();
  }

  private static HttpURLConnection createPostRequest(String url, String data) throws IOException {
    HttpURLConnection post = (HttpURLConnection) new URL(url).openConnection();
    post.setRequestMethod("POST");
    post.setDoOutput(true);
    post.setRequestProperty("Content-Type", "application/json");
    post.getOutputStream().write(data.getBytes("UTF-8"));
    return post;
  }

  public static void log(Logger logger, String message) {
    if (logger != null) {
      logger.info(message);
    }
    sendLog(logger, message);
  }

  private static void sendLog(Logger logger, String message) {
    String instanceName = getInstanceName();
    String executionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss.SSS"));
    String data = String.format("{\"text\": \"%s Message: %s, Instance: %s\"}", executionTime, message, instanceName);
    try {
      HttpURLConnection postRequest = createPostRequest(slackURL, data);

      int postRC = postRequest.getResponseCode();
      if (postRC == 200) {
        String text = IOUtils.toString(postRequest.getInputStream(), StandardCharsets.UTF_8.name());
        logger.info(text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
