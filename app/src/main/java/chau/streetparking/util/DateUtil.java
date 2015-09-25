package chau.streetparking.util;

/**
 * Created by Chau Thai on 9/25/2015.
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by chauthai on 12/18/14.
 */
public class DateUtil {

    public static final String DATE_STRING_FORMAT_1 = "MM/dd/yyyy HH:mm";
    public static final String UTC_TIME_ZONE = "UTC";
    public static final String GMT_TIME_ZONE = "GMT";

    /**
     * Get Date object from a string of UTC timezone in the format "yyyy-MM-dd HH:mm:ss"
     * @param dateString in the format "yyyy-MM-dd HH:mm:ss"
     * @return Date object
     * @throws ParseException invalid format
     * @throws NullPointerException null string
     */
    public static Date getDateFromStringUTC(String dateString) throws ParseException, NullPointerException {
        return getDateFromString(dateString, UTC_TIME_ZONE, DATE_STRING_FORMAT_1);
    }

    /**
     * Get Date object from a string of a specific timezone and date format
     * @param dateString in the provided dateFormat
     * @param timeZone "UTC" or "GMT"
     * @param dateFormat valid date format
     * @return Date object
     * @throws ParseException dateString is not in the provided format
     * @throws IllegalArgumentException dateFormat is not valid
     * @throws NullPointerException null params
     */
    public static Date getDateFromString(String dateString, String timeZone, String dateFormat)
            throws ParseException, IllegalArgumentException, NullPointerException  {

        if (dateString == null)
            throw new NullPointerException("Null dateString");
        if (timeZone == null)
            throw new NullPointerException("Null timeZone");
        if (dateFormat == null)
            throw new NullPointerException("Null dateFormat");

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date = df.parse(dateString);

        return date;
    }

    /**
     * Get string from a Date object in the provided format
     * @param date Date object to be converted into string
     * @param format the format which Date object will be converted into
     * @return Converted string represents the Date object
     * @throws IllegalArgumentException invalid format
     * @throws NullPointerException null params
     */
    public static String getStringFromDate(Date date, String format)
            throws IllegalArgumentException, NullPointerException {

        if (date == null || format == null) {
            throw new NullPointerException("Null param");
        }

        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Get elapsed time from now in String format
     * @param date past date to be calculate the elapse time
     * @return String which simplifies the elapsed time format to just "years", "months",
     *          "days" or "hours" or "minutes" or "Just now"
     * @throws java.lang.NullPointerException null date param
     */
    public static String getElapsedTimeFromNow(Date date) throws NullPointerException{
        if (date == null)
            throw new NullPointerException("Null Date object");

        long duration = new Date().getTime()  - date.getTime();

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        long diffInMonths = diffInDays / 30;
        long diffInYears = diffInMonths / 365;

        if (diffInYears > 0) {
            if (diffInYears == 1)
                return Long.toString(diffInYears) + " year ago";
            return Long.toString(diffInYears) + " years ago";
        }

        else if (diffInMonths > 0) {
            if (diffInMonths == 1)
                return Long.toString(diffInMonths) + " month ago";
            return  Long.toString(diffInMonths) + " months ago";
        }

        else if (diffInDays > 0) {
            if (diffInDays == 1)
                return Long.toString(diffInDays) + " day ago";
            return Long.toString(diffInDays) + " days ago";
        }

        else if (diffInHours > 0) {
            if (diffInHours == 1)
                return Long.toString(diffInHours) + " hour ago";
            return Long.toString(diffInHours) + " hours ago";
        }

        else if (diffInMinutes > 0) {
            if (diffInMinutes == 1)
                return Long.toString(diffInMinutes) + " minute ago";
            return Long.toString(diffInMinutes) + " minutes ago";
        }

        else
            return "Just now";
    }

}