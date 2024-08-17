package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageUtilService {
    private final StageRepository stageRepository;

    public List<Stage> findAllByIds(List<Long> ids) {
        return stageRepository.findAllById(ids);
    }
}
