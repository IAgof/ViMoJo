package com.videonasocialmedia.videonamediaframework.utils;

/**
 * Created by jliarte on 21/11/16.
 */

public class TimeUtils {
  public static String toFormattedTimeHoursMinutesSecond(int time) {
    int remainingTime = time;

    int hours = remainingTime / com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_HOUR;
    remainingTime -= hours * com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_HOUR;

    int minutes = remainingTime / com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_MINUTE;
    remainingTime -= minutes * com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_MINUTE;

    int seconds = remainingTime / com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_SECOND;
    remainingTime -= seconds * com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_SECOND;

    int milliseconds = remainingTime;

    if (remainingTime > com.videonasocialmedia.vimojo.utils.TimeUtils.MilliSeconds.ONE_SECOND / 2) {
      seconds++;
    }

    if (seconds == com.videonasocialmedia.vimojo.utils.TimeUtils.Seconds.ONE_MINUTE) {
      minutes++;
      seconds -= com.videonasocialmedia.vimojo.utils.TimeUtils.Seconds.ONE_MINUTE;
    }

    return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
            : String.format("%02d:%02d", minutes, seconds);

  }
}
