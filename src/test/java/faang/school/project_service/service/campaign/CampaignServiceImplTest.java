package faang.school.project_service.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.campaign.CampaignCreatedAtFilter;
import faang.school.projectservice.filter.campaign.CampaignCreatedByFilter;
import faang.school.projectservice.filter.campaign.CampaignStatusFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.campaign.CampaignService;
import faang.school.projectservice.service.campaign.CampaignServiceImpl;
import faang.school.projectservice.validation.campaign.CampaignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {
    private final CampaignMapper campaignMapper = Mappers.getMapper(CampaignMapper.class);
    private final CampaignFilterDto campaignFilterDto = CampaignFilterDto.builder()
            .createdAt(LocalDateTime.now().minusDays(5))
            .createdBy(345L)
            .status(CampaignStatus.CANCELED)
            .build();

    private final Project project = Project.builder()
            .id(1L)
            .build();

    private final Campaign mockCampaign = Campaign.builder()
            .id(2L)
            .createdBy(34L)
            .updatedBy(434L)
            .status(CampaignStatus.ACTIVE)
            .project(project)
            .build();

    private final CreateCampaignDto createCampaignDto = CreateCampaignDto.builder()
            .projectId(project.getId())
            .build();

    Long updatedBy = 4533456L;
    Long previousUpdatedBy = mockCampaign.getUpdatedBy();

    @Captor
    private ArgumentCaptor<Campaign> campaignArgumentCaptor;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignValidator campaignValidator;

    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        campaignService = new CampaignServiceImpl(campaignRepository, projectRepository, campaignMapper, userContext,
                campaignValidator,
                List.of(new CampaignCreatedAtFilter(), new CampaignStatusFilter(), new CampaignCreatedByFilter()));
    }

    @Test
    void testCreateThrowsExceptionIfProjectNotFound() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> campaignService.create(createCampaignDto));
    }

    @Test
    void testCreatePositive() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenReturn(project);
        when(campaignRepository.save(Mockito.any(Campaign.class))).thenReturn(mockCampaign);
        when(userContext.getUserId()).thenReturn(mockCampaign.getCreatedBy());

        CampaignDto savedCampaignDto = campaignService.create(createCampaignDto);

        verify(campaignValidator).validateUser(Mockito.eq(project), Mockito.any(UserContext.class));
        verify(campaignRepository).save(campaignArgumentCaptor.capture());

        Campaign campaignToSave = campaignArgumentCaptor.getValue();

        assertEquals(mockCampaign.getId(), savedCampaignDto.id());
        assertEquals(mockCampaign.getProject().getId(), campaignToSave.getProject().getId());
        assertEquals(mockCampaign.getStatus(), campaignToSave.getStatus());
        assertEquals(mockCampaign.getCreatedBy(), campaignToSave.getCreatedBy());
    }

    @Test
    void testUpdateThrowsExceptionIfCampaignNotFound() {
        when(campaignRepository.getByIdOrThrow(mockCampaign.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.update(mockCampaign.getId(), UpdateCampaignDto.builder().build()));
    }

    @Test
    void testUpdatePositive() {
        getCreateDeleteCustomMocks();

        UpdateCampaignDto updateCampaignDto = UpdateCampaignDto.builder()
                .status(CampaignStatus.CANCELED)
                .build();

        CampaignDto updatedCampaignDto = campaignService.update(mockCampaign.getId(), updateCampaignDto);

        verify(campaignValidator).validateUser(Mockito.eq(project), Mockito.any(UserContext.class));
        verify(campaignRepository).save(campaignArgumentCaptor.capture());

        Campaign campaignToSave = campaignArgumentCaptor.getValue();

        assertEquals(mockCampaign.getId(), updatedCampaignDto.id());
        assertEquals(updateCampaignDto.status(), campaignToSave.getStatus());
        assertEquals(updatedBy, campaignToSave.getUpdatedBy());
        assertNotEquals(previousUpdatedBy, campaignToSave.getUpdatedBy());
    }

    @Test
    void testSoftDeleteThrowsExceptionIfCampaignNotFound() {
        when(campaignRepository.getByIdOrThrow(mockCampaign.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.softDelete(mockCampaign.getId()));
    }

    @Test
    void testSoftDeletePositive() {
        getCreateDeleteCustomMocks();
        campaignService.softDelete(mockCampaign.getId());

        verify(campaignValidator).validateUser(Mockito.eq(project), Mockito.any(UserContext.class));
        verify(campaignRepository).save(campaignArgumentCaptor.capture());

        Campaign campaignToSave = campaignArgumentCaptor.getValue();

        assertEquals(updatedBy, campaignToSave.getUpdatedBy());
        assertNotEquals(previousUpdatedBy, campaignToSave.getUpdatedBy());
        assertEquals(CampaignStatus.DELETED, campaignToSave.getStatus());
    }

    @Test
    void testGetByIdThrowsExceptionIfCampaignNotFound() {
        long id = 435;
        when(campaignRepository.getByIdOrThrow(id)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> campaignService.getCampaignById(id));
    }

    @Test
    void testGetByIdPositive() {
        when(campaignRepository.getByIdOrThrow(mockCampaign.getId())).thenReturn(mockCampaign);

        long idToCheck = mockCampaign.getId();

        CampaignDto campaignById = campaignService.getCampaignById(mockCampaign.getId());

        assertEquals(idToCheck, campaignById.id());
    }

    @Test
    void testGetByFiltersReturnEmptyListIfNothingFoundInBase() {
        when(campaignRepository.findAll()).thenReturn(List.of());

        List<CampaignDto> campaignServiceByFilters = campaignService.getByFilters(campaignFilterDto);

        assertTrue(campaignServiceByFilters.isEmpty());
    }

    @Test
    void testGetByFiltersReturnEmptyListIfNothingFoundByFilters() {
        Campaign campaignOne = Campaign.builder()
                .status(CampaignStatus.ACTIVE)
                .createdAt(campaignFilterDto.createdAt())
                .createdBy(campaignFilterDto.createdBy())
                .build();

        Campaign campaignTwo = Campaign.builder()
                .status(campaignFilterDto.status())
                .createdAt(campaignFilterDto.createdAt().minusDays(3))
                .createdBy(campaignFilterDto.createdBy())
                .build();

        Campaign campaignThree = Campaign.builder()
                .status(campaignFilterDto.status())
                .createdAt(campaignFilterDto.createdAt())
                .createdBy(campaignFilterDto.createdBy() + 34)
                .build();

        when(campaignRepository.findAll()).thenReturn(List.of(campaignOne, campaignTwo, campaignThree));

        List<CampaignDto> campaignServiceByFilters = campaignService.getByFilters(campaignFilterDto);

        assertTrue(campaignServiceByFilters.isEmpty());
    }

    @Test
    void testGetByFiltersPositive() {
        Campaign campaignOne = Campaign.builder()
                .id(1L)
                .status(campaignFilterDto.status())
                .createdAt(campaignFilterDto.createdAt())
                .createdBy(campaignFilterDto.createdBy())
                .build();

        Campaign campaignTwo = Campaign.builder()
                .id(2L)
                .status(campaignFilterDto.status())
                .createdAt(campaignFilterDto.createdAt())
                .createdBy(campaignFilterDto.createdBy())
                .build();

        Campaign campaignThree = Campaign.builder()
                .id(3L)
                .status(campaignFilterDto.status())
                .createdAt(campaignFilterDto.createdAt())
                .createdBy(campaignFilterDto.createdBy())
                .build();

        List<Campaign> campaigns = List.of(campaignOne, campaignTwo, campaignThree);
        List<Long> campaignsIds = campaigns.stream().map(Campaign::getId).toList();

        when(campaignRepository.findAll()).thenReturn(campaigns);

        List<CampaignDto> campaignsByFilters = campaignService.getByFilters(campaignFilterDto);

        assertEquals(campaigns.size(), campaignsByFilters.size());
        assertTrue(campaignsByFilters.stream().map(CampaignDto::id).toList().containsAll(campaignsIds));
    }

    private void getCreateDeleteCustomMocks() {
        when(campaignRepository.getByIdOrThrow(mockCampaign.getId())).thenReturn(mockCampaign);
        when(campaignRepository.save(Mockito.any(Campaign.class))).thenReturn(mockCampaign);
        when(userContext.getUserId()).thenReturn(updatedBy);
    }
}