package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> filters;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.validateToCreate(momentDto);

        Moment moment = momentMapper.toEntity(momentDto);
        List<TeamMember> teamMembers = getTeamMembersFromProjects(moment.getProjects());
        moment.setTeamMembers(teamMembers);

        log.info("Moment (id = {}) successfully created and saved to database", moment.getId());
        return momentMapper.toDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto update(Long id, MomentDto momentDto) {
        momentValidator.validateToUpdate(momentDto);

        Moment moment = getMoment(id);

        updateMoment(moment, momentDto);

        Set<Project> projects = new HashSet<>(moment.getProjects());
        Set<TeamMember> teamMembers = new HashSet<>(moment.getTeamMembers());

        updateProjects(moment, momentDto, teamMembers, projects);
        updateTeamMembers(moment, momentDto, teamMembers, projects);

        moment.setProjects(new ArrayList<>(projects));
        moment.setTeamMembers(new ArrayList<>(teamMembers));

        log.info("Moment (id = {}) successfully updated and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllWithFilter(MomentFilterDto filterDto) {
        List<Moment> moments = momentRepository.findAll();
        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(moments, filterDto));

        return moments.stream()
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAll() {
        return momentRepository.findAll().stream()
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MomentDto getById(Long id) {
        return momentMapper.toDto(getMoment(id));
    }

    private void updateMoment(Moment moment, MomentDto momentDto) {
        moment.setName(momentDto.getName());
        moment.setDate(momentDto.getDate());
        if (Objects.nonNull(momentDto.getDescription())) {
            moment.setDescription(momentDto.getDescription());
        }
    }

    private void updateProjects(Moment moment, MomentDto momentDto,
                                Set<TeamMember> teamMembers, Set<Project> projects) {
        if (hasOtherProjects(moment, momentDto.getProjectIds())) {
            List<Project> oldProjects = moment.getProjects();
            List<Project> newProjects = projectRepository.findAllByIds(momentDto.getProjectIds());

            List<TeamMember> oldTeamMembers = moment.getTeamMembers();
            List<TeamMember> newTeamMembers = getTeamMembersFromProjects(newProjects);

            oldProjects.forEach(projects::remove);
            oldTeamMembers.forEach(teamMembers::remove);

            projects.addAll(newProjects);
            teamMembers.addAll(newTeamMembers);
        }
    }

    private void updateTeamMembers(Moment moment, MomentDto momentDto,
                                   Set<TeamMember> teamMembers, Set<Project> projects) {
        if (hasOtherTeamMembers(moment, momentDto.getTeamMemberIds())) {
            List<TeamMember> oldTeamMembers = moment.getTeamMembers().stream()
                        .filter(teamMember -> !teamMembers.contains(teamMember))
                        .toList();
            List<TeamMember> newTeemMembers = teamMemberJpaRepository.findAllById(momentDto.getTeamMemberIds());

            List<Project> oldProjects = moment.getProjects().stream()
                    .filter(project -> !projects.contains(project))
                    .toList();
            List<Project> newProjects = getProjectsFromTeamMembers(newTeemMembers);

            oldProjects.forEach(projects::remove);
            oldTeamMembers.forEach(teamMembers::remove);

            projects.addAll(newProjects);
            teamMembers.addAll(newTeemMembers);
        }
    }

    private boolean hasOtherTeamMembers(Moment moment, List<Long> teamMemberIds) {
        return Objects.nonNull(teamMemberIds)
                && !moment.getTeamMembers().stream()
                .map(TeamMember::getId)
                .toList().equals(teamMemberIds);
    }

    private boolean hasOtherProjects(Moment moment, List<Long> projectIds) {
        return Objects.nonNull(projectIds)
                && !moment.getProjects().stream()
                .map(Project::getId)
                .toList().equals(projectIds);
    }

    private List<Project> getProjectsFromTeamMembers(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(teamMember -> teamMember.getTeam().getProject())
                .distinct()
                .toList();
    }

    private List<TeamMember> getTeamMembersFromProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .toList();
    }

    private Moment getMoment(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id = " + id + " does not exist"));
    }
}
