package dateTimeUtils;

import objectsUtils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.Year.isLeap;

/**
 * A DateTime object that holds methods for dates
 * Please note that this object has instance methods and static methods.
 * i.e:
 * You can either initialize an instance of DateTime that holds the date and use the methods on this instance, or
 * You can refer to the static methods with a different instance or without, according to the method.
 * For example - you can use the static method: DateTime.now() - the method will return the current local date and time.
 * another example:
 * static String toStringFormat(DateTime dateTime, String format) - the static method receives a DateTime instance and Date format and will return the Date String, while
 * String toStringFormat(String format) - the non-static method receives the Date format and will return the Date String of the current DateTime instance
 * @author genosar.dafna
 * @since 20.09.2022
 */
@SuppressWarnings("unused")
public class DateTime implements Comparable<DateTime>{

   /* public static void main(String args[]){
        DateTime dateTime = new DateTime(2020,10,10,10,00,00);
        /*dateTime = dateTime.convertToUTC("+03:00");
        logger.info(dateTime.toLongDateTimeString());*/

       /* dateTime = dateTime.convertDateTimeFromTzToTz("+03:00","+05:00");
        logger.info(dateTime.toLongDateTimeString());
    }*/

    private static final Logger logger = LoggerFactory.getLogger(DateTime.class);

    private final Calendar calendar;

    public DateTime(DateTime dateTime){
        this(dateTime.getDateObject());
    }

    public DateTime(int year, int month, int day){
        this(year, month, day, 0, 0,0);
    }

    public DateTime (int year, int month, int day, int hour, int minutes){
        this(year, month, day, hour, minutes,0);
    }

    public DateTime (int year, int month, int day, int hour, int minutes, int seconds){

        if(day < 1 || day > 31)
            throw new Error(String.format("Cannot create DateTime instance. <br> The day value must be between 1-31. Received: %d", day));

        if(month < 1 || month > 12)
            throw new Error(String.format("Cannot create DateTime instance. <br> The month value must be between 1-12. Received: %d", month));

        //If the date contains February 29, Check if it is in leap year
        if((month == 2) && (day > 28) && (!isLeapYear(year)))
            throw new Error(String.format("Cannot create DateTime instance for February %d %d. <br> The year %d is not a leap year", day, year, year));

        if(hour < 0 || hour > 23)
            throw new Error(String.format("Cannot create DateTime instance. <br> The hour value must be between 0-23. Received: %d", hour));

        if(minutes < 0 || minutes > 59)
            throw new Error(String.format("Cannot create DateTime instance. <br> The minutes value must be between 0-59. Received: %d", minutes));

        if(seconds < 0 || seconds > 59)
            throw new Error(String.format("Cannot create DateTime instance. <br> The seconds value must be between 0-59. Received: %d", seconds));

        this.calendar = new GregorianCalendar();
        calendar.set(year, convertToJavaCalendarMonthIntValue(month), day, hour, minutes,seconds);
    }

    public DateTime(Calendar calendar){
        this.calendar = calendar;
    }

    public DateTime(Date date){
        this.calendar = new GregorianCalendar();
        this.calendar.setTime(date);
    }

    public DateTime(LocalDateTime localDateTime){
        this.calendar = new GregorianCalendar();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        calendar.setTime(date);
    }

    /**
     * @return an object of type Date.java
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public Date getDateObject(){
        return this.calendar.getTime();
    }

    /**
     * @return an object of type LocalDateTime.java
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public LocalDateTime getLocalDateTimeObject(){

        // Getting the timezone
        TimeZone tz = getTimeZone();

        // Getting zone id
        ZoneId zoneId = tz.toZoneId();

        //conversion
        return LocalDateTime.ofInstant(this.calendar.toInstant(), zoneId);
    }

    /**
     * @return this instance timezone
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public TimeZone getTimeZone(){
        return this.calendar.getTimeZone();
    }

    /**
     * Static method
     * @return A DateTime object whose value is the current local date and time.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static DateTime now() {
        Calendar calendar = Calendar.getInstance();
        return new DateTime(calendar);
    }

    /**
     * Static method
     * @return A DateTime object that is set to today's date, with the time component set to 00:00:00.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static DateTime today() {
        Calendar calendar = Calendar.getInstance();
        int calendarMonth = calendar.get(Calendar.MONTH);
        int georgianMonth = calendarMonth + 1;
        DateTime dt = new DateTime(calendar.get(Calendar.YEAR), georgianMonth, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        dt.calendar.clear(Calendar.MILLISECOND);
        return dt;
    }

    /**
     * @return The year component of THIS DateTime instance. between 1 and 9999.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getYear() {
        return this.calendar.get(Calendar.YEAR);
    }

    /**
     * @return The month component of THIS DateTime instance, expressed as a value between 1 and 12.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getMonth() {
        return this.calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * @return The day component of THIS DateTime instance, expressed as a value between 1 and 31.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getDay () {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return The hour component of THIS DateTime instance, expressed as a value between 0 and 23.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getHour () {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @return The minute component of THIS DateTime instance, expressed as a value between 0 and 59.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getMinute () {
        return this.calendar.get(Calendar.MINUTE);
    }

    /**
     * @return The seconds component of THIS DateTime instance, expressed as a value between 0 and 59.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getSecond () {
        return this.calendar.get(Calendar.SECOND);
    }

    /**
     * @return The milliseconds component of THIS DateTime instance.
     * @author genosar.dafna
     * @since 22.08.2022
     */
    public int getMilliSecond () {
        return this.calendar.get(Calendar.MILLISECOND);
    }

    /**
     * Get the week number in the date's year
     * @param firstDayOfWeek the first day of the week as an int or the calendar day, like: Calendar.SUNDAY
     * SUNDAY is 1 - SATURDAY is 7
     * @author genosar.dafna
     * @since 29.01.2024
     */
    public int getWeekOfYear(int firstDayOfWeek){
        if(firstDayOfWeek < 1 || firstDayOfWeek > 7)
            throw new Error(String.format("firstDayOfWeek value received in getWeekOfYear() method must be between 1-7. Received: %d", firstDayOfWeek));

        Calendar calendar = this.calendar;

        calendar.setFirstDayOfWeek(firstDayOfWeek);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Get the date of the first day of the given year's week
     * @param week the desired week of the year
     * @param year the year
     * @param firstDayOfWeek the first day of the week as an int or the calendar day, like: Calendar.SUNDAY
     * SUNDAY is 1 - SATURDAY is 7
     * @return the date of the first day of the given year's week
     * For example: the 1st date of week 5 in year 2024 (when the 1st day is set to Sunday) is 28/01/2024
     * When the first day is set to Monday, the date will be 29/01/2024
     * @author genosar.dafna
     * @since 12.02.2024
     */
    public static DateTime getFirstDayOfWeek(int week, int year, int firstDayOfWeek){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, year);
        calendar.setFirstDayOfWeek(firstDayOfWeek);

        Date date = calendar.getTime();
        return new DateTime(date);
    }

    /**
     * @return The first day of this DateTime instance week as an int between 1-7
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getFirstDayOfWeek() {
        return this.calendar.getFirstDayOfWeek();
    }

    /**
     * @return The first day of this instance week as a DayOfWeek enum
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DayOfWeek getFirstDayOfWeekName () {
        int dayInNumber = getFirstDayOfWeek();
        return DateTime.getDayOfWeekName(dayInNumber);
    }

    /**
     * Static method - Gets the day of the week as an ENUM Value, according to the received int value
     * @param dayInNumber the int value of the day to return Sunday(1) to Saturday (7)
     * @return the day of the week as a DayOfWeek enum
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static DayOfWeek getDayOfWeekName(int dayInNumber) {

        if(dayInNumber<1 || dayInNumber>7)
            throw new Error(String.format("Cannot return day of the week. The int value must be between 1-7.<br>Received value: %d", dayInNumber));

        DayOfWeek dayOfWeek;
        if(dayInNumber == 1)
            dayOfWeek = DayOfWeek.of(7);
        else
            dayOfWeek = DayOfWeek.of(dayInNumber-1);

        return dayOfWeek;
    }

    /**
     * Gets the day of the week of THIS instance as an ENUM Value
     * The resulting value ranges from Sunday(1) to Saturday (7).
     * @return the day of the week as a DayOfWeek enum
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DayOfWeek getDayOfWeekName() {

        int dayInNumber = getDayOfWeekNumber(); //1 (Sunday) to 7 (Saturday)
        return DateTime.getDayOfWeekName(dayInNumber);
    }

    /**
     * Gets the day of this instance week as an int.
     * The resulting number ranges from 1 (Sunday) to 7 (Saturday).
     * @return the day of the week as an int
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int getDayOfWeekNumber() {
        return this.calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT (Epoch time)
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public long getTime() {
        return this.calendar.getTime().getTime();
    }

    /**
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT (Epoch time)
     * @author genosar.dafna
     * @since 07.11.2024
     */
    public long getEpochTime() {
        return this.calendar.getTime().getTime();
    }

    /**
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT (Epoch time)
     * @author genosar.dafna
     * @since 07.11.2024
     */
    public static long getEpochTime(DateTime date) {
        return date.calendar.getTime().getTime();
    }

    /**
     * Get the current UTC DateTime
     * @return the current UTC DateTime
     * @author genosar.dafna
     * @since 19.12.2022
     */
    public static DateTime getCurrentUtcDateTime(){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        return new DateTime(offsetDateTime.toLocalDateTime());
    }

    /**
     * Convert Zulu time to the given zone and return in the required pattern
     * @param zuluDateTime example: "2025-08-07T13:00:00.000Z"
     * @param zoneId like "Asia/Jerusalem"
     * @param pattern like "yyyy-MM-dd HH:mm:ss"
     * @return converted zulu time to the required zone and pattern
     * @author genosar.dafna
     * @since 10.08.2025
     */
    public static String convertZuluTimeToZoneTime(String zuluDateTime, String zoneId, String pattern){

        // Parse as Instant (UTC)
        Instant instant = Instant.parse(zuluDateTime);

        // Convert to local timezone (Israel)
        ZonedDateTime localTime = instant.atZone(ZoneId.of(zoneId));

        // Format for printing
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localTime.format(formatter);
    }

    /**
     * Convert zone time to Zulu time
     * @param zoneTime example: "2025-02-08T17:52:44+03:00"
     * @return converted zone time to Zulu time
     * @author genosar.dafna
     * @since 10.08.2025
     */
    public static String convertZoneTimeToZuluTime(String zoneTime){

        Instant instant = Instant.parse(zoneTime);
        return instant.toString();
    }

    /**
     * Compares the time values (millisecond offsets) between 2 DateTime objects.
     * @param dateTimeToCompareTo another DateTime object to compare to
     * @return the value 0 if the time represented by the instance is equal to the time represented by the param;
     *         the value -1 if the time represented by the instance is before the time represented by the param;
     *         the value 1 if the time represented by the instance is after the time represented by the param
     * @author genosar.dafna
     * @since 20.09.2022
     * @since 22.08.2023
     */
    @Override
    public int compareTo(DateTime dateTimeToCompareTo) {
        return this.calendar.compareTo(dateTimeToCompareTo.calendar);
    }

    public int compareTo(DateTime dateTimeToCompareTo, boolean compareMillisecAsWell){
        if(!compareMillisecAsWell){
            DateTime dt =  new DateTime(getYear(), getMonth(), getDay(), 0, 0, 0);
            dt.calendar.clear(Calendar.MILLISECOND);

            dateTimeToCompareTo.calendar.clear(Calendar.MILLISECOND);

            return dt.calendar.compareTo(dateTimeToCompareTo.calendar);
        }
        return this.calendar.compareTo(dateTimeToCompareTo.calendar);
    }

    public Calendar getCalendar(){
        return this.calendar;
    }

    /**
     * Clear the milisec to be 0
     * @return the datetime
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public DateTime clearMilliseconds(){
        this.calendar.clear(Calendar.MILLISECOND);
        return this;
    }

    /**
     * Clear the seconds to be 0
     * @return the datetime
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public DateTime clearSeconds(){
        this.calendar.set(Calendar.SECOND, 0);
        return this;
    }

    /**
     * Clear the minutes to be 0
     * @return the datetime
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public DateTime clearMinutes(){
        this.calendar.set(Calendar.MINUTE, 0);
        return this;
    }

    /**
     * Clear the hours to be 0
     * @return the datetime
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public DateTime clearHours(){
        this.calendar.set(Calendar.HOUR_OF_DAY, 0);
        return this;
    }

    /**
     * Clear the time to be 12am
     * @return the datetime
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public DateTime clearTime(){
        return new DateTime(getYear(), getMonth(), getDay(), 0, 0, 0);
    }

    /**
     * Static method
     * @param year The year.
     * @param month The month (a number ranging from 1 to 12).
     * @return Returns the number of days in the specified month and year.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static int daysInMonth(int year, int month){
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    /**
     * @return Returns the number of days in the INSTANCE month and year.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public int numOfDaysInMonth(){
        YearMonth yearMonthObject = YearMonth.of(getYear(), getMonth());
        return yearMonthObject.lengthOfMonth();
    }

    /**
     * Checks if the instance and the param have the same date and time.
     * @param dateTimeToCompareTo a DateTime to compare to
     * @return true/false if the date and time of the instance and the param are the same
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public boolean equals(DateTime dateTimeToCompareTo){
        return compareTo(dateTimeToCompareTo) == 0;
    }

    /**
     * Static method
     * Returns an indication whether the specified year is a leap year.
     * @param year the year
     * @return true if year is a leap year; otherwise, false.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static boolean isLeapYear(int year){
        return isLeap(year);
    }

    /**
     * Returns an indication whether the INSTANCE'S year is a leap year.
     * @return true if the INSTANCE'S year is a leap year; otherwise, false.
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public boolean isInLeapYear(){
        return isLeap(getYear());
    }

    /**
     * Return true is the date string is in US style (months before days)
     * @param dateFormat the date format
     * @return true if the date string is in US style (months before days)
     * @author genosar.dafna
     * @since 29.06.2023
     */
    public static boolean isFormatInUsStyle(String dateFormat){

        boolean isMonthFirst = false;

        //if the date string does not contain M - then it is not a US style - return false
        if(dateFormat.contains("M")){
            //if the date string contains d the check which letter is first
            if(dateFormat.contains("d")){
                isMonthFirst = StringUtils.isLetterBefore(dateFormat, 'M', 'd');
            }
            else
                isMonthFirst = true;
        }
        return isMonthFirst;
    }

    /**
     * Static method
     * Converts the string representation of a date and time to DateTime
     * @param dateString A string that contains a date and time to convert
     * @param format the required format
     * @return A DateTime object that is equivalent to the date and time contained in the String
     * @author genosar.dafna
     * @since 28.12.2023
     */
    public static DateTime parse(String dateString, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            logger.info(String.format("Parsing date String : %s | in format : %s", dateString, format));
            Date date = sdf.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return new DateTime(cal);
        }
        catch (ParseException e) {
            throw new Error(String.format("Failed to parse date String '%s'<br>Error: %s", dateString, e.getMessage()));
        }
    }

    /**
     * Static method
     * Converts the string representation of a date and time to DateTime
     * If no matching format is returned, you can add the format to the list DateParser
     * @param dateString A string that contains a date and time to convert
     * @return A DateTime object that is equivalent to the date and time contained in the String
     * @author genosar.dafna
     * @since 20.09.2022
     * @since 28.12.2023
     */
    public static DateTime parse(String dateString){
        return parse(dateString, false);
    }

    public static DateTime parse(String dateString, @Nullable Boolean usStyle){
        if (dateString == null){
            throw new Error("Failed to parse date String. dateString is null");
        }
        if (dateString.equals("")){
            throw new Error("Failed to parse date String. dateString is empty");
        }

        //Determine the format
        String format = DateParser.determineDateFormat(dateString, usStyle);
        if (format == null){
            throw new Error(String.format("Failed to parse date String '%s'. The format could not be recognized", dateString));
        }

        return parse(dateString, format);
    }

    /**
     * Adds/increments the specified number of days to/from the value of this instance.
     * @param value number of days to add/increment. minus number would decrement the days
     * @return the instance after the addition/incrementation of the days
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addDays(int value){
        this.calendar.add(Calendar.DATE, value); //minus number would decrement the days
        return this;
    }

    /**
     * Adds/increments the specified number of months to/from the value of this instance.
     * @param value number of months to add/increment. minus number would decrement the months
     * @return the instance after the addition/incrementation of the months
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addMonths(int value){
        this.calendar.add(Calendar.MONTH, value); //minus number would decrement the months
        return this;
    }

    /**
     * Adds/increments the specified number of years to/from the value of this instance.
     * @param value number of years to add/increment. minus number would decrement the years
     * @return the instance after the addition/incrementation of the years
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addYears(int value){
        this.calendar.add(Calendar.YEAR, value); //minus number would decrement the years
        return this;
    }

    /**
     * Adds/increments the specified number of hours to/from the value of this instance.
     * @param value number of hours to add/increment. minus number would decrement the hours
     * @return the instance after the addition/incrementation of the hours
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addHours(int value){
        this.calendar.add(Calendar.HOUR, value); //minus number would decrement the hours
        return this;
    }

    /**
     * Adds/increments the specified number of minutes to/from the value of this instance.
     * @param value number of minutes to add/increment. minus number would decrement the minutes
     * @return the instance after the addition/incrementation of the minutes
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addMinutes(int value){
        this.calendar.add(Calendar.MINUTE, value); //minus number would decrement the minutes
        return this;
    }

    /**
     * Adds/increments the specified number of seconds to/from the value of this instance.
     * @param value number of seconds to add/increment. minus number would decrement the seconds
     * @return the instance after the addition/incrementation of the seconds
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public DateTime addSeconds(int value){
        this.calendar.add(Calendar.SECOND, value); //minus number would decrement the seconds
        return this;
    }

    /**
     * Adds/increments the specified number of milliseconds to/from the value of this instance.
     * @param value number of milliseconds to add/increment. minus number would decrement the seconds
     * @return the instance after the addition/incrementation of the milliseconds
     * @author genosar.dafna
     * @since 22.08.2022
     */
    public DateTime addMilliSeconds(int value){
        this.calendar.add(Calendar.MILLISECOND, value); //minus number would decrement the seconds
        return this;
    }

    public DateTime addTime(Time time){
        addSeconds((int)time.totalSeconds);
        return this;
    }

    public DateTime addTime(String timeString){
        Time time = new Time(timeString);
        boolean add = ((time.plusMinusPrefix == null) || (time.plusMinusPrefix.equals("+")));
        if(add)
            addSeconds((int)time.totalSeconds);
        else
            addSeconds(-(int)time.totalSeconds);
        return this;
    }

    /**
     * Subtract a dateTime from the current instance and return the different in milliseconds as a long
     * @param dateTime DateTime to subtract
     * @return long that represents the difference in milliseconds between the current instance and the given one
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public long subtract(DateTime dateTime){
        Date firstDate = this.calendar.getTime();
        Date secondDate = dateTime.calendar.getTime();
        return firstDate.getTime() - secondDate.getTime();
    }

    /**
     * Get the time difference between the 2 Datetimes and return a Time object that holds parameters, like hours, minutes, seconds
     * @param endDateTime the Datetime to compare to
     * @return the time difference between the 2 Datetimes as Time object
     * @author genosar.dafna
     * @since 28.10.2022
     */
    public Time getTimeDifference(DateTime endDateTime){
        long timeStamp = endDateTime.subtract(this) ;
        return new Time(timeStamp);
    }

    /**
     * Get the days difference between the 2 Datetimes and return
     * @param firstDate the first date
     * @param secondDate the second date
     * @return the days difference between the 2 Datetimes and return
     * @author genosar.dafna
     * @since 29.01.2024
     */
    public static long getDaysDifference(DateTime firstDate, DateTime secondDate)
    {
        return ChronoUnit.DAYS.between(firstDate.getDateObject().toInstant(), secondDate.getDateObject().toInstant());
    }

    /**
     * Get the weeks difference between the 2 Datetimes and return
     * @param firstDate the first date
     * @param secondDate the second date
     * @return the weeks difference between the 2 Datetimes and return
     * @author genosar.dafna
     * @since 29.01.2024
     */
    public static int getWeeksDifference(DateTime firstDate, DateTime secondDate) {

        if (secondDate.compareTo(firstDate) < 0) {
            return -getWeeksDifference(secondDate, firstDate);
        }
        firstDate = resetTime(firstDate);
        secondDate = resetTime(secondDate);

        Calendar cal = new GregorianCalendar();
        cal.setTime(firstDate.getDateObject());
        int weeks = 0;
        while (firstDate.compareTo(secondDate) < 0) {
            // add another week
            firstDate.calendar.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }

    /**
     * Get the months difference between the 2 Datetimes and return
     * @param firstDate the first date
     * @param secondDate the second date
     * @return the weeks difference between the 2 Datetimes and return
     * @author genosar.dafna
     * @since 29.01.2024
     */
    public static int getMonthsDifference(DateTime firstDate, DateTime secondDate) {

        LocalDate startDate = LocalDate.of(firstDate.getYear(), firstDate.getMonth(), firstDate.getDay());
        LocalDate endDate = LocalDate.of(secondDate.getYear(), secondDate.getMonth(), secondDate.getDay());

        // Calculate the period between the two dates
        Period period = Period.between(startDate, endDate);

        // Extract the number of months from the period
        return period.getYears() * 12 + period.getMonths();
    }

    public static DateTime resetTime (DateTime dateTime) {
        Calendar cal = dateTime.calendar;
        cal.setTime(dateTime.getDateObject());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();

        return new DateTime(date);
    }

    /**
     * Change the DateTime to a different time zone
     * @param timeZone the time zone to change to.
     * Can be the long name of the timezone, like 'Asia/Jerusalem' or the short version, like 'IST'
     * @return the current DateTime object in the selected timezone
     * @author genosar.dafna
     * @since 19.12.2022
     */
    public DateTime toTimeZone(String timeZone){
        Calendar cal = this.calendar.getInstance(TimeZone.getTimeZone(timeZone), Locale.getDefault());
        return new DateTime(cal);
    }

    /**
     * Convert the Datetime from Locale to UTC DateTime
     * @return the current Datetime object in UTC DateTime
     * @author genosar.dafna
     * @since 19.12.2022
     */
    public DateTime fromLocaleToUtcDateTime(){
        Date date = this.getDateObject();
        OffsetDateTime offsetDateTime = date.toInstant().atOffset(ZoneOffset.UTC);
        return new DateTime(offsetDateTime.toLocalDateTime());
    }

    /**
     * Converts the value of the current instance to its equivalent long date string representation: dddd, MMMM d, yyyy
     * Example: Wednesday, May 16, 2001
     * @return the value of the current instance to its equivalent long date string representation: dddd, MMMM d, yyyy
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toLongDateString(){
        return toStringFormat("EEEE, MMMM d, yyyy");
    }

    /**
     * Converts the value of the current instance to its equivalent long date and time string representation: dddd, MMMM d, yyyy h:mm:ss a
     * Example: Wednesday, May 16, 2001 14:05:59 PM
     * @return the value of the current instance to its equivalent long date and time string representation: dddd, MMMM d, yyyy h:mm:ss a
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toLongDateTimeString(){
        return toStringFormat("EEEE, MMMM d, yyyy h:mm:ss a");
    }

    /**
     * Converts the value of the current instance to its equivalent long time string representation: h:mm:ss a
     * Example: 3:02:15 AM
     * @return the value of the current instance to its equivalent long time string representation: h:mm:ss a
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toLongTimeString(){
        return toStringFormat("h:mm:ss a");
    }

    /**
     * Converts the value of the current instance to its equivalent short date string representation
     * @param us true for US style: M/d/yyyy (5/16/2001) / false for non US style d/M/yyyy (16/5/2001)
     * @return the value of the current instance to its equivalent short date string representation
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toShortDateString(boolean us){
        if(us)
            return toStringFormat("M/d/yyyy");
        else
            return toStringFormat("d/M/yyyy");
    }

    /**
     * Converts the value of the current instance to its equivalent short date and time string representation
     * @param us true for US style: M/d/yyyy h:mm a (5/16/2001 2:05 AM) / false for non US style d/M/yyyy h:mm a (16/5/2001 2:05 AM)
     * @return the value of the current instance to its equivalent short date and time string representation
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toShortDateTimeString(boolean us){
        if(us)
            return toStringFormat("M/d/yyyy h:mm a");
        else
            return toStringFormat("d/M/yyyy h:mm a");
    }

    /**
     * Converts the value of the current instance to its equivalent short time string representation: h:mm a
     * Example: 3:02 AM
     * @return the value of the current instance to its equivalent short time string representation: h:mm a
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toShortTimeString(){
        return toStringFormat("h:mm a");
    }

    /**
     * Converts the value of the current instance to its equivalent date string representation according to the given format
     * @param format String date format
     * @return the value of the current instance to its equivalent date string representation according to the given format
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public String toStringFormat(String format){
        Date date = this.calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Static method
     * Converts the value of the given DateTime to its equivalent date string representation according to the given format
     * @param dateTime DateTime to convert
     * @param format String date format
     * @return the value of the given DateTime to its equivalent date string representation according to the given format
     * @author genosar.dafna
     * @since 20.09.2022
     */
    public static String toStringFormat(DateTime dateTime, String format){
        return dateTime.toStringFormat(format);
    }

    /**
     * Static method
     * Converts the date String from its current format to the given format
     * @param dateTimeString a String date to convert
     * @param format String date format
     * @param monthFirst true if the month is before the day, false if the day is before the month
     * @return Converts the date String from its current format to the given format
     * @author genosar.dafna
     * @since 20.11.2022
     */
    public static String convertStringFormat(String dateTimeString, String format, @Nullable Boolean monthFirst){
        DateTime dateTime = DateTime.parse(dateTimeString, monthFirst);
        return dateTime.toStringFormat(format);
    }

    /**
     * Turn unix timestamps into a DateTime
     * @param epoch Unix timestamp.
     * @return the instance
     * @author genosar.dafna
     * @since 20.09.2022
     * @since 07.11.2024
     */
    public static DateTime convertFromEpoch(Long epoch)
    {
        Date date = new Date(epoch * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new DateTime(calendar);
    }

    /**
     * Static method
     * Return the String representation of the month according to the selected style.
     * Example:
     * TextStyle.FULL - January
     * TextStyle.SHORT - Jan
     * TextStyle.NARROW - J
     * @param month the selected month
     * @return the String representation of the month according to the selected style.
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public static String getMonthString(Month month, TextStyle style){
        return month.getDisplayName(style, Locale.getDefault());
    }

    /**
     * Static method
     * Gets the month-of-year int value.
     * The values are numbered following the ISO-8601 standard,
     * from 1 (January) to 12 (December).
     * @param month an Enumeration representation of the month
     * @return the month-of-year, from 1 (January) to 12 (December)
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public static int getMonthValueFromInstance(Month month) {
        return month.getValue();
    }

    /**
     * Static method
     * Gets the month-of-year int value.
     * The values are numbered following the ISO-8601 standard,
     * from 1 (January) to 12 (December).
     * The String must have at least 3 letter of the month, like FEB, FEBRUARY
     * Example:
     * getMonthValueFromString("jan") -> Will return 1
     * @param monthString a String representation of the month - at least 3 letter of the month, like FEB, FEBRUARY
     * @return the month-of-year, from 1 (January) to 12 (December)
     * @author genosar.dafna
     * @since 29.02.2024
     */
    public static int getMonthValueFromString(String monthString) {
        Month month = getMonthInstanceFromString(monthString);
        return getMonthValueFromInstance(month);
    }

    /**
     * Static method
     * Obtains an instance of Month from an int value.
     * Month is an enum representing the 12 months of the year.
     * The int value follows the ISO-8601 standard, from 1 (January) to 12 (December).
     * Example:
     * getMonthInstanceFromInt(1) -> Will return JANUARY
     * @param month an int representation of the month
     * @return the month's enumeration value of the given month int value
     * @author genosar.dafna
     * @since 28.09.2022
     */
    public static Month getMonthInstanceFromInt(int month){
        if(month > 12 || month < 1)
            throw new Error(String.format("Cannot return Month instance. The int value must be from 1 (January) to 12 (December). <br>Int received: %d", month));
        return Month.of(month);
    }

    /**
     * Static method
     * Obtains an instance of Month from a String value.
     * Month is an enum representing the 12 months of the year.
     * The String must have at least 3 letter of the month, like FEB, FEBRUARY
     * Example:
     * getMonth("jan") -> Will return JANUARY
     * @param monthString a String representation of the month - at least 3 letter of the month, like FEB, FEBRUARY
     * @return the month's enumeration value of the given month int value
     * @author genosar.dafna
     * @since 29.02.2024
     */
    public static Month getMonthInstanceFromString(String monthString){

        if(monthString.length()<3)
            throw new Error(String.format("Month String received in method getMonthInstanceFromString() has to have at least 3 letters. Received: %s", monthString));

        monthString = monthString.toUpperCase();

        for(Month month : Month.values()){
            if(month.toString().contains(monthString))
                return month;
        }
        throw new Error(String.format("Cannot return a Month instance from month String received: %s", monthString));
    }

    /**
     *
     * Convert time to UTC according to timezone offset
     * Example:  DateTime dateTime = new DateTime(2020,10,10,10,00,00);  //2020/10/10 10:00:00
     *         dateTime = dateTime.convertToUTC("+03:00");
     *         will return: 2020/10/10 07:00:00
     * @param timeStringOffsetFromUTC the dateTime offset from UTF (if negative we need to add hours, if positive we need to decrease).
     * @return timezone in UTC
     * @author sela.zvika
     * @since 23.01.2023
     */
    public DateTime convertToUTC(String timeStringOffsetFromUTC){
        Time time = new Time(timeStringOffsetFromUTC);
        boolean dec = ((time.plusMinusPrefix == null) || (time.plusMinusPrefix.equals("+")));
        if(dec)
            this.addSeconds(-(int)time.totalSeconds);
        else
            this.addSeconds((int)time.totalSeconds);
        return this;
    }

    /**
     * Convert time from one TZ to another
     * Example:
     * DateTime dateTime = new DateTime(2020,10,10,10,00,00);  //2020/10/10 10:00:00
     * dateTime = dateTime.convertDateTimeFromTzToTz("+03:00","+05:00");
     *  will return: 2020/10/10 12:00:00
     * @param timeStringOffsetOfFromTz the dateTime offset from UTF
     * @param timeStringOffsetOfToTz the dateTime offset from UTF
     * @return DateTime object in new TZ
     * @author sela.zvika
     * @since 23.01.2023
     */
    public DateTime convertDateTimeFromTzToTz(String timeStringOffsetOfFromTz,String timeStringOffsetOfToTz) {
        return this.convertToUTC(timeStringOffsetOfFromTz).addTime(timeStringOffsetOfToTz);
    }

    /**
     * Convert UTC DateTime to another TZ
     * Example:
     * DateTime utcDateTime = new DateTime(2020,10,10,10,00,00);  //2020/10/10 10:00:00
     * dateTime = dateTime.convertUtcToTZ(utcDateTime,"+05:00");
     *  will return: 2020/10/10 15:00:00
     * @param utcDateTime the UTC dateTime tp convert
     * @param timeStringOffsetOfToTz the time offset of the new time zone
     * @return DateTime object in new TZ
     * @author genosar.dafna
     * @since 20.02.2023
     */
    public static DateTime convertUtcToTZ(DateTime utcDateTime, String timeStringOffsetOfToTz){
        return utcDateTime.addTime(timeStringOffsetOfToTz);
    }

    /**
     * Gets the calendar month Value, according to the received int value. Calendar months are from 0-11 (January to December)
     * Therefore the method will subtract 1 to the received int value
     * @param month the int value of the month to return
     * @return the calendar month Value, according to the received int value. Calendar months are from 0-11 (January to December)
     * @author genosar.dafna
     * @since 29.09.2022
     */
    private int convertToJavaCalendarMonthIntValue(int month) {

        if(month<1 || month>12)
            throw new Error(String.format("Cannot return Java calendar's month int value. The received int value must be between 1-12.<br>Received value: %d", month));
        return month-1;
    }
}

