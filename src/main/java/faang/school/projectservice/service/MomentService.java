package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
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
        MomentDto returnMomentDto = mapper.toDto(moment);
        log.info("Create moment with id = " + returnMomentDto.id());
        return returnMomentDto;
    }

    public MomentDto update(MomentDto momentDto) {
        Optional<Moment> momentOpt = momentRepository.findById(momentDto.id());
        if (momentOpt.isEmpty()) {
            throw new DataValidationException("Переданного момента не существует в бд");
        }

        Moment moment = mapper.toEntity(momentDto);
        Moment momentOld = momentOpt.get();
        List<Long> teamMemberIds = getNewTeamMemberIds(moment, momentOld);
        List<Project> projects = getNewProjects(moment, momentOld);

        if (teamMemberIds != null) {
            moment.getUserIds().addAll(teamMemberIds);
        }

        List<Long> projectIds = null;
        if (projects != null) {
            List<Project> projectsToAdd = Stream.concat(moment.getProjects().stream(), projects.stream()).toList();
            moment.setProjects(projectsToAdd);
            projectIds = projects.stream()
                    .map(Project::getId)
                    .toList();
        }
        MomentDto returnMomentDto = mapper.toDto(momentRepository.save(moment));
        log.info("Updated moment with id = " + returnMomentDto.id() +
                " add new team members with id: " + teamMemberIds +
                " add new project with id: " + projectIds);
        return returnMomentDto;
    }

    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        List<Moment> moments = momentRepository.findAll();
        List<MomentFilter> filters = momentFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
        Stream<Moment> streamMoments = moments.stream();
        for (MomentFilter filter : filters) {
            streamMoments = filter.apply(filterDto, streamMoments);
        }
        List<MomentDto> momentDtos = mapper.toDto(streamMoments.toList());
        List<Long> momentIds = momentDtos.stream()
                        .map(MomentDto::id)
                        .toList();
        log.info("Returning moments with id: " + momentIds);
        return momentDtos;
    }

    public List<MomentDto> getAllMoments() {
        List<MomentDto> momentDtos = mapper.toDto(momentRepository.findAll());
        List<Long> momentIds = momentDtos.stream()
                .map(MomentDto::id)
                .toList();
        log.info("Returning moments with id: " + momentIds);
        return momentDtos;
    }

    public MomentDto getMoment(long id) {
        MomentDto momentDto = mapper.toDto(momentRepository.findById(id).orElse(null));
        if (momentDto == null) {
            log.info("Returning empty moment");
        } else {
            log.info("Returning moment with id = " + momentDto.id());
        }
        return momentDto;
    }

    private List<Long> getNewTeamMemberIds(Moment moment, Moment momentOld) {
        List<Long> newProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .filter(projectId -> momentOld.getProjects().stream()
                        .map(Project::getId)
                        .toList()
                        .contains(projectId)
                )
                .toList();
        List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);
        if (newProjects.isEmpty()) {
            return null;
        }
        return newProjects.stream()
                    .flatMap(project -> project.getTeams().stream()
                            .flatMap(team -> team.getTeamMembers().stream()
                                    .map(TeamMember::getId)
                            )
                    ).toList();
    }

    private List<Project> getNewProjects(Moment moment, Moment momentOld) {
        List<Long> newUserIds = moment.getUserIds().stream()
                .filter(userId -> momentOld.getUserIds().contains(userId))
                .toList();
        List<TeamMember> newTeamMembers = teamMemberRepository.findAllById(newUserIds);
        if (newTeamMembers.isEmpty()) {
            return null;
        }

        return newTeamMembers.stream()
                    .map(teamMember -> teamMember.getTeam().getProject())
                    .toList();
    }
}
