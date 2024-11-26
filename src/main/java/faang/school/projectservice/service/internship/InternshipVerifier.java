package faang.school.projectservice.service.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.FOREIGN_MENTOR_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NEW_INTERNS_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERN_EXCEPTION;

@Component
class InternshipVerifier {
    public void verifyExistenceOfAllInterns(List<TeamMember> interns, Integer internsNumInDto) {
        if (interns.size() != internsNumInDto) {
            throw new DataValidationException(NON_EXISTING_INTERN_EXCEPTION.getMessage());
        }
    }

    public void verifyMentorsProject(Project project, TeamMember mentor) {
        Project mentorsProject = mentor.getTeam().getProject();

        if (!project.equals(mentorsProject)) {
            throw new DataValidationException(FOREIGN_MENTOR_EXCEPTION.getMessage());
        }
    }

    public void verifyUpdatedInterns(Internship internshipBeforeUpdate, List<TeamMember> internsAfterUpdate) {
        if (!internshipBeforeUpdate.getInterns().containsAll(internsAfterUpdate)) {
            throw new DataValidationException(NEW_INTERNS_EXCEPTION.getMessage());
        }
    }
}