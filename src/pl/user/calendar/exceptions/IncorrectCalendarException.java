package pl.user.calendar.exceptions;

public class IncorrectCalendarException extends  Exception{
        private String message = "IncorrectCalendarException: The calendar fails the pattern test. Change your meeting or working times and then try again";
        public IncorrectCalendarException() {
        }

        @Override
        public String getMessage() {
                return message;
        }

        public String toString() { return message; }
}
