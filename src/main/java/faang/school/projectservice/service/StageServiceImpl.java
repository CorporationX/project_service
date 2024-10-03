package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.validator.StageServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageJpaRepository stageRepository;
    private final ProjectJpaRepository projectRepository;
    private final StageMapper mapper;
    private final StageServiceValidator validator;
    private final List<StageFilter> filters;

    @Override
    public void create(StageDto stageDto) {
        validator.validateExecutorsStageRoles(stageDto);
        if (!projectRepository.existsById(stageDto.getStageId())) {
            throw new DataValidationException("project not exist");
        }
        stageRepository.save(mapper.toStage(stageDto));
    }

    @Override
    public List<StageDto> getAllStages(Long projectId) {
        return mapper.toStageDtoList(projectRepository.getReferenceById(projectId).getStages());
    }

    @Override
    public StageDto getStageById(Long id) {
        return mapper.toDto(stageRepository.getById(id));
    }

    @Override
    public void deleteStage(StageDto stageDto) {
        validator.validateExecutorsStageRoles(stageDto);
        stageRepository.deleteById(stageDto.getStageId());
    }

    @Override
    public List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto) {
        Project project = projectRepository.getReferenceById(projectId);

        Stream<Stage> stageStream = project.getStages().stream();
        return filters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filterDto))
                .flatMap(stageFilter -> stageFilter.apply(stageStream, filterDto))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void updateStage(StageDto stageDto) {
        if (!projectRepository.existsById(stageDto.getStageId())) {
            throw new DataValidationException("project not exist");
        }
        validator.validateExecutorsStageRoles(stageDto);
        sentInvitationToNeededExecutors(getNumberOfRolesInvolved(stageDto),
                stageDto);
    }

    public Map<TeamRole, Long> getNumberOfRolesInvolved(StageDto stageDto) {
        return stageDto.getExecutorsDtos().stream()
                .flatMap(teamMemberDto -> teamMemberDto.stageRoles().stream())
                .collect(Collectors.groupingBy(
                        role -> role,
                        Collectors.counting()
                ));
    }

    public void sentInvitationToNeededExecutors
            (Map<TeamRole, Long> mapRolesInvolved, StageDto stageDto) {
        for (StageRolesDto dto : stageDto.getStageRoles()) {
            if (dto.count() > mapRolesInvolved.get(dto.role())) {
                send(dto.count() - mapRolesInvolved.get(dto.role()));
            }
        }
    }

    private void send(Long countOfExecutors) {
        validator.validateCount(countOfExecutors);
        //отправка
    }
}
