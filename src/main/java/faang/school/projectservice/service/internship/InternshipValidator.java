package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * InternshipValidator — сервис валидации стажировок
 * <p>
 * Отвечает за проверку корректности DTO при создании и обновлении стажировок,
 * логику завершения стажировки и повышения ролей
 * А так же за проверку соответствия ментора и студентов
 * </p>
 *
 * @author Evgeniy
 * @since 05.08.2025
 */
@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private final UserServiceClient userServiceClient;
    private final TaskRepository taskRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * Проверяет, что у каждого стажёра в InternshipDto есть соответствующий ментор
     * И что сам ментор является участником проекта
     *
     * @param project проект, в рамках которого проводится стажировка
     * @param dto     DTO с информацией о стажировке
     */
    public void validateMentorShip(Project project, InternshipDto dto) {
        long mentorId = dto.getMentorId();
        List<Integer> traineeIds = dto.getTraineeIds();

        for (Integer traineeId : traineeIds) {
            var mentorDto = userServiceClient.getMentor(traineeId);
            if (mentorDto == null || mentorDto.id() != mentorId) {
                throw new IllegalArgumentException("Trainee " + traineeId + " has a different mentor");
            }
        }

        boolean mentorInTeam = project.getTeams().stream()
                .anyMatch(member -> member.getId().equals(mentorId));

        if (!mentorInTeam) {
            throw new IllegalArgumentException("Mentor must be a member of the project team");
        }
    }

    /**
     * Проверяет корректность DTO при создании стажировки
     *
     * @param dto DTO стажировки
     */
    public void validateCreateDto(InternshipDto dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }

        if (dto.getEndDate().isAfter(dto.getStartDate().plusMonths(3))) {
            throw new IllegalArgumentException("Internship cannot last more than 3 months");
        }

        if (dto.getTraineeIds() == null || dto.getTraineeIds().isEmpty()) {
            throw new IllegalArgumentException("Intern list cannot be empty");
        }

        if (dto.getMentorId() == null) {
            throw new IllegalArgumentException("Mentor must be provided");
        }
    }

    /**
     * Проверяет корректность DTO при обновлении стажировки
     * <p>
     * Если стажировка уже началась (IN_PROGRESS), то нельзя изменять список стажёров и ментора
     * </p>
     *
     * @param existing существующая стажировка
     * @param dto      DTO стажировки
     */
    public void validateUpdateDto(Internship existing, InternshipDto dto) {
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date must be specified");
        }

        var existingStatus = existing.getStatus();

        if (existingStatus == faang.school.projectservice.model.InternshipStatus.IN_PROGRESS) {
            if (!existing.getInterns().stream().map(TeamMember::getId)
                    .map(Long::intValue).toList()
                    .equals(dto.getTraineeIds())) {
                throw new IllegalArgumentException("Cannot change interns after internship has started");
            }

            if (!existing.getMentorId().getId().equals(dto.getMentorId().longValue())) {
                throw new IllegalArgumentException("Cannot change mentor after internship has started");
            }
        }
    }

    /**
     * Применяет логику завершения стажировки
     * <p>
     * Для каждого стажёра:
     * - Если все задачи выполнены, то повышаем роль до разработчика
     * - Иначе удаляем стажёра из проекта
     * <p>
     *
     * @param internship объект стажировки
     */
    public void applyCompletionLogic(Internship internship) {
        for (TeamMember intern : internship.getInterns()) {
            boolean completed = hasCompletedAllTasks(intern.getId(), internship.getId());

            InternshipStatus status = internshipMapper.map(internship.getStatus());
            if (status == InternshipStatus.COMPLETED && completed) {
                promoteIntern(intern.getId(), internship.getProject().getId());
            } else {
                removeMemberFromProject(intern.getId(), internship.getProject().getId());
            }
        }
    }

    /**
     * Проверяет, выполнены ли все задачи для стажёра в рамках проекта
     */
    private boolean hasCompletedAllTasks(Long userId, Long projectId) {
        List<Task> tasks = taskRepository.findAllByProjectIdAndPerformerUserId(projectId, userId);
        return tasks.stream().allMatch(task -> task.getStatus() == TaskStatus.DONE);
    }

    /**
     * Повышает роль стажёра до разработчика
     */
    private void promoteIntern(Long userId, Long projectId) {
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        member.getRoles().remove(TeamRole.INTERN);
        member.getRoles().add(TeamRole.DEVELOPER);
        teamMemberRepository.save(member);
    }

    /**
     * Удаляет стажёра из проекта, если стажировка не завершена успешно
     */
    private void removeMemberFromProject(Long userId, Long projectId) {
        teamMemberRepository.removeMemberFromProjectOrThrow(userId, projectId);
    }
}