package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundElementInDataBase;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
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
    public MomentDto getMomentById(Long momentId) {
        String messageError = "Not found momentId id: " + momentId;
        if (momentId != null || momentId < 0) {
            log.error(messageError);
            throw new NotFoundElementInDataBase(messageError);
        }
        var moment = momentRepository.findById(momentId)
                .orElseThrow(() ->
                        new NotFoundElementInDataBase(messageError));
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getListMomentForFilter(Long projectId, MomentFilterDto filters) {
        validateProjectId(projectId);
        var listAllMoment = momentRepository.findAllByProjectId(projectId);
        return momentFilters.stream()
                .filter(moment -> moment.isApplicable(filters))
                .flatMap(moment -> moment.apply(listAllMoment, filters))
                .map(moment -> momentMapper.toDto(moment))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMomentProject(Long projectId) {
        validateProjectId(projectId);
        var momentsForProject = projectRepository.getProjectById(projectId).getMoments();
        return momentsForProject.stream().map(moment -> momentMapper.toDto(moment)).toList();
    }

    @Transactional
    public MomentDto createMoment(Long projectId, MomentDto momentDto) {
        validateProjectId(projectId);
        var projectsList = List.of(projectRepository.getProjectById(projectId));
        var moment = momentMapper.toEntity(momentDto);
        moment.setProjects(projectsList);
        moment.setDate(LocalDateTime.now());
        var returnMoment = momentRepository.save(moment);
        return momentMapper.toDto(returnMoment);
    }

    @Transactional
    public MomentDto updateMoment(Long projectId, MomentDto momentDto) {
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
        if (projectId == null || projectId < 0) {
            String errorMessage = "projectId not be null or negative in MomentService class id: " + projectId;
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
        if (projectRepository.existsById(projectId)) {
            String errorMessage = "Not found projectId in DataBase for id in MomentService class id: " + projectId;
            log.error(errorMessage);
            throw new NotFoundElementInDataBase(errorMessage);
        }
    }
}
