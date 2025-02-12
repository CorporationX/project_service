package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static faang.school.projectservice.model.CampaignStatus.CANCELED;
import static faang.school.projectservice.model.CampaignStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private CampaignValidationService campaignValidationService;

    @InjectMocks
    private CampaignService campaignService;

    private Campaign campaign;
    private CampaignDto campaignDto;
    private Project project;

    @BeforeEach
    void setUp() {
        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Test Campaign");
        campaign.setGoal(BigDecimal.valueOf(1000));
        campaign.setAmountRaised(BigDecimal.valueOf(500));

        campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setTitle("Test Campaign");

        project = new Project();
        project.setId(1L);
    }

    @Test
    void createCampaign_ShouldCreateAndReturnCampaign() {
        when(projectService.getProjectById(anyLong())).thenReturn(project);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        Campaign result = campaignService.createCampaign(campaign, project.getId(), 1L);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void getCampaignById_ShouldReturnCampaign() {
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        Campaign result = campaignService.getCampaignById(campaign.getId());

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(campaignRepository, times(1)).findById(campaign.getId());
    }

    @Test
    void getCampaignById_ShouldThrowExceptionIfNotFound() {
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> campaignService.getCampaignById(campaign.getId()));

        assertEquals("Campaign with id 1 not found", exception.getMessage());
    }

    @Test
    void updateCampaign_ShouldUpdateAndReturnCampaign() {
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(campaignMapper.updateCampaignFromDto(any(CampaignDto.class), any(Campaign.class))).thenReturn(campaign);

        Campaign result = campaignService.updateCampaign(campaign.getId(), 1L, campaignDto);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void updateCampaign_ShouldUpdateAndReturnCompletedCampaign() {
        campaign.setAmountRaised(campaign.getGoal());
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(campaignMapper.updateCampaignFromDto(any(CampaignDto.class), any(Campaign.class))).thenReturn(campaign);

        Campaign result = campaignService.updateCampaign(campaign.getId(), 1L, campaignDto);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        assertEquals(COMPLETED, result.getStatus());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void deleteCampaign_ShouldCancelAndReturnCampaign() {
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        Campaign result = campaignService.deleteCampaign(campaign.getId(), 1L);

        assertNotNull(result);
        assertEquals(CANCELED, result.getStatus());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void getCampaignsByProjectIdAndFilter_ShouldReturnCampaignList() {
        CampaignFilterDto filter = new CampaignFilterDto();
        List<Campaign> campaigns = List.of(campaign);

        when(campaignRepository.findAllByFiltersAndProjectId(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(campaigns);

        List<Campaign> result = campaignService.getCampaignsByProjectIdAndFilter(project.getId(), filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Campaign", result.get(0).getTitle());
    }

    @Test
    void getCampaignsByProjectIdAndFilter_ShouldReturnEmptyList() {
        CampaignFilterDto filter = new CampaignFilterDto();

        when(campaignRepository.findAllByFiltersAndProjectId(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        List<Campaign> result = campaignService.getCampaignsByProjectIdAndFilter(project.getId(), filter);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
