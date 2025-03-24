package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerTest {
    private Owner owner = new Owner(1L);
    Campaign campaign = new Campaign();

    @BeforeEach
    void setUp() {
        campaign.setCreatedBy(1L);
    }

    @Test
    public void equal() {
        assertTrue(owner.matches(campaign));
    }

    @Test
    public void unequal() {
        owner = new Owner(2L);
        assertFalse(owner.matches(campaign));
    }

    @Test
    public void isNull() {
        owner = new Owner(null);
        assertTrue(owner.matches(campaign));
    }
}