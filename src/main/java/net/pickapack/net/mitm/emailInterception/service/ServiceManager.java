package net.pickapack.net.mitm.emailInterception.service;

public class ServiceManager {
    public static final String DATABASE_URL = "jdbc:mysql://localhost/email_interception?user=root&password=1026@ustc";

    private static EmailInterceptionService emailInterceptionService;

    static {
        emailInterceptionService = new EmailInterceptionServiceImpl();
    }

    public static EmailInterceptionService getEmailInterceptionService() {
        return emailInterceptionService;
    }
}
