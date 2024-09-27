package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.moment.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentServiceImpl implements MomentService {
    private final MomentValidator momentValidator;
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Override
    public MomentDto createMoment(MomentDto momentDto) {
        momentValidator.validateMomentDto(momentDto);
        log.info("Creating moment with DTO = {}", momentDto);

        List<Project> projects = getProjects(momentDto);
        Moment moment = momentMapper.toMoment(momentDto);
        Moment saveMoment = saveMoment(moment, projects);

        log.info("Moment created with ID = {}", saveMoment.getId());
        return momentMapper.toMomentDto(saveMoment);
    }


    @Override
    public MomentDto updateMomentByProjects(MomentDto momentDto) {
        log.info("Updating moment with DTO = {}", momentDto);

        List<Project> projects = getProjects(momentDto);
        Moment updateMoment = getMoment(momentDto);
        Moment saveMoment = saveMoment(updateMoment, projects);

        log.info("Moment update with ID = {}", saveMoment.getId());
        return momentMapper.toMomentDto(saveMoment);
    }

    @Override
    public MomentDto updateMomentByUser(long userId, MomentDto momentDto) {
        log.info("Updating moment with DTO = {} and by user ID = {}", momentDto, userId);

        List<Project> projects = momentValidator.validateProjectsByUserIdAndStatus(userId);
        Moment updateMoment = getMoment(momentDto);
        Moment saveMoment = saveMoment(updateMoment, projects);

        log.info("Moment update with ID = {}", saveMoment.getId());
        return momentMapper.toMomentDto(saveMoment);
    }

    private Moment getMoment(MomentDto momentDto) {
        return momentValidator.validateExistingMoment(momentDto.Id());
    }

    private List<Project> getProjects(MomentDto momentDto) {
        return momentValidator.validateProjectsByIdAndStatus(momentDto);
    }

    private Moment saveMoment(Moment moment, List<Project> projects) {
        moment.setProjects(projects);
        moment.setUserIds(getUserIdsByProjects(projects));
        for (Project project : projects) {
            if (project.getMoments().isEmpty()) {
                project.setMoments(new ArrayList<>(Collections.singletonList(moment)));
            } else {
                project.getMoments().add(moment);
            }
        }
        return momentRepository.save(moment);
    }

    private List<Long> getUserIdsByProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();
    }

    @Override
    public List<MomentDto> getMomentsByFilters(long projectId, MomentFilterDto filterDto) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);
        return momentFilters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(moments.stream(), (stream, filter) -> filter
                        .apply(stream, filterDto), (s1, s2) -> s1)
                .map(momentMapper::toMomentDto)
                .toList();
    }

    @Override
    public List<MomentDto> getAllMoments() {
        return momentMapper.toMomentDto(momentRepository.findAll());
    }

    @Override
    public MomentDto getMomentById(long momentId) {
        return momentMapper.toMomentDto(momentValidator.validateExistingMoment(momentId));
    }
}