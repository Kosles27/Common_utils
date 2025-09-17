package objectsUtils;

import dateTimeUtils.DateTime;
import dateTimeUtils.Time;
import enumerations.BugSeverityEnum;
import reportUtils.Report;

/**
 * Represents a bug and its details.
 * Bug number (can be what the Jira displays like: BAL-1234) or just a number
 * Details - the bug title
 * Severity
 * Number of days since the bug was opened
 * Date it was created
 * @author genosar.dafna
 * @since 08.12.2024
 */
public class Bug {

    public String bugNumber;
    public String title;
    public BugSeverityEnum severity;
    public int days;
    public DateTime createDate;

    public Bug(String bugNumber, String title, BugSeverityEnum severity, int days){
        this.bugNumber = bugNumber;
        this.title = title;
        this.severity = severity;
        this.days = days;
        createDate = DateTime.today().addDays(-days);
    }

    public Bug(String bugNumber, String title, BugSeverityEnum severity, DateTime createDate){
        this.bugNumber = bugNumber;
        this.title = title;
        this.severity = severity;
        this.createDate = createDate;
        Time timeDif = DateTime.today().getTimeDifference(createDate);
        long totalNumberOfSecondsSinceCreationDate = timeDif.totalSeconds;
        long numberOfDaysDif = totalNumberOfSecondsSinceCreationDate / 86400;
        this.days = Math.abs((int)numberOfDaysDif) + 1;
    }

    /**
     * Report a highlighted bug line with severity and date of creation
     * @author genosar.dafna
     * @since 08.12.2024
     */
    public void report(){
        Report.reportBug(String.format("%s: %s", this.bugNumber, this.title), this.severity, this.createDate);
    }
}
