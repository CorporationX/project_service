package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternshipDataPreparer {
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final ScheduleRepository scheduleRepository;
    private final List<InternshipFilter> internshipFilters;

    public Internship prepareEntityForCreate(InternshipDto dto, Internship entity) {
        TeamMember mentor = teamMemberRepository.findById(dto.getMentorId());
        Project project = projectRepository.getProjectById(dto.getProjectId());
        Schedule schedule = getScheduleById(dto.getScheduleId());
        List<TeamMember> interns = genInterns(dto.getInternIds());
        entity.setMentor(mentor);
        entity.setProject(project);
        entity.setSchedule(schedule);
        entity.setInterns(interns);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());
        return entity;
    }

    public Internship prepareEntityForUpdate(InternshipDto dto, Internship entity) {
        if (!Objects.equals(dto.getMentorId(), entity.getMentor().getId())) {
            entity.setMentor(teamMemberRepository.findById(dto.getMentorId()));
        }
        if (!Objects.equals(dto.getScheduleId(), entity.getSchedule().getId())) {
            entity.setSchedule(getScheduleById(dto.getScheduleId()));
        }
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setEndDate(dto.getEndDate());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    public List<Internship> filterInternships(List<Internship> internships, InternshipFilterDto filters) {

        List<InternshipFilter> actualFilters = internshipFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .toList();

        return internships.stream()
                .filter(e -> actualFilters.stream()
                        .allMatch(f -> f.apply(e, filters)))
                .toList();
    }

    public void evaluationInterns(Internship entity, TeamRole role) {
        List<TeamMember> interns = entity.getInterns();
        List<TeamMember> teamMembers = interns.stream()
                .peek(teamMember -> {
                    teamMember.getRoles().remove(TeamRole.INTERN);
                    if (checkExamResult(teamMember)) {
                        // возможное переопределение в другую команду
                        teamMember.getRoles().add(role);
                        return;
                    }
                    // возможное удаление из проекта или команды
                })
                .toList();

        entity.setInterns(teamMembers);
    }

    private boolean checkExamResult(TeamMember teamMember) {
        return teamMember.getStages().stream()
                .map(s -> s.getTasks().stream()
                        .anyMatch(t -> !t.getStatus().equals(TaskStatus.DONE)))
                .filter(isFailed -> isFailed.equals(true))
                .findFirst()
                .isEmpty();
    }

    private Schedule getScheduleById(long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Такого расписания не существует."));
    }

    private List<TeamMember> genInterns(List<Long> internIds) {
        return internIds.stream()
                .map(teamMemberRepository::findById).toList();
    }
}
