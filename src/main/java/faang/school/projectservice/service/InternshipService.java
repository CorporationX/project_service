package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
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

    public InternshipDto getInternshipById(Long internshipId) {
        return internshipRepository.findById(internshipId)
                .map(internshipMapper::toInternshipDto)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found for search by id"));
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
                -> new EntityNotFoundException("Internship not found for update"));
        if (internship.getStartDate().isAfter(LocalDateTime.now())) {
            addNewInterns(internship, internshipDto.getInternsId());
        }
        if (internshipDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            completeInternship(internship);
        }
        if(internshipDto.getStatus().equals(InternshipStatus.IN_PROGRESS)){
            log.info("The internship is still ongoing");
            aheadOfSchedule(internship,internshipDto);
        }
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto createInternship(InternshipDto internshipDto) {

        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new InternshipGetInternsIdException("The list of interns is empty");
        }

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate()
                .plusMonths(INTERNSHIP_DURATION_THREE_MONTHS))) {
            throw new IllegalArgumentException("The internship cannot last more than 3 months.");
        }

        Project project = projectRepository.findById(internshipDto.getProjectId()).orElseThrow(()
                -> new EntityNotFoundException("Project not found"));

        List<Team> teams = project.getTeams();

        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId()).orElseThrow(()
                -> new EntityNotFoundException("No mentor"));

        Team team = teamMember.getTeam();
        if (!teams.contains(team)) {
            throw new EntityNotFoundException("Mentor from another project");
        }
        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setProject(project);
        internship.setMentorId(teamMember);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map((id) -> teamMemberRepository.findById(id).orElseThrow(()
                        -> new EntityNotFoundException("TeamMembers id " + id + " not found")))
                .peek(member -> member.getRoles().add(TeamRole.INTERN))
                .toList();
        internship.setInterns(teamMembers);
        internship.setCreatedAt(LocalDateTime.now());
        internship.setEndDate(internship.getStartDate().plusMonths(INTERNSHIP_DURATION_THREE_MONTHS));

        log.info("Internship successfully created");
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
        internship.setInterns(new ArrayList<>());
        log.info("Internship Completed. Completed Interns: {}", completedInterns);
        log.info("Remote Interns: {}", notCompletedInterns);
    }

    private void addNewInterns(Internship internship, List<Long> internsId) {
        List<TeamMember> interns = internshipRepository.findByInternshipIdIn(internsId);
        if (!interns.isEmpty()) {
            internship.setInterns((interns));
        }
    }

    private boolean checkAllTasksCompleted(TeamMember intern) {
        List<Stage> stages = intern.getStages();
        if (stages.isEmpty()) {
            log.warn("Stage list is empty for TeamMember: {}", intern.getId());
            return false;
        }
        return stages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
    }

    private void aheadOfSchedule(Internship internship, InternshipDto internshipDto) {
        List<TeamMember> oldList = internship.getInterns();
        List<TeamMember> newList = internshipRepository.findByInternshipIdIn(internshipDto.getInternsId());
        oldList.removeAll(newList);
        if (!oldList.isEmpty()) {
            log.info("There are no people who passed the test early or were dismissed early.");
        }
        for (TeamMember intern : oldList) {
            if(checkAllTasksCompleted(intern)){
               intern.getRoles().add(TeamRole.DEVELOPER);
                intern.getRoles().remove(TeamRole.INTERN);
                log.info("Completed Interns: {}", intern);
            }else {
                log.info("Dismissed interns: {}", intern);
            }
        }
        internship.setInterns(newList);
    }
}
