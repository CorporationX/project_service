package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignByStatusFilter;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private CampaignFilterDto filterDto;
    private Campaign campaign1;
    private Campaign campaign2;
    private CampaignDto campaignDto1;
    private CampaignDto campaignDto2;

    @BeforeEach
    public void setUp() {
        campaign1 = new Campaign();
        campaign1.setId(1L);
        campaign1.setCreatedAt(LocalDateTime.of(2025, 5, 20, 10, 0));
        campaign1.setStatus(CampaignStatus.ACTIVE);

        campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setCreatedAt(LocalDateTime.of(2025, 5, 18, 10, 0));
        campaign2.setStatus(CampaignStatus.CANCELED);

        filters = new ArrayList<>();

        ReflectionTestUtils.setField(campaignService, "filters", filters);

        campaignDto1 = CampaignDto.builder().id(1L).createdAt(campaign1.getCreatedAt()).build();
        campaignDto2 = CampaignDto.builder().id(2L).createdAt(campaign2.getCreatedAt()).build();

        filterDto = CampaignFilterDto.builder().build();
    }

    @Test
    public void testGetWithFiltersWithNoApplies() {
        List<Campaign> campaignsFromDb = Arrays.asList(campaign1, campaign2);

        Page<Campaign> mockedPage = new PageImpl<>(campaignsFromDb);

        when(campaignRepoAdapter.getAllPages(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockedPage);

        when(campaignMapper.toDto(campaign1)).thenReturn(campaignDto1);
        when(campaignMapper.toDto(campaign2)).thenReturn(campaignDto2);


        Pageable defaultPageable = Pageable.unpaged();
        Page<CampaignDto> resultPage = campaignService.
                getCampaignDtoWithFilters(filterDto, defaultPageable);


        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent())
                .containsExactlyInAnyOrder(campaignDto1, campaignDto2);

        verify(campaignRepoAdapter).getAllPages(any(Specification.class), any(Pageable.class));
        verify(campaignMapper).toDto(campaign1);
        verify(campaignMapper).toDto(campaign2);
    }

    @Test
    public void getWithFiltersWithStatusFilter() {
        CampaignFilterDto statusFilterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE).build();

        CampaignByStatusFilter mockStatusFilter = mock(CampaignByStatusFilter.class);

        filters.add(mockStatusFilter);

        when(mockStatusFilter.isApplicable(statusFilterDto)).thenReturn(true);
        when(mockStatusFilter.apply(statusFilterDto)).thenReturn(
                (root, query, criteriaBuilder)
                        -> criteriaBuilder.equal(root.get("status"), CampaignStatus.ACTIVE)
        );

        List<Campaign> filteredCampaignsFromDb = List.of(campaign1);
        Page<Campaign> mockedPage = new PageImpl<>(filteredCampaignsFromDb);

        when(campaignRepoAdapter.getAllPages(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockedPage);

        when(campaignMapper.toDto(campaign1)).thenReturn(campaignDto1);

        Pageable defaultPageable = Pageable.unpaged();

        Page<CampaignDto> resultPage = campaignService
                .getCampaignDtoWithFilters(statusFilterDto, defaultPageable);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).containsExactly(campaignDto1);

        verify(mockStatusFilter).isApplicable(statusFilterDto);
        verify(mockStatusFilter).apply(statusFilterDto);
        verify(campaignRepoAdapter)
                .getAllPages(any(Specification.class), any(Pageable.class));
        verify(campaignMapper).toDto(campaign1);
        verify(campaignRepoAdapter, never()).getAll();
    }
}
