package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class CampaignValidatorTest {
    private CampaignValidator campaignValidator;

    @BeforeEach
    void setUp() {
        campaignValidator = new CampaignValidator();
    }

    @Test
    void testValidateCampaignStatus_ShouldThrowExceptionWhenStatusIsNotActive() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.COMPLETED);
        assertThrows(DataValidationException.class, () -> campaignValidator.validateCampaignStatus(campaign));
    }

    @Test
    void testValidateCampaignStatus_Success() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.ACTIVE);
        assertDoesNotThrow(() -> campaignValidator.validateCampaignStatus(campaign));
    }
}
