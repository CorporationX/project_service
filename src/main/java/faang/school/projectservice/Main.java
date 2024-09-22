package faang.school.projectservice;

import faang.school.projectservice.controller.VacancyController;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProjectServiceApplication.class, args);
        VacancyController controller = context.getBean(VacancyController.class);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(4L);
        vacancyDto.setName("kukukuk");
        vacancyDto.setDescription("Kak dela");
        vacancyDto.setProjectId(1L);
        vacancyDto.setCreatedBy(10L);
        vacancyDto.setStatus(VacancyStatus.CLOSED); // Предполагается, что VacancyStatus - это перечисление
        vacancyDto.setSalary(1500.00);
        vacancyDto.setWorkSchedule(WorkSchedule.FULL_TIME); // Предполагается, что WorkSchedule - это перечисление
        vacancyDto.setCount(5);
        vacancyDto.setCandidateIds(Arrays.asList(1L, 2L, 3L)); // Список ID кандидатов

        controller.update(vacancyDto);
        context.close();
    }
}
