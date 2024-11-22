package ru.mudan.services.parent;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.repositories.ParentRepository;
import ru.mudan.dto.parent.ParentDTO;
import ru.mudan.exceptions.entity.not_found.ParentNotFoundException;
import ru.mudan.facade.parent.ParentFacade;
import ru.mudan.services.auth.MyUserDetailsService;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Parent
 */
@Service
@RequiredArgsConstructor
public class ParentService {

    private final MyUserDetailsService myUserDetailsService;
    private final ParentRepository parentRepository;
    private final ParentFacade parentFacade;

    /**
     * Метод для получения родителя по аутентификации
     *
     * @param authentication - текущая аутентификация
     */
    public ParentDTO findParentByAuth(Authentication authentication) {
        var parent = (Parent) myUserDetailsService.loadUserByUsername(authentication.getName());

        return parentFacade.convertEntityToDTO(parent);
    }

    /**
     * Метод для получения списка всех родителей
     */
    public List<ParentDTO> findAllParents() {
        var allParents = parentRepository.findAll();

        return allParents.stream()
                .map(parentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения родителя по id
     *
     * @param id - id родителя
     */
    public ParentDTO findParentById(Long id) {
        var foundParent = parentRepository.findById(id)
                .orElseThrow(() -> new ParentNotFoundException(id));

        return parentFacade.convertEntityToDTO(foundParent);
    }
}