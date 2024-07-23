package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyValidator vacancyValidator;

    @Spy
    private VacancyMapperImpl vacancyMapper;

    VacancyDto vacancy;

    @BeforeEach
    public void init() {
        vacancy = new VacancyDto();
        vacancy.setName("Google");
        vacancy.setCount(3);
    }

    @Test
    public void testCreateVacancy() {
        vacancyService.createVacancy(vacancy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).
                save(vacancyMapper.toEntity(vacancy));
    }
}
