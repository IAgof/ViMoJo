package com.videonasocialmedia.vimojo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alvaro on 13/12/16.
 */

public class DateUtils {
  public static final String OLD_LOCAL_DATE_FORMAT = "yyyy-MM-dd-hh.mm.ss";
  public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss z";
  public static final String API_DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

  public static String getDateRightNow() {
    Date today = Calendar.getInstance().getTime();
    SimpleDateFormat formatter = new SimpleDateFormat(LOCAL_DATE_FORMAT);
    return formatter.format(today);
  }

  public static String toFormatDateDayMonthYear(String date) {
    Calendar calendar = Calendar.getInstance();
    Date dateFormat = parseStringDate(date);
    if (dateFormat == null) {
      return "";
    }
    calendar.setTime(dateFormat);
    return calendar.get(Calendar.DAY_OF_MONTH) + " - " + (calendar.get(Calendar.MONTH) + 1) + " - "
        + calendar.get(Calendar.YEAR);
  }

  public static Date parseStringDate(String date) {
    Date dateFormat = null;
    try {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss z").parse(date);
    } catch (ParseException e) {
      // TODO(jliarte): 9/08/18 now try this format (from API?) Thu Aug 09 11:50:24 GMT+02:00 2018
      try {
        dateFormat = new SimpleDateFormat(API_DATE_FORMAT).parse(date);
      } catch (ParseException e1) {
        try {
          dateFormat = new SimpleDateFormat(OLD_LOCAL_DATE_FORMAT).parse(date);
        } catch (ParseException e2) {
          e2.printStackTrace();
        }
      }
    }
    return dateFormat;
  }
}
