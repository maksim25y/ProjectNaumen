# Проект "Электронный дневник", выполненный в рамках курса "Промышленная разработка на Java" от компании Naumen
## Описание решения
Стек технологий:
* Spring Boot (Web, Validation, Test, Testcontainers)
* Liquabase
* PostgreSQL
* Maven
* Lombok
* Testcontainers
* JUnit
* Docker/Docker Compose
* Grafana
* Prometheus
## Инструкция по запуску
**Требования: в системе должен быть установлен docker и docker-compose**

1. Скачайте архив с репозиторием в удобное место у себя на компьютере:
    ```
    git clone https://github.com/maksim25y/ProjectNaumen.git
    ```
2. Далее перейдите в директорию с файлом *docker-compose.yml*:
    ```
    cd ProjectNaumen
    ```
3. Теперь можно указать почту для нотификаций, указав адрес электронной почты и пароль приложения:
   * С помощью любого удобного редактора откройте файл `.env`, находящийся в директории docker
   и отредактируйте следующую переменную:
     ```
     ...
     MAIL_USERNAME=<EMAIL>
     Пример: MAIL_USERNAME=test@gmail.com
     MAIL_PASSWORD=<PASSWORD>
     Пример: MAIL_PASSWORD=ertqydrthwerhtyj
     ...
     Вместо `<EMAIL>` необходимо вставить адрес электронной почты gmail.
     Вместо `<PASSWORD>` необходимо вставить пароль приложения для указанного адреса почты.
     Подробнее: ссылка на создание
     ```
4. Теперь можно запустить приложение:
    * Для Linux систем:
      ```
      docker compose up
      ```
    * Для Windows систем:
      ```
      docker-compose up
      ```
