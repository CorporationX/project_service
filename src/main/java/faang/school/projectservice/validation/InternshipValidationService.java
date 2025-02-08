package faang.school.projectservice.validation;

import faang.school.projectservice.dto.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.internship.InternshipUpdateRequest;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class InternshipValidationService {
    private final ProjectRepository projectRepository;

    public void validateRequest(Object request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос не может быть пустым");
        }

        if (request instanceof InternshipCreateRequest createRequest) {
            validateCommonFields(createRequest);
        } else if (request instanceof InternshipUpdateRequest updateRequest) {
            validateCommonFields(updateRequest);
        } else {
            throw new IllegalArgumentException("Неизвестный тип запроса");
        }
    }

    private void validateCommonFields(Object request) {
        if (request instanceof InternshipCreateRequest dto) {
            if (dto.startDate().isAfter(dto.endDate())) {
                throw new IllegalArgumentException("Начало стажировки должно быть раньше чем ее конец");
            }
            long monthsBetween = ChronoUnit.MONTHS.between(
                    dto.startDate().toLocalDate(),
                    dto.endDate().toLocalDate());
            if (monthsBetween > 3) {
                throw new IllegalArgumentException("Длительность стажировки не должна превышать 3-х месяцев");
            }
            long mentorId = dto.mentorId();
            Project project = projectRepository.getById(dto.projectId());
            boolean isInternshipMentorInProjectTeam = project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(teamMember -> teamMember.getId().equals(mentorId));
            if (!isInternshipMentorInProjectTeam) {
                throw new IllegalArgumentException("Ментор должен состоять в команде проекта");
            }
        } else if (request instanceof InternshipUpdateRequest dto && dto.getName() != null) {
            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                throw new IllegalArgumentException("Начало стажировки должно быть раньше чем ее конец");
            }
            long monthsBetween = ChronoUnit.MONTHS.between(
                    dto.getStartDate().toLocalDate(),
                    dto.getEndDate().toLocalDate());
            if (monthsBetween > 3) {
                throw new IllegalArgumentException("Длительность стажировки не должна превышать 3-х месяцев");
            }
            long mentorId = dto.getMentorId();
            Project project = projectRepository.getById(dto.getProjectId());
            boolean isInternshipMentorInProjectTeam = project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(teamMember -> teamMember.getId().equals(mentorId));
            if (!isInternshipMentorInProjectTeam) {
                throw new IllegalArgumentException("Ментор должен состоять в команде проекта");
            }
        }
    }
}