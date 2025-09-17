package dateTimeUtils;

import enumerations.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Gets the year value.
     * This method returns the primitive {@code int} value for the year.
     * @param date the date to check
     * @return the year as an int
     */
    public static int getYear(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
    }

    /**
     * Gets the month-of-year value from 1 to 12.
     * This method returns the month as an {@code int} from 1 to 12.
     * @param date the date to check
     * @return the month-of-year, from 1 to 12
     */
    public static int getMonthValue(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
    }

    /**
     * Gets the day-of-month value.
     * This method returns the primitive {@code int} value for the day-of-month.
     * @param date the date to check
     * @return the day-of-month, from 1 to 31
     */
    public static int getDayOfMonth(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth();
    }

    /**
     * Gets the first day of month of the received date. example: if the date received date is 22/12/2021
     * the method will return 1/12/2021
     * @param date the date to modify
     * @return the first day of month of the received date
     */
    public static LocalDate getFirstDayOfMonth(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1);
    }

    /**
     * Turn unix timestamps into a string of date in a desired pattern
     * @param epoch Unix timestamp.
     * @param datePattern Pattern of date to be returned
     * @return string of date in a desired pattern
     */
    public static String convertEpochToDateString(Long epoch, String datePattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(new Date(epoch));
    }

    /**
     * Get a Date in X days from today (negative or positive) in String format
     * example: getDateInXDaysFromToday("MM/dd/YYYY", -2);
     *
     * @param myformat The format of date
     * @param inXdays  amount of days in the future or past from current date
     * @return The date as String
     * @author Sela Tzvika
     * @since 25.05.2021
     */
    public static String getDateInXDaysFromToday(String myformat, int inXdays){
        Date d = updateDateByNumberOfDays(new Date(), inXdays);
        SimpleDateFormat formatter = new SimpleDateFormat(myformat);
        return formatter.format(d);
    }


    /**
     * Converting a string in a certain format to a Date object
     *
     * @param myformat     The format of date
     * @param myDateString The date as String
     * @return The date as Date
     * @throws Exception when parsing fails
     */
    public static Date convertingStringInFormatToDate(String myformat, String myDateString) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myformat);
        return simpleDateFormat.parse(myDateString);
    }

    /**
     * Converting a Date instance to a String in a format as sent to method
     *
     * @param myformat The format of date
     * @param myDate   Date instance
     * @return Converted String
     */
    public static String convertDateToStringInFormat(String myformat, Date myDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myformat);
        return simpleDateFormat.format(myDate);
    }

    /**
     * Converting a Date instance to a String according to format and timezone sent to method
     *
     * @param myFormat The format of date
     * @param myDate   Date instance
     * @return Converted date as String
     * @author Tzvika.Sela
     * @since 31.01.2023
     */
    public static String convertDateToStringInFormatWithTimezone(String myFormat, Date myDate,String timezone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return simpleDateFormat.format(myDate);
    }

    /**
     * Retrieving a String in a format as sent to method that holds today's date
     *
     * @param myformat The format of date
     * @return String with the specified format
     */
    public static String getTodayDateInFormat(String myformat) {
        Date date = new Date();
        return convertDateToStringInFormat(myformat, date);
    }

    /**
     * Adding or substracting a number of days from a date as sent to method and retrieving the resulted date
     *
     * @param myDate    Original date
     * @param numOfDays Number of days to add (could be a negative number)
     * @return new date
     */
    public static Date updateDateByNumberOfDays(Date myDate, int numOfDays) {
        return updatedDate(myDate, Calendar.DATE, numOfDays);
    }

    /**
     * Adding or substracting a number of days from a date as sent to method and retrieving the resulted date
     *
     * @param myDate    Original date
     * @param numOfDays Number of days to add (could be a negative number)
     * @param FORMAT    date FORMAT for print the date in console
     * @return The calculated date
     */
    public static Date updateDateByNumberOfDaysWithPrintToLog(Date myDate, int numOfDays, String FORMAT) {
        Date newdDate = updateDateByNumberOfDays(myDate, numOfDays);
        printDateToLogAsString(newdDate, FORMAT);
        return newdDate;
    }

    /**
     * Print to log date according to asked format
     *
     * @param myDate Date
     * @param FORMAT Date format
     */
    public static void printDateToLogAsString(Date myDate, String FORMAT){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        logger.info(sdf.format(myDate));
    }

    /**
     * Adding or substracting a number of hours from a date as sent to method and retrieving the resulted date
     *
     * @param myDate     Original date
     * @param numOfHours Number of hours to add (could be a negative number)
     * @return new date
     */
    public static Date updateDateByNumberOfHours(Date myDate, int numOfHours) {
        return updatedDate(myDate, Calendar.HOUR_OF_DAY, numOfHours);
    }

    /**
     * function get date, type of time to add (day,hour..) and amount of asked change
     *
     * @param myDate myDate
     * @param field  type of term to change
     * @param amount amount of asked term to change
     * @return update date after change
     */
    public static Date updatedDate(Date myDate, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(myDate);
        c.add(field, amount);
        return c.getTime();
    }

    /**
     * @param dateForCompare dateForCompare
     * @param startDate      start check range
     * @param endDate        end check range
     * @return False - Not in range. True - In range
     */
    public static boolean verifyDateInRange(Date dateForCompare, Date startDate, Date endDate) {

        return !(dateForCompare.before(startDate) || dateForCompare.after(endDate));

    }

    /**
     * Select from date picker the day date. call this function after open the date picker element
     *
     * @param driver    driver
     * @param dayString the number of the date in month (1,2,..31)
     * @param xpathDay  anchor of day xpath in datepicker
     */
    public static void setDayDateInDatePicker(WebDriver driver, int dayString, String xpathDay) {

        xpathDay = xpathDay + "'" + dayString + "']";
        driver.findElement(By.xpath(xpathDay)).click();

    }

    /**
     * this function return the number of month between second date to first date
     *
     * @param firstDate  original Date
     * @param secondDate asked Date
     * @return number of months
     */
    public static int numOfMonthBetweenDates(Date firstDate, Date secondDate) {

        Calendar cFirstDate = Calendar.getInstance();
        cFirstDate.setTime(firstDate);

        Calendar cSecondDate = Calendar.getInstance();
        cSecondDate.setTime(secondDate);

        int monthsDiff = cSecondDate.get(Calendar.MONTH) - cFirstDate.get(Calendar.MONTH);

        if (monthsDiff < 0) {
            monthsDiff = 12 - Math.abs(monthsDiff);
        }

        return monthsDiff;
    }

    /**
     * this function mavigate in the datepicker to the asked month according to the asked date.
     *
     * @param driver       driver
     * @param originalDate originalDate
     * @param askedDate    askedDate
     * @param xpathNextBtn xpath of next btn in datepicker
     */
    public static void setMonthInDatePicker(WebDriver driver, Date originalDate, Date askedDate, String xpathNextBtn) {
        int askMonth = numOfMonthBetweenDates(originalDate, askedDate);
        if (askMonth > 0) {
            for (int i = 0; i <= askMonth - 1; i++) {
                driver.findElement(By.xpath(xpathNextBtn)).click();
            }
        }
    }

    /**
     * This method converts the date as string pattern
     *
     * @param date              the date we wish to change
     * @param currentDateFormat the existing date current pattern
     * @param newDateFormat     the new pattern we wish to output
     * @return a String representing the date with a new pattern
     * @throws ParseException when fails to parse current date pattern
     */
    public static String changeDateFormat(String date, String currentDateFormat, String newDateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentDateFormat);
        Date parsedDate = simpleDateFormat.parse(date);
        simpleDateFormat.applyPattern(newDateFormat);
        return simpleDateFormat.format(parsedDate);
    }

    /**
     * This method will generate a String timestamp based on <ThreadID>_<ProjectName>_<date+time in milliseconds>
     * @author sela.tzvika
     * @since 27.06.2021
     * @return a String representing the unique timestamp
     */
    public static String getUniqueTimestamp(){
        //get the thread id
        String ThreadID = String.valueOf(Thread.currentThread().getId());
        //get the project name from path, for example: D:\Workspace\customer_area_new
        String projectPath = System.getProperty("user.dir");
        File file = new File(projectPath);
        String projectName = file.getName();
        //get the date-time in millis
        String dateTimeInMillis = "";
        try {
            dateTimeInMillis = getDateInXDaysFromToday("yyyyMMdd_hhmmssSSS", 0);
        }
        catch (Exception e){
            logger.error(Arrays.toString(e.getStackTrace()));
        }

        return ThreadID + "_" + projectName + "_" + dateTimeInMillis;
    }

    /** This method will generate a String timestamp based on <ThreadID>_<date+time in milliseconds>
     *
     * @return a String representing the unique timestamp
     * @author umflat.lior
     * @since 13.6.2023
     */
    public static String getUniqueTimestamp2(){
        //get the thread id
        String ThreadID = String.valueOf(Thread.currentThread().getId());

        //get the date-time in millis
        String dateTimeInMillis = "";
        try {
            dateTimeInMillis = getDateInXDaysFromToday("yyyyMMdd_hhmmss", 0);
        }
        catch (Exception e){
            logger.error(Arrays.toString(e.getStackTrace()));
        }

        return ThreadID + "_" + dateTimeInMillis;
    }

    /** convert String date in format dd.MM.yyyy to XMLGregorianCalendar format
     *
     * @param date the date in String format dd.MM.yyyy
     * @return the date in XMLGregorianCalendar format
     * @author umflat.lior
     * @since 6.10.2022
     */
    public static XMLGregorianCalendar convertStringToXMLGregorianCalendar(String date) throws DatatypeConfigurationException {
        XMLGregorianCalendar gDate = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar(Integer.parseInt(date.substring(6)),
                        Integer.parseInt(date.substring(3,5)) -1,
                        Integer.parseInt(date.substring(0,2))));
        return gDate;
    }

    /**
     * add current date and time as a string
     *
     * @param pattern - pattern of the date
     * @return the date in pattern format
     */
    public String getCurrentDateAndTimeAsString(String pattern) {
        Date date = new Date();
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * class that stores different kind of date patterns as String
     */
    public static class DatePatterns {
        //Date patterns - https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        public static final String DDMMYYYY_HHMMSS = "ddMMyyyy HHmmss";
        public static final String DDMMMYYYY = "dd-MMM-yyyy";
        public static final String DAY_OF_MONTH = "d";
    }


    /**
     * Retrieve number of day in week of present day
     * @return number of day in week of present day (e.g. 1 for Sunday)
     * @author plot.ofek
     * @since 22.07.2021
     */
    public static int getNumberOfDayInTheWeekOfPresentDay() {

        return getNumberOfDayInTheWeekForAGivenDate(new Date());

    }

    /**
     * Retrieve number of day in week for a given date
     * @param date The date for which to retrieve the number of day in a week
     * @return number of day in week for a given date (e.g. 1 for Sunday)
     * @author plot.ofek
     * @since 22.07.2021
     */
    public static int getNumberOfDayInTheWeekForAGivenDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * Return the asked date.If the current date is 24.10.2021 and I want to get the 10-Aug-2021:
     *                         getSpecificDateOfDayCustomPreviousMonth(dd-MMM-yyyy,2,"10")
     * @param datePattern date pattern
     * @param monthToSubtract number of months to reduce
     * @param dayOfMonth The specific needed day in month
     * @return specific date of the relevant months
     */
    public String getSpecificDateOfDayCustomPreviousMonth(String datePattern, long monthToSubtract,String dayOfMonth) {
        if(dayOfMonth.length()==1){
            dayOfMonth="0" + dayOfMonth;
        }
        return dayOfMonth.concat(DateTimeFormatter.ofPattern(datePattern).format(LocalDate.now().minusMonths(monthToSubtract)).substring(2));
    }

    /**
     * this function return difference seconds between 2 dates as long number
     * @param firstDate - first date
     * @param secondDate - second date
     * @param timeUnit - calculate difference as time unit
     * @return Difference Between Dates according to value differenceAs
     * @author Yael Rozenfeld
     * @since 25.1.2022
     */
    public static double getDifferenceBetweenDates(Date firstDate, Date secondDate, TimeUnit timeUnit) {
        Long difference = secondDate.getTime()-firstDate.getTime();
        return convertMillisecondsToAnotherTimeUnit(difference,timeUnit);
    }

    /**
     * convert milliseconds to another time unit
     * @param milliseconds - long number of millisecond
     * @param timeUnit - time unit to convert
     * @return - double number after convert to the request time unit
     * @author Yael Rozenfeld
     * @since 26.01.2022
     */
    public static double convertMillisecondsToAnotherTimeUnit(Long milliseconds, TimeUnit timeUnit) {

        switch (timeUnit){
            case MILLISECOND:{
                return milliseconds.doubleValue();
            }
            case SECOND:{
                return milliseconds.doubleValue()/1000;
            }
            case MINUTE:{
                return milliseconds.doubleValue()/1000/60;
            }
            case HOUR:{
                return milliseconds.doubleValue()/1000/60/60;
            }
            case DAY:{
                return milliseconds.doubleValue()/1000/60/60/24;
            }
            default:
                throw new Error(String.format("timeUnit %s isn't valid. can't convert milliseconds to another timeUnit.",timeUnit));
        }

    }

    /** add x hours to the current time and return the time and date according to the format that was send to the method
     *
     * @param x number of hours to add to the current time
     * @param dateFormat the format of the date and time that will be returned
     * @return the time and date in x hours than now
     * @author umflat.lior
     * @since 24.7.2023
     */
    public static String addHoursToCurrentTime(int x, String dateFormat) {
        // Get the current date and time
        Calendar now = Calendar.getInstance();

        // Add x hours to the current date and time
        now.add(Calendar.HOUR_OF_DAY, x);

        // Get the updated date and time as a Date object
        Date updatedDate = now.getTime();

        // Format the date and time using the provided date format
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDateTime = sdf.format(updatedDate);

        return formattedDateTime;
    }

    /** add x minutes to the current time and return the time and date according to the format that was send to the method
     *
     * @param x number of minutes to add to the current time
     * @param dateFormat the format of the date and time that will be returned
     * @return the time and date in x hours than now
     * @author umflat.lior
     * @since 12.9.2023
     */
    public static String addMinutesToCurrentTime(int x, String dateFormat) {
        // Get the current date and time
        Calendar now = Calendar.getInstance();

        // Add x minutes to the current date and time
        now.add(Calendar.MINUTE, x);

        // Get the updated date and time as a Date object
        Date updatedDate = now.getTime();

        // Format the date and time using the provided date format
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDateTime = sdf.format(updatedDate);

        return formattedDateTime;
    }

}
