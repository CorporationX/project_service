package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.InternshipToCreateDto;
import faang.school.projectservice.dto.client.InternshipToUpdateDto;
import faang.school.projectservice.exception.internship.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.projectservice.exception.internship.InternshipError.*;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserValidator userValidator;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final static int MONTH_AMOUNT = 3;

    public void validateForCreation(long userId, InternshipToCreateDto internshipDto) {
        userValidator.validateUserExistence(userId);
        validateInternsExists(internshipDto.getInternsId());
        validateMentorExists(internshipDto.getMentorId());
        validateProjectExists(internshipDto.getProjectId());
    }

    public void validateForUpdate(long userId, InternshipToUpdateDto internshipDto, Internship internship) {
        userValidator.validateUserExistence(userId);
        validateInternshipExists(internship.getId());
        validateMentorExists(internshipDto.getMentorId());
    }

    //вызвать когда будет проверка на даты в апдейт методе
    public void validateInternshipDuration(LocalDateTime start, LocalDateTime end) {
        LocalDateTime dateBetweenStartAndEnd = start.plusMonths(MONTH_AMOUNT);
        if (dateBetweenStartAndEnd.isAfter(end)) {
            throw new DataValidationException(INTERNSHIP_DURATION_EXCEPTION);
        }
    }

    public void validateInternsExists(List<Long> interns) {
        List<TeamMember> membersInRepository = teamMemberJpaRepository.findAllById(interns);
        Set<Long> membersId = membersInRepository.stream()
                .map(TeamMember::getId)
                .collect(Collectors.toSet());
        interns.forEach(intern ->
        {
            if (!membersId.contains(intern)) {
                throw new DataValidationException(ENTITY_NOT_FOUND);
            }
        });
    }

    public void validateMentorExists(Long mentorId) {
        if (mentorId != null) teamMemberRepository.findById(mentorId);
    }

    private void validateProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId))
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION);
    }

    private void validateInternshipExists(long internshipId) {
        internshipRepository.findById(internshipId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION));
    }
}
