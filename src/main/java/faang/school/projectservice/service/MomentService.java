package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentDtoUpdate;
import faang.school.projectservice.exceptions.InvalidCurrentUserException;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.moments.MomentMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilter;

    @Transactional
    public Moment createMoment(MomentDto momentDto, Long currentUserId) {
        Project projectFromDto = projectRepository.getProjectById(momentDto.getIdProject());
        validateCurrentUser(projectFromDto, currentUserId);
        if (projectFromDto.getStatus().equals(ProjectStatus.CREATED)
                || projectFromDto.getStatus().equals(ProjectStatus.IN_PROGRESS)
                || projectFromDto.getStatus().equals(ProjectStatus.ON_HOLD)) {
            return momentRepository.save(momentMapper.dtoToMoment(momentDto));
        } else {
            throw new NotFoundException(String.format("Project with id %d does not exist", momentDto.getIdProject()));
        }
    }

    @Transactional
    public void updateMoment(MomentDtoUpdate momentDtoUpdate, Long currentUserId) {
        Project projectFromDto = projectRepository.getProjectById(momentDtoUpdate.getIdProject());
        validateCurrentUser(projectFromDto, currentUserId);
        Moment deprecatedMoment = momentRepository.findById(momentDtoUpdate.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("moment with %d wasn't found", momentDtoUpdate.getId())));
        Moment updatedMoment = momentMapper.updateMomentFromUpdatedDto(momentDtoUpdate, deprecatedMoment);
        momentRepository.save(updatedMoment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto, Long idProject, Long currentUserId) {
        Project project = projectRepository.getProjectById(idProject);
        validateCurrentUser(project, currentUserId);
        Stream<Moment> allMoments = momentRepository.findAll().stream()
                .filter(moment -> moment.getProject().stream()
                            .map(Project::getId)
                            .anyMatch(id -> id.equals(idProject)));
        List<MomentFilter> requiredFilters = momentFilter.stream()
                .filter(filter -> filter.isApplicable(filterMomentDto))
                .toList();
        for (MomentFilter requiredFilter : requiredFilters) {
            allMoments = requiredFilter.apply(allMoments, filterMomentDto);
        }
        return allMoments.map(momentMapper::momentToDto)
                .peek(momentDto -> momentDto.setIdProject(idProject)).toList();
    }

    public List<MomentDtoUpdate> getAllMoments(Long currentUserId, Long idProject) {
        Project project = projectRepository.getProjectById(idProject);
        validateCurrentUser(project, currentUserId);
        return momentMapper.listMomentToUpdatedDto(momentRepository.findAll());
    }

    public MomentDtoUpdate getMoment(long momentId, Long currentUserId) {
        validateUserByMoment(momentId, currentUserId);
        return momentMapper.momentToDtoUpdated(momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("moment with %d wasn't found", momentId))));
    }

    private void validateCurrentUser(Project project, Long userId){
        if(project.getTeam().getTeamMembers().stream()
                .map(TeamMember::getUserId)
                .noneMatch(teamUserId -> teamUserId.equals(userId))){
            throw new InvalidCurrentUserException(ErrorMessages.INVALID_CURRENT_USER);
        }
    }

    private void validateUserByMoment(Long momentId, Long currentUserId){
        Moment currentMoment = validateMoment(momentId);
        List<Project> momentProjects = currentMoment.getProject().stream()
                .filter(project -> project.getTeam().getTeamMembers().stream()
                        .map(TeamMember::getUserId)
                        .anyMatch(teamUserId -> teamUserId.equals(currentUserId)))
                .toList();
        if(momentProjects.size() < 1){
            throw new InvalidCurrentUserException(ErrorMessages.INVALID_CURRENT_USER);
        }
    }

    private Moment validateMoment(Long momentId){
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NO_SUCH_MOMENTS));
    }
}