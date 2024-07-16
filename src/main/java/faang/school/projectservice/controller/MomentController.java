package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.dto.client.moment.MomentUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moments")
@RequiredArgsConstructor
@Validated
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public MomentResponseDto addNew(@RequestBody @Validated MomentRequestDto momentRequestDto,
                                    @Positive @RequestHeader("x-user-id") long creatorId) {

        validateProjectsAndMembers(momentRequestDto);
        return momentService.addNew(momentRequestDto, creatorId);
    }

    @PatchMapping
    public MomentResponseDto update(@RequestBody @Validated MomentUpdateDto momentUpdateDto) {

        return momentService.update(momentUpdateDto);
    }


    private void validateProjectsAndMembers(MomentRequestDto momentRequestDto) {
        if (momentRequestDto.getProjectIds() == null
                && momentRequestDto.getTeamMemberIds() == null) {

            throw new DataValidationException(ErrorMessage.MOMENT_PROJECTS_AND_MEMBERS_NULL);
        }
    }
}
