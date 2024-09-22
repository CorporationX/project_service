package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VacancyService {
    private VacancyRepository vacancyRepository;

    public void createVacancy(VacancyDTO vacancyDTO){
        if(vacancyDTO.getProject() == null){
            throw new IllegalArgumentException("Вакансия должна быть привязана к проекту");
        }
        Kurator  = vacancyRepository.findById(vacancyDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Куратор не найден"));

    }
    public void updateVacancy(VacancyDTO vacancyDTO){

    }
    public void deleteVacancy(Long id){

    }
    public List<Vacancy> findVacancyByName(String Name){
        List<Vacancy> vacancyList = new ArrayList<>();
        return vacancyList;
    }

}
