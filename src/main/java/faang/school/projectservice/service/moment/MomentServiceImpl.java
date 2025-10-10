package faang.school.projectservice.service.moment;

import com.amazonaws.util.StringUtils;
import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MomentServiceImpl {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectRepository projectRepository;

    public MomentDto createMoment(@NonNull CreateMomentDto momentDto) throws Exception {
        validateCreateMomentDto(momentDto);
        Moment moment = momentMapper.toMoment(momentDto);
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < momentDto.projectIds().size(); i++) {
            Optional<Project> project = projectRepository.findById(momentDto.projectIds().get(i));
            project.ifPresent(projects::add);
        }
        moment.setProjects(projects);
        log.info("Новый Moment c названием: {} успешно добавлен", momentDto.name());
        return momentMapper.toMomentDto(momentRepository.save(moment));
    }

    public MomentDto updateMoment(long momentId, @NonNull UpdateMomentDto momentDto) throws Exception {
        validateUpdateMomentDto(momentDto);
        Optional<Moment> optionalMoment = momentRepository.findById(momentId);
        if (optionalMoment.isEmpty()) {
            log.error("Moment c id: {} не найден.", momentId);
            throw new EntityNotFoundException("Этот Moment невозможно обновить, он не существует.");
        }
        Moment moment = optionalMoment.get();
        momentMapper.update(momentDto, moment);
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < momentDto.projectIds().size(); i++) {
            Optional<Project> project = projectRepository.findById(momentDto.projectIds().get(i));
            project.ifPresent(projects::add);
        }
        moment.setProjects(projects);
        log.info("Moment c id: {} успешно обновлен", momentId);
        return momentMapper.toMomentDto(momentRepository.save(moment));
    }

    public MomentDto getById(long momentId) {
        Optional<Moment> optionalMoment = momentRepository.findById(momentId);
        if (optionalMoment.isEmpty()) {
            log.error("Moment c id: {} невозможно получить, он не существует.", momentId);
            throw new EntityNotFoundException("Этот Moment невозможно обновить, он не существует.");
        }
        return momentMapper.toMomentDto(optionalMoment.get());
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        List<MomentDto> momentDtos = new ArrayList<>();
        for (Moment moment : moments) {
            momentDtos.add(momentMapper.toMomentDto(moment));
        }
        return momentDtos;
    }

    public void deleteById(long momentId) {
        try {
            momentRepository.deleteById(momentId);
            log.info("Moment c id: {} успешно удален.", momentId);
        } catch (Exception e) {
            log.error("Moment c id: {} невозможно удалить, он не существует.", momentId);
        }
    }

    private void validateCreateMomentDto(CreateMomentDto momentDto) throws Exception {
        validateNullOrEmpty(momentDto.name(), "Name");
        validateNullOrEmpty(momentDto.description(), "Description");
        validateNullOrEmpty(momentDto.date().toString(), "Date");
        validateEmptyList(momentDto.projectIds(), "ProjectIds");
        validateEmptyList(momentDto.userIds(), "UserIds");
        validateNullOrEmpty(momentDto.imageId(), "ImageId");
        validateNullOrEmpty(momentDto.createdAt().toString(), "CreatedAt");
        validateNullOrEmpty(momentDto.createdBy().toString(), "CreatedBy");
    }

    private void validateUpdateMomentDto(UpdateMomentDto momentDto) throws Exception {
        validateNullOrEmpty(momentDto.name(), "Name");
        validateNullOrEmpty(momentDto.description(), "Description");
        validateNullOrEmpty(momentDto.date().toString(), "Date");
        validateEmptyList(momentDto.projectIds(), "ProjectIds");
        validateEmptyList(momentDto.userIds(), "UserIds");
        validateNullOrEmpty(momentDto.updatedAt().toString(), "UpdatedAt");
        validateNullOrEmpty(momentDto.updatedBy().toString(), "UpdatedBy");
    }

    private void validateNullOrEmpty(String value, String param) {
        if (StringUtils.isNullOrEmpty(value)) {
            throw new DataValidationException(param + " - пустое или равно null.");
        }
    }

    private void validateEmptyList(List<Long> list, String param) throws Exception {
        if (list.isEmpty()) {
            throw new Exception(param + " - пуст. Добавьте в него хотя бы 1 элемент.");
        }
    }
}
