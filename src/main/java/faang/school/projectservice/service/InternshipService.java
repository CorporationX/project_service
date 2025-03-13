package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto getInternshipById(Long internshipId) {
        return internshipRepository.findById(internshipId)
                .map(internshipMapper::toInternshipDto)
                .orElseThrow(() -> new NotFoundException("Internship not found"));

    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public List<InternshipDto> getInternshipsFiltered(InternshipFilterDto filterDto) {
        Stream<Internship> internshipsStream = internshipRepository.findAll().stream();
        for (InternshipFilter internshipFilter : internshipFilters) {
            if (internshipFilter.isApplicable(filterDto)) {
                internshipsStream = internshipFilter.apply(internshipsStream, filterDto);
            }
        }
        return internshipsStream.map(internshipMapper::toInternshipDto).toList();
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId()).orElseThrow(()
                -> new NotFoundException("Internship not found"));
        //если стажировка еще не началась
        if (internship.getStartDate().isAfter(LocalDateTime.now())) {
            addNewInterns(internship, internshipDto.getInternsId());

            //если стажировка закончилась
        } else if (internship.getEndDate().isBefore(LocalDateTime.now())) {
            completeInternship(internship);

            // стажировка идет
        } else {
            log.info("Стажировка еще идет");
            handleOngoingInterns(internship);


            if (LocalDateTime.now().isAfter(internship.getStartDate().plusMonths(2))) {
                log.info("Прошло более двух месяцев с даты начала стажировки.");
                //список интернов которые по истечению 2 месяцем все задачи со статусом 1todo
                handleInternsNotCompletedTasks(internship);
            }
        }
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto createInternship(InternshipDto internshipDto) {

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plusMonths(3))) {
            throw new IllegalArgumentException("Стажировка не может длиться более 3 месяцев.");
        }

        Project project = projectRepository.findById(internshipDto.getProjectId()).orElseThrow(()
                -> new NotFoundException("Project not found"));

        List<Team> teams = project.getTeams();

        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId()).orElseThrow(()
                -> new NotFoundException("Нет ментора"));

        Team team = teamMember.getTeam();
        if (!teams.contains(team)) {
            throw new NotFoundException(" ментор из другого проекта");
        }
        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setProject(project);
        internship.setMentorId(teamMember);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map((id) -> teamMemberRepository.findById(id).orElseThrow(()
                        -> new RuntimeException("Стажер с ID " + id + " не найден")))
                .peek(member -> member.getRoles().add(TeamRole.INTERN))
                .toList();
        internship.setInterns(teamMembers);
        internship.setCreatedAt(LocalDateTime.now());
        internship.setEndDate(internship.getStartDate().plusMonths(3));

        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    private boolean checkAllTasksCompleted(TeamMember intern) {
        List<Stage> stages = intern.getStages();
        if (stages.isEmpty()) {
            throw new RuntimeException("Stages is empty");
        }
        for (Stage stage : stages) {
            boolean allTaskCompleted = stage.getTasks().stream()
                    .allMatch(task -> task.getStatus() == TaskStatus.DONE);
            if (!allTaskCompleted) {
                return false;
            }
        }
        // Если все задачи завершены на всех стадиях, возвращаем true
        return true;
    }

    private void completeInternship(Internship internship) {
        List<TeamMember> completedInterns = internship.getInterns().stream()
                .filter(this::checkAllTasksCompleted)
                .toList();
        completedInterns.forEach(intern -> {
            intern.getRoles().add(TeamRole.DEVELOPER); // Добавляем роль разработчика
            intern.getRoles().remove(TeamRole.INTERN); // Убираем роль стажера
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

    private boolean checkAllTasksNotCompleted(TeamMember intern) {
        List<Stage> stages = intern.getStages();
        if (stages.isEmpty()) {
            throw new RuntimeException("Stages is empty");
        }
        for (Stage stage : stages) {
            boolean allTaskCompleted = stage.getTasks().stream()
                    .allMatch(task -> task.getStatus() == TaskStatus.TODO);
            if (!allTaskCompleted) {
                return false;
            }
        }
        // Если все задачи завершены на всех стадиях, возвращаем true
        return true;
    }

    private void handleOngoingInterns(Internship internship) {
        List<TeamMember> internsCompletionTask = internship.getInterns().stream()
                .filter(this::checkAllTasksCompleted)
                .toList();

        if (!internsCompletionTask.isEmpty()) {
            internsCompletionTask.forEach(intern -> {
                intern.getRoles().add(TeamRole.DEVELOPER); // Добавляем роль разработчика
                intern.getRoles().remove(TeamRole.INTERN); // Убираем роль стажера
                internship.getInterns().remove(intern); // Убираем из списка стажеров
                log.info("Завершенные стажеры: {}", intern); // Логируем каждого завершенного стажера
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
                internship.getInterns().remove(intern); // Убираем из списка стажеров
                log.info("Уволенные стажеры: {}", intern);
            });
        }
    }
}
