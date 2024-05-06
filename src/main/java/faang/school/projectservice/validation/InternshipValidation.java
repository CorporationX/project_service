package faang.school.projectservice.validation;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.FOREIGN_MENTOR_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_PROJECT_EXCEPTION;

@Component
@RequiredArgsConstructor
public class InternshipValidation {
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyRepository vacancyRepository;

    public void validationCreate(InternshipDto internshipDto) {
        projectValidation(internshipDto.getProjectId());
        mentorValidation(internshipDto.getMentorId(), internshipDto.getProjectId());
        internsValidation(internshipDto.getInternsIds());
    }

    private void internsValidation(List<Long> internsIds) {
        internsIds.forEach(teamMemberRepository::findById);
    }

    private void mentorValidation(Long mentorId, Long projectId) {
        var mentor = teamMemberRepository.findById(mentorId);
        var mentorsProjectId = mentor.getTeam().getProject().getId();

        if (!mentorsProjectId.equals(projectId)) {
            throw new DataValidationException(FOREIGN_MENTOR_EXCEPTION.getMessage());
        }
    }

    private void projectValidation(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION.getMessage());
        }
    }
}
