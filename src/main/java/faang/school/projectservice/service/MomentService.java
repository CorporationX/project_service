package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentServiceValidator momentServiceValidator;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public MomentDto createMoment(MomentDto momentDto) {
        momentServiceValidator.validateCreateMoment(momentDto);
        List<Project> projects = momentDto.getProjectsIds().stream()
                .map(projectRepository::getProjectById)
                .toList();
        momentDto.setUserIds(getNewMemberIds(projects));


        momentRepository.save(momentMapper.toEntity(momentDto));
        return momentDto;
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment moment = momentRepository.findById(momentDto.getId())
                .orElseThrow(() -> new DataValidationException("Moment not found"));

        List<Project> differentProjects = findDifferentProjects(moment.getProjects(),
                new ArrayList<>(momentDto.getProjectsIds()));
        if (!differentProjects.isEmpty()) {
            moment.getUserIds().addAll(getNewMemberIds(differentProjects));
        }

        List<Long> differentMemberIds = findDifferentMemberIds(moment.getUserIds(),
                new ArrayList<>(momentDto.getUserIds()));
        if (!differentMemberIds.isEmpty()) {
            moment.getProjects().addAll(getNewProjects(differentMemberIds));
        }

        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    public List<MomentDto> getAllMoments(MomentFilterDto momentFilterDto) {
        List<Moment> moments = momentRepository.findAll();

        List<Moment> filteredMoments = momentFilters.stream()
                .filter(filter -> filter.isApplicable(momentFilterDto))
                .reduce(moments, (filtered, filter) ->
                        filter.apply(filtered.stream(), momentFilterDto).toList(), (a, b) -> b);

        return momentMapper.toDto(filteredMoments);
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.toDto(momentRepository.findAll());
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Moment not found"));


        return momentMapper.toDto(moment);
    }

    private List<Project> findDifferentProjects(List<Project> projectsFromDataBase, List<Long> newProjectIds) {
        List<Long> existingProjectIds = projectsFromDataBase.stream()
                .map(Project::getId)
                .toList();
        newProjectIds.removeAll(existingProjectIds);
        return convertProjectsByIds(newProjectIds);
    }

    private List<Long> getNewMemberIds(List<Project> projects) {
        Set<Long> memberIds = new HashSet<>();
        projects.forEach(project -> project.getTeams()
                .forEach(team -> team.getTeamMembers()
                        .forEach(member -> memberIds.add(member.getId()))));

        return new ArrayList<>(memberIds);
    }

    private List<Long> findDifferentMemberIds(List<Long> userIdsFromDataBase, List<Long> newUserIds) {
        newUserIds.removeAll(userIdsFromDataBase);
        return newUserIds;
    }

    private List<Project> getNewProjects(List<Long> userIds) {
        Set<Project> projects = new HashSet<>();
        userIds.forEach(userId -> {
            List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
            teamMembers.forEach(teamMember -> projects.add(teamMember.getTeam().getProject()));
        });
        return new ArrayList<>(projects);
    }

    private List<Project> convertProjectsByIds(List<Long> projectIds) {
        return projectIds.stream()
                .map(projectRepository::getProjectById)
                .toList();
    }
}

