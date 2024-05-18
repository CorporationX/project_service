package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
public class InvitationFilterTest {

    private List<InvitationFilter> invitationFilter;
    private InvitationFilterDto invitationFilterDtoAuthor;
    private InvitationFilterDto invitationFilterDtoStage;
    private InvitationFilterDto invitationFilterDtoStatus;

    @BeforeEach
    public void setUp() {
        invitationFilter = List.of(new InvitationAuthorFilter(), new InvitationStageFilter(), new InvitationStatusFilter());
        invitationFilterDtoAuthor = InvitationFilterDto.builder().authorId(1L).build();
        invitationFilterDtoStatus = InvitationFilterDto.builder().status(StageInvitationStatus.REJECTED).build();
        invitationFilterDtoStage = InvitationFilterDto.builder().stageId(1L).build();
    }

    @Test
    public void testIsApplicableWithAuthor() {
        invitationFilter.stream().filter(filter -> filter.isApplicable(invitationFilterDtoAuthor)).findFirst().ifPresentOrElse(
                filter -> assertInstanceOf(InvitationFilter.class, filter),
                Assertions::fail
        );
    }

    @Test
    public void testIsApplicableWithStatus() {
        invitationFilter.stream().filter(filter -> filter.isApplicable(invitationFilterDtoStatus)).findFirst().ifPresentOrElse(
                filter -> assertInstanceOf(InvitationFilter.class, filter),
                Assertions::fail
        );
    }

    @Test
    public void testIsApplicableWithStage() {
        invitationFilter.stream().filter(filter -> filter.isApplicable(invitationFilterDtoStage)).findFirst().ifPresentOrElse(
                filter -> assertInstanceOf(InvitationFilter.class, filter),
                Assertions::fail
        );
    }
}
