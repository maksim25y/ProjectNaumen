package ru.mudan.util.enums;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayOfWeek {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница");

    private final String name;

    private static final Map<Integer, DayOfWeek> DAYS_OF_WEEK = Map.of(
            1, MONDAY,
            2, TUESDAY,
            3, WEDNESDAY,
            4, THURSDAY,
            5, FRIDAY
    );

    public static DayOfWeek getDayOfWeekByNumber(Integer dayOfWeekNumber) {
        return DAYS_OF_WEEK.get(dayOfWeekNumber);
    }
}
