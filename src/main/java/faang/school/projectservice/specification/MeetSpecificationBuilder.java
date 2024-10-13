package faang.school.projectservice.specification;

import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.model.entity.Meet;
import org.springframework.data.jpa.domain.Specification;

public class MeetSpecificationBuilder {

    public static Specification<Meet> buildSpecification(MeetFilterDto meetFilterDto) {
        Specification<Meet> spec = Specification.where(null);

        if (meetFilterDto.getTitle() != null) {
            spec = spec.and(MeetSpecifications.hasTitle(meetFilterDto.getTitle()));
        }

        if (meetFilterDto.getStartDateAfter() != null) {
            spec = spec.and(MeetSpecifications.startDateAfter(meetFilterDto.getStartDateAfter()));
        }

        if (meetFilterDto.getStartDateBefore() != null) {
            spec = spec.and(MeetSpecifications.startDateBefore(meetFilterDto.getStartDateBefore()));
        }

        if (meetFilterDto.getEndDateAfter() != null) {
            spec = spec.and(MeetSpecifications.endDateAfter(meetFilterDto.getEndDateAfter()));
        }

        if (meetFilterDto.getEndDateBefore() != null) {
            spec = spec.and(MeetSpecifications.endDateBefore(meetFilterDto.getEndDateBefore()));
        }

        if (meetFilterDto.getCreatedAfter() != null) {
            spec = spec.and(MeetSpecifications.createdAfter(meetFilterDto.getCreatedAfter()));
        }

        if (meetFilterDto.getCreatedBefore() != null) {
            spec = spec.and(MeetSpecifications.createdBefore(meetFilterDto.getCreatedBefore()));
        }

        return spec;
    }
}

