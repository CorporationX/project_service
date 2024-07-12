package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Удалить этап.
    public void deleteStage(Long id) {
        stageRepository.delete(stageRepository.getById(id));
    }

    // Обновить этап.
    public void updateStage(StageDto stageDto) {
        stageRepository.getById(stageDto.getStageId());
    }

    // Получить все этапы проекта.
    public List<StageDto> getStagesOfProject(long projectId) {
        return stageRepository.findAll()
                .stream()
                .filter(stage -> stage.getProject()
                        .equals(projectRepository.getProjectById(projectId)))
                .toList().stream()
                .map(stageMapper::stageToDto)
                .toList();
    }

    // Получить конкретный этап по Id.
    public StageDto getStageById(long stageId) {
        return stageMapper.stageToDto(stageRepository.getById(stageId));
    }
}
