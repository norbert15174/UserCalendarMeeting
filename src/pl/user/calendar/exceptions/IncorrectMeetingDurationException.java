package pl.user.calendar.exceptions;

public class IncorrectMeetingDurationException extends  Exception{
    private String message = "IncorrectMeetingDurationException: The duration of the meeting does not pass the benchmark test";
    public IncorrectMeetingDurationException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String toString() { return message; }
}
