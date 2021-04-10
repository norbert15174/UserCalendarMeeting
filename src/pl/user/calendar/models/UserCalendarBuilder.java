package pl.user.calendar.models;

import java.util.List;

public class UserCalendarBuilder {
    private DayHours workingHours;
    private List <DayHours> plannedMeetings;

    public UserCalendarBuilder setWorkingHours(DayHours workingHours) {
        this.workingHours = workingHours;
        return this;
    }

    public UserCalendarBuilder setPlannedMeetings(List <DayHours> plannedMeetings) {
        this.plannedMeetings = plannedMeetings;
        return this;
    }

    public UserCalendar createUserCalendar() {
        return new UserCalendar(workingHours , plannedMeetings);
    }
}