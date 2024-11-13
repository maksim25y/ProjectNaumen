package ru.mudan.services;

import liquibase.Liquibase;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mudan.ProjectNaumenApplication;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

@AutoConfigureMockMvc
@SpringBootTest(classes = ProjectNaumenApplication.class)
@ActiveProfiles("test")
@Testcontainers
public abstract class IntegrationTest {

    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("project")
                .withUsername("postgres")
                .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) throws Exception {
        Path path = new File(".").toPath().toAbsolutePath().getParent().resolve("migrations/db/changelog/");

        Connection connection = DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword());
        Database database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase("db.changelog-test.yml", new DirectoryResourceAccessor(path), database);

        liquibase.update(new Contexts(), new LabelExpression());
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
