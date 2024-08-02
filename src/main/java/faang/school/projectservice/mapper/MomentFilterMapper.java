package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentFilterMapper {

    @Mapping(target = "userIds", expression = "java(this.setUserIds())")
    @Mapping(target = "projectIds", expression = "java(this.setProjectIds())")
    MomentDto toMomentDto(MomentFilterDto momentFilterDto);

    @Named("setUserIds")
    default List<Long> setUserIds() {
        return new ArrayList<>();
    }

    @Named("setProjectIds")
    default List<Long> setProjectIds() {
        return new ArrayList<>();
    }

}
