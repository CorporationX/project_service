package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.CampaignCreatorModificationException;
import faang.school.projectservice.exception.CampaignNotFoundException;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    CampaignMapper campaignMapper;

    @InjectMocks
    CampaignService campaignService;

    @Test
    @DisplayName("Negative: error when project not found")
    void testCreateNegativeNoProject() {
        CampaignCreateDto dto = createCampaignCreateDto();
        var userId = dto.getUpdatedBy();
        when(campaignMapper.toEntity(dto)).thenReturn(new Campaign());
        when(teamMemberRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(projectRepository.findById(userId)).thenReturn(Optional.empty());

        assertException(() -> campaignService.create(dto), ProjectNotFoundException.class,
                ExceptionMessage.PROJECT_NOT_FOUND.formatMessage(userId));
    }

    @Test
    @DisplayName("Negative: error when user is not manager and not owner of the project")
    void testCreateNegativePermissionDenied() {
        CampaignCreateDto dto = createCampaignCreateDto();
        var userId = dto.getUpdatedBy();
        when(campaignMapper.toEntity(dto)).thenReturn(new Campaign());
        when(teamMemberRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(projectRepository.findById(userId)).thenReturn(Optional.of(createProject(10L)));

        assertException(() -> campaignService.create(dto), PermissionDeniedException.class,
                ExceptionMessage.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("Positive: successful creation campaign")
    void testCreateSuccess() {
        CampaignCreateDto campaignCreateDto = createCampaignCreateDto();
        CampaignUpdateDto campaignUpdateDto = createCampaignUpdateDto();
        Campaign campaign = createCampaign();
        var userId = campaignCreateDto.getUpdatedBy();

        when(campaignMapper.toEntity(campaignCreateDto)).thenReturn(new Campaign());
        when(teamMemberRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(projectRepository.findById(userId)).thenReturn(Optional.of(createProject(userId)));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignUpdateDto);

        campaignService.create(campaignCreateDto);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
        assertEquals(campaignUpdateDto.getCreatedBy(), campaign.getCreatedBy());
    }

    @Test
    @DisplayName("Negative: error when campaign not found")
    void testUpdateNegativeNoCampaign() {
        CampaignUpdateDto campaignUpdateDto = createCampaignUpdateDto();
        var idForSearch = campaignUpdateDto.getId();

        when(campaignRepository.findById(idForSearch)).thenReturn(Optional.empty());

        assertException(() -> campaignService.update(idForSearch, campaignUpdateDto), CampaignNotFoundException.class,
                ExceptionMessage.CAMPAIGN_NOT_FOUND.formatMessage(idForSearch));
    }

    @Test
    @DisplayName("Negative: error when trying to change company creator")
    void testUpdateNegativeChangeCreator() {
        CampaignUpdateDto campaignUpdateDto = createCampaignUpdateDto();
        Campaign campaign = createCampaign();
        campaign.setCreatedBy(2L);
        var idForSearch = campaignUpdateDto.getId();

        when(campaignRepository.findById(idForSearch)).thenReturn(Optional.of(campaign));

        assertException(() -> campaignService.update(idForSearch, campaignUpdateDto), CampaignCreatorModificationException.class,
                ExceptionMessage.CAMPAIGN_CREATOR_MODIFICATION.getMessage());
    }

    @Test
    @DisplayName("Positive: successful campaign update")
    void testUpdateSuccess() {
        CampaignUpdateDto campaignUpdateDto = createCampaignUpdateDto();
        Campaign campaign = createCampaign();
        var idForSearch = campaignUpdateDto.getId();

        when(campaignRepository.findById(idForSearch)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(campaign)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignUpdateDto);
        doAnswer(invocation -> {
            Campaign entity = invocation.getArgument(0);
            CampaignUpdateDto dto = invocation.getArgument(1);

            entity.setDescription(dto.getDescription());

            return null;
        }).when(campaignMapper).update(campaign, campaignUpdateDto);

        campaignService.update(idForSearch, campaignUpdateDto);
        verify(campaignRepository, times(1)).save(campaign);

        assertEquals(campaign.getDescription(), campaignUpdateDto.getDescription());
    }

    @Test
    @DisplayName("Negative: error when deleting when campaign not found")
    void testDeleteNoCampaign() {
        var idForDelete = 1L;
        when(campaignRepository.findById(idForDelete)).thenReturn(Optional.empty());

        assertException(() -> campaignService.delete(idForDelete), CampaignNotFoundException.class,
                ExceptionMessage.CAMPAIGN_NOT_FOUND.formatMessage(idForDelete));
    }

    @Test
    @DisplayName("Positive: successful deleting campaign")
    void testDeleteSuccess() {
        Campaign campaign = createCampaign();
        var idForDelete = 1L;
        when(campaignRepository.findById(idForDelete)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(campaign)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(createCampaignUpdateDto());

        campaignService.delete(idForDelete);
        verify(campaignRepository, times(1)).save(campaign);

        assertEquals(CampaignStatus.CANCELED, campaign.getStatus());
    }

    @Test
    @DisplayName("Negative: error when campaign not found")
    void testFindByIdNegativeNoCampaign() {
        var idForSearch = 1L;
        when(campaignRepository.findById(idForSearch)).thenReturn(Optional.empty());

        assertException(() -> campaignService.findById(idForSearch), CampaignNotFoundException.class,
                ExceptionMessage.CAMPAIGN_NOT_FOUND.formatMessage(idForSearch));
    }

    @Test
    @DisplayName("Positive: campaign found")
    void testFindByIdSuccess() {
        CampaignUpdateDto campaignUpdateDto = createCampaignUpdateDto();

        when(campaignRepository.findById(campaignUpdateDto.getId())).thenReturn(Optional.of(new Campaign()));
        when(campaignMapper.toDto(any(Campaign.class))).thenReturn(campaignUpdateDto);

        CampaignUpdateDto foundDto = campaignService.findById(campaignUpdateDto.getId());
        assertEquals(foundDto, campaignUpdateDto);
    }

    private void assertException(Executable executable, Class<? extends Exception> expectedException, String expectedMessage) {
        var exception = assertThrows(expectedException, executable);

        assertEquals(exception.getMessage(), expectedMessage);
    }

    private CampaignCreateDto createCampaignCreateDto() {
        return CampaignCreateDto.builder()
                .title("Test title")
                .description("Test description")
                .projectId(1L)
                .updatedBy(1L)
                .build();
    }

    private CampaignUpdateDto createCampaignUpdateDto() {
        return CampaignUpdateDto.builder()
                .id(1L)
                .title("Test title")
                .description("Test description update")
                .projectId(1L)
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }

    private Campaign createCampaign() {
        return Campaign.builder()
                .title("Test title")
                .description("Test description")
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }

    private Project createProject(Long id) {
        Project project = new Project();
        project.setOwnerId(id);
        return project;
    }
}
