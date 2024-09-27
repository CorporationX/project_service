package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController("/vacancy")
public class VacancyController {
    private VacancyService vacancyService;

    @PostMapping("/create")
    public ResponseEntity<VacancyDTO> createVacancy(@RequestBody VacancyDTO vacancyDTO){
        vacancyService.createVacancy(vacancyDTO);
        return ResponseEntity.ok(vacancyDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateVacancy(@RequestBody VacancyDTO vacancyDTO){
        vacancyService.updateVacancy(vacancyDTO);
        return ResponseEntity.ok("Вакансия обновлена");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVacancy(@PathVariable Long id){
        vacancyService.deleteVacancy(id);
        return ResponseEntity.ok("Вакансия удалена");
    }

    @GetMapping("/getList/{name}")
    public List<Vacancy> findVacancyByName(@PathVariable String name){
        return vacancyService.findVacancyByName(name);
    }

    @GetMapping("/getVacancy/{id}")
    public Vacancy getVacancyById(@PathVariable Long id){
        return vacancyService.getVacancyById(id);
    }
}
