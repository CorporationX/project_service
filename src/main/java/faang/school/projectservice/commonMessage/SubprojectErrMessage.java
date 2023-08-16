package faang.school.projectservice.commonMessage;

public final class SubprojectErrMessage {
    public static final String ERR_VISIBILITY_PARENT_PROJECT_FORMAT =
            "You can not to create a subproject with visibility {0}, because parent project visibility - PUBLIC";
    public static final String PROJECT_IS_NOT_SUBPROJECT_FORMAT = "This subproject with id {0} don not have parent project.";

    public static final String PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT = "Parent status: {0}  don not allow change subproject";
}
