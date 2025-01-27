package faang.school.projectservice.unit;

import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationTest {

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @InjectMocks
    private StageInvitationService stageInvitationService;




}
