package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceGetByIdTest {
    @Mock
    private CampaignRepoAdapter campaignRepoAdapter;

    @Mock
    private CampaignMapper campaignMapper;

    @InjectMocks
    private CampaignService campaignService;

    @Test
    public void testGetByIdExists() {
        Campaign existing = new Campaign();
        existing.setId(77L);
        existing.setTitle("Test");
        when(campaignRepoAdapter.getCampaignById(77L)).thenReturn(existing);

        CampaignDto dto = CampaignDto.builder()
                .id(77L)
                .title("Test")
                .build();
        when(campaignMapper.toDto(existing)).thenReturn(dto);

        CampaignDto result = campaignService.getCampaignById(77L);

        assertThat(result).isEqualTo(dto);
        verify(campaignRepoAdapter).getCampaignById(77L);
        verify(campaignMapper).toDto(existing);
    }

    @Test
    public void testGetByIdNotFound() {
        when(campaignRepoAdapter.getCampaignById(123L))
                .thenThrow(new EntityNotFoundException("Not found"));

        assertThatThrownBy(() ->
                campaignService.getCampaignById(123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Not found");

        verify(campaignRepoAdapter).getCampaignById(123L);
        verifyNoInteractions(campaignMapper);
    }
}
