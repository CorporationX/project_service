package faang.school.projectservice;

import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.model.enums.CampaignStatus;
import faang.school.projectservice.service.CampaignService;
import faang.school.projectservice.util.PostgreSQLContainerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class CampaignServiceIntegrationTest extends PostgreSQLContainerConfig {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE campaign RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE project RESTART IDENTITY CASCADE;");
    }

    @Test
    @Sql(scripts = "/data-scripts/campaign-test-filter-data.sql")
    void testFilter() {
        CampaignFilterDto campaignFilterByStatus = new CampaignFilterDto();
        campaignFilterByStatus.setStatus(CampaignStatus.ACTIVE);

        List<CampaignDto> byStatus = campaignService.getByFilter(campaignFilterByStatus);
        Assertions.assertEquals(2, byStatus.size());

        Assertions.assertTrue(byStatus.stream().anyMatch(campaign -> campaign.getId() == 1),
                "Expected campaign with ID 1 in the result");
        Assertions.assertTrue(byStatus.stream().anyMatch(campaign -> campaign.getId() == 2),
                "Expected campaign with ID 2 in the result");

        CampaignFilterDto campaignFilterByCreator = new CampaignFilterDto();
        campaignFilterByCreator.setCreatedBy(2L);

        List<CampaignDto> byCreator = campaignService.getByFilter(campaignFilterByCreator);
        Assertions.assertEquals(2, byCreator.size());

        Assertions.assertTrue(byCreator.stream().anyMatch(campaign -> campaign.getId() == 2),
                "Expected campaign with ID 2 in the result");
        Assertions.assertTrue(byCreator.stream().anyMatch(campaign -> campaign.getId() == 3),
                "Expected campaign with ID 3 in the result");

        CampaignFilterDto campaignFilterByCreatedAfter = new CampaignFilterDto();
        campaignFilterByCreatedAfter.setCreatedAtAfter(LocalDateTime.of(2020, 1, 1, 1, 1, 1));

        List<CampaignDto> byCreatedAfter = campaignService.getByFilter(campaignFilterByCreatedAfter);
        Assertions.assertEquals(3, byCreatedAfter.size());

        Assertions.assertTrue(byCreatedAfter.stream().anyMatch(campaign -> campaign.getId() == 1),
                "Expected campaign with ID 1 in the result");
        Assertions.assertTrue(byCreatedAfter.stream().anyMatch(campaign -> campaign.getId() == 2),
                "Expected campaign with ID 2 in the result");
        Assertions.assertTrue(byCreatedAfter.stream().anyMatch(campaign -> campaign.getId() == 3),
                "Expected campaign with ID 3 in the result");

        CampaignFilterDto complexCampaignFilter = new CampaignFilterDto();
        complexCampaignFilter.setCreatedAtAfter(LocalDateTime.of(2023, 2, 1, 1, 1, 1));
        complexCampaignFilter.setStatus(CampaignStatus.ACTIVE);

        List<CampaignDto> byComplexFilter = campaignService.getByFilter(complexCampaignFilter);
        Assertions.assertEquals(1, byComplexFilter.size());

        Assertions.assertTrue(byComplexFilter.stream().anyMatch(campaign -> campaign.getId() == 2),
                "Expected campaign with ID 2 in the result");
    }
}
