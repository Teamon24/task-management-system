package org.effective_mobile.task_management_system.resource;

public final class Api {
    public static final String SIGN_IN = "/signin";
    public static final String SIGN_UP = "/signup";
    public static final String SIGN_OUT = "/signout";
    public static final String TASK = "/task";

    public static final String EXECUTOR = "/executor";
    public static final String EXECUTOR_USERNAME = "userName";

    public static final String UNASSIGN = "/unassign";
    public static final String NEW_STATUS_PARAM = "value";
    public static final String STATUS = "/status";
    public static final String PRIORITIES = "/priorities";

    public static final String COMMENT_TASK_ID = "task_id";
    public static final String COMMENT = TASK + "/{" + COMMENT_TASK_ID + "}/comment";
}
