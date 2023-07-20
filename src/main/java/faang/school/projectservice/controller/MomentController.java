package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    public final MomentService momentService;

    public MomentDto createMoment(String name) {
        validateName(name);
        return momentService.createMoment(name);
    }

    public void updateMoment(MomentDto momentDto) {
        validateDtoMoment(momentDto);
        momentService.updateMoment(momentDto);
    }

    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto) {
        return momentService.getFilteredMoments(filterMomentDto);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto getMoment(long momentId) {
        validateId(momentId);
        return momentService.getMoment(momentId);
    }

    private void validateName(String name) {
        if (name.isEmpty()) {
            throw new NullPointerException(ErrorMessages.EMPTY_NAME);
        }
    }

    private void validateDtoMoment(MomentDto momentDto) {
        if (momentDto == null || momentDto.getDtoId() <= 0 || momentDto.getDtoDate() == null
                || momentDto.getDtoName().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_DTO);
        }
    }

    private void validateId(long id){
        if(id <= 0){
            throw new IllegalArgumentException(ErrorMessages.INVALID_ID);
        }
    }
}
