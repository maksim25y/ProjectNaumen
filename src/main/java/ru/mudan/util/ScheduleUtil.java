package ru.mudan.util;

import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("MagicNumber")
public class ScheduleUtil {
    public static Map<Integer, String> days = Map.of(
            1, "Понедельник",
            2, "Вторник",
            3, "Среда",
            4, "Четверг",
            5, "Пятница"
            );
}
