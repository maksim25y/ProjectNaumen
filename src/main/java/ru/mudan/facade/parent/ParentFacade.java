package ru.mudan.facade.parent;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.dto.parent.ParentDTO;
import ru.mudan.facade.BaseFacade;

@Component
public class ParentFacade implements BaseFacade<Parent, ParentDTO> {

    @Override
    public ParentDTO convertEntityToDTO(Parent entity) {
        return ParentDTO
                .builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .email(entity.getEmail())
                .build();
    }
}
