package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class VacancyController {
    private VacancyService vacancyService;

    public ResponseEntity<String> createVacancy(@RequestBody VacancyDTO vacancyDTO){
        vacancyService.createVacancy(vacancyDTO);
        return ResponseEntity.ok("Вакансия создана");
    }
    public ResponseEntity<String> updateVacancy(@RequestBody VacancyDTO vacancyDTO){
        vacancyService.updateVacancy(vacancyDTO);
        return ResponseEntity.ok("Вакансия обновлена");
    }
    public ResponseEntity<String> deleteVacancy(@PathVariable Long id){
        vacancyService.deleteVacancy(id);
        return ResponseEntity.ok("Вакансия удалена");
    }

    public List<Vacancy> findVacancyByName(@PathVariable String name){
        return vacancyService.findVacancyByName(name);
    }
}
