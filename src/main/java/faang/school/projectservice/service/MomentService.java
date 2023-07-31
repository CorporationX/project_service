package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentDtoUpdate;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.moments.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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
    public Moment createMoment(MomentDto momentDto) {
        Project projectFromDto = projectRepository.getProjectById(momentDto.getIdProject());
        if (projectFromDto.getStatus().equals(ProjectStatus.CREATED)
                || projectFromDto.getStatus().equals(ProjectStatus.IN_PROGRESS)
                || projectFromDto.getStatus().equals(ProjectStatus.ON_HOLD)) {
            return momentRepository.save(momentMapper.dtoToMoment(momentDto));
        } else {
            throw new NotFoundException(String.format("Project with id %d does not exist", momentDto.getIdProject()));
        }
    }

    @Transactional
    public void updateMoment(MomentDtoUpdate momentDtoUpdate) {
        Moment deprecatedMoment = momentRepository.findById(momentDtoUpdate.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("moment with %d wasn't found", momentDtoUpdate.getId())));
        Moment updatedMoment = momentMapper.updateMomentFromUpdatedDto(momentDtoUpdate, deprecatedMoment);
        momentRepository.save(updatedMoment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto, Long idProject) {
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

    public List<MomentDto> getAllMoments() {
        return momentMapper.listMomentToDto(momentRepository.findAll());
    }

    public MomentDto getMoment(long momentId) {
        return momentMapper.momentToDto(momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("moment with %d wasn't found", momentId))));
    }
}