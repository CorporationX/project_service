package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.apimodel.InternshipStatusDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.FilterService;
import faang.school.projectservice.service.filter.intership.InternshipSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final TaskRepository taskRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FilterService<Internship, InternshipFilterDto> internshipFilterService;

    @Override
    @Transactional
    public InternshipDto create(Long projectId, InternshipDto dto) {
        internshipValidator.validateCreateDto(dto);

        Project project = projectRepository.getByIdOrThrow(projectId);
        internshipValidator.validateMentorship(project, dto);

        Internship internship = internshipMapper.toEntityWithProject(dto, project);

        initializeMembers(internship, dto);

        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toDto(saved);
    }

    @Override
    @Transactional
    public InternshipDto update(Long id, InternshipDto dto) {
        Internship existing = internshipRepository.getRequiredById(id);

        internshipValidator.validateUpdateDto(existing, dto);

        existing.setStatus(internshipMapper.map(dto.getStatus()));
        existing.setEndDate(dto.getEndDate().toLocalDateTime());

        if (dto.getStatus() == InternshipStatusDto.COMPLETED || dto.getStatus() == InternshipStatusDto.FAILED) {
            applyCompletionLogic(existing);
        }

        Internship saved = internshipRepository.save(existing);
        return internshipMapper.toDto(saved);
    }

    @Override
    public List<InternshipDto> findAll() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public InternshipDto findById(Long id) {
        Internship internship = internshipRepository.getRequiredById(id);
        return internshipMapper.toDto(internship);
    }

    public List<Internship> getFilteredInternships(InternshipFilterDto dto) {
        return internshipRepository.findAll(
                InternshipSpecifications.byFilter(dto)
        );
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
    private void applyCompletionLogic(Internship internship) {
        for (TeamMember intern : internship.getInterns()) {
            boolean completed = hasCompletedAllTasks(intern.getId(), internship.getId());

            InternshipStatusDto status = internshipMapper.map(internship.getStatus());
            if (status == InternshipStatusDto.COMPLETED && completed) {
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
        teamMemberRepository.deleteByUserIdAndProjectIdOrThrow(userId, projectId);
    }

    private void initializeMembers(Internship internship, InternshipDto dto) {
        TeamMember mentor = new TeamMember();
        mentor.setId(dto.getMentorId().longValue());
        internship.setMentorId(mentor);

        internship.setInterns(
                dto.getTraineeIds().stream()
                        .map(id -> new TeamMember(id.longValue()))
                        .toList()
        );
    }
}