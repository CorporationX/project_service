package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @InjectMocks
    private CampaignService campaignService;

    private Campaign existing;

    @Captor
    private ArgumentCaptor<Campaign> campaignCaptor;

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
        when(campaignRepoAdapter.save(any(Campaign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignMapper.toDto(any(Campaign.class)))
                .thenReturn(CampaignDto.builder().id(7L).build());
    }

    @Test
    public void testSoftDeleteAsCreator(){
        CampaignDto result = campaignService.softDelete(7L, 100L);

        verify(campaignRepoAdapter).save(campaignCaptor.capture());

        Campaign capturedCampaign = campaignCaptor.getValue();

        assertThat(capturedCampaign.getStatus()).isEqualTo(CampaignStatus.CANCELED);
        assertThat(capturedCampaign.getUpdatedBy()).isEqualTo(100L);
        assertThat(capturedCampaign.getUpdatedAt()).isNull();

        assertThat(result.getId()).isEqualTo(7L);

        verify(campaignMapper).toDto(capturedCampaign);
    }

    @Test
    public void testSoftDeleteAsOwner(){
        CampaignDto result = campaignService.softDelete(7L, 200L);

        assertThat(existing.getStatus()).isEqualTo(CampaignStatus.CANCELED);
        assertThat(existing.getUpdatedBy()).isEqualTo(200L);
        assertThat(existing.getUpdatedAt()).isNull();
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
