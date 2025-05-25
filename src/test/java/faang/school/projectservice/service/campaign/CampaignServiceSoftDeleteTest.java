package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.service.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceSoftDeleteTest {

    @Mock
    private CampaignRepoAdapter campaignRepoAdapter;

    @Mock
    private CampaignMapper campaignMapper;

    @Spy
    private List<CampaignFilter> filters = new ArrayList<>();

    @InjectMocks
    private CampaignService campaignService;

    private Campaign existing;
    private CampaignDto outputDto;

    @BeforeEach
    void setUp() {
        existing = new Campaign();
        existing.setId(7L);
        existing.setCreatedBy(100L);

        Project project = new Project();
        project.setId(10L);
        project.setOwnerId(200L);

        existing.setProject(project);
        existing.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepoAdapter.getCampaignById(7L)).thenReturn(existing);
    }

    @Test
    public void testSoftDeleteAsCreator(){
        when(campaignRepoAdapter.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(campaignMapper.toDto(any()))
                .thenReturn(CampaignDto.builder().id(7L).build());

        CampaignDto result = campaignService.softDelete(7L, 100L);

        assertThat(existing.getStatus()).isEqualTo(CampaignStatus.CANCELED);
        assertThat(existing.getUpdatedBy()).isEqualTo(100L);
        assertThat(existing.getUpdatedAt()).isNotNull();
        assertThat(result.getId()).isEqualTo(7L);

        verify(campaignRepoAdapter).save(existing);
        verify(campaignMapper).toDto(existing);
    }

    @Test
    public void testSoftDeleteAsOwner(){
        when(campaignRepoAdapter.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(campaignMapper.toDto(any()))
                .thenReturn(CampaignDto.builder().id(7L).build());

        CampaignDto result = campaignService.softDelete(7L, 200L);

        assertThat(existing.getStatus()).isEqualTo(CampaignStatus.CANCELED);
        assertThat(existing.getUpdatedBy()).isEqualTo(200L);
        assertThat(existing.getUpdatedAt()).isNotNull();
        assertThat(result.getId()).isEqualTo(7L);

        verify(campaignRepoAdapter).save(existing);
        verify(campaignMapper).toDto(existing);
    }

    @Test
    public void testSoftDeleteNotAllowed(){
        assertThatThrownBy(() ->
                campaignService.softDelete(7L, 300L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not allowed to delete campaign");

        verify(campaignRepoAdapter, never()).save(any());
    }
}
