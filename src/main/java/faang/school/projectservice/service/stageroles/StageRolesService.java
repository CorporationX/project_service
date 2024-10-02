package faang.school.projectservice.service.stageroles;

import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRolesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageRolesService {
    private final StageRolesRepository stageRolesRepository;

    public List<StageRoles> getAllById(List<Long> stageRolesIds) {
        return Optional.of(stageRolesRepository.findAllById(stageRolesIds))
                .orElseThrow(EntityNotFoundException::new);
    }
}
