package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;

 public class ValidateMentorBelongsToProjectTest {

    @Test
    public void testProjectNotFound() {
        CreateInternshipDto dto = new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                InternshipStatus.IN_PROGRESS,
                TeamRole.DEVELOPER,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                1L,
                2L,
                Collections.singletonList(3L)
        );
        Mockito.when()
    }


}