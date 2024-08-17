package faang.school.projectservice.project;

import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.project.TeamService;
import faang.school.projectservice.service.utilservice.StageUtilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageUtilService stageUtilService;
    @InjectMocks
    private StageService stageService;

    @Test
    public void testFindTeamsByIds(){
        stageUtilService.findAllByIds(List.of(1L));
        verify(stageUtilService).findAllByIds(any());
    }

}
