package net.pickapack.net.mitm.emailInterception.service;

import java.io.File;

public class ServiceManager {
    public static final String USER_HOME_TEMPLATE_ARG = "<user.home>";

    public static final String DATABASE_REVISION = "1";
    public static final String DATABASE_DIRECTORY = System.getProperty("user.dir") + "/" + "experiments";
    public static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_DIRECTORY + "/v" + DATABASE_REVISION + ".sqlite";

    private static EmailInterceptionService emailInterceptionService;

    static {
        new File(ServiceManager.DATABASE_DIRECTORY).mkdirs();
        emailInterceptionService = new EmailInterceptionServiceImpl();
    }

    public static EmailInterceptionService getEmailInterceptionService() {
        return emailInterceptionService;
    }
}
