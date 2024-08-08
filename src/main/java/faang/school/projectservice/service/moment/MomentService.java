package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationExceptions;
import faang.school.projectservice.exception.NotFoundEntityException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public MomentDto getMomentById(long momentId) {
        var moment = momentRepository.findById(momentId)
                .orElseThrow(() ->
                        new NotFoundEntityException("Not found moment for id: " + momentId));
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getListMomentForFilter(long projectId, MomentFilterDto filters) {
        var listAllMoment = momentRepository.findAllByProjectId(projectId);
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(listAllMoment, filters))
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMomentProject(long projectId) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);
        return momentMapper.toListDto(moments);
    }

    @Transactional
    public MomentDto createMoment(long projectId, MomentDto momentDto) {
        var projectDto = projectService.getProjectDtoById(projectId);
        if (projectDto.getStatus() == ProjectStatus.CANCELLED) {
            String errorMessage = "Сan't create moment for a closed project in MomentService class id: " + projectId;
            log.info(errorMessage);
            throw new DataValidationExceptions(errorMessage);
        }
        List<ProjectDto> projectIdList = List.of(projectDto);
        momentDto.setProjectsId(projectIdList.stream()
                .map(ProjectDto::getId)
                .toList()
        );
        momentDto.setDate(LocalDateTime.now());
        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    @Transactional
    public MomentDto updateMoment(long projectId, MomentDto momentDto) {
        ProjectDto projectDto = projectService.getProjectDtoById(projectId);
        momentDto.getProjectsId().add(projectDto.getId());
        var moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }
}
