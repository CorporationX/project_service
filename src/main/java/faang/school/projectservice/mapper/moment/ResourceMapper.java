package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Named("toListResourceId")
    default List<Long> toListResourceId(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }

    @Named("toListResource")
    default List<Resource> toListResource(List<Long> resourceIds) {
        return resourceIds.stream().map(id -> {
            Resource resource = new Resource();
            resource.setId(id);
            return resource;
        }).toList();
    }
}
