package ru.mudan.services.auth;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.entity.users.Student;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.*;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.*;
import ru.mudan.services.students.StudentService;

@SuppressWarnings("MultipleStringLiterals")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MyUserDetailsService myUserDetailsService;
    private final GradeRepository gradeRepository;
    private final SubjectsRepository subjectsRepository;
    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public void teacherHasGradeOrRoleIsAdmin(Long id, Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_TEACHER")) {
            var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

            var grade = gradeRepository.findById(id)
                    .orElseThrow(() -> new GradeNotFoundException(id));

            var subjectForGrade = grade.getSubject();

            if (!teacher.getSubjects().contains(subjectForGrade)) {
                throw new ApplicationForbiddenException();
            }
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
            var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

            var subject = subjectsRepository.findById(subjectId)
                    .orElseThrow(() -> new SubjectNotFoundException(subjectId));

            if (!teacher.getSubjects().contains(subject)) {
                throw new ApplicationForbiddenException();
            }
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
            var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

            var hw = homeworkRepository.findById(hwId)
                    .orElseThrow(() -> new HomeworkNotFoundException(hwId));

            var subjectForHW = hw.getSubject();

            if (!teacher.getSubjects().contains(subjectForHW)) {
                throw new ApplicationForbiddenException();
            }
        } else {
            throw new ApplicationForbiddenException();
        }
    }

    public void hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(
            Long classId,
            Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_STUDENT")) {
            var student = (Student) myUserDetailsService.loadUserByUsername(authentication.getName());

            var foundClass = classRepository.findById(classId)
                    .orElseThrow(() -> new ClassEntityNotFoundException(classId));

            var studentsForClass = foundClass.getStudents();

            if (!studentsForClass.contains(student)) {
                throw new ApplicationForbiddenException();
            }
        } else if (role.equals("ROLE_PARENT")) {
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
                throw new ApplicationForbiddenException();
            }
        } else {
            throw new ApplicationForbiddenException();
        }
    }

    public void hasRoleAdminOrStudentThatInClassThatContainsSubject(Long studentId,
                                                                    Long subjectId,
                                                                    Authentication authentication) {
        var role = getAuthority(authentication);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (role.equals("ROLE_STUDENT")) {
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
        } else if (role.equals("ROLE_PARENT")) {
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
        } else {
            throw new ApplicationForbiddenException();
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
            throw new ApplicationForbiddenException();
        }
    }
}
