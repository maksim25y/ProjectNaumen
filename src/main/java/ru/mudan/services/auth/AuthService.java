package ru.mudan.services.auth;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.entity.users.Student;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.*;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.*;

@Slf4j
@SuppressWarnings("MultipleStringLiterals")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MyUserDetailsService myUserDetailsService;
    private final GradeRepository gradeRepository;
    private final SubjectsRepository subjectsRepository;
    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    public void teacherHasGradeOrRoleIsAdmin(Long id, Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_TEACHER")) {
            checkTeacherHasGrade(id, authentication);
        } else {
            throw new ApplicationForbiddenException();
        }
    }

    public void teacherHasSubjectOrRoleIsAdmin(Long subjectId, Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_TEACHER")) {
            checkTeacherHasSubject(subjectId, authentication);
        } else {
            throw new ApplicationForbiddenException();
        }
    }

    public void teacherHasHomeworkOrRoleIsAdmin(Long hwId, Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_TEACHER")) {
            checkTeacherHasHW(hwId, authentication);
        } else {
            throw new ApplicationForbiddenException();
        }
    }

    public void hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(
            Long classId,
            Authentication authentication) {
        var role = getAuthority(authentication);

        switch (role) {
            case "ROLE_ADMIN" -> {
                return;
            }
            case "ROLE_STUDENT" -> {
                checkStudentFromClass(classId, authentication);
            }
            case "ROLE_PARENT" -> {
                checkParentHasStudentInClass(classId, authentication);
            }
            default -> throw new ApplicationForbiddenException();
        }

    }

    public void hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(Long studentId,
                                                                                 Long subjectId,
                                                                                 Authentication authentication) {
        var role = getAuthority(authentication);

        switch (role) {
            case "ROLE_ADMIN" -> {
                return;
            }
            case "ROLE_STUDENT" -> {
                checkStudentFromClassContainsSubject(studentId, subjectId, authentication);
            }
            case "ROLE_PARENT" -> {
                checkParentHasStudentInClassContainsSubject(studentId, subjectId, authentication);
            }
            default -> throw new ApplicationForbiddenException();
        }

    }

    private void checkParentHasStudentInClassContainsSubject(Long studentId,
                                                             Long subjectId,
                                                             Authentication authentication) {
        var parent = (Parent) myUserDetailsService.loadUserByUsername(authentication.getName());

        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        var studentsForParent = parent.getStudents();

        if (!studentsForParent.contains(foundStudent)) {
            throw new ApplicationForbiddenException();
        }

        if (subjectId != null) {
            checkClassContainsSubject(subjectId, foundStudent);
        }
    }

    private void checkStudentFromClassContainsSubject(Long studentId, Long subjectId, Authentication authentication) {
        var student = (Student) myUserDetailsService.loadUserByUsername(authentication.getName());
        var studentById = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if (!studentById.getId().equals(student.getId())) {
            throw new ApplicationForbiddenException();
        }

        var classForStudent = studentById.getClassEntity();

        if (subjectId != null && classForStudent != null) {
            var subjectForStudent = subjectsRepository.findById(subjectId)
                    .orElseThrow(() -> new SubjectNotFoundException(subjectId));

            var subjectsFromClass = classForStudent.getSubjects();

            if (!subjectsFromClass.contains(subjectForStudent)) {
                throw new ApplicationForbiddenException();
            }
        }
    }

    private void checkClassContainsSubject(Long subjectId, Student foundStudent) {
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var classForStudent = foundStudent.getClassEntity();

        if (classForStudent != null) {
            var subjectsForClass = classForStudent.getSubjects();
            if (!subjectsForClass.contains(foundSubject)) {
                throw new ApplicationForbiddenException();
            }
        }
    }

    private String getAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream().findFirst().get().getAuthority();
    }

    public void hasRoleAdmin(Authentication authentication) {
        var role = getAuthority(authentication);

        if (!role.equals("ROLE_ADMIN")) {
            throw new ApplicationForbiddenException();
        }
    }

    public void teacherContainSubject(Long subjectId, Authentication authentication) {
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

        if (!teacher.getSubjects().contains(foundSubject)) {
            log.info("Teacher not contains subject with id={}", subjectId);
            throw new ApplicationForbiddenException();
        }
    }

    private void checkStudentFromClass(Long classId, Authentication authentication) {
        var student = (Student) myUserDetailsService.loadUserByUsername(authentication.getName());

        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));

        var studentsForClass = foundClass.getStudents();

        if (!studentsForClass.contains(student)) {
            log.info("Student with not from class with id={}", classId);
            throw new ApplicationForbiddenException();
        }
    }

    private void checkTeacherHasSubject(Long subjectId, Authentication authentication) {
        var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

        var subject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        if (!teacher.getSubjects().contains(subject)) {
            log.info("Teacher has not subject with id={}", subjectId);
            throw new ApplicationForbiddenException();
        }
    }

    private void checkParentHasStudentInClass(Long classId, Authentication authentication) {
        var parent = (Parent) myUserDetailsService.loadUserByUsername(authentication.getName());

        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));

        var studentsForParent = parent.getStudents();

        AtomicBoolean has = new AtomicBoolean(false);

        studentsForParent.forEach(st -> {
            var classEntity = st.getClassEntity();
            if (classEntity != null) {
                if (classEntity.getId().equals(foundClass.getId())) {
                    has.set(true);
                }
            }
        });

        if (!has.get()) {
            log.info("Teacher has not students in class with id={}", classId);
            throw new ApplicationForbiddenException();
        }
    }

    private void checkTeacherHasGrade(Long gradeId, Authentication authentication) {
        var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

        var grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new GradeNotFoundException(gradeId));

        var subjectForGrade = grade.getSubject();

        if (!teacher.getSubjects().contains(subjectForGrade)) {
            log.info("Teacher has not grade with id={}", gradeId);
            throw new ApplicationForbiddenException();
        }
    }

    private void checkTeacherHasHW(Long hwId, Authentication authentication) {
        var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

        var hw = homeworkRepository.findById(hwId)
                .orElseThrow(() -> new HomeworkNotFoundException(hwId));

        var subjectForHW = hw.getSubject();

        if (!teacher.getSubjects().contains(subjectForHW)) {
            log.info("Teacher has not homework with id={}", hwId);
            throw new ApplicationForbiddenException();
        }
    }
}
