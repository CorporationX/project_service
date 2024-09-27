package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;

@Service
@AllArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;

    public VacancyDTO createVacancy(VacancyDTO vacancyDTO){
        createVacancyValidation(vacancyDTO);
        vacancyRepository.save(vacancyMapper.vacancyDTOToVacancy(vacancyDTO));
        return vacancyDTO;
    }

    public void updateVacancy(VacancyDTO vacancyDTO){
        long acceptedCandidates = vacancyDTO.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(ACCEPTED)).count();
        if(vacancyDTO.getCount() == acceptedCandidates){
            vacancyRepository.findById(vacancyDTO.getId()).orElseThrow().setStatus(VacancyStatus.valueOf("CLOSED"));
        }
        vacancyRepository.flush();
    }

    public void deleteVacancy(Long id){
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        vacancy.orElseThrow().getCandidates().removeIf(candidate -> !candidate.getCandidateStatus().equals(ACCEPTED));
        vacancyRepository.deleteById(id);
    }

    public List<Vacancy> findVacancyByName(String name){
        List<Vacancy> vacancyList = vacancyRepository.findByName(name);
        return vacancyList;
    }

    public Vacancy getVacancyById(Long id){
        return vacancyRepository.findById(id).orElseThrow();
    }

    public void createVacancyValidation(VacancyDTO vacancyDTO){
        if(vacancyDTO.getProject() == null){
            throw new IllegalArgumentException("Вакансия должна быть привязана к проекту");
        }
        if(vacancyDTO.getCreatedBy() == null){
            throw new IllegalArgumentException("У вакансии должен быть куратор");
        }
        TeamMember teamMember = teamMemberRepository.findById(vacancyDTO.getCreatedBy());
        if(!teamMember.getRoles().contains("MANAGER") || !teamMember.getRoles().contains("OWNER")){
            throw new IllegalArgumentException("Куратором вакансии может быть член команды с ролью MANAGER");
        }
    }

}
