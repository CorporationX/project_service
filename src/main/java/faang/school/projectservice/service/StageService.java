package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach(stageRole ->
                stageRole.setStage(stage)
        );
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByStatus(String status) {
        List<Stage> stages = stageRepository.findAll();
        stages.removeIf(stage -> !stage.getStatus().toString().equalsIgnoreCase(status));
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        stageRepository.deleteById(stageId);
    }

    @Transactional
    public StageDto updateStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Stage stageFromRepository = stageRepository.getById(stage.getStageId());
        List<StageRoles> stageRoles = stage.getStageRoles();
        List<StageRoles> stageRolesFromRepository = stageFromRepository.getStageRoles();
        List<StageRoles> newStageRoles = new ArrayList<>();
        stageRolesFromRepository.forEach(stageRoleFromRepository -> {
            if (stageRoles.stream()
                    .map(StageRoles::getTeamRole)
                    .noneMatch(teamRole -> stageRoleFromRepository.getTeamRole().equals(teamRole))) {
                newStageRoles.add(stageRoleFromRepository);
            }
        });
        Map<TeamRole, Integer> teamRoleAndCount = newStageRoles.stream()
                .collect(Collectors.groupingBy(StageRoles::getTeamRole, Collectors.summingInt(StageRoles::getCount)));
        for (Map.Entry<TeamRole, Integer> entry : teamRoleAndCount.entrySet()) {
            TeamRole teamRole = entry.getKey();
            Integer count = entry.getValue();
           stage.getProject().getStages().forEach(stage1 -> stage1.getExecutors().forEach(executor -> {
               if(executor.getRoles().contains(teamRole)){
                   Long userId = executor.getUserId();
                   }
                   //TODO
                   teamRoleAndCount.put(teamRole, count-1);

           }));
        }


        return stageMapper.toDto(stage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}