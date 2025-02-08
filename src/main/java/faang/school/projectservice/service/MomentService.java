package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final ProjectService projectService;
    private final UserContext userContext;
    private final MomentMapper momentMapper;

    public CreateMomentResponse createMoment(CreateMomentRequest createMomentRequest) {
        Long creatorId = userContext.getUserId();
        List<Project> projects = getProjects(createMomentRequest.projectIds());
        Moment moment = momentMapper.toEntity(createMomentRequest, projects, creatorId);
        moment = momentRepository.save(moment);
        return momentMapper.toCreateMomentResponse(moment);
    }

    @Transactional
    public UpdateMomentResponse updateMoment(long id, UpdateMomentRequest updateMomentRequest) {
        Moment moment = findMomentById(id);
        Set<Project> projects = new HashSet<>(moment.getProjects());
        Set<Long> userIds = new HashSet<>(moment.getUserIds());

        Set<Project> newProjects = updateMomentRequest.projectIds()
                .stream()
                .map(projectService::getActiveProjectById)
                .collect(Collectors.toSet());

        newProjects.stream()
                .filter(project -> project.getTeams() != null)
                .flatMap(project -> project.getTeams().stream())
                .filter(team -> team.getTeamMembers() != null)
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .forEach(userIds::add);
        projects.addAll(newProjects);

        Set<Long> newUserIds =  updateMomentRequest.userIds()
                .stream()
                .filter(userId -> !userIds.contains(userId))
                .collect(Collectors.toSet());

        newUserIds.stream()
                .map(projectService::findAllProjects)
                .flatMap(List::stream)
                .map(project -> projectService.getActiveProjectById(project.getId()))
                .forEach(projects::add);
        userIds.addAll(newUserIds);

        moment.setUserIds(userIds.stream().toList());
        moment.setProjects(projects.stream().toList());
        Long updatedBy = userContext.getUserId();
        momentMapper.updateMoment(moment, updateMomentRequest, updatedBy);
        return momentMapper.toUpdateMomentResponse(moment);
    }

    public List<GetMomentResponse> getMoments(MomentFilter momentFilter) {
        Specification<Moment> spec = momentFilter.toSpecification();
        List<Moment> moments = momentRepository.findAll(spec);
        return momentMapper.toGetMomentResponseList(moments);
    }

    public GetMomentResponse getMoment(long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Момент с айди %s не был найден".formatted(id)));
        return momentMapper.toGetMomentResponse(moment);
    }

    private Moment findMomentById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Момент с айди %s не был найден".formatted(id)));
    }

    private List<Project> getProjects(List<Long> projectIds) {
        return projectIds.stream()
                .map(projectService::getActiveProjectById)
                .toList();
    }
}
