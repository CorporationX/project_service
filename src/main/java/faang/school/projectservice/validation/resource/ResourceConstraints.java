package faang.school.projectservice.validation.resource;

import lombok.Getter;

@Getter
public enum ResourceConstraints {
    RESOURCE_CAN_NOT_CHANGE("Resource can not be change because this user not created and not owner project"),
    RESOURCE_CAN_NOT_DELETE("Resource can not be delete because this user not created and not owner project");
    private final String message;
    ResourceConstraints(String message){
        this.message = message;
    }
}
