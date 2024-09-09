package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageMapper mapper;
    @Mock
    private StageServiceValidator validator;
    @Mock
    private List<StageFilter> filters;
    @InjectMocks
    private StageService service;

    private StageDto stageDto;

    @BeforeEach
    public void init() {
        stageDto = new StageDto(1L, "1", 1L, List.of());
    }

    @Test
    void create() {
        Mockito.when(mapper.toStage(stageDto))
                        .thenReturn(null);

        service.create(stageDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateStageDto(stageDto);
        Mockito.verify(mapper, Mockito.times(1))
                .toStage(stageDto);
        Mockito.verify(stageRepository, Mockito.times(1))
                .save(null);
    }

    @Test
    void testGetStageById() {
        Long stageId = stageDto.getStageId();

        Mockito.when(stageRepository.getById(stageId))
                .thenReturn(null);

        service.getStageById(stageId);

        Mockito.verify(mapper, Mockito.times(1))
                .toDto(null);
        Mockito.verify(stageRepository, Mockito.times(1))
                .getById(stageId);
    }

    @Test
    void testDeleteStage(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        stageRepository.deleteById(stageDto.getStageId());


    }
}
