package pl.user.calendar.models;

import java.time.LocalTime;

public class DayHoursBuilder {
    private LocalTime start;
    private LocalTime end;

    public DayHoursBuilder setStart(LocalTime start) {
        this.start = start;
        return this;
    }

    public DayHoursBuilder setEnd(LocalTime end) {
        this.end = end;
        return this;
    }

    public DayHours createDayHours() {
        return new DayHours(start , end);
    }
}