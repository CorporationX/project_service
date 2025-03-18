package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.exception.stage.DataValidException;
import faang.school.projectservice.mapper.StageCreateMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final StageRolesRepository stageRolesRepository;
    private final ProjectRepository projectRepository;
    private final StageCreateMapper stageCreateMapper;
    private final StageRolesMapper stageRolesMapper;
    private final StageMapper stageMapper;

    @Autowired
    public StageService(StageRepository stageRepository, StageRolesRepository stageRolesRepository, ProjectRepository projectRepository, StageCreateMapper stageCreateMapper, StageRolesMapper stageRolesMapper, StageMapper stageMapper) {
        this.stageRepository = stageRepository;
        this.stageRolesRepository = stageRolesRepository;
        this.projectRepository = projectRepository;
        this.stageCreateMapper = stageCreateMapper;
        this.stageRolesMapper = stageRolesMapper;
        this.stageMapper = stageMapper;
    }
    @Transactional
    public StageDTO create(StageDtoCreate stageDtoCreate, Long creatorId, Long projectId) {
        if (stageDtoCreate == null || creatorId == null || projectId == null) {
            log.error("Creator ID: {}, Project ID: {}, Stage DTO: {}", creatorId, projectId, stageDtoCreate);
            throw new DataValidException("Data not valid");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        Stage stage = stageCreateMapper.toEntity(stageDtoCreate);

        List<StageRoles> stageRoles = stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        stage.setStageRoles(stageRoles);

        if (project.getStages() == null) {
            project.setStages(new ArrayList<>());
        }
        stage.setProject(project);
        project.getStages().add(stage);

        stageRepository.save(stage);
//TODO что то с ДТО
        log.info("Created stage: {}", stage);
        log.info("\n Dto to created stage: {}", stageMapper.toDto(stage));
        return stageMapper.toDto(stage);
    }
}
