package faang.school.projectservice.service;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    private static final long CAMPAIGN_ID = 1L;
    private static final Campaign campaign = Campaign.builder().id(1L).build();
    @Mock
    private CampaignRepository campaignRepository;
    @InjectMocks
    private CampaignService campaignService;

    @Test
    public void testCampaignExist() {
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        assertDoesNotThrow(() -> campaignService.findById(CAMPAIGN_ID));
    }

    @Test
    public void testCampaignNotExist() {
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            campaignService.findById(CAMPAIGN_ID);
        });

        Assertions.assertEquals(exception.getMessage(), String.format("Компания c ID <%d> не найдена", CAMPAIGN_ID));
    }

}
