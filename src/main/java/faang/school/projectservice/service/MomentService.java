package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.adapter.moment.MomentRepositoryAdapter;
import faang.school.projectservice.repository.adapter.project.ProjectRepositoryAdapter;
import faang.school.projectservice.repository.adapter.team.TeamRepositoryAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepositoryAdapter momentRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final TeamMemberRepoAdapter teamMemberRepoAdapter;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {

        Project mainProject = projectRepositoryAdapter.getProjectById(momentDto.getProjectId());

        List<Project> projects = new ArrayList<>();
        projects.add(mainProject);

        if (momentDto.getProjectIds() != null) {
            List<Project> partnerProjects = projectRepositoryAdapter
                    .getAllProjectsById(momentDto.getProjectIds());

            for (Project partnerProject : partnerProjects) {
                if (!partnerProject.getId().equals(mainProject.getId())) {
                    projects.add(partnerProject);
                }
            }
        }

        Moment moment = momentMapper.toEntity(momentDto);
        moment.setProjects(projects);

        return momentMapper.toDto(momentRepositoryAdapter.save(moment));
    }

    public MomentDto updateMoment(Long id, MomentDto momentDto) {
        Moment moment = momentRepositoryAdapter.getMomentById(id);

        Optional.ofNullable(momentDto.getName()).ifPresent(moment::setName);
        Optional.ofNullable(momentDto.getDescription()).ifPresent(moment::setDescription);
        Optional.ofNullable(momentDto.getDate()).ifPresent(moment::setDate);
        Optional.ofNullable(momentDto.getImageId()).ifPresent(moment::setImageId);

        updateMomentAssociations(moment, momentDto);

        return momentMapper.toDto(momentRepositoryAdapter.save(moment));
    }

    private void updateMomentAssociations(Moment moment, MomentDto momentDto) {
        Set<Long> projectIds = new LinkedHashSet<>();
        moment.getProjects().forEach(project -> projectIds.add(project.getId()));

        if (momentDto.getProjectIds() != null) {
            projectIds.addAll(momentDto.getProjectIds());
        }

        Set<Long> userIds = new LinkedHashSet<>();

        if (moment.getUserIds() != null) {
            userIds.addAll(moment.getUserIds());
        }
        if (momentDto.getUserIds() != null) {
            userIds.addAll(momentDto.getUserIds());
        }

        for (Long projectId : projectIds) {
            List<Team> teams = teamRepositoryAdapter.getTeamsByProjectId(projectId);
            teams.forEach(team -> team.getTeamMembers()
                    .forEach(tm -> userIds.add(tm.getUserId())));
        }

        for (Long userId : userIds) {
            List<TeamMember> teamMembers = teamMemberRepoAdapter.getAllTeamMembersByUserId(userId);
            teamMembers.forEach(tm -> projectIds.add(tm.getTeam().getProject().getId()));
        }

        List<Project> projects = projectRepositoryAdapter.getAllProjectsById(new ArrayList<>(projectIds));
        moment.setProjects(projects);
        moment.setUserIds(new ArrayList<>(userIds));
    }

    public List<MomentDto> getMomentsWithFilter(MomentDto momentDto) {
        Stream<Moment> filteredMoments = momentRepositoryAdapter.getAllMoments().stream();

        for (MomentFilter filter : momentFilters) {
            if (filter.isApplicable(momentDto)) {
                filteredMoments = filter.apply(filteredMoments, momentDto);
            }
        }

        return filteredMoments.map(momentMapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepositoryAdapter.getAllMoments();

        return moments.stream()
                .map(momentMapper::toDto).toList();
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepositoryAdapter.getMomentById(id);

        return momentMapper.toDto(moment);
    }
}
