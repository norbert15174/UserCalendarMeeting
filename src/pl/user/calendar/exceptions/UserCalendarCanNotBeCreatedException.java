package pl.user.calendar.exceptions;

public class UserCalendarCanNotBeCreatedException extends Exception{
    private String message = "UserCalendarCanNotBeCreatedException: Unable to create user calendar, please try again";
    public UserCalendarCanNotBeCreatedException() {
    }
    public String toString() { return message; }

    @Override
    public String getMessage() {
        return message;
    }
}
