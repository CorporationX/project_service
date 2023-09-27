package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.moment.MomentRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;
    private final TeamMemberService teamMemberService;
    @Lazy
    private final ProjectService projectService;

    @Transactional
    public MomentDto createMoment(MomentDto momentDto) {
        momentValidator.isProjectClosed(momentDto.getProjectIds());
        momentDto.setDate(LocalDateTime.now());

        Moment moment = momentMapper.toMoment(momentDto);
        Set<TeamMember> members = getMembersByProjects(projectService.getProjectsByIds(momentDto.getProjectIds()));
        moment.setMembers(List.copyOf(members));
        momentRepository.save(moment);

        return momentMapper.toMomentDto(moment);
    }

    public MomentDto getMomentById(Long momentId) {
        return momentMapper.toMomentDto(findMomentById(momentId));
    }

    private Moment findMomentById(Long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new DataValidationException("Moment by id: " + momentId + " not found"));
    }

    public Page<MomentDto> getAllMoments(Pageable pageable) {
        return momentRepository.findAll(pageable)
                .map(momentMapper::toMomentDto);
    }

    public Page<MomentDto> getAllMoments(MomentFilterDto filter, Pageable pageable) {
        return momentRepository.findByProjectsAndDate(filter.getProjectIds(), filter.getDateFrom(), filter.getDateTo(), pageable).map(momentMapper::toMomentDto);
    }

    @Transactional
    public MomentDto updateMoment(Long momentId, MomentDto updatedMomentDto) {
        Moment oldMoment = findMomentById(momentId);

        if (updatedMomentDto.getName() != null && !updatedMomentDto.getName().isBlank()) {
            oldMoment.setName(updatedMomentDto.getName());
        }

        if (updatedMomentDto.getDescription() != null && !updatedMomentDto.getDescription().isBlank()) {
            oldMoment.setDescription(updatedMomentDto.getDescription());
        }

        Set<TeamMember> newMembers = getNewMembers(oldMoment, updatedMomentDto);
        List<Project> newMembersProjects = getProjectsByMembers(newMembers);
        Set<Project> newProjects = getNewProjects(oldMoment, updatedMomentDto);

        if (!newProjects.isEmpty() || !newMembers.isEmpty()) {
            List<TeamMember> newMembersAndNewProjectsMembers = Stream.concat(
                            newMembers.stream(),
                            getMembersByProjects(List.copyOf(newProjects)).stream())
                    .distinct()
                    .toList();
            oldMoment.getMembers().addAll(newMembersAndNewProjectsMembers);
        }

        if (!newMembersProjects.isEmpty() || !newProjects.isEmpty()) {
            List<Project> newMembersProjectsAndNewProjects = Stream.concat(
                            newMembersProjects.stream(),
                            newProjects.stream())
                    .distinct()
                    .toList();
            oldMoment.getProjects().addAll(newMembersProjectsAndNewProjects);
        }

        return momentMapper.toMomentDto(oldMoment);
    }

    private Set<TeamMember> getNewMembers(Moment oldMoment, MomentDto updatedMomentDto) {
        Set<TeamMember> newMembers = new HashSet<>();
        if (updatedMomentDto.getMemberIds() != null && !updatedMomentDto.getMemberIds().isEmpty()) {
            newMembers.addAll(teamMemberService.getTeamMembersByIds(updatedMomentDto.getMemberIds()));
            oldMoment.getMembers().forEach(newMembers::remove);
        }
        return newMembers;
    }

    private Set<Project> getNewProjects(Moment oldMoment, MomentDto updatedMomentDto) {
        Set<Project> newProjects = new HashSet<>();
        if (updatedMomentDto.getProjectIds() != null && !updatedMomentDto.getProjectIds().isEmpty()) {
            newProjects.addAll(projectService.getProjectsByIds(updatedMomentDto.getProjectIds()));
            oldMoment.getProjects().forEach(newProjects::remove);
        }
        return newProjects;
    }

    private Set<TeamMember> getMembersByProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .collect(Collectors.toSet());
    }

    private List<Project> getProjectsByMembers(Set<TeamMember> members) {
        return members.stream()
                .map(TeamMember::getTeam)
                .map(Team::getProject)
                .toList();
    }
}
