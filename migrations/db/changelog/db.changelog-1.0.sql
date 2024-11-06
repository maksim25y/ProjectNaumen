-- Ready
CREATE TABLE classes
(
    id          BIGSERIAL PRIMARY KEY,
    letter      VARCHAR(10),
    number      INTEGER,
    description VARCHAR
);
CREATE TABLE parents
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR NOT NULL,
    lastname        VARCHAR NOT NULL,
    email           VARCHAR NOT NULL,
    patronymic      VARCHAR NOT NULL,
    hashed_password VARCHAR NOT NULL
);
-- Ready
CREATE TABLE students
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
CREATE TABLE teachers
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR(255) NOT NULL,
    lastname        VARCHAR(255) NOT NULL,
    patronymic      VARCHAR(255) NOT NULL,
    email           VARCHAR      NOT NULL,
    hashed_password VARCHAR      NOT NULL
);
-- Ready
CREATE TABLE subjects
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    type        VARCHAR(50),
    code        VARCHAR(50) UNIQUE,
    description VARCHAR(255),
    class_id    BIGINT,
    teacher_id  BIGINT,
    FOREIGN KEY (class_id)  REFERENCES classes (id) ON DELETE SET NULL ,
    FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE SET NULL
);

CREATE TABLE schedules
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

CREATE TABLE homeworks
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

CREATE TABLE grades
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

CREATE TABLE app_users
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT,
    role_name VARCHAR NOT NULL,
    email     VARCHAR NOT NULL
);
CREATE TABLE admins
(
    id              BIGSERIAL PRIMARY KEY,
    firstname       VARCHAR NOT NULL,
    lastname        VARCHAR NOT NULL,
    patronymic      VARCHAR NOT NULL,
    email           VARCHAR NOT NULL,
    hashed_password VARCHAR NOT NULL
);

CREATE TABLE class_to_teacher
(
    teacher_id BIGINT,
    class_id   BIGINT,
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE CASCADE
);



INSERT INTO admins(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Кирилл', 'Кириллов', 'Кириллович', 'admin@mail.ru','$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');
INSERT INTO teachers(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Сергей', 'Сергеев', 'Сергеевич', 'techer@mail.ru', '$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');
INSERT INTO parents(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Петр', 'Петров', 'Петрович', 'parent@mail.ru', '$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');

INSERT INTO app_users(user_id, role_name, email)
VALUES (1, 'ROLE_ADMIN', 'admin@mail.ru');
INSERT INTO app_users(user_id, role_name, email)
VALUES (1, 'ROLE_TEACHER', 'techer@mail.ru');
INSERT INTO app_users(user_id, role_name, email)
VALUES (1, 'ROLE_PARENT', 'parent@mail.ru');

-- Test classes
INSERT INTO classes (letter, number, description)
VALUES ('Д', 10, 'Introduction to Computer Science');
INSERT INTO classes (letter, number, description)
VALUES ('Г', 2, 'Calculus II');
INSERT INTO classes (letter, number, description)
VALUES ('В', 1, 'Composition I');
INSERT INTO classes (letter, number, description)
VALUES ('Б', 6, 'US History II');
INSERT INTO classes (letter, number)
VALUES ('А', 4);

INSERT INTO students(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Иван', 'Иванов', 'Иванович', 'student@mail.ru', '$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');
INSERT INTO app_users(user_id, role_name, email)
VALUES (1, 'ROLE_STUDENT', 'student@mail.ru');

INSERT INTO students(firstname, lastname, patronymic, email, hashed_password)
VALUES ('Степан', 'Степанов', 'Степанович', 'student2@mail.ru', '$2a$10$WFRQhlz7Ul85HsRjMg3XNutiB//3HLloe3vTuW6GDPD9eeXeYXiJe');
INSERT INTO app_users(user_id, role_name, email)
VALUES (2, 'ROLE_STUDENT', 'student2@mail.ru');

INSERT INTO subjects (name, type, code, description, class_id, teacher_id)
VALUES ('Математика', 'Базовый', 'MATH6A', 'Введение в алгебру и геометрию', 1, 1),
       ('Физика', 'Базовый', 'PHYS6A', 'Основы механики и волн', 1, 1),
       ('Химия', 'Базовый', 'CHEM6A', 'Введение в химические принципы', 1, 1),
       ('Биология', 'Базовый', 'BIOL6A', 'Введение в науки о жизни', 1, 1),
       ('Английская литература', 'Факультативный', 'ENGL6A', 'Исследование классической литературы', 1, 1);
INSERT INTO subjects (name, type, code, description, teacher_id)
VALUES ('Математика', 'Базовый', 'MATH2A', 'Введение в алгебру и геометрию',1);

INSERT INTO homeworks(title, description, deadline, class_id, subject_id)
VALUES ('Тест 4','Пройти тест по теме 4','2024-09-20',1,1);

-- Вставка расписания для понедельника (day_of_week = 1) с началом в 9:00 для класса с id = 1 по предмету с id = 2
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (1, '2024-10-23 09:00:00', 101, 1, 1);

-- Вставка расписания для пятницы (day_of_week = 5) с началом в 14:00 для класса с id = 3 по предмету с id = 4
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (5, '12:00:00', 202, 1, 1);

-- Вставка расписания для пятницы (day_of_week = 5) с началом в 14:00 для класса с id = 3 по предмету с id = 4
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (4, '12:00:00', 202, 1, 1);

-- Вставка расписания для пятницы (day_of_week = 5) с началом в 14:00 для класса с id = 3 по предмету с id = 4
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (3, '12:00:00', 202, 1, 1);

-- Вставка расписания для пятницы (day_of_week = 5) с началом в 14:00 для класса с id = 3 по предмету с id = 4
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (2, '12:00:00', 202, 1, 1);

-- Вставка первой записи
INSERT INTO grades (mark, date_of_mark, comment, student_id, subject_id)
VALUES (4, '2023-10-26', 'Хорошо выполненная работа', 1, 1);

-- Вставка второй записи
INSERT INTO grades (mark, date_of_mark, comment, student_id, subject_id)
VALUES (3, '2023-10-27', 'Нужно больше практики', 1, 1);

-- Вставка второй записи
INSERT INTO grades (mark, date_of_mark, comment, student_id, subject_id)
VALUES (3, '2023-10-27', 'Нужно больше практики', 1, 2);
