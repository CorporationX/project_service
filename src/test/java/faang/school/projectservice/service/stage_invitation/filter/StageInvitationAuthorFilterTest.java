package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationAuthorFilterTest {

    @InjectMocks
    private StageInvitationAuthorFilter stageInvitationAuthorFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;
    private StageInvitation stageInvitation1, stageInvitation2;

    @BeforeEach
    void setUp() {
        long authorId = 1L;

        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .authorId(authorId)
                .build();

        stageInvitation1 = StageInvitation.builder()
                .author(TeamMember.builder().id(authorId).build())
                .build();

        stageInvitation2 = StageInvitation.builder()
                .author(TeamMember.builder().id(2L).build())
                .build();
    }

    @Test
    void isAcceptable() {
        assertTrue(stageInvitationAuthorFilter.isAcceptable(stageInvitationFilterDto));
    }

    @Test
    void apply() {
        Stream<StageInvitation> invitationStream = Stream.of(stageInvitation1, stageInvitation2);

        List<StageInvitation> actual = stageInvitationAuthorFilter.apply(invitationStream, stageInvitationFilterDto).toList();

        assertIterableEquals(List.of(stageInvitation1), actual);
    }
}