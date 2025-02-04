package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.MomentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;
    private final MomentServiceValidator momentServiceValidator;
    private final ProjectRepository projectRepository;

    @Override
    public MomentResponseDto createMoment(MomentCreateRequestDto momentCreateRequestDto) {
        momentServiceValidator.validateMomentProjectIds(momentCreateRequestDto.projectIds());
        Moment momentSaved = momentRepository.save(momentMapper.toMomentEntity(momentCreateRequestDto));
        MomentResponseDto createdMomentResponseDto = momentMapper.toMomentResponseDto(momentSaved);
        log.info("Created moment {}", createdMomentResponseDto);
        return createdMomentResponseDto;
    }

    @Override
    public MomentResponseDto updateMoment(Long momentId, MomentUpdateRequestDto momentUpdateRequestDto) {
        Moment initialMoment = findMoment(momentId);
        Moment updatedMoment = updateMomentData(initialMoment, momentUpdateRequestDto);
        Moment momentSaved = momentRepository.save(updatedMoment);
        MomentResponseDto momentSavedDto = momentMapper.toMomentResponseDto(momentSaved);
        log.info("Updated moment {}", momentSavedDto);
        return momentSavedDto;
    }

    @Override
    public List<MomentResponseDto> getMoments(MomentFilterDto filter) {
        List<Moment> moments = momentRepository.findAll();
        return getFilteredMoments(moments.stream(), filter);
    }

    @Override
    public List<MomentResponseDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toMomentResponseDtos(moments);
    }

    @Override
    public MomentResponseDto getMoment(Long momentId) {
        return momentMapper.toMomentResponseDto(findMoment(momentId));
    }

    private Moment findMoment(Long momentId) {
        Optional<Moment> optionalMoment = momentRepository.findById(momentId);
        return optionalMoment
                .orElseThrow(() -> new EntityNotFoundException("Moment with id = " + momentId + " is not found"));
    }

    private List<Long> getProjectsTeamMemberIds(List<Project> projects) {
        return projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.IN_PROGRESS)
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .sorted()
                .toList();
    }

    private Moment updateMomentData(Moment initialMoment, MomentUpdateRequestDto updatedMomentDto) {
        List<Long> allTeamMembersIds = getUpdatedMomentTeamMembersIds(initialMoment,
                updatedMomentDto.teamMemberToAddIds());
        List<Project> allProjects = getUpdatedMomentProjects(initialMoment,
                updatedMomentDto.projectToAddIds());
        return Moment.builder()
                .userIds(allTeamMembersIds)
                .projects(allProjects)
                .name(updatedMomentDto.name())
                .description(updatedMomentDto.description())
                .build();
    }

    private List<Long> getUpdatedMomentTeamMembersIds(Moment initialMoment,
                                                      List<Long> addedTeamMembersIds) {
        List<Project> projects = initialMoment.getProjects();
        List<Long> initialAllProjectTeamMembersIds
                = getProjectsTeamMemberIds(projects);
        List<Long> addedProjectTeamMembersIds
                = getProjectsTeamMemberIds(projects);
        List<Long> initialTeamMembersIds = initialMoment.getUserIds();
        List<Long> resultTeamMemberIds = new ArrayList<>() {{
            addAll(initialTeamMembersIds);
            addAll(initialAllProjectTeamMembersIds);
            addAll(addedProjectTeamMembersIds);
            addAll(addedTeamMembersIds);
        }};

        return resultTeamMemberIds.stream()
                .distinct()
                .sorted()
                .toList();
    }

    private List<Project> getUpdatedMomentProjects(Moment initialMoment,
                                                List<Long> addedProjectIds) {
        List<Project> initialAllProjects = initialMoment.getProjects();
        List<Project> addedProjects = getProjectsByIds(addedProjectIds);
        List<Project> resultProjects = new ArrayList<>() {{
            addAll(initialAllProjects);
            addAll(addedProjects);
        }};
        return resultProjects.stream()
                .distinct()
                .sorted()
                .toList();
    }

    private List<MomentResponseDto> getFilteredMoments(Stream<Moment> moments, MomentFilterDto momentFilterDto) {

        LocalDateTime dateFrom = Optional.ofNullable(momentFilterDto.dateFrom()).orElse(LocalDateTime.MIN);
        LocalDateTime dateTo = Optional.ofNullable(momentFilterDto.dateTo()).orElse(LocalDateTime.MAX);
        if (dateFrom.isAfter(dateTo) && !dateFrom.isEqual(dateTo)) {
            throw new IllegalArgumentException("Dates dateFrom and dateTo are inconsistent!");
        }
        for (MomentFilter momentFilter : momentFilters) {
            if (momentFilter.isApplicable(momentFilterDto)) {
                moments = momentFilter.apply(moments, momentFilterDto);
            }
        }
        return moments
                .map(momentMapper::toMomentResponseDto)
                .toList();
    }

    private List<Project> getProjectsByIds (List<Long> projectIds) {
        return projectRepository.findAllById(projectIds);
    }
}