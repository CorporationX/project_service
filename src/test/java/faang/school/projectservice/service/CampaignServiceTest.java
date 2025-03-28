package faang.school.projectservice.service;

import faang.school.projectservice.adapter.CampaignRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.specification.CampaignSpecification;
import faang.school.projectservice.validator.CampaignValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;
    @Spy
    private CampaignMapper mapper = Mappers.getMapper(CampaignMapper.class);
    @Mock
    private CampaignRepositoryAdapter campaignRepositoryAdapter;
    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;
    @Mock
    private CampaignSpecification specification;
    @Mock
    private CampaignValidator validator;
    @Mock
    private UserContext userContext;

    CampaignDto requestDto = CampaignDto.builder()
            .title("title")
            .description("description")
            .goal(BigDecimal.valueOf(1))
            .status(CampaignStatus.ACTIVE)
            .projectId(1L)
            .currency(Currency.USD)
            .build();

    @Test
    @DisplayName("Positive test for the create campaign method")
    void createCampaignTest() {
        CampaignDto expectResponseDto = CampaignDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .goal(BigDecimal.valueOf(1))
                .amountRaised(BigDecimal.valueOf(0))
                .status(CampaignStatus.ACTIVE)
                .projectId(1L)
                .currency(Currency.USD)
                .createdBy(1L)
                .build();

        when(userContext.getUserId()).thenReturn(1L);
        when(campaignRepositoryAdapter.save(any())).thenAnswer(invocationOnMock -> {
            Campaign argument = invocationOnMock.getArgument(0);
            argument.setId(1L);
            return argument;
        });
        when(projectRepositoryAdapter.getById(1L)).thenReturn(Project.builder().id(1L).build());
        assertEquals(expectResponseDto, campaignService.createCampaign(requestDto));
    }

    @Test
    void updateCampaignTest() {
        Campaign campaignFromDB = new Campaign();
        campaignFromDB.setId(1L);
        campaignFromDB.setTitle("some title");
        campaignFromDB.setDescription("some description");
        campaignFromDB.setGoal(BigDecimal.valueOf(2));
        campaignFromDB.setAmountRaised(BigDecimal.valueOf(2));
        campaignFromDB.setStatus(CampaignStatus.ACTIVE);
        campaignFromDB.setProject(Project.builder().id(1L).build());
        campaignFromDB.setCurrency(Currency.EUR);
        campaignFromDB.setCreatedBy(1L);

    }

}
