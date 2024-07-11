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
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentServiceValidator momentServiceValidator;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void createMoment(MomentDto momentDto) {
        momentServiceValidator.validateCreateMoment(momentDto);
        List<Project> projects = momentDto.getProjectsIDs().stream()
                .map(projectRepository::getProjectById).toList();

        addMembersIDsToTheMoment(momentDto, projects);
        momentRepository.save(momentMapper.toEntity(momentDto));
    }

    public void updateMoment(MomentDto momentDto) {
        Moment momentFromTheDatabase = momentRepository.findById(momentDto.getId())
                .orElseThrow(() -> new DataValidationException("Moment not found"));

        checkAndAddProjectsByNewMember(momentFromTheDatabase, momentDto);
        checkAndAddMembersByNewProjects(momentFromTheDatabase, momentDto);

        momentRepository.save(momentMapper.toEntity(momentDto));
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

    private void addMembersIDsToTheMoment(MomentDto momentDto, List<Project> projects) {
        List<Long> members = momentDto.getUserIDs();

        List<Long> tempMembers = new ArrayList<>();
        projects.stream()
                .forEach(project -> project.getTeams()
                        .forEach(team -> team.getTeamMembers()
                                .forEach(teamMember -> {
                                    if (!members.contains(teamMember.getId())) {
                                        tempMembers.add(teamMember.getId());
                                    }
                                })));
        members.addAll(tempMembers);
    }

    private void checkAndAddProjectsByNewMember(Moment momentFromTheDatabase, MomentDto momentDto) {
        List<Long> differentElements = new ArrayList<>(momentDto.getUserIDs());
        differentElements.removeAll(momentFromTheDatabase.getUserIds());

        if (!differentElements.isEmpty()) {
            differentElements.forEach(userId -> {
                List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
                teamMembers.forEach(teamMember -> momentDto.getProjectsIDs()
                        .add(teamMember
                                .getTeam()
                                .getProject()
                                .getId()));
            });
        }
    }

    private void checkAndAddMembersByNewProjects(Moment momentFromTheDatabase, MomentDto momentDto) {
        List<Long> differentProjectIds = new ArrayList<>(momentDto.getProjectsIDs());
        differentProjectIds.removeAll(momentFromTheDatabase.getProjects().stream()
                .map(Project::getId).toList());

        if (!differentProjectIds.isEmpty()) {
            addMembersIDsToTheMoment(momentDto, differentProjectIds.stream()
                    .map(projectRepository::getProjectById).toList());
        }
    }
}

