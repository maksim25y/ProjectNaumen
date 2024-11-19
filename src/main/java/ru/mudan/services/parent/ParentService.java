package ru.mudan.services.parent;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.dto.parent.ParentDTO;
import ru.mudan.services.auth.MyUserDetailsService;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Parent
 */
@Service
@RequiredArgsConstructor
public class ParentService {

    private final MyUserDetailsService myUserDetailsService;

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
}