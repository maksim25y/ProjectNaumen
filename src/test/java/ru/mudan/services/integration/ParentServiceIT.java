package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.repositories.ParentRepository;
import ru.mudan.exceptions.entity.not_found.ParentNotFoundException;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.parent.ParentService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.mudan.UtilConstants.*;

public class ParentServiceIT extends IntegrationTest {

    @Autowired
    private ParentService parentService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    private ParentRepository parentRepository;

    private Long parentId;
    private Parent parentCreated;

    @BeforeEach
    public void registerParent_valid() {
        var parent = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerParent(parent);

        var parentCreated = parentRepository.findAll().getFirst();

        this.parentId = parentCreated.getId();
        this.parentCreated = parentCreated;
    }

    @AfterEach
    public void clearTables() {
        myUserDetailsService.deleteUserByEmail(parentCreated.getEmail());
    }

    @Test
    public void findParentById_existed() {
        var foundParent = parentService.findParentById(parentId);

        assertAll("Grouped assertions for found parent",
                () -> assertEquals(parentCreated.getId(), foundParent.id()),
                () -> assertEquals(parentCreated.getFirstname(), foundParent.firstname()),
                () -> assertEquals(parentCreated.getLastname(), foundParent.lastname()),
                () -> assertEquals(parentCreated.getPatronymic(), foundParent.patronymic()),
                () -> assertEquals(parentCreated.getEmail(), foundParent.email()));
    }

    @Test
    public void findParentById_notExisted() {
        assertThrows(ParentNotFoundException.class, () -> parentService.findParentById(parentId+1));
    }

    @Test
    public void findAllParents_notEmpty() {
        var foundParents = parentService.findAllParents();

        var firstTeacher = foundParents.getFirst();

        assertAll("Grouped assertions for found parents",
                () -> assertEquals(1, foundParents.size()),
                () -> assertEquals(parentCreated.getId(), firstTeacher.id()),
                () -> assertEquals(parentCreated.getFirstname(), firstTeacher.firstname()),
                () -> assertEquals(parentCreated.getLastname(), firstTeacher.lastname()),
                () -> assertEquals(parentCreated.getPatronymic(), firstTeacher.patronymic()),
                () -> assertEquals(parentCreated.getEmail(), firstTeacher.email()));
    }
}
