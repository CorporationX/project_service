package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternshipDtoValidator {
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    private final int DURATION_INTERNSHIP_IN_MONTH = 3;

    public boolean checkCompletedStatus(InternshipDto dto, Internship entity) {
        InternshipStatus statusDto = dto.getStatus();
        InternshipStatus statusEntity = entity.getStatus();
        InternshipStatus statusCompleted = InternshipStatus.COMPLETED;

        if (statusEntity.equals(statusCompleted)) {
            throw new DataValidateException("В завершённую стажировку, нельзя вносить изменения.");
        }

        return statusDto.equals(statusCompleted);
    }

    public void validateMentorIsMember(InternshipDto dto) {
        TeamMember mentor = teamMemberRepository.findById(dto.getMentorId());
        Project project = projectRepository.getProjectById(dto.getProjectId());

        boolean resultValidate = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));

        if (!resultValidate) {
            throw new DataValidateException("Ментор не из команды проекта.");
        }
    }

    public void validateStartEndDate(InternshipDto dto) {
        LocalDateTime start = dto.getStartDate();
        LocalDateTime end = dto.getEndDate();
        if (end.isBefore(start)) {
            throw new DataValidateException("Дата окончания стажировки не может быть раньше начала.");
        }
        if (end.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new DataValidateException("Дата окончания стажировки не может быть раньше текущего времени.");
        }
        if (end.isBefore(start.plusMonths(DURATION_INTERNSHIP_IN_MONTH))) {
            throw new DataValidateException("Продолжительность стажировки не может быть меньше %d месяцев.".formatted(DURATION_INTERNSHIP_IN_MONTH));
        }
    }

    public void checkChangeInterns(InternshipDto dto, Internship entity) {
        List<Long> dtoInternIds = dto.getInternIds();
        List<Long> entityInternIds = entity.getInterns().stream().map(TeamMember::getId).toList();
        if (!Objects.equals(dtoInternIds, entityInternIds)) {
            throw new DataValidateException("Во время стажировки состав стажёров менять нельзя.");
        }
    }

}
