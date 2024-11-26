package faang.school.projectservice.service.stage;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private StageMapper stageMapper;
    private StageRepository stageRepository;

    public StageDto getById(Long stageId){
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}
