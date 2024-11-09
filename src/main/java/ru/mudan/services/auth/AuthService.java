package ru.mudan.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.GradeRepository;
import ru.mudan.domain.repositories.HomeworkRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.exceptions.entity.not_found.GradeNotFoundException;
import ru.mudan.exceptions.entity.not_found.HomeworkNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;

@SuppressWarnings("MultipleStringLiterals")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MyUserDetailsService myUserDetailsService;
    private final GradeRepository gradeRepository;
    private final SubjectsRepository subjectsRepository;
    private final HomeworkRepository homeworkRepository;

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
                //TODO - поправить на спец исключение
                throw new GradeNotFoundException(id);
            }
        } else {
            //TODO - поправить на спец исключение
            throw new GradeNotFoundException(id);
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
                //TODO - поправить на спец исключение
                throw new SubjectNotFoundException(subjectId);
            }
        } else {
            //TODO - поправить на спец исключение
            throw new SubjectNotFoundException(subjectId);
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
                //TODO - поправить на спец исключение
                throw new HomeworkNotFoundException(hwId);
            }
        } else {
            //TODO - поправить на спец исключение
            throw new HomeworkNotFoundException(hwId);
        }
    }


    private static String getAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream().findFirst().get().getAuthority();
    }

    public void hasRoleAdmin(Authentication authentication) {
        var role = getAuthority(authentication);

        if (!role.equals("ROLE_ADMIN")) {
            //TODO - поправить на спец исключение
            throw new RuntimeException();
        }
    }
}
