package ru.mudan.dto;

import lombok.Builder;

@Builder
public record ClassDTO(Long id,
                               Character letter,
                               Integer number,
                               String description
) {
}
