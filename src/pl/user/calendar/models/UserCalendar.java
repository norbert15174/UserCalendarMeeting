package pl.user.calendar.models;

import java.util.List;

public class UserCalendar {

    private static long id = 0;
    private DayHours workingHours;
    private List <DayHours> plannedMeetings;

    public UserCalendar(DayHours workingHours , List <DayHours> plannedMeetings) {
        id++;
        this.workingHours = workingHours;
        this.plannedMeetings = plannedMeetings;
    }

    public DayHours getWorkingHours() {
        return workingHours;
    }

    public List <DayHours> getPlannedMeetings() {
        return plannedMeetings;
    }

    @Override
    public String toString() {
        return "UserCalendar{" +
                "workingHours=" + workingHours +
                ", plannedMeetings=" + plannedMeetings +
                '}';
    }
}
