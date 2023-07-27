package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final ProjectJpaRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Transactional
    public ResponseInternshipDto create(CreateInternshipDto dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project is not found"));
        TeamMember mentor = teamMemberRepository.findByIdAndProjectId(dto.getMentorId(), dto.getProjectId());
        List<TeamMember> interns = teamMemberRepository.findAllById(dto.getInternIds());
        interns.forEach(intern -> addRole(dto.getInternshipRole(), intern));

        validateCreateDto(dto, mentor);

        Internship internship = internshipMapper.createDtoToEntity(dto);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);
        internship.setCreatedAt(LocalDateTime.now());

        return internshipMapper.entityToResponseDto(internshipRepository.save(internship));
    }

    @Transactional
    public ResponseInternshipDto update(UpdateInternshipDto dto) {
        Internship internship = internshipRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("The internship not found"));
        Project project = internship.getProject();

        finishInternship(dto, internship, project);
        internship.setStatus(dto.getStatus());
        internship.setUpdatedAt(LocalDateTime.now());
        internship.setName(dto.getName());
        internship.setUpdatedBy(dto.getUpdatedBy());

        return internshipMapper.entityToResponseDto(internship);
    }

    @Transactional(readOnly = true)
    public List<ResponseInternshipDto> findByFilter(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internships =
                internshipRepository.findAllByProjectId(internshipFilterDto.getProjectId()).stream();

        for (InternshipFilter filter : internshipFilters) {
            if (filter.isApplicable(internshipFilterDto)) {
                internships = filter.apply(internships, internshipFilterDto);
            }
        }

        return internshipMapper.entityListToDtoList(internships.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseInternshipDto> findAll() {
        return internshipMapper.entityListToDtoList(internshipRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ResponseInternshipDto findById(Long id) {
        return internshipMapper.entityToResponseDto(
                internshipRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("The internship with id " + id + " is not found")));
    }

    private void addRole(TeamRole teamRole, TeamMember intern) {
        if (intern.getRoles() == null) {
            intern.setRoles(new ArrayList<>());
        }
        intern.getRoles().add(teamRole);
    }

    private void giveProjectRole(TeamMember intern) {
        List<TeamRole> roles = intern.getRoles();
        TeamRole role = roles.stream().filter(r ->
                        r.equals(TeamRole.INTERN_ANALYST) ||
                                r.equals(TeamRole.INTERN_DESIGNER) ||
                                r.equals(TeamRole.INTERN_DEVELOPER) ||
                                r.equals(TeamRole.INTERN_MANAGER) ||
                                r.equals(TeamRole.INTERN_TESTER)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team member with id " + intern.getId() + " doesn't has intern role"));

        switch (role) {
            case INTERN_ANALYST -> {
                roles.remove(TeamRole.INTERN_ANALYST);
                roles.add(TeamRole.ANALYST);
            }
            case INTERN_DESIGNER -> {
                roles.remove(TeamRole.INTERN_DESIGNER);
                roles.add(TeamRole.DESIGNER);
            }
            case INTERN_DEVELOPER -> {
                roles.remove(TeamRole.INTERN_DEVELOPER);
                roles.add(TeamRole.DEVELOPER);
            }
            case INTERN_MANAGER -> {
                roles.remove(TeamRole.INTERN_MANAGER);
                roles.add(TeamRole.MANAGER);
            }
            case INTERN_TESTER -> {
                roles.remove(TeamRole.INTERN_TESTER);
                roles.add(TeamRole.TESTER);
            }
        }
        intern.setRoles(roles);
    }

    private void finishInternship(UpdateInternshipDto dto, Internship internship, Project project) {
        if (dto.getStatus().equals(InternshipStatus.COMPLETED) && internship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            List<TeamMember> interns = internship.getInterns();
            for (TeamMember intern : interns) {
                List<TaskStatus> taskOfIntern =
                        taskRepository.findAllByProjectIdAndPerformerUserId(internship.getProject().getId(), intern.getId())
                                .stream()
                                .map(Task::getStatus)
                                .toList();
                if (taskOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
                    giveProjectRole(intern);
                } else {
                    project.getTeams().forEach(team -> team.getTeamMembers().remove(intern));
                }
            }
        }
    }

    private void validateCreateDto(CreateInternshipDto dto, TeamMember mentor) {
        if (Period.between(dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new IllegalArgumentException("The duration of the internship should not exceed 3 months");
        }
        if (mentor == null) {
            throw new IllegalArgumentException("The internship should contain mentor from project");
        }
    }
}
