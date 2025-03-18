package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ScheduleRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {
    private static final int INTERNSHIP_DURATION_THREE_MONTHS = 3;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ScheduleRepository scheduleRepository;

    public InternshipDto getInternshipById(Long internshipId) {
        Objects.requireNonNull(internshipId, "internshipId is null");
        return internshipRepository.findById(internshipId)
                .map(internshipMapper::toInternshipDto)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found for search by id: " + internshipId));
    }

    public List<InternshipDto> getAllInternships() {
        List<Internship> internshipList = internshipRepository.findAll();
        if (internshipList.isEmpty()) {
            throw new EntityNotFoundException("The list of internShip is empty");
        }
        return internshipList.stream()
                .map(internshipMapper::toInternshipDto).toList();
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

    public InternshipDto updateInternship(InternshipDto internshipDto, Long internshipId) {
        Objects.requireNonNull(internshipId, "internshipId is null");
        Objects.requireNonNull(internshipDto, "internshipDto is null");
        Internship internship = internshipRepository.findById(internshipId).orElseThrow(()
                -> new EntityNotFoundException("Internship not found for search by id : " + internshipId));

        if (internship.getStartDate().isAfter(LocalDateTime.now())) {
            addNewInterns(internship, internshipDto.getInternsId());
            internshipRepository.save(internship);
            return internshipMapper.toInternshipDto(internship);
        }
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            completeInternship(internship);
            internshipRepository.save(internship);
            return internshipMapper.toInternshipDto(internship);
        }
        if (internship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            log.info("The internship is still ongoing");
            aheadOfSchedule(internship, internshipDto);
            internshipRepository.save(internship);
            return internshipMapper.toInternshipDto(internship);
        }
        log.info("стажировка не обновилась");
        return internshipDto;
    }

    public InternshipDto createInternship(InternshipDto internshipDto) {
        Objects.requireNonNull(internshipDto, "internshipDto is null");

        validateInternsId(internshipDto);
        validateInternshipDuration(internshipDto);
        Project project = validateProject(internshipDto.getProjectId());
        TeamMember teamMember = validateMentor(internshipDto.getMentorId(), project);
        List<TeamMember> teamMembers = validateInterns(internshipDto.getInternsId());

        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setProject(project);
        internship.setMentorId(teamMember);
        internship.setInterns(teamMembers);
        internship.setSchedule(validateSchedule(internshipDto.getScheduleId()));

        log.info("Internship successfully created");
        InternshipDto dto = internshipMapper.toInternshipDto(internship);
        internshipRepository.save(internship);
        return dto;
    }

    private void completeInternship(Internship internship) {
        List<TeamMember> completedInterns = internship.getInterns().stream()
                .filter(this::checkAllTasksCompleted)
                .toList();
        List<TeamMember> notCompletedInterns = new ArrayList<>(internship.getInterns());
        notCompletedInterns.removeAll(completedInterns);
        updateRolesForCompletedInterns(completedInterns);
        removeInternRoleFromNotCompleted(notCompletedInterns);
        internship.setInterns(new ArrayList<>());
        log.info("Internship Completed. Completed Interns: {}", completedInterns);
        log.info("Remote Interns: {}", notCompletedInterns);
    }

    private void updateRolesForCompletedInterns(List<TeamMember> completedInterns) {
        completedInterns.forEach(intern -> {
            if (intern.getRoles().contains(TeamRole.INTERN)) {
                intern.getRoles().remove(TeamRole.INTERN);
            }
            intern.getRoles().add(TeamRole.DEVELOPER);
        });
    }

    private void removeInternRoleFromNotCompleted(List<TeamMember> notCompletedInterns) {
        notCompletedInterns.forEach(intern -> {
            if (intern.getRoles().contains(TeamRole.INTERN)) {
                intern.getRoles().remove(TeamRole.INTERN);
            }
        });
    }

    private void addNewInterns(Internship internship, List<Long> internsId) {
        List<TeamMember> interns = internshipRepository.findByInternshipIdIn(internsId);
        if (!interns.isEmpty()) {
            internship.setInterns((interns));
        }
    }

    private boolean checkAllTasksCompleted(TeamMember intern) {
        if (intern == null) {
            log.warn("TeamMember is null");
            return false;
        }
        List<Stage> stages = intern.getStages();
        if (stages.isEmpty()) {
            log.warn("Stage list is empty for TeamMember: {}", intern.getId());
            return false;
        }
        boolean allTasksCompleted = stages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
        if (!allTasksCompleted) {
            log.info("Not all tasks are completed for TeamMember: {}", intern.getId());
        }
        return allTasksCompleted;
    }

    private void aheadOfSchedule(Internship internship, InternshipDto internshipDto) {
        List<TeamMember> oldList = internship.getInterns();
        List<TeamMember> newList = internshipRepository.findByInternshipIdIn(internshipDto.getInternsId());
        oldList.removeAll(newList);
        if (oldList.isEmpty()) {
            log.info("There are no people who passed the test early or were dismissed early.");
            return;
        }
        for (TeamMember intern : oldList) {
            if (checkAllTasksCompleted(intern)) {
                intern.getRoles().add(TeamRole.DEVELOPER);
                intern.getRoles().remove(TeamRole.INTERN);
            } else {
                log.info("Dismissed interns: {}", intern);
            }
        }
        internship.setInterns(newList);
    }

    private void validateInternsId(InternshipDto internshipDto) {
        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new InternshipGetInternsIdException("The list of interns is empty");
        }
    }

    private void validateInternshipDuration(InternshipDto internshipDto) {
        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate()
                .plusMonths(INTERNSHIP_DURATION_THREE_MONTHS))) {
            throw new IllegalArgumentException("The internship cannot last more than 3 months.");
        }
    }

    private Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException("Project not found for search by id: " + projectId));
    }

    private TeamMember validateMentor(Long mentorId, Project project) {
        TeamMember teamMember = teamMemberRepository.findById(mentorId).orElseThrow(() ->
                new EntityNotFoundException("No mentor"));

        Team team = teamMember.getTeam();
        if (!project.getTeams().contains(team)) {
            throw new EntityNotFoundException("Mentor from another project");
        }
        return teamMember;
    }

    private List<TeamMember> validateInterns(List<Long> internsId) {
        return internsId.stream()
                .map(id -> teamMemberRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("TeamMembers id " + id + " not found")))
                .peek(member -> member.getRoles().add(TeamRole.INTERN))
                .toList();
    }

    private Schedule validateSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    }
}
