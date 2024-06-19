package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.calendar.AclDto;
import faang.school.projectservice.model.aclRole.AclRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ScopeMapper.class)
public interface AclMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "enumToString")
    com.google.api.services.calendar.model.AclRule toModel(AclDto aclDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToEnum")
    AclDto toDto(com.google.api.services.calendar.model.AclRule acl);

    List<AclDto> toDtos(List<com.google.api.services.calendar.model.AclRule> aclList);

    @Named("enumToString")
    default String enumToString(AclRole role) {
        return role.getRole();
    }

    @Named("stringToEnum")
    default AclRole stringToEnum(String role) {
        return AclRole.findByKey(role);
    }
}
