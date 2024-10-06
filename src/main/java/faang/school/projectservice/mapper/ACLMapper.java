package faang.school.projectservice.mapper;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.calendar.ACLDto;
import faang.school.projectservice.model.enums.ACLRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ScopeMapper.class)
public interface ACLMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "enumToString")
    AclRule toModel(ACLDto aclDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToEnum")
    ACLDto toDto(AclRule acl);

    List<ACLDto> toDtos(List<AclRule> aclList);

    @Named("enumToString")
    default String enumToString(ACLRole role) {
        return role.getRole();
    }

    @Named("stringToEnum")
    default ACLRole stringToEnum(String role) {
        return ACLRole.findByKey(role);
    }
}
