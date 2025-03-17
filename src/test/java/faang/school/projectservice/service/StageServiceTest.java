package faang.school.projectservice.service;

import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@RequiredArgsConstructor
class StageServiceTest {
    @Mock
    private final StageRepository stageRepository;
    @Mock
    private final StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private final StageMapper stageMapper;
    @Mock
    private final StageRolesMapper stageRolesMapper;

    @InjectMocks
    private StageService stageService;

    @Test
    void PositiveCreate_ShouldCreateNewStage() {
    }
}