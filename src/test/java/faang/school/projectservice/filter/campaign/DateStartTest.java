package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateStartTest {
    private DateStart dateStart = new DateStart(LocalDateTime.parse("2025-03-20T00:00:00"));
    Campaign campaign = new Campaign();

    @BeforeEach
    void setUp() {
        campaign.setCreatedAt(LocalDateTime.parse("2025-03-21T00:00:00"));
    }

    @Test
    public void equal() {
        assertTrue(dateStart.matches(campaign));
    }

    @Test
    public void unequal() {
        dateStart = new DateStart(LocalDateTime.parse("2025-03-22T00:00:00"));
        assertFalse(dateStart.matches(campaign));
    }

    @Test
    public void isNull() {
        dateStart = new DateStart(null);
        assertTrue(dateStart.matches(campaign));
    }
}