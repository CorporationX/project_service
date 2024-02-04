package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;

@Component
@RequiredArgsConstructor
public class StageValidator {

    private final ProjectRepository projectRepository;

    public void validateStage(StageDto stageDto) {
        if (stageDto.getStageName() == null || stageDto.getStageName().isEmpty()) {
            throw new DataValidationException("Введите наименование этапа");
        }
        if (stageDto.getProjectId() == null) {
            throw new DataValidationException("Введите id проекта");
        }
        if (stageDto.getStageRolesDto().isEmpty()) {
            throw new DataValidationException("Определите список ролей и количество человек для каждой роли");
        }
    }

    public void validateNullStageId(Long stageId) {
        if (stageId == null) {
            throw new DataValidationException("Введите id этапа");
        }
    }

    public void validateStatusProject(Long id) {
        if (projectRepository.getProjectById(id).getStatus().equals(CANCELLED)) {
            throw new DataValidationException("Нельзя создать этап для проекта со статусом - " + CANCELLED);
        }
    }
}