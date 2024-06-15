package faang.school.projectservice.mapper.calendar;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.calendar.AclDto;
import faang.school.projectservice.model.enums.calendar.AclRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ScopeMapper.class)
public interface AclMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "enumToString")
    AclRule toModel(AclDto aclDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToEnum")
    AclDto toDto(AclRule acl);

    List<AclDto> toDtos(List<AclRule> aclList);

    @Named("enumToString")
    default String enumToString(AclRole role) {
        return role.getRole();
    }

    @Named("stringToEnum")
    default AclRole stringToEnum(String role) {
        return AclRole.findByKey(role);
    }
}
