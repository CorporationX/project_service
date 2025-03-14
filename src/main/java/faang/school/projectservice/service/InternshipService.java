package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {
    private static final int INTERNSHIP_DURATION_TWO_MONTHS = 2;
    private static final int INTERNSHIP_DURATION_THREE_MONTHS = 3;

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto getInternshipById(Long internshipId) {
        return internshipRepository.findById(internshipId)
                .map(internshipMapper::toInternshipDto)
                .orElseThrow(() -> new EntityNotFoundException("Стажировка не найдена"));
    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public List<InternshipDto> getInternshipsFiltered(InternshipFilterDto filterDto) {
        Objects.requireNonNull(filterDto, "internshipFilterDto is null");
        Stream<Internship> internshipsStream = internshipRepository.findAll().stream();
        for (InternshipFilter internshipFilter : internshipFilters) {
            if (internshipFilter.isApplicable(filterDto)) {
                internshipsStream = internshipFilter.apply(internshipsStream, filterDto);
            }
        }
        return internshipsStream.map(internshipMapper::toInternshipDto).toList();
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        Objects.requireNonNull(internshipDto, "internshipDto is null");
        Internship internship = internshipRepository.findById(internshipDto.getId()).orElseThrow(()
                -> new EntityNotFoundException("Стажировка не найдена"));

        if (internship.getStartDate().isAfter(LocalDateTime.now())) {
            addNewInterns(internship, internshipDto.getInternsId());

        } else if (internship.getEndDate().isBefore(LocalDateTime.now())) {
            completeInternship(internship);

        } else {
            log.info("Стажировка еще идет");
            handleOngoingInterns(internship);

            if (LocalDateTime.now().isAfter(internship.getStartDate().plusMonths(INTERNSHIP_DURATION_TWO_MONTHS))) {
                log.info("Прошло более двух месяцев с даты начала стажировки.");
                handleInternsNotCompletedTasks(internship);
            }
        }
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto createInternship(InternshipDto internshipDto) {

        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new InternshipGetInternsIdException("The list of interns cannot be empty.");
        }

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate()
                .plusMonths(INTERNSHIP_DURATION_THREE_MONTHS))) {
            throw new IllegalArgumentException("Стажировка не может длиться более 3 месяцев.");
        }

        Project project = projectRepository.findById(internshipDto.getProjectId()).orElseThrow(()
                -> new EntityNotFoundException("Проект не найден"));

        List<Team> teams = project.getTeams();

        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId()).orElseThrow(()
                -> new EntityNotFoundException("Нет ментора"));

        Team team = teamMember.getTeam();
        if (!teams.contains(team)) {
            throw new EntityNotFoundException(" Ментор из другого проекта");
        }
        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setProject(project);
        internship.setMentorId(teamMember);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map((id) -> teamMemberRepository.findById(id).orElseThrow(()
                        -> new EntityNotFoundException("Стажер с ID " + id + " не найден")))
                .peek(member -> member.getRoles().add(TeamRole.INTERN))
                .toList();
        internship.setInterns(teamMembers);
        internship.setCreatedAt(LocalDateTime.now());
        internship.setEndDate(internship.getStartDate().plusMonths(INTERNSHIP_DURATION_THREE_MONTHS));

        log.info("Стажировка успешно создана");
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    private void completeInternship(Internship internship) {
        List<TeamMember> completedInterns = internship.getInterns().stream()
                .filter(this::checkAllTasksCompleted)
                .toList();
        completedInterns.forEach(intern -> {
            intern.getRoles().add(TeamRole.DEVELOPER);
            intern.getRoles().remove(TeamRole.INTERN);
        });
        List<TeamMember> notCompletedInterns = internship.getInterns().stream()
                .filter(intern -> !checkAllTasksCompleted(intern))
                .toList();
        internship.getInterns().removeAll(notCompletedInterns);
        log.info("Стажировка завершена. Завершенные стажеры: {}", completedInterns);
        log.info("Удаленные стажеры: {}", notCompletedInterns);
    }

    private void addNewInterns(Internship internship, List<Long> internsId) {
        List<TeamMember> newInterns = internshipRepository.findByInternshipIdIn(internsId);
        if (!newInterns.isEmpty()) {
            internship.getInterns().addAll(newInterns);
        }
    }

    private boolean checkAllTasksStatus(TeamMember intern, TaskStatus status) {
        List<Stage> stages = intern.getStages();
        if (stages.isEmpty()) {
            log.warn("Список стадий пуст для TeamMember: {}", intern.getId());
            return false;
        }
        return stages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(status));
    }

    private boolean checkAllTasksCompleted(TeamMember intern) {
        return checkAllTasksStatus(intern, TaskStatus.DONE);
    }

    private boolean checkAllTasksNotCompleted(TeamMember intern) {
        return checkAllTasksStatus(intern, TaskStatus.TODO);
    }

    private void handleOngoingInterns(Internship internship) {
        List<TeamMember> internsCompletionTask = internship.getInterns().stream()
                .filter(this::checkAllTasksCompleted)
                .toList();
        if (!internsCompletionTask.isEmpty()) {
            internsCompletionTask.forEach(intern -> {
                intern.getRoles().add(TeamRole.DEVELOPER);
                intern.getRoles().remove(TeamRole.INTERN);
                internship.getInterns().remove(intern);
                log.info("Завершенные стажеры: {}", intern);
            });
        } else {
            log.info("Нет стажеров, заблаговременно завершивших все задачи");
        }
    }

    private void handleInternsNotCompletedTasks(Internship internship) {
        List<TeamMember> internsNotCompletionTask = internship.getInterns().stream()
                .filter(this::checkAllTasksNotCompleted)
                .toList();

        if (!internsNotCompletionTask.isEmpty()) {
            internsNotCompletionTask.forEach(intern -> {
                internship.getInterns().remove(intern);
                log.info("Уволенные стажеры: {}", intern);
            });
        }
    }
}
