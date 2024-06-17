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
    @Mapping(source = "role", target = "role", qualifiedByName = "enum2String")
    com.google.api.services.calendar.model.AclRule toModel(AclDto aclDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "string2Enum")
    AclDto toDto(com.google.api.services.calendar.model.AclRule acl);

    List<AclDto> toDtos(List<com.google.api.services.calendar.model.AclRule> aclList);

    @Named("enum2String")
    default String enum2String(AclRole role) {
        return role.getRole();
    }

    @Named("string2Enum")
    default AclRole string2Enum(String role) {
        return AclRole.findByKey(role);
    }
}
