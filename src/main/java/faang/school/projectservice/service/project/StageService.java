package faang.school.projectservice.service.project;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.utilservice.StageUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageUtilService stageUtilService;

    public List<Stage> findAllByIds(List<Long> ids) {
       return stageUtilService.findAllByIds(ids);
    }
}
