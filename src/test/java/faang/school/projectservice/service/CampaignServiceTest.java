package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Campaign;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        campaign.setTitle("Hello ");
        campaign.setDescription("world!");

        campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setProjectId(project.getId());
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
}