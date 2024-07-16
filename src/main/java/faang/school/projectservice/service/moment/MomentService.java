package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exceptions.NotFoundElementInDataBaseException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import jakarta.validation.constraints.NotNull;
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
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional(readOnly = true)
    public MomentDto getMomentById(@NotNull Long momentId) {
        var moment = momentRepository.findById(momentId)
                .orElseThrow(() ->
                        new NotFoundElementInDataBaseException("Not found moment for id: " + momentId));
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getListMomentForFilter(Long projectId, @NotNull MomentFilterDto filters) {
        validateProjectId(projectId);
        var listAllMoment = momentRepository.findAllByProjectId(projectId);
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(listAllMoment, filters))
                .map(moment -> momentMapper.toDto(moment))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMomentProject(@NotNull Long projectId) {
        validateProjectId(projectId);
        var momentsForProject = projectRepository.getProjectById(projectId).getMoments();
        return momentsForProject.stream().map(moment -> momentMapper.toDto(moment)).toList();
    }

    @Transactional
    public MomentDto createMoment(Long projectId, @NotNull MomentDto momentDto) {
        validateProjectId(projectId);
        var projectsList = List.of(projectRepository.getProjectById(projectId));
        var moment = momentMapper.toEntity(momentDto);
        moment.setProjects(projectsList);
        moment.setDate(LocalDateTime.now());
        var returnMoment = momentRepository.save(moment);
        return momentMapper.toDto(returnMoment);
    }

    @Transactional
    public MomentDto updateMoment(@NotNull Long projectId, @NotNull MomentDto momentDto) {
        validateProjectId(projectId);
        var project = projectRepository.getProjectById(projectId);
        var moment = momentMapper.toEntity(momentDto);
        moment.getProjects().add(project);
        moment.setUpdatedAt(LocalDateTime.now());
        var returnMoment = momentRepository.save(moment);
        return momentMapper.toDto(returnMoment);
    }

    @Transactional(readOnly = true)
    private void validateProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            String errorMessage = "Not found projectId in DataBase for id in MomentService class id: " + projectId;
            log.error(errorMessage);
            throw new NotFoundElementInDataBaseException(errorMessage);
        }
    }
}
