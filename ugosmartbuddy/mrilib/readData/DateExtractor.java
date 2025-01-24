package com.inventive.ugosmartbuddy.mrilib.readData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateExtractor {

	public static String now() {
        return new SimpleDateFormat("HH:mm:ss.SSS")
                .format(Calendar.getInstance().getTime());
    }

	/**
	 * convert string date(yyyy-mm-dd hh:mm:ss) to date according to ist
	 * @param dateVal
	 * @return
	 */
	public static Calendar convertDateToIstCal(String dateVal) throws InvalidDateFormatException, NumberFormatException
	{
		Calendar start = null;
		try {
			String datetime[] = dateVal.split(" ");
			String dates[] = datetime[0].split("-");
			String times[] = datetime[1].split(":");

			start = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0])); // set hour to
			// midnight
			start.set(Calendar.MINUTE, Integer.parseInt(times[1])); // set minute in
			// hour
			start.set(Calendar.SECOND, Integer.parseInt(times[2])); // set second in
			// minute
			start.set(Calendar.MILLISECOND, 0);
			//start.add(java.util.Calendar.DATE, -30);

			start.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1])-1,Integer.parseInt(dates[2])); // set hour to

		}
		catch (NumberFormatException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return start;
	}
}
