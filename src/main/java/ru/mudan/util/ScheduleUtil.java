package ru.mudan.util;

import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * Класс, содержащий утилитные переменные
 */
@UtilityClass
@SuppressWarnings("MagicNumber")
public class ScheduleUtil {

    /**
     * Словарь, хранящий дни недели, которые могут быть указаны в Schedule
     */
    public static Map<Integer, String> days = Map.of(
            1, "Понедельник",
            2, "Вторник",
            3, "Среда",
            4, "Четверг",
            5, "Пятница"
            );
}
