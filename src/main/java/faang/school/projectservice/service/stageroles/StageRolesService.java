package faang.school.projectservice.service.stageroles;

import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageRolesService {
    private final StageRolesRepository stageRolesRepository;

    public List<StageRoles> getAllById(List<Long> stageRolesIds) {
        return stageRolesRepository.findAllById(stageRolesIds);
    }
}
