package pl.user.calendar.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.user.calendar.exceptions.IncorrectCalendarException;
import pl.user.calendar.exceptions.IncorrectMeetingDurationException;
import pl.user.calendar.exceptions.UserCalendarCanNotBeCreatedException;
import pl.user.calendar.models.DayHours;
import pl.user.calendar.models.UserCalendar;
import pl.user.calendar.models.UserCalendarBuilder;
import pl.user.calendar.services.CalendarService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CalendarServiceTest {

    @Test
    void shouldTestCalendarPatternAndReturnTrue(){
        List <String> dataToTest = new ArrayList <>();
        dataToTest.add("{working_hours: {start: \"09:00\",end: \"19:55\"},planned_meeting: [{start: \"09:00\",end: \"10:30\"},{start: \"14:00\",end: \"15:00\"}]}");
        dataToTest.add("{working_hours: {start: \"09:00\",end: \"19:55\"},planned_meeting: []}");
        dataToTest.add("{working_hours: {start: \"09:00\",end: \"19:55\"},planned_meeting: [{start: \"12:00\",end: \"14:30\"},{start: \"16:00\",end: \"18:00\"}]}");
        dataToTest.forEach(data -> {
            try {
                Assertions.assertTrue(CalendarService.checkIfCalendarCorrect(data));
            } catch ( IncorrectCalendarException e ) {
                e.printStackTrace();
            }
        });
    }
    @Test
    void shouldTestCalendarPatternAndThrowIncorrectCalendarException(){
        List <String> dataToTest = new ArrayList <>();
        dataToTest.add("{working_hours: {end: \"19:55\"},planned_meeting: [{start: \"09:00\",end: \"10:30\"},{start: \"14:00\",end: \"15:00\"}]}");
        dataToTest.add("\"{working_s: {start: \\\"09:00\\\",end: \\\"19:55\\\"},planned_meeting: [{start: \\\"12:00\\\",end: \\\"14:30\\\"},{start: \\\"16:00\\\",end: \\\"18:00\\\"}]}\"");
        dataToTest.add("\"{wo9:55\\\"},planned_meeting: [{start: \\\"12:00\\\",end: \\\"14:30\\\"},{start: \\\"16:00\\\",end: \\\"18:00\\\"}]}\"");

        dataToTest.forEach(data -> {
            Throwable exception = assertThrows(IncorrectCalendarException.class, () -> CalendarService.checkIfCalendarCorrect(data));
            String expectedMessage = "IncorrectCalendarException: The calendar fails the pattern test. Change your meeting or working times and then try again";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        });
    }

    @Test
    void shouldTestMeetingDurationPatternAndReturnTrue(){
        List <String> dataToTest = new ArrayList <>();
        dataToTest.add("[20:20]");
        dataToTest.add("[00:10]");
        dataToTest.add("[02:05]");
        dataToTest.add("[00:10]");
        dataToTest.add("[22:22]");
        dataToTest.forEach(data -> {
            try {
                Assertions.assertTrue(CalendarService.checkIfMeetingDurationCorrect(data));
            } catch ( IncorrectMeetingDurationException e ) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void shouldTestMeetingDurationPatternAndThrowIncorrectMeetingDurationException(){
        List <String> dataToTest = new ArrayList <>();
        dataToTest.add("[20:20");
        dataToTest.add("[25:10]");
        dataToTest.add("02:05]");
        dataToTest.add("[0:10]");
        dataToTest.add("[22:");
        dataToTest.forEach(data -> {
            Throwable exception = assertThrows(IncorrectMeetingDurationException.class, () -> CalendarService.checkIfMeetingDurationCorrect(data));
            String expectedMessage = "IncorrectMeetingDurationException: The duration of the meeting does not pass the benchmark test";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        });
    }
    @Test
    void ShouldCreateUserCalendarAndReturnCorrectUserCalendar() throws UserCalendarCanNotBeCreatedException {
        List<DayHours> dayHours = new ArrayList <>();
        dayHours.add(CalendarService.createDayHours("10:00","11:30"));
        dayHours.add(CalendarService.createDayHours("13:00","14:00"));
        dayHours.add(CalendarService.createDayHours("14:30","15:00"));
        dayHours.add(CalendarService.createDayHours("16:00","17:00"));
        DayHours workingHours = CalendarService.createDayHours("10:00","18:30");
        UserCalendar userCalendar = new UserCalendarBuilder().setPlannedMeetings(dayHours).setWorkingHours(workingHours).createUserCalendar();
        UserCalendar userCalendarToTest = CalendarService.createUserCalendar("{working_hours: {start: \"10:00\",end: \"18:30\"},planned_meeting: [{start: \"10:00\",end: \"11:30\"},{start: \"13:00\",end: \"14:00\"},{start: \"14:30\",end: \"15:00\"},{start: \"16:00\",end: \"17:00\"}]}");
        Assertions.assertEquals(userCalendar.getWorkingHours().toString(),userCalendarToTest.getWorkingHours().toString());
        Assertions.assertEquals(userCalendar.getPlannedMeetings().toString(),userCalendarToTest.getPlannedMeetings().toString());
    }

    @Test
    void ShouldCheckIfUserCalendarDataCorrectAndThrowUserCalendarCanNotBeCreatedException(){
        Throwable exception = assertThrows(UserCalendarCanNotBeCreatedException.class, () -> CalendarService.createUserCalendar("{working_hours: {start: \"10:00\",end: \"18:30\"},planned_meeting: [{start: \"12:00\",end: \"11:30\"},{start: \"13:00\",end: \"14:00\"},{start: \"14:30\",end: \"15:00\"},{start: \"16:00\",end: \"17:00\"}]}"));
        String expectedMessage = "UserCalendarCanNotBeCreatedException: Unable to create user calendar, please try again";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void ShouldReturnAllPossibleMeetings() throws UserCalendarCanNotBeCreatedException {
        String meetingDuration = "[00:30]";
        List<DayHours> dayHours = new ArrayList <>();
        dayHours.add(CalendarService.createDayHours("09:00","10:30"));
        dayHours.add(CalendarService.createDayHours("12:00","13:00"));
        dayHours.add(CalendarService.createDayHours("16:00","18:00"));
        DayHours workingHours = CalendarService.createDayHours("09:00","19:55");
        UserCalendar userCalendar = new UserCalendarBuilder().setPlannedMeetings(dayHours).setWorkingHours(workingHours).createUserCalendar();
        List<DayHours> dayHours2 = new ArrayList <>();
        dayHours2.add(CalendarService.createDayHours("10:00","11:30"));
        dayHours2.add(CalendarService.createDayHours("12:30","14:30"));
        dayHours2.add(CalendarService.createDayHours("14:30","15:00"));
        dayHours2.add(CalendarService.createDayHours("16:00","17:00"));
        DayHours workingHours2 = CalendarService.createDayHours("10:00","18:30");
        UserCalendar userCalendar2 = new UserCalendarBuilder().setPlannedMeetings(dayHours2).setWorkingHours(workingHours2).createUserCalendar();
        UserCalendar userCalendar3 = CalendarService.createUserCalendar("{working_hours: {start: \"10:00\",end: \"18:30\"},planned_meeting: [{start: \"11:00\",end: \"14:00\"},{start: \"18:00\",end: \"18:30\"}]}");
        UserCalendar userCalendar4 = CalendarService.createUserCalendar("{working_hours: {start: \"10:00\",end: \"18:30\"},planned_meeting: []}");

        List<DayHours> test1ReturnList = Arrays.asList(CalendarService.createDayHours("10:30","11:00"),CalendarService.createDayHours("14:00","16:00"));
        List<DayHours> test2ReturnList = Arrays.asList(CalendarService.createDayHours("11:30","12:00"),CalendarService.createDayHours("15:00","16:00"),CalendarService.createDayHours("18:00","18:30"));
        List<DayHours> test3ReturnList = Arrays.asList(CalendarService.createDayHours("10:30","12:00"),CalendarService.createDayHours("13:00","16:00"),CalendarService.createDayHours("18:00","18:30"));
        assertEquals(test1ReturnList.toString(),CalendarService.findAllPossibleMeetings(userCalendar,userCalendar3,meetingDuration).toString());
        assertEquals(test2ReturnList.toString(),CalendarService.findAllPossibleMeetings(userCalendar,userCalendar2,meetingDuration).toString());
        assertEquals(test3ReturnList.toString(),CalendarService.findAllPossibleMeetings(userCalendar,userCalendar4,meetingDuration).toString());



    }


}