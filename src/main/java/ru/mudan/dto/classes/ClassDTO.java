package ru.mudan.dto.classes;

import java.util.List;
import lombok.Builder;

@Builder
public record ClassDTO(Long id,
                               Character letter,
                               Integer number,
                               String description,
                       List<Long> studentsIds,
                       List<Long> subjectsIds
) {
}
