package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository repository;
    private final StageMapper mapper;
    public void create(StageDto stageDto) {
        repository.save(mapper.toStage(stageDto));
    }
}
