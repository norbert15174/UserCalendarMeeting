package pl.user.calendar;


import pl.user.calendar.exceptions.UserCalendarCanNotBeCreatedException;
import pl.user.calendar.models.DayHours;
import pl.user.calendar.models.UserCalendar;
import pl.user.calendar.services.CalendarService;

import java.util.List;


public class Main {


    public static void main(String[] args) {

        //Getting calendar and time duration data
        List <String> items = CalendarService.enterData();
        //Checking if returned data isn't null
        if ( items != null ) {
            try {
                UserCalendar first = CalendarService.createUserCalendar(items.get(0));
                UserCalendar second = CalendarService.createUserCalendar(items.get(1));
                //Getting available meetings
                List <DayHours> possibleMeetings = CalendarService.findAllPossibleMeetings(first , second , items.get(2));
                System.out.println("Possible meeting time: ");
                possibleMeetings.forEach(System.out::println);
            } catch ( UserCalendarCanNotBeCreatedException e ) {
                System.err.println(e);
            }
        } else {
            System.err.println("Incorrect data entered");
        }


    }
}
