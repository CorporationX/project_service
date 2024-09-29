package faang.school.projectservice.mapper;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.calendar.ScopeDto;
import faang.school.projectservice.model.enums.ACLScopeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScopeMapper {
    @Mapping(source = "type", target = "type", qualifiedByName = "enumToString")
    AclRule.Scope toModel(ScopeDto scopeDto);

    @Mapping(source = "type", target = "type", qualifiedByName = "stringToEnum")
    ScopeDto toDto(AclRule.Scope scope);

    List<ScopeDto> toDtos(List<AclRule.Scope> scopes);

    @Named("enumToString")
    default String enumToString(ACLScopeType type) {
        return Objects.requireNonNull(type.getType());
    }

    @Named("stringToEnum")
    default ACLScopeType stringToEnum(String type) {
        return Objects.requireNonNull(ACLScopeType.findByKey(type));
    }
}
