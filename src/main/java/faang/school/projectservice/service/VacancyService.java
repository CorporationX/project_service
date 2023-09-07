package faang.school.projectservice.service;

//@Service
//@RequiredArgsConstructor
//public class VacancyService {
//    private static final int VACANCY_PLACES = 5;
//    private final ProjectRepository projectRepository;
//    private final VacancyRepository vacancyRepository;
//    private final TeamMemberRepository teamMemberRepository;
//    private final VacancyMapper vacancyMapper;
//    private final VacancyFilter filter;
//
//    public VacancyDto create(VacancyDto vacancyDto) {
//        validateVacancy(vacancyDto);
//        Vacancy saveVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
//        return vacancyMapper.toDto(saveVacancy);
//    }
//
//    public VacancyDto update(VacancyDto vacancyDto) {
//        if (vacancyDto.getStatus() == VacancyStatus.CLOSED) {
//            Vacancy vacancyToUpdate = vacancyRepository
//                    .findById(vacancyDto.getId())
//                    .orElseThrow(() -> new EntityNotFoundException("Vacancy with this id doesn't exist"));
//            if (vacancyToUpdate.getCandidates().size() < VACANCY_PLACES) {
//                throw new IllegalArgumentException("There are not enough candidates for this vacancy to close");
//            }
//        }
//        validateVacancy(vacancyDto);
//        Vacancy saveVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
//        return vacancyMapper.toDto(saveVacancy);
//    }
//
//    public void delete(long id) {
//        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
//        if (vacancy.isPresent()) {
//            vacancy.get().setStatus(VacancyStatus.DELETED);
//            vacancyRepository.delete(vacancy.get());
//        }
//        vacancy.orElseThrow(() -> new EntityNotFoundException("This vacancy doesn't exist"));
//    }
//
//    public List<VacancyDto> getByFilters(VacancyFilterDto vacancyFilterDto) {
//        if (filter.isApplicable(vacancyFilterDto)) {
//            throw new DataValidationException("Vacancy name doesn't exist");
//        }
//        Stream<Vacancy> streamVacancy = vacancyRepository.findById(vacancyFilterDto.getId()).stream();
//        List<Vacancy> filteredVacancy = filter.apply(streamVacancy, vacancyFilterDto).toList();
//        return filteredVacancy.stream()
//                .map(vacancy -> vacancyMapper.toDto(vacancy))
//                .collect(Collectors.toList());
//    }
//
//    public VacancyDto findVacancyById(long vacancyId) {
//        Optional<Vacancy> vacancyById = vacancyRepository.findById(vacancyId);
//        if (vacancyById.isEmpty()) {
//            throw new EntityNotFoundException("Vacancy doesn't exist");
//        }
//        return vacancyMapper.toDto(vacancyById.get());
//    }
//
//    private void validateVacancy(VacancyDto vacancyDto) {
//        if (!vacancyRepository.existsById(vacancyDto.getId())) {
//            throw new EntityNotFoundException("Vacancy doesn't exist");
//        }
//        if (projectRepository.existsById(vacancyDto.getProjectId())) {
//            throw new EntityNotFoundException("Project doesn't exist");
//        }
//        TeamMember manager = teamMemberRepository.findById(vacancyDto.getCreatedBy());
//        List<TeamRole> roles = manager.getRoles();
//        if (!roles.contains(TeamRole.MANAGER)) {
//            throw new DataValidationException("This vacancy manager has wrong role");
//        }
//    }
//}
