package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Resource;
import org.mapstruct.Named;

import java.util.List;

public interface ResourceIdMapper {

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
