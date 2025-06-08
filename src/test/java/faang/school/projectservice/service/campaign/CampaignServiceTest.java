package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.exception.CampaignCreationException;
import faang.school.projectservice.filter.campaign.CampaignCreatedByFilterStrategy;
import faang.school.projectservice.filter.campaign.CampaignCreationDateFilterStrategy;
import faang.school.projectservice.filter.campaign.CampaignFilterStrategy;
import faang.school.projectservice.filter.campaign.CampaignStatusFilterStrategy;
import faang.school.projectservice.mapper.CampaignDtoMapper;
import faang.school.projectservice.mapper.CampaignDtoMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CampaignServiceTest {
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private CampaignCreatedByFilterStrategy campaignCreatedByFilterStrategy;
    @Mock
    private CampaignCreationDateFilterStrategy campaignCreationDateFilterStrategy;
    @Mock
    private CampaignStatusFilterStrategy campaignStatusFilterStrategy;

    private List<CampaignFilterStrategy> campaignFilterStrategies;
    @Spy
    private final CampaignDtoMapper campaignDtoMapper = new CampaignDtoMapperImpl();

    private CampaignServiceImpl campaignServiceImpl;

    private TeamMember withoutManagerOrOwnerRoles;
    private TeamMember manager;
    private Project project;
    private CampaignDto campaignDTO;
    private Campaign campaign;
    private Campaign campaignForFilters;
    private List<CampaignDto> campaignDtos;
    private List<Campaign> campaigns;

    @BeforeEach
    void setUp() {
        campaignFilterStrategies = List.of(
                campaignCreatedByFilterStrategy,
                campaignStatusFilterStrategy,
                campaignCreationDateFilterStrategy
        );

        campaignServiceImpl = new CampaignServiceImpl(
                campaignRepository,
                projectRepository,
                campaignDtoMapper,
                teamMemberRepository,
                campaignFilterStrategies
        );
        withoutManagerOrOwnerRoles = TeamMember.builder().id(1L).roles(List.of(TeamRole.DEVELOPER)).build();
        manager = TeamMember.builder().id(2L).roles(List.of(TeamRole.MANAGER)).build();
        project = Project.builder().id(1L).name("project").build();
        campaignDTO = CampaignDto.builder().createdBy(1L).createdBy(1L).project(project).build();
        campaign = new Campaign();
        campaignForFilters = new Campaign();
        campaignDtos = new ArrayList<>();
        campaigns = new ArrayList<>();

        campaign.setCreatedBy(1L);
        campaign.setProject(project);
        campaignForFilters.setCreatedBy(2L);
        campaignForFilters.setProject(project);

        campaignDtos.add(campaignDtoMapper.toCampaignDto(campaign));
        campaignDtos.add(campaignDtoMapper.toCampaignDto(campaign));
        campaignDtos.add(campaignDtoMapper.toCampaignDto(campaignForFilters));
        campaigns.add(campaign);
        campaigns.add(campaign);
        campaigns.add(campaignForFilters);




    }


    @Test
    void test_createCampaign_WhenCreatedBy_OwnerOrManager() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(manager);
        campaignServiceImpl.createCampaign(campaignDTO);
        verify(campaignRepository).save(any());
    }

    @Test
    void test_createCampaign_WhenNotCreatedBy_OwnerOrManager() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(withoutManagerOrOwnerRoles);
        Assertions.assertThrows(CampaignCreationException.class, () -> campaignServiceImpl.createCampaign(campaignDTO));
    }

    @Test
    void test_updateCampaign_WhenCreatorChanged() {
        campaign.setCreatedBy(1L);
        campaign.setId(1L);
        campaignDTO.setUpdatedBy(10L);
        when(campaignRepository.findById(1L)).thenReturn(Optional.ofNullable(campaign));
        Assertions.assertThrows(CampaignCreationException.class, () -> campaignServiceImpl.updateCampaign(campaignDTO, 1L));
    }

    @Test
    void test_updateCampaign_When_CreatorWasNotChanged() {
        campaign.setCreatedBy(1L);
        campaign.setId(1L);
        campaign.setUpdatedBy(1L);
        campaignDTO.setUpdatedBy(1L);
        campaignDTO.setCreatedBy(1L);
        when(campaignRepository.findById(1L)).thenReturn(Optional.ofNullable(campaign));
        campaignServiceImpl.updateCampaign(campaignDTO, 1L);
        verify(campaignRepository).save(any());
    }

    @Test
    void test_update_WhenUpdatingPersonWasNotFilled() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.ofNullable(campaign));
        campaign.setId(1L);
        campaign.setCreatedBy(1L);
        campaignDTO.setCreatedBy(1L);
        Assertions.assertThrows(CampaignCreationException.class, () -> campaignServiceImpl.updateCampaign(campaignDTO, 1L));
    }

    @Test
    void test_deleteCampaign_Should_ChangeStatusToCancelled() {
        campaign.setStatus(CampaignStatus.ACTIVE);
        when(campaignRepository.findById(1L)).thenReturn(Optional.ofNullable(campaign));
        campaignServiceImpl.deleteCampaign(1L);
        Assertions.assertSame(campaign.getStatus(), CampaignStatus.CANCELED);
    }

    @Test
    void test_findById_When_IsPresent() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.ofNullable(campaign));
        Assertions.assertNotNull(campaignServiceImpl.findById(1L));
    }

    @Test
    void test_findById_When_Absent() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> campaignServiceImpl.findById(1L));
    }

    @Test
    void test_findAll_When_Dto_is_Absent() {
        when(campaignRepository.findAll()).thenReturn(campaigns);
        Assertions.assertEquals(campaignDtos, campaignServiceImpl.findAll(null));
    }

    @Test
    void test_findAll_When_Dto_is_Present() {
        CampaignFilterDto filterDto = new CampaignFilterDto(
                LocalDate.now().minusDays(30), CampaignStatus.ACTIVE, 2L
        );
        campaignForFilters.setStatus(CampaignStatus.ACTIVE);
        campaignForFilters.setCreatedAt(LocalDateTime.now().minusDays(30));
        campaignForFilters.setCreatedBy(2L);

        when(campaignRepository.findAll()).thenReturn(campaigns);

        when(campaignCreatedByFilterStrategy.isApplicable(filterDto)).thenReturn(true);
        when(campaignStatusFilterStrategy.isApplicable(filterDto)).thenReturn(true);
        when(campaignCreationDateFilterStrategy.isApplicable(filterDto)).thenReturn(true);

        when(campaignCreatedByFilterStrategy.filter(campaignForFilters, filterDto)).thenReturn(true);
        when(campaignStatusFilterStrategy.filter(campaignForFilters, filterDto)).thenReturn(true);
        when(campaignCreationDateFilterStrategy.filter(campaignForFilters, filterDto)).thenReturn(true);

        when(campaignCreatedByFilterStrategy.filter(campaign, filterDto)).thenReturn(false);

        List<CampaignDto> campaignDtos = campaignServiceImpl.findAll(filterDto);

        Assertions.assertEquals(1, campaignDtos.size());
    }
}
