package pl.user.calendar.services;

import pl.user.calendar.exceptions.IncorrectCalendarException;
import pl.user.calendar.exceptions.IncorrectMeetingDurationException;
import pl.user.calendar.exceptions.UserCalendarCanNotBeCreatedException;
import pl.user.calendar.models.DayHours;
import pl.user.calendar.models.DayHoursBuilder;
import pl.user.calendar.models.UserCalendar;
import pl.user.calendar.models.UserCalendarBuilder;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CalendarService {

    public CalendarService() {
    }

    //Method is responsible for reading data from user
    private static String readCalendar(Scanner scan) {
        String calendar = "";
        while (scan.hasNextLine()) {
            calendar += scan.nextLine();
            if ( calendar.endsWith("}") ) {
                if ( calendar.substring(calendar.length() - 2).matches("]}") )
                    break;
            }
        }
        return calendar;
    }

    //The method creates an array containing the user's calendar information
    public static List <String> enterData() {
        try {
            //First calendar
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter the first Calendar");
            String firstCalendar = readCalendar(scan);
            checkIfCalendarCorrect(firstCalendar);

            //Second calendar
            System.out.println("Enter the second Calendar");
            String secondCalendar = readCalendar(scan);
            checkIfCalendarCorrect(secondCalendar);

            //Meeting duration
            System.out.println("Enter the meeting duration ([hh:mm])");
            String meetingDuration = scan.nextLine();
            checkIfMeetingDurationCorrect(meetingDuration);


            return Arrays.asList(firstCalendar , secondCalendar , meetingDuration);

        } catch ( IncorrectCalendarException | IncorrectMeetingDurationException e ) {

            System.err.println(e);
            return null;

        }
    }

    //Method is responsible for checking the format of the input calendar. If the input is invalid, an exception will be thrown;
    public static boolean checkIfCalendarCorrect(String calendar) throws IncorrectCalendarException {
        Pattern pattern = Pattern.compile(".working_hours.+start.+([0-1][0-9]|[2][0-3]):[0-5][0-9].+end.+(1[0-9]|2[0-3]):[0-5][0-9].+planned_meeting.+");
        if ( pattern.matcher(calendar).matches() ) return true;
        throw new IncorrectCalendarException();
    }
    //Method is responsible for checking the format of the duration of the meeting. If the input is invalid, an exception will be thrown.
    public static boolean checkIfMeetingDurationCorrect(String meeting) throws IncorrectMeetingDurationException {
        Pattern pattern = Pattern.compile(".+([0-1][0-9]|[2][0-3]):[0-5][0-9].+");
        if ( pattern.matcher(meeting).matches() ) return true;
        throw new IncorrectMeetingDurationException();
    }

    //The method splits the calendar data and extracts the time information. Then creating UserCalendar. If the data cannot be extracted or the calendar cannot be created, an exception will be thrown.
    public static UserCalendar createUserCalendar(String calendar) throws UserCalendarCanNotBeCreatedException {

        //Extracting data
        String[] splitCalendar = calendar.split(",planned_meeting");
        String[] splitWorkingHours = splitCalendar[0].split(" \"");
        String[] splitPlannedMeetings = splitCalendar[1].split("},");

        //Getting working hours
        DayHours working = createDayHours(splitWorkingHours[1].substring(0 , 5) , splitWorkingHours[2].substring(0 , 5));
        if ( working.getStart().isAfter(working.getEnd()) ) throw new UserCalendarCanNotBeCreatedException();

        //Getting current meetings
        List <DayHours> planned = new ArrayList <>();
        for (int i = 0; i < splitPlannedMeetings.length && splitPlannedMeetings.length > 1; i++) {
            if(splitPlannedMeetings[i].length() < 6) continue;
            planned.add(createDayHours(splitPlannedMeetings[i].split(" \"")[1].substring(0 , 5) , splitPlannedMeetings[i].split(" \"")[2].substring(0 , 5)));
        }


        //Checking if the data is correct, if the end time is less than the start time, the method will throw an exception.
        for (DayHours hours : planned) {
            if ( hours.getStart().isAfter(hours.getEnd()) ) throw new UserCalendarCanNotBeCreatedException();
        }

        return new UserCalendarBuilder().setPlannedMeetings(planned).setWorkingHours(working).createUserCalendar();
    }

    //Creating DayHours
    public static DayHours createDayHours(String hourStart , String hourEnd) {
        DayHours dayHours = new DayHoursBuilder().createDayHours();
        dayHours.setStart(inputDateParser(hourStart));
        dayHours.setEnd(inputDateParser(hourEnd));
        return dayHours;
    }

    //Parsing data from string to LocalTime
    public static LocalTime inputDateParser(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(date , dateTimeFormatter);
    }

    //The method returns free time to organize a new meeting.
    public static List <DayHours> personFreeMeetingTime(List <DayHours> meeting , long meetingDurationMinutes) {
        List <DayHours> possibleMeeting = new ArrayList <>();
        for (int i = 1; i < meeting.size(); i++) {
            if ( Math.abs(Duration.between(meeting.get(i - 1).getEnd() , meeting.get(i).getStart()).toMinutes()) >= meetingDurationMinutes ) {
                possibleMeeting.add(new DayHoursBuilder().setStart(meeting.get(i - 1).getEnd()).setEnd(meeting.get(i).getStart()).createDayHours());
            }
        }
        return possibleMeeting;
    }

    //The method combines users' calendar meetings with working hours and returns a sorted list.
    public static List <DayHours> insertDataToList(UserCalendar calendar) {
        List <DayHours> calendarList = calendar.getPlannedMeetings();
        calendarList.add(new DayHoursBuilder().setEnd(calendar.getWorkingHours().getStart()).setStart(inputDateParser("00:00")).createDayHours());
        calendarList.add(new DayHoursBuilder().setStart(calendar.getWorkingHours().getEnd()).setEnd(inputDateParser("23:59")).createDayHours());
        return calendarList.stream().sorted(Comparator.comparing(DayHours::getStart)).collect(Collectors.toList());
    }

    //Method return list of hours which are free to both of users
    public static List <DayHours> findAllPossibleMeetings(UserCalendar first , UserCalendar second , String meetingDuration) {
        //Getting the meeting time in minutes
        long meetingDurationMinutes = inputDateParser(meetingDuration.substring(1 , 6)).get(ChronoField.MINUTE_OF_DAY);
        //Getting sorted and linked data
        List <DayHours> firstMeetings = insertDataToList(first);
        List <DayHours> secondMeetings = insertDataToList(second);


        List <DayHours> firstPossibleMeetings;
        List <DayHours> secondPossibleMeetings;
        //Setting bigger list as first
        if ( firstMeetings.size() > secondMeetings.size() ) {
            firstPossibleMeetings = personFreeMeetingTime(firstMeetings , meetingDurationMinutes);
            secondPossibleMeetings = personFreeMeetingTime(secondMeetings , meetingDurationMinutes);
        } else {
            secondPossibleMeetings = personFreeMeetingTime(firstMeetings , meetingDurationMinutes);
            firstPossibleMeetings = personFreeMeetingTime(secondMeetings , meetingDurationMinutes);
        }


        List <DayHours> schedules = new ArrayList <>();
        //Finding all possibilities to organize a meeting
        for (DayHours f : firstPossibleMeetings) {
            DayHours currentValue = f;
            List<DayHours> valueToSave = new ArrayList <>();
            for (DayHours s : secondPossibleMeetings) {
                if ( currentValue.getStart().compareTo(s.getStart()) <= 0 && currentValue.getEnd().compareTo(s.getEnd()) >= 0 ) {
                    valueToSave.add(new DayHoursBuilder().setStart(s.getStart()).setEnd(s.getEnd()).createDayHours());
                } else if ( currentValue.getStart().compareTo(s.getStart()) <= 0 && currentValue.getEnd().compareTo(s.getEnd()) <= 0 && currentValue.getEnd().compareTo(s.getStart()) > 0 ) {
                    valueToSave.add(new DayHoursBuilder().setStart(s.getStart()).setEnd(currentValue.getEnd()).createDayHours());
                } else if ( currentValue.getStart().compareTo(s.getStart()) >= 0 && currentValue.getEnd().compareTo(s.getEnd()) >= 0 && currentValue.getStart().compareTo(s.getEnd()) < 0 ) {
                    valueToSave.add(new DayHoursBuilder().setStart(currentValue.getStart()).setEnd(s.getEnd()).createDayHours());
                } else if ( currentValue.getStart().compareTo(s.getStart()) >= 0 && currentValue.getEnd().compareTo(s.getEnd()) <= 0 ) {
                    valueToSave.add(new DayHoursBuilder().setStart(currentValue.getStart()).setEnd(currentValue.getEnd()).createDayHours());
                }
            }

            //Check if the time slots found are long enough to organize the meeting
            valueToSave.forEach(v -> {
                if ( Math.abs(Duration.between(v.getStart() , v.getEnd()).toMinutes()) >= meetingDurationMinutes ) {
                    schedules.add(v);
                }
            });

        }
        //Return all meeting options
        return schedules;
    }

}
