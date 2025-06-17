package faang.school.projectservice.controller;

import faang.school.projectservice.controller.campaign.CampaignController;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CampaignControllerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    CampaignDto campaignDto;
    CampaignFilterDto campaignFilterDto;

    @BeforeEach
    public void setUp() {
        campaignDto = CampaignDto.builder().build();
        campaignFilterDto = new CampaignFilterDto();
    }

    @Test
    public void testFindAllCampaigns_Success() {
        campaignController.findAllCampaigns(campaignFilterDto);
        verify(campaignService).findAll(campaignFilterDto);
    }

    @Test
    public void testCreateCampaign_Success() {
        campaignController.createCampaign(campaignDto);
        verify(campaignService).createCampaign(campaignDto);
    }

    @Test
    public void testFindCampaignById_Success() {
        Long id = 1L;
        campaignController.findCampaignById(id);
        verify(campaignService).findById(id);
    }

    @Test
    public void testUpdateCampaign_Success() {
        Long id = 1L;
        campaignController.updateCampaign(id, campaignDto);
        verify(campaignService).updateCampaign(campaignDto,id);
    }

    @Test
    public void testDeleteCampaign_Success() {
        Long id = 1L;
        campaignController.deleteCampaign(id);
        verify(campaignService).deleteCampaign(id);
    }


}
