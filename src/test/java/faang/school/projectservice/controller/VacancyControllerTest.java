package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class VacancyControllerTest {
    @InjectMocks
    private VacancyController vacancyController;
    @Mock
    private VacancyService vacancyService;
    private VacancyDto vacancyDto;
    private Long vacancyId;
    private VacancyFilterDto firstVacancyFilterDto;
    private VacancyFilterDto secondVacancyFilterDto;

    @BeforeEach
    public void setUp() {
        vacancyId = 1L;
        vacancyDto = VacancyDto
                .builder()
                .build();

    }
}
