package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.StageRoleAndCountFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageRolesMapper stageRolesMapper;
    private final StageMapper stageMapper;

    @Transactional
    public StageDto createStage(StageDto stageDto, StageRolesDto stageRolesDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage = stageRepository.save(stage);


        StageRoles stageRoles = stageRolesMapper.toEntity(stageRolesDto);
        stageRoles = stageRepository.save(stageRolesDto)

        return stageMapper.toDto(stage);
    }

    @Transactional
    public List<StageDto> getAllStagesByRole(StageRoleAndCountFilter stageRoleAndCountFilter, Long userId) {

    }


    @Transactional
    public StageDto deleteStage() {

    }

    @Transactional
    public StageDto updateStage(StageDto stageDto) {
        Stage stage = findAndCheckStageId(stageDto);
        stageMapper.updateEntity(stageDto, stage);
        return stageMapper.toDto(stage);
    }

    public StageDto getAllStages() {

    }

    @Transactional
    public StageDto getStageById(Long stageId) throws EntityNotFoundException {
        return stageRepository.findById(stageId)
                .map(stageMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Stage with " + stageId + " id not found")
                );
    }

    private Stage findAndCheckStageId(StageDto stageDto) {
        return stageRepository.findById(stageDto.stageId()).orElseThrow(
                () -> new EntityNotFoundException("Stage with " + stageDto.stageId() + " id not found")
        );
    }

}
