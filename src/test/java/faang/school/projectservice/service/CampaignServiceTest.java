package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.campaign.CampaignFilter;
import faang.school.projectservice.filters.campaign.CampaignFilterByCreatedAt;
import faang.school.projectservice.filters.campaign.CampaignFilterByStatus;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.validator.CampaignServiceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Spy
    private CampaignServiceValidator campaignServiceValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    private CampaignDto campaignDto;
    private Campaign campaign;
    private Project project;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(2L);

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setProject(project);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setTitle("Hello ");
        campaign.setDescription("world!");

        campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setProjectId(project.getId());
        campaignDto.setCampaignStatus(CampaignStatus.ACTIVE);
        campaignDto.setTitle("Hello ");
        campaignDto.setDescription("world!");

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(List.of(TeamRole.MANAGER));

        team = new Team();
        team.setProject(project);
        team.setId(1L);
        team.setTeamMembers(List.of(teamMember));

        project.setTeams(List.of(team));
    }

    @Test
    public void publish_Successful() {
        Mockito.when(projectRepository.getProjectById(campaignDto.getProjectId()))
                .thenReturn(project);

        Mockito.lenient().when(userContext.getUserId())
                .thenReturn(1L);

        Mockito.when(teamMemberRepository.findById(1L))
                .thenReturn(teamMember);

        CampaignDto actual = campaignService.publish(campaignDto, 1L);
        Assertions.assertEquals(campaignDto, actual);

        Mockito.verify(campaignRepository, Mockito.times(1)).save(campaign);
    }

    @Test
    public void publish_CampaignNotFound() {
        Mockito.when(projectRepository.getProjectById(campaignDto.getProjectId()))
                .thenReturn(project);

        Mockito.lenient().when(userContext.getUserId())
                .thenReturn(1L);

        Mockito.when(teamMemberRepository.findById(1L))
                .thenReturn(teamMember);

        CampaignDto actual = campaignService.publish(campaignDto, 1L);
        Assertions.assertEquals(campaignDto, actual);

        Mockito.verify(campaignRepository).save(campaign);
    }

    @Test
    public void public_emptyTeamMember_throwException(){
        Mockito.when(teamMemberRepository.findById(4L))
                .thenReturn(TeamMember.builder().build());
        Mockito.when(projectRepository.getProjectById(campaignDto.getProjectId()))
                .thenReturn(project);
        Assertions.assertThrows(DataValidationException.class,
                () -> campaignService.publish(campaignDto, 4L));
    }

    @Test
    public void update_Successful() {
        Mockito.when(campaignRepository.findById(campaignDto.getId()))
                .thenReturn(Optional.of(campaign));

        Mockito.when(projectRepository.getProjectById(campaignDto.getProjectId()))
                .thenReturn(project);

        Mockito.lenient().when(userContext.getUserId())
                .thenReturn(1L);

        Mockito.when(teamMemberRepository.findById(1L))
                .thenReturn(teamMember);

        Mockito.when(campaignRepository.save(campaign))
                .thenReturn(campaign);

        CampaignDto actual = campaignService.update(campaignDto, 1L);

        Assertions.assertEquals(campaignDto, actual);

        Mockito.verify(campaignRepository, Mockito.times(1)).save(campaign);
    }

    @Test
    public void delete_Successful(){
        Mockito.when(campaignRepository.findById(campaign.getId()))
                .thenReturn(Optional.of(campaign));

        campaignService.delete(campaign.getId());

        Mockito.verify(campaignRepository).save(campaign);
    }

    @Test
    public void delete_throwException(){
        Mockito.when(campaignRepository.findById(campaign.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> campaignService.delete(campaign.getId()));
    }

    @Test
    public void getCampaign_Successful(){
        Mockito.when(campaignRepository.findById(campaignDto.getId()))
                .thenReturn(Optional.of(campaign));

        var some = campaignService.getCampaign(campaignDto.getId());
        Assertions.assertEquals(campaignDto, some);
    }

    @Test
    public void getCampaign_throwException() {
        Mockito.when(campaignRepository.findById(campaignDto.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> campaignService.getCampaign(campaignDto.getId()));
    }

    @ParameterizedTest
    @MethodSource("getProjectId")
    @DisplayName("get donations by userId")
    public void getDonationsByUserId_Successful(long projectId) {
        Campaign campaign1 = Campaign
                .builder()
                .id(2L)
                .project(project)
                .status(CampaignStatus.ACTIVE)
                .build();
        Campaign campaign2 = Campaign
                .builder()
                .id(3L)
                .project(project)
                .status(CampaignStatus.ACTIVE)
                .build();

        List<Optional<Campaign>> campaigns = List.of(Optional.of(campaign1), Optional.of(campaign2));
        Mockito.when(campaignRepository.findAllByProjectId(projectId))
                .thenReturn(campaigns);
        List<CampaignDto> campaignDtos = campaigns
                .stream()
                .map(campaign -> campaignMapper.toDto(campaign.get()))
                .toList();
        List<CampaignDto> campaignsByProjectId = campaignService.getAllCampaigns(projectId);

        Assertions.assertEquals(campaignsByProjectId, campaignDtos); 
    }

    @Test
    public void getByFilters() {
        CampaignFilterByCreatedAt campaignFilterByCreatedAt = new CampaignFilterByCreatedAt();
        CampaignFilterByStatus campaignFilterByStatus = new CampaignFilterByStatus();
        List<CampaignFilter> campaignFilters = List.of(campaignFilterByCreatedAt, campaignFilterByStatus);
        CampaignService campaignServiceNew = new CampaignService(campaignMapper, campaignRepository , projectRepository, teamMemberRepository, campaignServiceValidator, campaignFilters);

        LocalDateTime now = LocalDateTime.now();

        CampaignFilterDto campaignFilterDto = CampaignFilterDto
                .builder()
                .createdAt(now)
                .campaignStatus(CampaignStatus.ACTIVE)
                .createdBy(100L)
                .build();

        Campaign campaign1 = Campaign
                .builder()
                .createdAt(now)
                .status(CampaignStatus.ACTIVE)
                .createdBy(100L)
                .build();

        Campaign campaign2 = Campaign
                .builder()
                .createdAt(now)
                .status(CampaignStatus.CANCELED)
                .createdBy(100L)
                .build();

        Campaign campaign3 = Campaign
                .builder()
                .createdAt(LocalDateTime.now().minusDays(3))
                .status(CampaignStatus.ACTIVE)
                .createdBy(100L)
                .build();

        Mockito.when(campaignRepository.findAll())
                .thenReturn(List.of(campaign1, campaign2, campaign3));

        List<CampaignDto> actual = campaignServiceNew.getByFilters(campaignFilterDto);

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(campaignMapper.toDto(campaign1), actual.get(0));
    }

    static Stream<Arguments> getProjectId() {
        return Stream.of(
                Arguments.of(2L)
        );
    }
}