package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

class InternshipServiceTest {

    @Test
    void internshipCreation() {
    }

    @Test
    void testInternshipBusinessValidationFirst() {
        InternshipDto internshipDto = InternshipDto.builder().id(4L).projectId(3L)
                .mentorId(4L).internsId(List.of(3L, 5L)).startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now()).status()
                .build();
        when()
        assertThows()
    }
    @Test
    void testInternshipBusinessValidationSecond() {
    }
    @Test
    void testInternshipBusinessValidationThird() {
    }@Test
    void testInternshipBusinessValidationForth() {
    }



    @Test
    void gettingAllInternshipsAccordingToFilters() {
    }

    @Test
    void gettingAllInternships() {
    }

    @Test
    void getInternshipById() {
    }

    @Test
    void updateInternship() {
    }

    @Test
    void testInternshipCreation() {
    }

    @Test
    void testUpdateInternship() {
    }

    @Test
    void updateInternBeforeInternshipEnd() {
    }

    @Test
    void deleteIntern() {
    }

    @Test
    void testGettingAllInternshipsAccordingToFilters() {
    }

    @Test
    void testGettingAllInternships() {
    }

    @Test
    void testGetInternshipById() {
    }
}