package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private static final Integer MONTH_COUNT = 3;

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;

    public void validateInternshipCreation(InternshipCreateDto internshipDto) {
        validateProjectExists(internshipDto);
        validateInternshipDuration(internshipDto);
        validateMentorBelongsProject(internshipDto);
    }

    public void validateInternshipUpdating(InternshipEditDto internshipDto) {
        validateAddingInterns(internshipDto);
    }

    public boolean validateInternCompletedInternship(InternshipEditDto internshipDto, long internId) {
        List<Task> tasks = projectRepository.findById(internshipDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Проект с ID %d не найден", internshipDto.getProjectId())
                ))
                .getTasks();

        if (internshipDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            return tasks.stream()
                    .noneMatch(task ->
                            task.getPerformerUserId().equals(internId)
                                    && !(task.getStatus().equals(TaskStatus.DONE)));
        }

        return true;
    }

    private void validateProjectExists(InternshipCreateDto internshipDto) {
        Long projectId = internshipDto.getProjectId();

        if (!projectRepository.existsById(projectId)) {
            String message = String.format("Проекта с ID %d не существует!", projectId);
            throw new EntityNotFoundException(message);
        }
    }

    private void validateInternshipDuration(InternshipCreateDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();

        if (startDate.plusMonths(MONTH_COUNT).isBefore(endDate)) {
            throw new DataValidationException("Стажировка не может длиться дольше 3 месяцев!");
        }
    }

    private void validateMentorBelongsProject(InternshipCreateDto internshipDto) {
        Long mentorId = internshipDto.getMentorId();
        Project project = projectRepository.findById(internshipDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Проект с ID %d не найден", internshipDto.getProjectId())
                ));
        List<Team> team = project.getTeams();

        if (team.stream().noneMatch(member -> member.getId().equals(mentorId))) {
            String message = String.format("Ментор с ID %d не учавствует в проекте!", mentorId);
            throw new EntityNotFoundException(message);
        }
    }

    private void validateAddingInterns(InternshipEditDto internshipDto) {
        List<TeamMember> interns = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Стажировка с ID %d не найдена", internshipDto.getId())
                ))
                .getInterns();

        List<Long> internsIds = internshipDto.getInternsIds();

        if (internshipDto.getStartDate().isBefore(LocalDateTime.now())) {
            List<Long> currentIds = interns.stream()
                    .map(TeamMember::getId)
                    .toList();

            internsIds.retainAll(currentIds);

            if (!internsIds.isEmpty()) {
                throw new DataValidationException("Нельзя добавлять стажеров после начала стажировки!");
            }
        }
    }
}
