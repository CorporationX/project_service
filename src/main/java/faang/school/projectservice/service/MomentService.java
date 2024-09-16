package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.filter.moment.MomentFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class MomentService {
    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final MomentMapper mapper;
    private final List<MomentFilter> momentFilters;


    public MomentDto create(MomentDto momentDto) {
        List<Project> projects = projectRepository.findAllByIds(momentDto.projectIds());
        for (Project project : projects) {
            if (project.getStatus() == ProjectStatus.COMPLETED) {
                throw new DataValidationException("Момент можно создать только для незакрытого проекта");
            }
        }
        Moment moment = momentRepository.save(mapper.toEntity(momentDto));
        return mapper.toDto(moment);
    }

    public MomentDto update(MomentDto momentDto) {
        Optional<Moment> momentOpt = momentRepository.findById(momentDto.id());
        if (momentOpt.isEmpty()) {
            throw new DataValidationException("Переданного момента не существует в бд");
        }

        Moment moment = mapper.toEntity(momentDto);
        Moment momentOld = momentOpt.get();
        List<Long> newProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .filter(projectId -> momentOld.getProjects().stream()
                        .map(Project::getId)
                        .toList()
                        .contains(projectId)
                )
                .toList();
        List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);
        List<Long> teamMemberIds = null;
        if (!newProjects.isEmpty()) {
             teamMemberIds = newProjects.stream()
                    .flatMap(project -> project.getTeams().stream()
                            .flatMap(team -> team.getTeamMembers().stream()
                                    .map(TeamMember::getId)
                            )
                    ).toList();
        }

        List<Long> newUserIds = moment.getUserIds().stream()
                .filter(userId -> momentOld.getUserIds().contains(userId))
                .toList();
        List<TeamMember> newTeamMembers = teamMemberRepository.findAllById(newUserIds);
        List<Project> projects = null;
        if (!newTeamMembers.isEmpty()) {
            projects = newTeamMembers.stream()
                    .map(teamMember -> teamMember.getTeam().getProject())
                    .toList();
        }

        if (teamMemberIds != null) {
            moment.getUserIds().addAll(teamMemberIds);
        }

        if (projects != null) {
            List<Project> projectsToAdd = Stream.concat(moment.getProjects().stream(), projects.stream()).toList();
            moment.setProjects(projectsToAdd);
        }
        return mapper.toDto(momentRepository.save(moment));
    }

    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        List<Moment> moments = momentRepository.findAll();
        System.out.println(moments);
        for (MomentFilter filter : momentFilters) {
            if (filter.isApplicable(filterDto)) {
                moments = filter.apply(filterDto, moments);
            }
        }
        return mapper.toDto(moments);
    }

    public List<MomentDto> getAllMoments() {
        return mapper.toDto(momentRepository.findAll());
    }

    public MomentDto getMoment(long id) {
        return mapper.toDto(momentRepository.findById(id).orElse(null));
    }
}
