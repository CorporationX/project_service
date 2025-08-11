package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void validateMentorship(Project project, InternshipDto dto) {
        long mentorId = dto.getMentorId();
        List<Integer> traineeIds = dto.getTraineeIds();

        for (Integer traineeId : traineeIds) {
            var mentorDto = userServiceClient.getMentor(traineeId);
            if (mentorDto == null || !mentorDto.id().equals(mentorId)) {
                throw new IllegalArgumentException(
                        String.format("Trainee %d has a different mentor", traineeId)
                );
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

        if (existingStatus == InternshipStatus.IN_PROGRESS) {
            Set<Integer> existingTraineeIds = existing.getInterns().stream()
                    .map(TeamMember::getId)
                    .map(Long::intValue)
                    .collect(Collectors.toSet());

            Set<Integer> newTraineeIds = new HashSet<>(dto.getTraineeIds());

            if (!(newTraineeIds.containsAll(existingTraineeIds) && existingTraineeIds.containsAll(newTraineeIds))) {
                throw new IllegalArgumentException("Cannot change interns after internship has started");
            }

            Long existingMentorId = existing.getMentorId() != null ? existing.getMentorId().getId() : null;
            Long newMentorId = dto.getMentorId() != null ? dto.getMentorId().longValue() : null;

            if (!Objects.equals(existingMentorId, newMentorId)) {
                throw new IllegalArgumentException("Cannot change mentor after internship has started");
            }
        }
    }
}