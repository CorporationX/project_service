package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.exception.stage.DataValidException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageRolesRepository stageRolesRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;


    public StageDtoCreate create(StageDtoCreate stageDtoCreate, Long creatorId, Long projectId) {
        if (stageDtoCreate == null || creatorId == null || projectId == null) {
            log.error("Creator id: {} \n or project ID is null {} \n or DTO is null:\n {} ",
                    creatorId, projectId, stageDtoCreate);
            throw new DataValidException("Data not valid");
        }
        Stage stage = stageMapper.toEntity(stageDtoCreate);
        List<StageRoles> stageRoles = stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        stage.setStageRoles(stageRoles);
        stage.setProject(projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found")));
        stageRepository.save(stage);
        stageRolesRepository.saveAll(stageRoles);
        log.info("Created stage: {}", stage);

        return stageMapper.toDto(stage);
    }
}
