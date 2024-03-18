package com.cognifide.apm.core.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;

@Model(adaptables = {SlingHttpServletRequest.class})
public class DateFormatter {

  @RequestAttribute
  private Calendar date;

  private String formattedDate;

  @PostConstruct
  private void afterCreated() {
    formattedDate = determineFormattedDate(date);
  }

  public static String determineFormattedDate(Calendar date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return simpleDateFormat.format(date.getTime());
  }

  public String getFormattedDate() {
    return formattedDate;
  }
}
