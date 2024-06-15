package faang.school.projectservice.mapper.calendar;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.calendar.ScopeDto;
import faang.school.projectservice.model.enums.calendar.AclScopeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScopeMapper {
    @Mapping(source = "type", target = "type", qualifiedByName = "enumToString")
    AclRule.Scope toModel(ScopeDto scopeDto);

    @Mapping(source = "type", target = "type", qualifiedByName = "stringToEnum")
    ScopeDto toDto(AclRule.Scope scope);

    List<ScopeDto> toDtos(List<AclRule.Scope> scopes);

    @Named("enumToString")
    default String enumToString(AclScopeType type) {
        return type.getType();
    }

    @Named("stringToEnum")
    default AclScopeType stringToEnum(String type) {
        return AclScopeType.findByKey(type);
    }
}
