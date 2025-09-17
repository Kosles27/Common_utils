package dateTimeUtils;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Time objects.
 * @author genosar.dafna
 * @since 28.10.2022
 */
@SuppressWarnings("unused")
public class Time {

    public long timeStamp;
    public long totalSeconds;
    public long totalMinutes;
    public long totalHours;
    public long hours;
    public long minutes;
    public long seconds;

    public double doubleSeconds;
    public String plusMinusPrefix = null; // +/- before a time (in cases of time zone offset for example)

    public Time(Stopwatch stopwatch){
        this(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public Time(long timeStamp){
        this.timeStamp = timeStamp;
        totalSeconds = (this.timeStamp) / 1000;
        totalMinutes = totalSeconds / 60;
        totalHours = totalMinutes/60;
        hours = totalSeconds / 3600;
        long tsecs = totalSeconds % 3600;
        minutes = tsecs / 60;
        seconds = tsecs % 60;
        doubleSeconds = (double)timeStamp * 0.001;
    }

    public Time(String timeString){
        String plusOrMinus = timeString.substring(0, 1);
        if(plusOrMinus.equals("+") || plusOrMinus.equals("-")){
            plusMinusPrefix = plusOrMinus;
            timeString = timeString.substring(1);
        }

        String[] timeParts = timeString.split(":");
        hours = Long.parseLong(timeParts[0]);
        minutes = Long.parseLong(timeParts[1]);
        seconds = timeParts.length==3? Long.parseLong(timeParts[2]) : 0;
        totalSeconds = hours * 3600 + minutes * 60 + seconds;
        totalMinutes = totalSeconds / 60;
        totalHours = totalMinutes/60;
        this.timeStamp = totalSeconds * 1000;
        doubleSeconds = (double)timeStamp * 0.001;
    }

    public static long getStopwatchMilliseconds(Stopwatch stopwatch) {
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }

    public static double getStopwatchTotalSeconds(Stopwatch stopwatch){
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        //Get the time that passed in seconds
        double calcSeconds = millis * 0.001;
        String roundedSecString = String.format("%.2f", calcSeconds);
        return Double.parseDouble(roundedSecString);
    }
}
