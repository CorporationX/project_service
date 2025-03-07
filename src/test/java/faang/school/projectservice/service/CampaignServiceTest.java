package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @InjectMocks
    private CampaignService campaignService;

    private Project project;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Test Campaign");
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setProject(project);
    }

    @Test
    void testGetCampaignDtoById_Success() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        CampaignDto expected = new CampaignDto();
        expected.setId(1L);
        expected.setTitle("Test Campaign");
        expected.setStatus(CampaignStatus.ACTIVE);
        expected.setProjectId(1L);

        CampaignDto actual = campaignService.getCampaignDtoById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void testFindCampaignById_ShouldThrowExceptionWhenCampaignNotFound() {
        when(campaignRepository.findById(1L))
                .thenThrow(new EntityNotFoundException("Campaign not found with id: 1"));

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.findCampaignById(1L));
    }

    @Test
    void testDeleteCampaign_Success() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        campaignService.deleteCampaign(1L, 1L);

        assertEquals(CampaignStatus.CANCELED, campaign.getStatus());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void testUpdateCampaign_Success() {
        UpdateCampaignDto updateCampaignDto = new UpdateCampaignDto();
        updateCampaignDto.setTitle("Updated Campaign");

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(campaign)).thenReturn(campaign);

        CampaignDto expected = new CampaignDto();
        expected.setId(1L);
        expected.setTitle("Updated Campaign");
        expected.setStatus(CampaignStatus.ACTIVE);
        expected.setProjectId(1L);
        expected.setUpdatedBy(1L);

        CampaignDto actual = campaignService.updateCampaign(1L, 1L, updateCampaignDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetFilteredCampaigns_ShouldReturnFilteredList() {
        CampaignFilterDto filters = new CampaignFilterDto();
        filters.setProjectId(1L);

        when(campaignRepository.findAllByFilters(eq(1L), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Stream.of(campaign).toList());

        List<CampaignDto> response = campaignService.getFilteredCampaigns(filters);

        assertThat(response).isNotEmpty();
    }

    @Test
    void testCreateCampaign_Success() {
        campaign.setDescription("Test Description");
        campaign.setGoal(new BigDecimal(1000));
        campaign.setCurrency(Currency.USD);
        campaign.setCreatedBy(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        CreateCampaignDto createCampaignDto = new CreateCampaignDto();
        createCampaignDto.setTitle("Test Campaign");
        createCampaignDto.setDescription("Test Description");
        createCampaignDto.setGoal(new BigDecimal(1000));
        createCampaignDto.setProjectId(1L);
        createCampaignDto.setCurrency(Currency.USD);

        CampaignDto expected = new CampaignDto();
        expected.setId(1L);
        expected.setTitle("Test Campaign");
        expected.setDescription("Test Description");
        expected.setGoal(new BigDecimal(1000));
        expected.setStatus(CampaignStatus.ACTIVE);
        expected.setProjectId(1L);
        expected.setCurrency(Currency.USD);
        expected.setCreatedBy(1L);

        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        CampaignDto actual = campaignService.createCampaign(1L, createCampaignDto);

        assertEquals(expected, actual);
    }
}