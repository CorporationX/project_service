package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignPublishingDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.teamMember.TeamMemberValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private List<Filter<CampaignFilterDto, Campaign>> campaignFilters;

    @Mock
    private Filter<CampaignFilterDto, Campaign> campaignFilter;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private UserContext userContext;

    private static final long ID = 1L;
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String NEW_TITLE = "newTitle";
    private static final String NEW_DESCRIPTION = "newDescription";
    private static final BigDecimal GOAL = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT_RAISED = BigDecimal.valueOf(200);
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2024, 10, 10, 10, 10);
    private CampaignPublishingDto campaignPublishingDto;
    private CampaignUpdateDto campaignUpdateDto;
    private CampaignFilterDto campaignFilterDto;
    private List<Campaign> campaigns = new ArrayList<>();
    private CampaignDto campaignDto;
    private Campaign campaign;
    private Project project;

    @BeforeEach
    public void init() {
        project = Project.builder()
                .id(ID)
                .build();
        campaign = Campaign.builder()
                .id(ID)
                .status(CampaignStatus.ACTIVE)
                .title(TITLE)
                .description(DESCRIPTION)
                .project(project)
                .createdBy(ID)
                .createdAt(CREATED_AT)
                .build();
        campaignDto = CampaignDto.builder()
                .id(ID)
                .status(CampaignStatus.ACTIVE)
                .title(TITLE)
                .description(DESCRIPTION)
                .projectId(ID)
                .createdAt(CREATED_AT)
                .build();
        campaignPublishingDto = CampaignPublishingDto.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .projectId(ID)
                .build();
        campaignUpdateDto = CampaignUpdateDto.builder()
                .title(NEW_TITLE)
                .description(NEW_DESCRIPTION)
                .goal(GOAL)
                .amountRaised(AMOUNT_RAISED)
                .status(CampaignStatus.COMPLETED)
                .build();
        campaigns.add(campaign);
        campaigns.add(Campaign.builder()
                .status(CampaignStatus.CANCELED)
                .createdAt(LocalDateTime.now())
                .build());
        campaignFilterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        campaignFilters.add(campaignFilter);
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Success when publishing campaign")
        public void whenPublishingCampaignThenReturnCampaignDto() {
            when(userContext.getUserId()).thenReturn(ID);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.publishingCampaign(campaignPublishingDto);

            assertNotNull(result);
            verify(userContext).getUserId();
            verify(teamMemberValidator).validateUserHasStatusOwnerOrManagerInTeam(ID, ID);
            verify(campaignRepository).save(any(Campaign.class));
            verify(projectService).getProjectById(ID);
        }

        @Test
        @DisplayName("Success when update campaign")
        public void whenUpdateCampaignThenReturnCampaignDto() {
            when(campaignRepository.findById(ID)).thenReturn(Optional.of(campaign));
            doNothing().when(campaignMapper).updateCampaignFromDto(campaignUpdateDto, campaign);
            when(campaignRepository.save(campaign)).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.updateCampaign(ID, campaignUpdateDto);

            assertNotNull(result);
            verify(campaignRepository).findById(ID);
            verify(campaignMapper).updateCampaignFromDto(campaignUpdateDto, campaign);
            verify(campaignRepository).save(any(Campaign.class));
            verify(campaignMapper).toDto(campaign);
        }


        @Test
        @DisplayName("Success when delete campaign by id")
        public void whenDeleteCampaignByIdThenSuccess() {
            when(campaignRepository.findById(ID)).thenReturn(Optional.of(campaign));

            campaignService.deleteCampaignById(ID);

            assertEquals(CampaignStatus.CANCELED, campaign.getStatus());
            verify(campaignRepository).findById(ID);
            verify(campaignRepository).save(campaign);
        }

        @Test
        @DisplayName("Success when get campaign by id")
        public void whenGetCampaignByIdThenGetCampaignDto() {
            when(campaignRepository.findById(ID)).thenReturn(Optional.of(campaign));
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.getCampaignById(ID);

            assertNotNull(result);
            verify(campaignRepository).findById(ID);
            verify(campaignMapper).toDto(campaign);
        }

        @Test
        @DisplayName("Success when get all campaigns by projectId with CampaignFilterDto")
        public void whenGetAllCampaignsByProjectIdThenSuccess() {
            when(campaignRepository.findAllByProjectId(ID)).thenReturn(campaigns);
            when(campaignFilter.isApplicable(campaignFilterDto)).thenReturn(true);
            when(campaignFilter.applyFilter(any(), eq(campaignFilterDto))).thenReturn(Stream.of(campaign));
            when(campaignFilters.stream()).thenReturn(Stream.of(campaignFilter));
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            List<CampaignDto> result = campaignService.getAllCampaignsByProjectId(ID, campaignFilterDto);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(campaignFilterDto.getStatus(), result.get(0).getStatus());
            verify(campaignRepository).findAllByProjectId(ID);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Exception when delete campaign which does not exist")
        public void whenDeleteCampaignByIdWithCampaignDoesNotExistThenThrowException() {
            when(campaignRepository.findById(ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.deleteCampaignById(ID));
        }

        @Test
        @DisplayName("Exception when campaign does not exist")
        public void whenGetCampaignByIdCampaignDoesNotExistThenThrowException() {
            when(campaignRepository.findById(ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.deleteCampaignById(ID));
        }
    }
}