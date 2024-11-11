package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CampaignMapperTest {

    private final CampaignMapper campaignMapper = Mappers.getMapper(CampaignMapper.class);

    private static final long ID = 1L;
    private static final String TITLE = "title";
    private static final String NEW_TITLE = "newTitle";
    private static final String DESCRIPTION = "description";
    private static final String NEW_DESCRIPTION = "newDescription";
    private static final BigDecimal GOAL = BigDecimal.valueOf(100);
    private static final BigDecimal NEW_GOAL = BigDecimal.valueOf(300);
    private static final BigDecimal AMOUNT_RAISED = BigDecimal.valueOf(200);
    private static final BigDecimal NEW_AMOUNT_RAISED = BigDecimal.valueOf(400);
    private CampaignUpdateDto campaignUpdateDto;
    private Campaign campaign;
    private Project project;

    @BeforeEach
    public void init() {
        project = Project.builder()
                .id(ID)
                .build();
        campaign = Campaign.builder()
                .id(ID)
                .status(CampaignStatus.ACTIVE)
                .title(TITLE)
                .description(DESCRIPTION)
                .project(project)
                .goal(GOAL)
                .amountRaised(AMOUNT_RAISED)
                .build();
        campaignUpdateDto = CampaignUpdateDto.builder()
                .title(NEW_TITLE)
                .description(NEW_DESCRIPTION)
                .build();
    }

    @Test
    @DisplayName("Success mapping Campaign to CampaignDto")
    public void whenCampaignToDtoThenReturnCampaignDto() {
        CampaignDto result = campaignMapper.toDto(campaign);

        assertNotNull(result);
        assertEquals(campaign.getId(), result.getId());
        assertEquals(campaign.getTitle(), result.getTitle());
        assertEquals(campaign.getDescription(), result.getDescription());
        assertEquals(campaign.getStatus(), result.getStatus());
        assertEquals(campaign.getProject().getId(), result.getProjectId());
    }

    @Test
    @DisplayName("Success mapping List<Campaign> to List<CampaignDto>")
    public void whenListCampaignsToDtosThenReturnListCampaignDtos() {
        List<CampaignDto> result = campaignMapper.toDtos(List.of(campaign));

        assertNotNull(result);
        assertEquals(campaign.getId(), result.get(0).getId());
        assertEquals(campaign.getTitle(), result.get(0).getTitle());
        assertEquals(campaign.getDescription(), result.get(0).getDescription());
        assertEquals(campaign.getStatus(), result.get(0).getStatus());
        assertEquals(campaign.getProject().getId(), result.get(0).getProjectId());
    }

    @Test
    @DisplayName("Success mapping all CampaignUpdateDto fields to existing Campaign")
    public void whenUpdateCampaignFromDtoWithAllFieldsThenUpdateAllExistingCampaignFields() {
        campaignUpdateDto.setGoal(NEW_GOAL);
        campaignUpdateDto.setAmountRaised(NEW_AMOUNT_RAISED);
        campaignUpdateDto.setStatus(CampaignStatus.COMPLETED);
        campaignMapper.updateCampaignFromDto(campaignUpdateDto, campaign);

        assertEquals(campaignUpdateDto.getTitle(), campaign.getTitle());
        assertEquals(campaignUpdateDto.getDescription(), campaign.getDescription());
        assertEquals(campaignUpdateDto.getGoal(), campaign.getGoal());
        assertEquals(campaignUpdateDto.getAmountRaised(), campaign.getAmountRaised());
        assertEquals(campaignUpdateDto.getStatus(), campaign.getStatus());
    }

    @Test
    @DisplayName("Success mapping some CampaignUpdateDto fields to existing Campaign")
    public void whenUpdateCampaignFromDtoWithSomeFieldsThenUpdateSomeExistingCampaignFields() {
        campaignMapper.updateCampaignFromDto(campaignUpdateDto, campaign);

        assertEquals(campaignUpdateDto.getTitle(), campaign.getTitle());
        assertEquals(campaignUpdateDto.getDescription(), campaign.getDescription());
        assertEquals(GOAL, campaign.getGoal());
        assertEquals(AMOUNT_RAISED, campaign.getAmountRaised());
        assertEquals(CampaignStatus.ACTIVE, campaign.getStatus());
    }
}