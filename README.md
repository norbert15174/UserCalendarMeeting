# UserCalendarMeeting

### The program compares 2 calendars and returns all meeting options

##### SAMPLE INPUT AND OUTPUT:
## Enter the first Calendar
    {
    working_hours: {
    start: "09:00",
    end: "19:55"
    },
    planned_meeting: [
    {
    start: "09:00",
    end: "10:30"
    },
    {
    start: "12:00",
    end: "13:00"
    },
    {
    start: "16:00",
    end: "18:00"
    }
    ]
    }
## Enter the second Calendar
    {
    working_hours: {
    start: "10:00",
    end: "18:30"
    },
    planned_meeting: [
    {
    start: "10:00",
    end: "11:30"
    },
    {
    start: "12:30",
    end: "14:30"
    },
    {
    start: "14:30",
    end: "15:00"
    },
    {
    start: "16:00",
    end: "17:00"
    }
    ]
    }
## Enter the meeting duration ([hh:mm])
    [00:30]
    
## Output
    DayHours{start=11:30, end=12:00}
    DayHours{start=15:00, end=16:00}
    DayHours{start=18:00, end=18:30}
