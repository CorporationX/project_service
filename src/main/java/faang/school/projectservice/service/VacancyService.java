package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.*;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.multipartfile.CustomMultipartFile;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.vacancy.ValidatorVacancy;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class VacancyService {
    private static final int MAX_IMAGE_SIDE_SIZE = 512;

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final AmazonS3Service amazonS3Service;
    private final UserService userService;
    private final ValidatorVacancy validatorVacancy;
    private final UserContext userContext;
    private final CandidateRepository candidateRepository;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyCoverDto addVacancyCover(Long vacancyId, MultipartFile file) {
        Vacancy vacancy = findById(vacancyId);
        UserDto userDto = userService.getUser(vacancy.getProject().getOwnerId());

        userService.checkUser(userDto.id());

        MultipartFile resizedFile = resizeImage(file);
        String key = amazonS3Service.uploadFile(resizedFile, "vacancy");
        vacancy.setCoverImageKey(key);
        return vacancyMapper.toCoverDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyCoverDto deleteVacancyCover(Long vacancyId) {
        Vacancy vacancy = findById(vacancyId);
        UserDto userDto = userService.getUser(vacancy.getProject().getOwnerId());

        userService.checkUser(userDto.id());
        String key = vacancy.getCoverImageKey();

        amazonS3Service.deleteFile(key);
        vacancy.setCoverImageKey(null);

        return vacancyMapper.toCoverDto(vacancy);
    }

    private Vacancy findById(@NotNull Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена"));
    }

    private MultipartFile resizeImage(MultipartFile file) {
        var outputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(changeSize(file, MAX_IMAGE_SIDE_SIZE), "jpg", outputStream);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }

        return new CustomMultipartFile(file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                outputStream.toByteArray());
    }

    private BufferedImage changeSize(MultipartFile file, int maxSize) throws IOException {
        int newHeight;
        int newWidth;

        BufferedImage image = ImageIO.read(file.getInputStream());

        int originalHeight = image.getHeight();
        int originalWidth = image.getWidth();

        int scaleSize = (int) (originalWidth * ((double) maxSize / originalHeight));

        newHeight = (originalHeight > originalWidth) ? maxSize : scaleSize;
        newWidth = (originalHeight > originalWidth) ? scaleSize : maxSize;

        var resizedImage = new BufferedImage(
                newWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    public VacancyDto createVacancy(VacancyCreateDto dto) {
        validatorVacancy.validatorTeamRole(dto.getPosition());
        validatorVacancy.validatorProjectAvailability(dto.getProjectId());
        validatorVacancy.checkRoleCurator(dto);

        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy = vacancyRepository.save(vacancy);

        Long id = vacancy.getId();
        VacancyDto vacancyDto = vacancyMapper.toDtoVacancy(vacancy);
        vacancyDto.setId(id);
        return vacancyDto;
    }

    public VacancyDto updateVacancy(VacancyUpdateDto dto){
        Long id = userContext.getUserId();

        validatorVacancy.checkRoleUpdatingUser(id);
        validatorVacancy.checkingNumberCandidates(dto);

        Vacancy vacancy = vacancyRepository.getById(dto.getId());
        vacancyMapper.toEntityUpdateVacancy(dto, vacancy);
        Vacancy updatedEntity = vacancyRepository.save(vacancy);

        return vacancyMapper.toDtoVacancy(updatedEntity);
    }

    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getById(vacancyId);
        vacancy.getCandidates().forEach(candidateRepository::delete);
        vacancyRepository.delete(vacancy);
    }

    public List<VacancyDto> getFilteredVacancies(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        List<VacancyFilter> applicableFilters = vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .collect(Collectors.toList());

        if (applicableFilters.isEmpty()) {
            return vacancyRepository.findAll().stream()
                    .map(vacancyMapper::toDtoVacancy)
                    .collect(Collectors.toList());
        }

        return applicableFilters.stream()
                .reduce(vacancies,
                        (stream, filter) -> filter.apply(stream, filters),
                        (stream1, stream2) -> Stream.concat(stream1, stream2))
                .map(vacancyMapper::toDtoVacancy)
                .collect(Collectors.toList());
    }

    public VacancyDto getVacancyById(Long id){
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена"));
        return vacancyMapper.toDtoVacancy(vacancy);
    }
}

