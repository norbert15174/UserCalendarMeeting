package pl.user.calendar.models;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DayHours {
    private LocalTime start;
    private LocalTime end;

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start.truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end.truncatedTo(ChronoUnit.MINUTES);
    }

    public DayHours(LocalTime start , LocalTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "DayHours{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
