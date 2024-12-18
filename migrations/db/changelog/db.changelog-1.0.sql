-- Ready
CREATE TABLE IF NOT EXISTS classes
(
    id          BIGSERIAL PRIMARY KEY,
    letter      VARCHAR(10),
    number      INTEGER,
    description VARCHAR
);
CREATE TABLE IF NOT EXISTS parents
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR NOT NULL,
    lastname        VARCHAR NOT NULL,
    email           VARCHAR NOT NULL,
    patronymic      VARCHAR NOT NULL,
    hashed_password VARCHAR NOT NULL
);
-- Ready
CREATE TABLE IF NOT EXISTS students
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR(255),
    lastname        VARCHAR(255),
    patronymic      VARCHAR(255),
    email           varchar(255),
    hashed_password VARCHAR NOT NULL,
    class_id        BIGINT,
    parent_id       BIGINT,
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES parents (id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS teachers
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR(255) NOT NULL,
    lastname        VARCHAR(255) NOT NULL,
    patronymic      VARCHAR(255) NOT NULL,
    email           VARCHAR      NOT NULL,
    hashed_password VARCHAR      NOT NULL
);
-- Ready
CREATE TABLE IF NOT EXISTS subjects
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    type        VARCHAR(50),
    code        VARCHAR(50) UNIQUE,
    description VARCHAR(255),
    class_id    BIGINT,
    teacher_id  BIGINT,
    FOREIGN KEY (class_id)  REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS schedules
(
    id                  BIGSERIAL PRIMARY KEY,
    day_of_week         INTEGER,
    start_time          TIME,
    number_of_classroom INTEGER,
    class_id            BIGINT,
    subject_id          BIGINT,
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS homeworks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255),
    description VARCHAR,
    deadline    DATE,
    class_id    BIGINT,
    subject_id  BIGINT,
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grades
(
    id           BIGSERIAL PRIMARY KEY,
    mark         INTEGER,
    date_of_mark DATE,
    comment      VARCHAR,
    student_id   BIGINT DEFAULT NULL,
    subject_id   BIGINT DEFAULT NULL,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS app_users
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT,
    role_name VARCHAR NOT NULL,
    email     VARCHAR NOT NULL
);
CREATE TABLE  IF NOT EXISTS admins
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR NOT NULL,
    lastname        VARCHAR NOT NULL,
    patronymic      VARCHAR NOT NULL,
    email           VARCHAR NOT NULL,
    hashed_password VARCHAR NOT NULL
);

-- Создание аккаунта админа
INSERT INTO admins(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Кирилл', 'Кириллов', 'Кириллович', 'admin@mail.ru','$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');
INSERT INTO app_users(user_id, role_name, email)
VALUES (1, 'ROLE_ADMIN', 'admin@mail.ru');