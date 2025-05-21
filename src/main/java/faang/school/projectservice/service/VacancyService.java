package faang.school.projectservice.service;

import faang.school.projectservice.dto.CandidateDto;
import faang.school.projectservice.dto.CreateCandidateDto;
import faang.school.projectservice.dto.CreateVacancyDto;
import faang.school.projectservice.dto.DetailedVacancyDto;
import faang.school.projectservice.dto.UpdateVacancyDto;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
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
