package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CampaignMapperTest {

    private final  CampaignMapper campaignMapper = Mappers.getMapper(CampaignMapper.class);

    @Test
    public void toDto(){
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("title");
        campaign.setDescription("description");
        campaign.setGoal(BigDecimal.valueOf(5000));
        campaign.setAmountRaised(BigDecimal.valueOf(1000));
        campaign.setCurrency(Currency.USD);
        campaign.setStatus(CampaignStatus.ACTIVE);

        Project project = new Project();
        project.setId(10L);
        campaign.setProject(project);

        CampaignDto campaignDto = campaignMapper.toDto(campaign);

        assertEquals(campaign.getId(),campaignDto.getId());
        assertEquals(campaign.getTitle(),campaignDto.getTitle());
        assertEquals(campaign.getDescription(),campaignDto.getDescription());
        assertEquals(campaign.getGoal(),campaignDto.getGoal());
        assertEquals(campaign.getAmountRaised(),campaignDto.getAmountRaised());
        assertEquals(campaign.getCurrency(),campaignDto.getCurrency());
        assertEquals(campaign.getStatus(),campaignDto.getStatus());
        assertEquals(project.getId(),campaignDto.getProjectId());
    }

    @Test
    public void toEntity(){
        CampaignDto campaignDto = new CampaignDto();

        campaignDto.setTitle("title");
        campaignDto.setDescription("description");
        campaignDto.setGoal(BigDecimal.valueOf(5000));
        campaignDto.setAmountRaised(BigDecimal.valueOf(0));
        campaignDto.setCurrency(Currency.USD);
        campaignDto.setStatus(CampaignStatus.ACTIVE);
        campaignDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        campaignDto.setUpdatedAt(LocalDateTime.now());

        Project project = new Project();
        project.setId(10L);

        Campaign campaign = campaignMapper.toEntity(campaignDto,project,CampaignStatus.ACTIVE);

        assertEquals(campaignDto.getTitle(),campaign.getTitle());
        assertEquals(campaignDto.getDescription(),campaign.getDescription());
        assertEquals(campaignDto.getGoal(),campaign.getGoal());
        assertEquals(campaignDto.getAmountRaised(),campaign.getAmountRaised());
        assertEquals(campaignDto.getCurrency(),campaign.getCurrency());
        assertEquals(campaignDto.getStatus(),campaign.getStatus());
        assertEquals(campaign.getProject().getId(),project.getId());
    }

    @Test
    void updateEntity() {
        String updateTitle = "Updated title";
        String oldDesc = "Old desc";

        Campaign campaign = new Campaign();
        campaign.setTitle("Old title");
        campaign.setDescription("Old desc");

        CampaignDto dto = new CampaignDto();
        dto.setTitle(updateTitle);
        dto.setDescription(null);

        Project project = new Project();
        project.setId(123L);

        campaignMapper.updateEntity(campaign, dto, project, 77L, CampaignStatus.ACTIVE);

        assertEquals(oldDesc, campaign.getDescription());
        assertEquals(updateTitle, campaign.getTitle());
        assertEquals(77L, campaign.getUpdatedBy());
        assertEquals(CampaignStatus.ACTIVE, campaign.getStatus());
        assertEquals(project, campaign.getProject());
        assertNotNull(campaign.getUpdatedAt());
    }
}