package ru.mudan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.mudan.ProjectNaumenApplication;

@AutoConfigureMockMvc
@SpringBootTest(classes = ProjectNaumenApplication.class)
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;
}
