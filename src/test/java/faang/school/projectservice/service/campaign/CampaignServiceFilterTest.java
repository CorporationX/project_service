package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.service.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceFilterTest {

    @Mock
    private CampaignRepoAdapter campaignRepoAdapter;

    @Mock
    private CampaignMapper campaignMapper;

    private List<CampaignFilter> filters;

    @InjectMocks
    private CampaignService campaignService;

    private CampaignDto filterDto;
    private Campaign campaign1;
    private Campaign campaign2;
    private CampaignDto campaignDto1;
    private CampaignDto campaignDto2;

    @BeforeEach
    public void setUp() {
        campaign1 = new Campaign();
        campaign1.setId(1L);
        campaign1.setCreatedAt(LocalDateTime.of(2025, 5, 20, 10, 0));

        campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setCreatedAt(LocalDateTime.of(2025, 5, 18, 10, 0));

        filters = new ArrayList<>();
        ReflectionTestUtils.setField(campaignService, "filters", filters);

        campaignDto1 = CampaignDto.builder().id(1L).createdAt(campaign1.getCreatedAt()).build();
        campaignDto2 = CampaignDto.builder().id(2L).createdAt(campaign2.getCreatedAt()).build();

        filterDto = CampaignDto.builder().build();

        when(campaignRepoAdapter.getAll()).thenReturn(List.of(campaign1, campaign2));
    }

    @Test
    public void testGetWithFiltersWithNoApplies(){
        when(campaignMapper.toDto(campaign1)).thenReturn(campaignDto1);
        when(campaignMapper.toDto(campaign2)).thenReturn(campaignDto2);

        List<CampaignDto> result = campaignService.getCampaignDtoWithFilters(filterDto);

        assertThat(result).containsExactly(campaignDto1, campaignDto2);
        verify(campaignRepoAdapter).getAll();
        verify(campaignMapper).toDto(campaign1);
        verify(campaignMapper).toDto(campaign2);
    }

    @Test
    public void getWithFiltersWithSomeFilters(){
        when(campaignMapper.toDto(campaign2)).thenReturn(campaignDto2);

        CampaignFilter f1 = mock(CampaignFilter.class);
        CampaignFilter f2 = mock(CampaignFilter.class);

        when(f1.isApplicable(filterDto)).thenReturn(false);
        when(f2.isApplicable(filterDto)).thenReturn(true);
        when(f2.apply(any(Stream.class), eq(filterDto)))
                .thenAnswer(inv -> ((Stream<Campaign>)inv.getArgument(0))
                        .filter(c -> c.getId().equals(2L)));

        filters.add(f1);
        filters.add(f2);

        List<CampaignDto> result = campaignService.getCampaignDtoWithFilters(filterDto);

        assertThat(result).containsExactly(campaignDto2);

        verify(f1).isApplicable(filterDto);
        verify(f2).isApplicable(filterDto);
        verify(f2).apply(any(Stream.class), eq(filterDto));
    }
}
