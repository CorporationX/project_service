package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private CampaignValidator campaignValidator;

    @Spy
    private CampaignMapperImpl campaignMapper;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Captor
    private ArgumentCaptor<Campaign> campaignCaptor;

    private CampaignDto campaignDto;
    private Campaign campaign;
    private long projectId;
    private long creatorId;
    private long campaignId;
    private TeamMemberDto teamMember;
    private ProjectDto projectDto;
    private CampaignUpdateDto campaignUpdateDto;
    private CampaignFilterDto filterDto;
    private List<Campaign> campaigns;

    @BeforeEach
    public void setUp() {
        projectId = 1L;
        creatorId = 1L;
        campaignId = 1L;
        Project project = Project.builder()
                .id(projectId)
                .build();
        campaignDto = CampaignDto.builder()
                .id(campaignId)
                .creatorId(creatorId)
                .projectId(projectId)
                .build();
        campaign = Campaign.builder()
                .id(campaignId)
                .project(project)
                .createdBy(creatorId)
                .build();
        teamMember = new TeamMemberDto();
        projectDto = new ProjectDto();
        campaignUpdateDto = CampaignUpdateDto.builder()
                .id(creatorId)
                .title("Updated Campaign")
                .updatedBy(creatorId)
                .build();
        filterDto = new CampaignFilterDto();
        campaigns = Arrays.asList(
                Campaign.builder()
                        .project(project)
                        .createdBy(creatorId)
                        .build(),
                Campaign.builder()
                        .project(project)
                        .createdBy(creatorId + 1)
                        .build()
        );
    }

    @Test
    public void publishCampaign_ShouldReturnCampaignDto_WhenSuccessful() {
        when(teamMemberService.getTeamMember(creatorId, projectId)).thenReturn(teamMember);
        when(projectService.findById(creatorId)).thenReturn(projectDto);
        doNothing().when(campaignValidator).validationCampaignCreator(teamMember, projectDto);
        when(campaignRepository.save(campaign)).thenReturn(campaign);

        CampaignDto result = campaignService.publishCampaign(campaignDto);

        assertNotNull(result);
        assertEquals(campaignDto, result);
        verify(campaignRepository).save(campaign);
    }

    @Test
    public void publishCampaign_ShouldThrowException_WhenValidationFails() {
        String message = "Validation failed";
        when(teamMemberService.getTeamMember(creatorId, projectId)).thenReturn(teamMember);
        when(projectService.findById(creatorId)).thenReturn(projectDto);
        doThrow(new DataValidationException(message)).when(campaignValidator).validationCampaignCreator(teamMember, projectDto);

        Exception exception = assertThrows(DataValidationException.class,
                () -> campaignService.publishCampaign(campaignDto));

        assertEquals(message, exception.getMessage());
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    public void updateCampaign_ShouldUpdateCampaign_WhenCampaignExists() {
        campaign.setTitle(campaignUpdateDto.getTitle());
        when(campaignRepository.findById(creatorId)).thenReturn(Optional.of(campaign));

        campaignService.updateCampaign(campaignUpdateDto);

        verify(campaignMapper).updateFromDto(campaignUpdateDto, campaign);
        verify(campaignRepository).save(campaignCaptor.capture());

        Campaign campaign = campaignCaptor.getValue();
        assertNotNull(campaign.getUpdatedAt());
        assertEquals(campaignUpdateDto.getTitle(), campaign.getTitle());
        assertEquals(campaignUpdateDto.getUpdatedBy(), campaign.getUpdatedBy());
    }

    @Test
    public void updateCampaign_ShouldThrowException_WhenCampaignDoesNotExist() {
        String message = "Campaign with id 1 not found";
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> campaignService.updateCampaign(campaignUpdateDto));

        assertEquals(message, exception.getMessage());
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    public void deleteCampaign_ShouldCallSoftDelete_WhenCampaignExists() {
        campaignService.deleteCampaign(campaignId);

        verify(campaignRepository).softDelete(campaignId);
    }

    @Test
    public void getCampaign_ShouldReturnCampaignDto_WhenCampaignExists() {
        campaignDto.setId(campaignId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        CampaignDto result = campaignService.getCampaign(campaignId);

        assertEquals(campaignDto, result);
        verify(campaignRepository).findById(campaignId);
    }

    @Test
    public void getCampaign_ShouldThrowEntityNotFoundException_WhenCampaignDoesNotExist() {
        String message = "Campaign with id %d not found".formatted(campaignId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> campaignService.getCampaign(campaignId));

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void getCampaignsSortedByCreatedAtAndByFilter_ShouldReturnFilteredCampaigns_WhenCampaignsExist() {
        campaignDto.setId(null);
        mockFilter(invocation -> {
            Stream<Campaign> stream = invocation.getArgument(0);
            return stream.filter(c -> c.getCreatedBy() == creatorId);
        });
        mockPage();

        List<CampaignDto> result = campaignService.getCampaignsSortedByCreatedAtAndByFilter(filterDto);

        assertEquals(1, result.size());
        assertEquals(campaignDto, result.get(0));
        verify(campaignRepository).findAll(any(Pageable.class));
    }

    @Test
    public void getCampaignsSortedByCreatedAtAndByFilter_ShouldReturnEmptyList_WhenNoCampaignsMatchFilter() {
        mockFilter(invocation -> {
            Stream<Campaign> stream = invocation.getArgument(0);
            return stream.filter(c -> c.getCreatedBy() == 3L);
        });
        mockPage();

        List<CampaignDto> result = campaignService.getCampaignsSortedByCreatedAtAndByFilter(filterDto);

        assertTrue(result.isEmpty());
        verify(campaignRepository).findAll(any(Pageable.class));
    }


    @Test
    public void getCampaignsSortedByCreatedAtAndByFilter_ShouldReturnAllCampaigns_WhenFilterIsEmpty() {
        mockFilter(invocation -> invocation.<Stream<Campaign>>getArgument(0));
        mockPage();

        List<CampaignDto> result = campaignService.getCampaignsSortedByCreatedAtAndByFilter(filterDto);

        assertEquals(2, result.size());
        verify(campaignRepository).findAll(any(Pageable.class));
    }

    private void mockFilter(Answer<?> answer) {
        CampaignFilter mockFilter = mock(CampaignFilter.class);
        when(mockFilter.isAccepted(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), eq(filterDto))).thenAnswer(answer);

        List<CampaignFilter> campaignFilters = Collections.singletonList(mockFilter);
        ReflectionTestUtils.setField(campaignService, "campaignFilters", campaignFilters);
    }

    private void mockPage() {
        Page<Campaign> mockPage = mock(Page.class);
        when(mockPage.hasContent()).thenReturn(false);
        when(mockPage.getContent()).thenReturn(campaigns);
        when(campaignRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
    }
}
