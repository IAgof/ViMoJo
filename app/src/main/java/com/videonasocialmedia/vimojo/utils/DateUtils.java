package com.videonasocialmedia.vimojo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alvaro on 13/12/16.
 */

public class DateUtils {

  public static String getDateRightNow(){

    Date today = Calendar.getInstance().getTime();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

    return formatter.format(today);
  }

  public static String toFormatDateDayMonthYear(String date){

    Calendar calendar = Calendar.getInstance();
    Date dateFormat = null;
    try {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    calendar.setTime(dateFormat);

    return calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.MONTH) + " "
        + calendar.get(Calendar.YEAR);
  }
}
