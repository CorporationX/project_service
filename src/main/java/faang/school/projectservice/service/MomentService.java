package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamRepository teamRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);

        Moment moment = momentMapper.toEntity(momentDto);
        List<TeamMember> teamMembers = getTeamMembersFromProjects(moment.getProjects());
        moment.setTeamMembers(teamMembers);

        log.info("Moment (id = {}) successfully created and saved to database", moment.getId());
        return momentMapper.toDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto update(Long id, MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id = " + id + " does not exist"));

        moment.setName(momentDto.getName());
        moment.setDate(momentDto.getDate());

        if (Objects.nonNull(momentDto.getDescription())) {
            moment.setDescription(momentDto.getDescription());
        }

        if (hasDifferentTeamMembers(moment, momentDto.getTeamMemberIds())) {
            updateTeamMembers(moment, momentDto.getTeamMemberIds());
        }

        if (hasDifferentProjects(moment, momentDto.getProjectIds())) {
            updateProjects(moment, momentDto.getProjectIds());
        }

        log.info("Moment (id = {}) successfully updated and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    private void updateTeamMembers(Moment moment, List<Long> teamMemberIds) {
        List<TeamMember> newTeamMembers = teamMemberIds.stream()
                .map(teamMemberJpaRepository::getTeamMemberById)
                .toList();

        moment.getTeamMembers().clear();
        moment.getProjects().clear();

        moment.getTeamMembers().addAll(newTeamMembers);
        moment.getProjects().addAll(getProjectsFromTeamMembers(newTeamMembers));
    }

    private void updateProjects(Moment moment, List<Long> projectIds) {
        List<Project> newProjects = projectIds.stream()
                .map(projectRepository::getProjectById)
                .toList();

        moment.getProjects().clear();
        moment.getTeamMembers().clear();

        moment.getProjects().addAll(newProjects);
        moment.getTeamMembers().addAll(getTeamMembersFromProjects(newProjects));
    }

    private boolean hasDifferentTeamMembers(Moment moment, List<Long> teamMemberIds) {
        return Objects.nonNull(teamMemberIds)
                && !teamMemberIds.isEmpty()
                && !moment.getTeamMembers().stream()
                .map(TeamMember::getId)
                .toList().equals(teamMemberIds);
    }

    private boolean hasDifferentProjects(Moment moment, List<Long> projectIds) {
        return !moment.getProjects().stream()
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
                .flatMap(project -> teamRepository.findTeamsByProjectId(project.getId()).stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .toList();
    }
}
