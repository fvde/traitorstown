package com.individual.thinking.traitorstown.model;

public enum Day {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int day;

    Day(int day) {
        this.day = day;
    }

    public static boolean isElectionDay(int turn){
        return 0 == (turn % SUNDAY.day);
    }

    public static boolean isDayBeforeElections(int turn){
        return SATURDAY.day == (turn % SUNDAY.day);
    }
}
