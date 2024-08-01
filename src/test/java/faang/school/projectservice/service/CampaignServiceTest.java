package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.campaignFilter.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {

    private static final long PROJECT_ID = 1L;
    private static final long CAMPAIGN_ID = 2L;
    private static final long USER_ID = 3L;

    private CampaignDto campaignDto;
    private Campaign testCampaign;
    private CampaignUpdateDto updateCampaignDto;
    private Campaign updatedCampaign;

    @BeforeEach
    public void init() {
        campaignDto = CampaignDto.builder()
                .title("Campaign title")
                .description("Campaign description")
                .goal(BigDecimal.valueOf(100))
                .amountRaised(BigDecimal.valueOf(0))
                .currency(Currency.USD)
                .projectId(PROJECT_ID)
                .status(CampaignStatus.ACTIVE)
                .build();

        testCampaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .title("Campaign title")
                .description("Campaign description")
                .goal(BigDecimal.valueOf(100))
                .amountRaised(BigDecimal.valueOf(0))
                .createdBy(USER_ID)
                .updatedBy(USER_ID)
                .currency(Currency.USD)
                .project(Project.builder().id(PROJECT_ID).build())
                .status(CampaignStatus.ACTIVE)
                .build();

        updateCampaignDto = CampaignUpdateDto.builder()
                .title("Updated title")
                .description("Updated description")
                .goal(BigDecimal.valueOf(200))
                .status(CampaignStatus.ACTIVE)
                .build();

        updatedCampaign = Campaign.builder()
                .title("Updated title")
                .description("Updated description")
                .goal(BigDecimal.valueOf(200))
                .status(CampaignStatus.ACTIVE)
                .build();
    }


    @Mock
    private ProjectService projectService;
    @Mock
    private CampaignRepository campaignRepository;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @Spy
    private List<CampaignFilter> campaignFilters;
    @InjectMocks
    private CampaignService campaignService;

    @Test
    @DisplayName("Test create campaign with manager permission")
    void testCreateCampaign() {

        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(true);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(campaignRepository.save(any())).thenReturn(testCampaign);

        CampaignDto actualCampaignDto = campaignService.createCampaign(campaignDto, USER_ID);

        assertEquals(campaignDto, actualCampaignDto);
    }

    @Test
    @DisplayName("Test create campaign with owner permission")
    void testCreateCampaignWithOwnerPermission() {
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(true);
        when(campaignRepository.save(any())).thenReturn(testCampaign);

        CampaignDto actualCampaignDto = campaignService.createCampaign(campaignDto, USER_ID);

        assertEquals(campaignDto, actualCampaignDto);
    }

    @Test
    @DisplayName("Test create campaign with wrong permission")
    void testCreateCampaignWithWrongPermission() {
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> campaignService.createCampaign(campaignDto, USER_ID));
    }

    @Test
    @DisplayName("Test update campaign with manager permission")
    void testUpdateCampaign() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(testCampaign));
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(true);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(campaignRepository.save(any())).thenReturn(updatedCampaign);

        CampaignDto actualCampaignDto = campaignService.updateCampaign(updateCampaignDto, USER_ID, CAMPAIGN_ID);

        assertAll(
                () -> assertEquals(updateCampaignDto.getGoal(), actualCampaignDto.getGoal()),
                () -> assertEquals(updateCampaignDto.getStatus(), actualCampaignDto.getStatus()),
                () -> assertEquals(updateCampaignDto.getDescription(), actualCampaignDto.getDescription()),
                () -> assertEquals(updateCampaignDto.getTitle(), actualCampaignDto.getTitle())
        );
    }

    @Test
    @DisplayName("Test update campaign with wrong permission")
    void testUpdateCampaignWithWrongPermission() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(testCampaign));
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> campaignService.updateCampaign(updateCampaignDto, USER_ID, CAMPAIGN_ID));
    }

    @Test
    @DisplayName("Test update campaign when campaign not found")
    void testUpdateCampaignWhenCampaignNotFound() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> campaignService.updateCampaign(updateCampaignDto, USER_ID, CAMPAIGN_ID));
    }

    @Test
    @DisplayName("Test soft delete campaign with manager permission")
    void testSoftDeleteCampaign() {
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(true);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(testCampaign));
        when(campaignRepository.save(any())).thenReturn(updatedCampaign);

        campaignService.softDeleteCampaign(CAMPAIGN_ID, USER_ID);

        assertEquals(CampaignStatus.DELETED, testCampaign.getStatus());
        verify(campaignRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test soft delete campaign with wrong permission")
    void testSoftDeleteCampaignWithWrongPermission() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(testCampaign));
        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> campaignService.softDeleteCampaign(CAMPAIGN_ID, USER_ID));
    }

    @Test
    @DisplayName("Test soft delete campaign when campaign not found")
    void testSoftDeleteCampaignWhenCampaignNotFound() {

        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> campaignService.softDeleteCampaign(CAMPAIGN_ID, USER_ID));
    }

    @Test
    @DisplayName("Test get campaign by id")
    void testGetCampaignById() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(testCampaign));
        CampaignDto actualCampaignDto = campaignService.getCampaignDtoById(CAMPAIGN_ID);
        campaignDto.setUpdatedBy(USER_ID);
        campaignDto.setCreatedBy(USER_ID);
        assertEquals(campaignDto, actualCampaignDto);
    }

    @Test
    @DisplayName("Test get campaign by id when campaign not found")
    void testGetCampaignByIdWhenCampaignNotFound() {
        when(campaignRepository.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> campaignService.getCampaignDtoById(CAMPAIGN_ID));
    }

    @Test
    @DisplayName("Test get all campaigns by filter")
    void testGetAllCampaignsByFilter() {
        CampaignFilter campaignFilter = Mockito.mock(CampaignFilter.class);

        campaignService = new CampaignService(projectService, campaignRepository, campaignMapper, List.of(campaignFilter));

        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();
        List<Campaign> campaigns = new ArrayList<>(List.of(Campaign.builder().title("title 2").createdAt(LocalDateTime.now()).build(),
                Campaign.builder().title("title 3").createdAt(LocalDateTime.now().plusMinutes(1)).build(),
                Campaign.builder().title("title 1").createdAt(LocalDateTime.now().minusMinutes(1)).build(),
                Campaign.builder().title("title 4").createdAt(LocalDateTime.now().plusMinutes(2)).build()));

        List<CampaignDto> mappedCampaigns = List.of(CampaignDto.builder().title("title 4").build(),
                CampaignDto.builder().title("title 3").build(),
                CampaignDto.builder().title("title 2").build(),
                CampaignDto.builder().title("title 1").build());


        campaigns.sort(Comparator.comparing(Campaign::getCreatedAt).reversed());
        when(campaignRepository.findAll()).thenReturn(campaigns);
        when(campaignFilter.isApplicable(campaignFiltersDto)).thenReturn(true);
        when(campaignFilter.apply(campaigns, campaignFiltersDto)).thenReturn(campaigns.stream());

        List<CampaignDto> result = campaignService.getAllCampaignsByFilter(campaignFiltersDto);

        assertEquals(mappedCampaigns, result);
    }
}

