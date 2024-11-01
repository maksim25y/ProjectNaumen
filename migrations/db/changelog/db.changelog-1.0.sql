CREATE TABLE classes (
                         id BIGSERIAL PRIMARY KEY,
                         letter VARCHAR(10),
                         number INTEGER,
                         description VARCHAR
);

CREATE TABLE subjects (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255),
                          type VARCHAR(50),
                          code VARCHAR(50),
                          description VARCHAR(255)
);

CREATE TABLE students (
                          id BIGSERIAL PRIMARY KEY,
                          firstname VARCHAR(255),
                          lastname VARCHAR(255),
                          patronymic VARCHAR(255),
                          class_id BIGINT,
                          FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

CREATE TABLE schedules (
                           id BIGSERIAL PRIMARY KEY,
                           day_of_week INTEGER,
                           start_time TIMESTAMP,
                           number_of_classroom INTEGER,
                           class_id BIGINT,
                           subject_id BIGINT,
                           FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
                           FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE homeworks (
                           id BIGSERIAL PRIMARY KEY,
                           title VARCHAR(255),
                           description VARCHAR,
                           deadline DATE,
                           class_id BIGINT,
                           subject_id BIGINT,
                           FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
                           FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE grades (
                        id BIGSERIAL PRIMARY KEY,
                        mark INTEGER,
                        date_of_mark DATE,
                        comment VARCHAR,
                        student_id BIGINT,
                        subject_id BIGINT,
                        FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                        FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

INSERT INTO classes (letter, number, description)
VALUES
    ('B', 102, 'Advanced Programming'),
    ('C', 103, 'Data Structures and Algorithms');

-- Математика
INSERT INTO subjects (name, type, code, description)
VALUES ('Математика', 'Общеобразовательный', 'MAT101', 'Развитие математических навыков, алгебры, геометрии и тригонометрии');

-- Русский язык
INSERT INTO subjects (name, type, code, description)
VALUES ('Русский язык', 'Общеобразовательный', 'RUS101', 'Изучение русского языка, грамматики, орфографии, пунктуации и литературы');

-- Литература
INSERT INTO subjects (name, type, code, description)
VALUES ('Литература', 'Общеобразовательный', 'LIT101', 'Анализ и интерпретация литературных произведений, стилей и жанров');

-- История
INSERT INTO subjects (name, type, code, description)
VALUES ('История', 'Общеобразовательный', 'HIS101', 'Изучение истории человечества, ключевых событий, персоналий и цивилизаций');

-- География
INSERT INTO subjects (name, type, code, description)
VALUES ('География', 'Общеобразовательный', 'GEO101', 'Изучение Земли, ее ландшафтов, климата, населения и географических процессов');

-- Биология
INSERT INTO subjects (name, type, code, description)
VALUES ('Биология', 'Общеобразовательный', 'BIO101', 'Исследование живых организмов, их строения, функций, экосистем и эволюции');

-- Английский язык
INSERT INTO subjects (name, type, code, description)
VALUES ('Английский язык', 'Иностранный', 'ENG101', 'Изучение английского языка, грамматики, лексики, произношения и разговорной речи');

-- Информатика
INSERT INTO subjects (name, type, code, description)
VALUES ('Информатика', 'Общеобразовательный', 'INF101', 'Изучение основ информатики, программирования, компьютерных технологий и алгоритмов');

INSERT INTO students (firstname, lastname, patronymic, class_id)
VALUES ('Иван', 'Иванов', 'Иванович', 1);
INSERT INTO schedules (day_of_week, start_time, number_of_classroom, class_id, subject_id)
VALUES (2, '2023-10-26 10:00:00', 101, 1, 1);
INSERT INTO homeworks (title, description, deadline, class_id, subject_id)
VALUES ('Органические вещества', 'Нужно сделать таблицу', '2024-10-31', 1, 1);
INSERT INTO grades (mark, date_of_mark, comment, student_id, subject_id)
VALUES (4, '2024-10-25', 'Недочёты в задании 4', 1, 1);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       firstname VARCHAR NOT NULL,
                       lastname VARCHAR NOT NULL,
                       email VARCHAR NOT NULL,
                       role VARCHAR NOT NULL,
                       hashed_password VARCHAR NOT NULL
);

INSERT INTO users(firstname,lastname,email,role, hashed_password)
VALUES ('Макс','Максов','user@example.com','ROLE_ADMIN',
        '$2y$10$FZmMMjucUkXzZLQEpwQlZOHPHGpSrbXe2EfcDg9yNwXjbzmrPs9TS');

CREATE TABLE reports(
                        id BIGSERIAL PRIMARY KEY,
                        status VARCHAR,
                        content TEXT
);



