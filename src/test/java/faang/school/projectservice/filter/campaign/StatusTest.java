package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {
    private Status status = new Status(CampaignStatus.ACTIVE);
    Campaign campaign = new Campaign();

    @BeforeEach
    void setUp(){
        campaign.setStatus(CampaignStatus.ACTIVE);
    }

    @Test
    public void equal(){
        assertTrue(status.matches(campaign));
    }

    @Test
    public void unequal(){
        status = new Status(CampaignStatus.COMPLETED);
        assertFalse(status.matches(campaign));
    }

    @Test
    public void isNull(){
        status = new Status(null);
        assertTrue(status.matches(campaign));
    }
}