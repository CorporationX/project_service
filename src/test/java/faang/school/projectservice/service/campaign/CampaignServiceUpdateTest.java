package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceUpdateTest {

    @Mock
    private CampaignRepoAdapter campaignRepoAdapter;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private TeamMemberRepoAdapter teamMemberRepoAdapter;

    @Spy
    private List<CampaignFilter> filters;

    @InjectMocks
    private CampaignService campaignService;

    private Campaign existing;
    private CampaignDto inputDto;
    private CampaignDto outputDto;

    @BeforeEach
    public void setUp() {

        existing = new Campaign();
        existing.setId(5L);
        existing.setCreatedBy(100L);
        existing.setTitle("Old Title");
        existing.setDescription("Old Desc");
        existing.setGoal(new BigDecimal("500"));
        existing.setStatus(CampaignStatus.ACTIVE);

        Project project = new Project();
        project.setId(10L);
        project.setOwnerId(200L);
        existing.setProject(project);

        inputDto = CampaignDto.builder()
                .id(5L)
                .createdBy(100L)
                .title("New Title")
                .description("New Desc")
                .goal(new BigDecimal("1000"))
                .status(CampaignStatus.CANCELED)
                .build();

        outputDto = CampaignDto.builder().id(5L).build();

        when(campaignRepoAdapter.getCampaignById(5L)).thenReturn(existing);
    }

    @Test
    public void testUpdateAllFieldsPresentAppliesChanges() {
        when(campaignRepoAdapter.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(campaignMapper.toDto(any())).thenReturn(outputDto);

        CampaignDto result = campaignService.updateCampaign(5L, inputDto, 200L);

        assertThat(existing.getTitle()).isEqualTo("New Title");
        assertThat(existing.getDescription()).isEqualTo("New Desc");
        assertThat(existing.getGoal()).isEqualByComparingTo("1000");
        assertThat(existing.getStatus()).isEqualTo(CampaignStatus.CANCELED);
        assertThat(existing.getUpdatedBy()).isEqualTo(200L);
        assertThat(result).isEqualTo(outputDto);

        verify(campaignRepoAdapter).save(existing);
        verify(campaignMapper).toDto(existing);
    }

    @Test
    void testUpdateSomeFieldsNullLeavesOriginal() {
        Project projectForManager = new Project();
        projectForManager.setId(10L);
        projectForManager.setOwnerId(200L);

        Team teamForManager = new Team();
        teamForManager.setProject(projectForManager);

        TeamMember managerTeamMember = new TeamMember();
        managerTeamMember.setUserId(300L);
        managerTeamMember.setTeam(teamForManager);
        managerTeamMember.setRoles(List.of(TeamRole.MANAGER));

        when(teamMemberRepoAdapter.getByUserId(300L)).thenReturn(List.of(managerTeamMember));

        when(campaignRepoAdapter.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(campaignMapper.toDto(any())).thenReturn(outputDto);

        CampaignDto partial = inputDto.toBuilder()
                .title(null).description(null)
                .goal(null).status(null)
                .build();

        CampaignDto result = campaignService.updateCampaign(5L, partial, 300L);

        assertThat(existing.getTitle()).isEqualTo("Old Title");
        assertThat(existing.getDescription()).isEqualTo("Old Desc");
        assertThat(existing.getGoal()).isEqualByComparingTo("500");
        assertThat(existing.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(existing.getUpdatedBy()).isEqualTo(300L);
        assertThat(existing.getUpdatedAt()).isNull();
        assertThat(result).isEqualTo(outputDto);

        verify(campaignRepoAdapter).save(existing);
        verify(campaignMapper).toDto(existing);
        verify(teamMemberRepoAdapter).getByUserId(300L);
    }

    @Test
    void testUpdateAuthorMismatchThrows() {
        CampaignDto wrong = inputDto.toBuilder().createdBy(999L).build();

        assertThatThrownBy(() ->
                campaignService.updateCampaign(5L, wrong, 200L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot change author");

        verify(campaignRepoAdapter, never()).save(any());
    }
}
