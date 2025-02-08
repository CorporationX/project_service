package faang.school.projectservice.validation;

import faang.school.projectservice.model.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {

    @InjectMocks
    private VacancyValidator validator;
    private final Vacancy vacancy = Vacancy.builder()
            .id(1L)
            .count(3)
            .status(VacancyStatus.OPEN)
            .candidates(List.of(new Candidate(), new Candidate(), new Candidate()))
            .build();
    private final TeamMember creator = TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build();

    @Test
    public void validateCreateVacancy_Success() {
        validator.validateCreateVacancy(vacancy, Optional.of(creator));
    }

    @Test
    public void validateCreateAndUpdateVacancy_emptyCreator() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateCreateVacancy(vacancy, Optional.empty()));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUpdateVacancy(vacancy, Optional.empty()));
    }

    @Test
    public void validateCreateAndUpdateVacancy_CreatorIsNotManagerOrOwner() {
        creator.setRoles(Collections.emptyList());
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateCreateVacancy(vacancy, Optional.of(creator)));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUpdateVacancy(vacancy, Optional.of(creator)));
    }

    @Test
    public void validateCreateVacancy_countLessThanOne() {
        vacancy.setCount(0);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateCreateVacancy(vacancy, Optional.of(creator))
        );
    }

    @Test
    public void validateUpdateVacancy_Success() {
        validator.validateUpdateVacancy(vacancy, Optional.of(creator));
    }

    @Test
    public void validateUpdateVacancy_StatusIsClosed() {
        vacancy.setStatus(VacancyStatus.CLOSED);

        validator.validateUpdateVacancy(vacancy, Optional.of(creator));

        vacancy.setCount(1);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUpdateVacancy(vacancy, Optional.of(creator))
        );
    }

    @Test
    public void validateRemoveVacancy_Success() {
        vacancy.setCandidates(Collections.emptyList());
        validator.validateRemoveVacancy(Optional.of(vacancy));
    }

    @Test
    public void validateRemoveVacancy_emptyTarget() {
        vacancy.setCandidates(Collections.emptyList());
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateRemoveVacancy(Optional.empty())
        );
    }

    @Test
    public void validateRemoveVacancy_emptyCandidates() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> validator.validateRemoveVacancy(Optional.of(vacancy))
        );
    }
}
