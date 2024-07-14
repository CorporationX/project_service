package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;


    // Создание этапа.
    public StageDto createStage(StageDto stageDto) {
        if (!stageDto.getProject().getStatus().equals(ProjectStatus.IN_PROGRESS)) {
            throw new DataValidationException("Проект закрыт или отменён!");
        }
        return stageMapper.stageToDto(stageRepository
                .save(stageMapper.stageDtoToEntity(stageDto)));
    }

    // Получить все этапы проекта с фильтром по статусу (в работе, выполнено и т.д).
    public List<StageDto> getAllStagesFilteredByProjectStatus(Long projectId,
                                                              ProjectFilterDto filter) {
        Project projectOptional = Optional.ofNullable(projectRepository.getProjectById(projectId))
                .orElseThrow(() ->
                        new DataValidationException("Такого проекта не существует!"));
        if (validateProjectStatus(projectOptional, filter)) {
            return projectOptional.getStages()
                    .stream()
                    .map(stageMapper::stageToDto)
                    .toList();
        } else {
            throw new DataValidationException("У данного проекта другой статус!");
        }
    }

    // Удалить этап.
    public void deleteStage(Long id) {
        stageRepository.delete(stageRepository.getById(id));
    }

    // Обновить этап.
    public void updateStage(StageDto stageDto) {
        stageRepository.isExistById(stageDto.getStageId());
        Stage oldStage = stageRepository.getById(stageDto.getStageId());
//        stageMapper.stageToDto();
//                stageRepository.e
    }

    // Получить все этапы проекта.
    public List<StageDto> getStagesOfProject(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return stageRepository.findAll()
                .stream()
                .filter(stage -> stage.getProject()
                        .equals(project))
                .map(stageMapper::stageToDto)
                .toList();
    }

    // Получить конкретный этап по Id.
    public StageDto getStageById(long stageId) {
        return stageMapper.stageToDto(stageRepository.getById(stageId));
    }

    public boolean validateProjectStatus(Project project, ProjectFilterDto filter) {
        return project.getStatus().toString().equals(filter.getProjectStatus());
    }
}
