package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VacancyService {
    private VacancyRepository vacancyRepository;

    public void createVacancy(VacancyDTO vacancyDTO){
        if(vacancyDTO.getProject() == null){
            throw new IllegalArgumentException("Вакансия должна быть привязана к проекту");
        }
        Long kurator  = vacancyDTO.getCreatedBy();
        if(kurator == null){
            throw new IllegalArgumentException("Куратор не найден");
        }
        if(vacancyDTO.)
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
