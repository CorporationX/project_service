package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageServiceValidator {
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;

    public void validateStageDto(StageDto stageDto) {
        if (!projectRepository.existsById(stageDto.getProjectId())) {
            throw new IllegalArgumentException("stage do not have a project");
        }
    }

    public void validateProjectId(Long id) {
        if (projectRepository.existsById(id)) {
            throw new IllegalArgumentException("project with id = " + id + " does not exist");
        }
    }
}
