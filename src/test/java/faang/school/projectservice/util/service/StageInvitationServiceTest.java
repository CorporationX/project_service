package faang.school.projectservice.util.service;

import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Test
    void testCreateFail(){

    }

    @Test
    void testCreate(){

    }

    @Test
    void testAcceptFail(){

    }

    @Test
    void testAccept(){

    }

    @Test
    void testRejectFail(){

    }

    @Test
    void testReject(){

    }

    @Test
    void testGetAllFail(){

    }

    @Test
    void testGetAll(){

    }

}
