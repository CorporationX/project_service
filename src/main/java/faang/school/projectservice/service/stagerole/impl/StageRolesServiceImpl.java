package faang.school.projectservice.service.stagerole.impl;

import faang.school.projectservice.dto.stagerole.StageRolesDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.stagerole.StageRolesService;
import faang.school.projectservice.validator.stage.StageValidator;
import faang.school.projectservice.validator.stagerole.StageRolesValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageRolesServiceImpl implements StageRolesService {
    private final StageRolesRepository stageRolesRepository;
    private final StageRolesValidator stageRolesValidator;
    private final StageValidator stageValidator;
    private final StageRolesMapper mapper;

    @Override
    @Transactional
    public StageRolesDto create(StageRolesDto dto) {
        StageRoles stageRolesEntity = getStageRolesEntity(dto);
        stageRolesEntity = stageRolesRepository.save(stageRolesEntity);

        return mapper.toDto(stageRolesEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageRolesDto> getAllByStageId(long stageId) {
        stageValidator.validateStageExistence(stageId);
        List<StageRoles> entities = stageRolesRepository.findAllByStageStageId(stageId);

        return mapper.toDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public StageRolesDto getById(long stageRoleId) {
        StageRoles stageRolesEntity = stageRolesValidator.validateStageRolesExistence(stageRoleId);

        return mapper.toDto(stageRolesEntity);
    }

    @Override
    @Transactional
    public void delete(long stageRoleId) {
        StageRoles stageRolesEntity = stageRolesValidator.validateStageRolesExistence(stageRoleId);

        stageRolesRepository.delete(stageRolesEntity);
    }


    private StageRoles getStageRolesEntity(StageRolesDto dto) {
        Long stageId = dto.getStageId();
        Stage stageEntity = stageValidator.validateStageExistence(stageId);
        StageRoles stageRolesEntity = mapper.toEntity(dto);
        stageRolesEntity.setStage(stageEntity);

        return stageRolesEntity;
    }
}
