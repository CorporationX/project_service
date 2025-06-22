package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.candidate.CreateCandidateDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.DetailedVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VacancyService {
    DetailedVacancyDto create(CreateVacancyDto dto);

    DetailedVacancyDto update(Long id, UpdateVacancyDto dto);

    CandidateDto addCandidate(Long id, CreateCandidateDto dto);

    DetailedVacancyDto close(Long id);

    DetailedVacancyDto getById(Long id);

    Page<VacancyDto> getAll(VacancyFilterDto filter, Pageable pageable);
}
