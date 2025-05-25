package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.repository.adapter.project.ProjectRepoAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import faang.school.projectservice.service.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CampaignServiceCreateTest {

    @Mock
    private CampaignRepoAdapter campaignRepoAdapter;

    @Mock
    private ProjectRepoAdapter projectRepoAdapter;

    @Mock
    private TeamMemberRepoAdapter teamMemberRepoAdapter;

    @Mock
    private CampaignMapper campaignMapper;

    @InjectMocks
    private CampaignService campaignService;

    private CampaignDto inputDto;
    private Campaign entity;
    private CampaignDto outputDto;
    private Project project;

    @BeforeEach
    public void setUp() {
        inputDto = CampaignDto.builder()
                .title("Fundraiser")
                .description("Help us build")
                .goal(new BigDecimal("1000"))
                .projectId(10L)
                .currency(null)
                .build();

        entity = new Campaign();
        outputDto = CampaignDto.builder()
                .id(123L)
                .build();

        project = new Project();
        project.setId(10L);
    }

    @Test
    void testCreateCampaignAsOwner() {
        Long userId = 42L;
        project.setOwnerId(userId);

        Mockito.when(projectRepoAdapter.getProjectById(10L)).thenReturn(project);
        Mockito.when(teamMemberRepoAdapter.getByUserId(userId))
                .thenReturn(Collections.emptyList());
        Mockito.when(campaignMapper.toEntity(inputDto)).thenReturn(entity);
        Mockito.when(campaignRepoAdapter.save(entity)).thenReturn(entity);
        Mockito.when(campaignMapper.toDto(entity)).thenReturn(outputDto);

        CampaignDto result = campaignService.createCampaign(inputDto, userId);

        assertThat(result).isEqualTo(outputDto);
        verify(projectRepoAdapter).getProjectById(10L);
        verify(campaignMapper).toEntity(inputDto);
        verify(campaignRepoAdapter).save(entity);
        verify(campaignMapper).toDto(entity);
    }

    @Test
    void testCreateCampaignAsManager() {
        Long ownerId = 99L;
        Long userId = 42L;
        project.setOwnerId(ownerId);

        Team team = new Team();
        team.setProject(project);

        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(userId);
        teamMember.setTeam(team);

        teamMember.setRoles(List.of(TeamRole.MANAGER));

        when(projectRepoAdapter.getProjectById(10L)).thenReturn(project);
        when(teamMemberRepoAdapter.getByUserId(userId))
                .thenReturn(Collections.singletonList(teamMember));
        when(campaignMapper.toEntity(inputDto)).thenReturn(entity);
        when(campaignRepoAdapter.save(entity)).thenReturn(entity);
        when(campaignMapper.toDto(entity)).thenReturn(outputDto);

        CampaignDto result = campaignService.createCampaign(inputDto, userId);

        assertThat(result).isEqualTo(outputDto);
        verify(teamMemberRepoAdapter).getByUserId(userId);
    }

    @Test
    void createCampaignNotAllowedThrows() {
        Long ownerId = 99L;
        Long userId  = 42L;
        project.setOwnerId(ownerId);

        when(projectRepoAdapter.getProjectById(10L)).thenReturn(project);
        when(teamMemberRepoAdapter.getByUserId(userId))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() ->
                campaignService.createCampaign(inputDto, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not allowed to create campaign");
    }
}
