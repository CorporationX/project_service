package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class StageInvitationStageFilterTest {

    @InjectMocks
    private StageInvitationStageFilter stageInvitationStageFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;
    private StageInvitation stageInvitation1, stageInvitation2;

    @BeforeEach
    void setUp() {
        long stageId = 1L;

        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .stageId(stageId)
                .build();

        stageInvitation1 = StageInvitation.builder()
                .stage(Stage.builder().stageId(stageId).build())
                .build();

        stageInvitation2 = StageInvitation.builder()
                .stage(Stage.builder().stageId(2L).build())
                .build();
    }

    @Test
    void isAcceptable() {
        assertTrue(stageInvitationStageFilter.isAcceptable(stageInvitationFilterDto));
    }

    @Test
    void apply() {
        Stream<StageInvitation> invitationStream = Stream.of(stageInvitation1, stageInvitation2);

        List<StageInvitation> actual = stageInvitationStageFilter.apply(invitationStream, stageInvitationFilterDto).toList();

        assertIterableEquals(List.of(stageInvitation1), actual);
    }
}