package ru.mudan.dto;

import lombok.Builder;

@Builder
public record ClassResponseDTO(Long id,
                               Character letter,
                               Integer number,
                               String description
) {
}
