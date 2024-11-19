package ru.mudan.services.parent;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.repositories.ParentRepository;
import ru.mudan.dto.parent.ParentDTO;
import ru.mudan.exceptions.entity.not_found.ParentNotFoundException;
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

    /**
     * Метод для получения родителя по аутентификации
     *
     * @param authentication - текущая аутентификация
     */
    public ParentDTO findParentByAuth(Authentication authentication) {
        var parent = (Parent) myUserDetailsService.loadUserByUsername(authentication.getName());

        return ParentDTO
                .builder()
                .id(parent.getId())
                .firstname(parent.getFirstname())
                .lastname(parent.getLastname())
                .patronymic(parent.getPatronymic())
                .email(parent.getEmail())
                .build();
    }

    public List<ParentDTO> findAllParents() {
        var allParents = parentRepository.findAll();

        return allParents.stream()
                .map(parent -> ParentDTO
                        .builder()
                        .id(parent.getId())
                        .firstname(parent.getFirstname())
                        .lastname(parent.getLastname())
                        .patronymic(parent.getPatronymic())
                        .email(parent.getEmail())
                        .build())
                .toList();
    }

    public ParentDTO findParentById(Long id) {
        var foundParent = parentRepository.findById(id)
                .orElseThrow(() -> new ParentNotFoundException(id));

        return ParentDTO
                .builder()
                .id(foundParent.getId())
                .firstname(foundParent.getFirstname())
                .lastname(foundParent.getLastname())
                .patronymic(foundParent.getPatronymic())
                .email(foundParent.getEmail())
                .build();
    }
}