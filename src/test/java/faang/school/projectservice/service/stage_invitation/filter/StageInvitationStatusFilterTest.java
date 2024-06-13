package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
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
class StageInvitationStatusFilterTest {

    @InjectMocks
    private StageInvitationStatusFilter stageInvitationStatusFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;
    private StageInvitation stageInvitation1, stageInvitation2;

    @BeforeEach
    void setUp() {
        StageInvitationStatus status = StageInvitationStatus.ACCEPTED;

        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .status(status)
                .build();

        stageInvitation1 = StageInvitation.builder()
                .status(status)
                .build();

        stageInvitation2 = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .build();
    }

    @Test
    void isAcceptable() {
        assertTrue(stageInvitationStatusFilter.isAcceptable(stageInvitationFilterDto));
    }

    @Test
    void apply() {
        Stream<StageInvitation> invitationStream = Stream.of(stageInvitation1, stageInvitation2);

        List<StageInvitation> actual = stageInvitationStatusFilter.apply(invitationStream, stageInvitationFilterDto).toList();

        assertIterableEquals(List.of(stageInvitation1), actual);
    }
}