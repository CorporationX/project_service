package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class PromotionTest extends TestSetUp {
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @InjectMocks
    private Promotion promotion;

    @Test
    @DisplayName("Testing promotion of succeeded interns")
    void testPromoteSucceededInterns() {
        Mockito.when(teamMemberJpaRepository.findByUserIdAndProjectId(firstIntern.getUserId(), firstInternship.getProject().getId()))
                .thenReturn(firstIntern);
        Mockito.when(teamMemberJpaRepository.findByUserIdAndProjectId(secondIntern.getUserId(), firstInternship.getProject().getId()))
                .thenReturn(secondIntern);

        List<TeamMember> result = promotion.promoteSucceededInterns(firstInternship, succeededUserIds);

        Assert.assertEquals(afterPromotion, result);
    }

    @Test
    @DisplayName("Testing demotion of failed interns")
    void testDemoteFailedInterns() {
        Mockito.when(teamMemberJpaRepository.findByUserIdAndProjectId(thirdIntern.getUserId(), firstInternship.getProject().getId()))
                .thenReturn(thirdIntern);

        List<TeamMember> result = promotion.demoteFailedInterns(firstInternship, failedInterns);

        Assert.assertEquals(afterDemotion, result);

    }
}