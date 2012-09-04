package net.pickapack.net.mitm.emailInterception.service;

public class ServiceManager {
    public static String databaseUser = "root";
    public static String databasePassword = "1026@ustc";

    private static EmailInterceptionService emailInterceptionService;

    static {
        emailInterceptionService = new EmailInterceptionServiceImpl();
    }

    public static EmailInterceptionService getEmailInterceptionService() {
        return emailInterceptionService;
    }

    public static String getDatabaseUrl() {
        return "jdbc:mysql://localhost/email_interception?user=" + databaseUser + "&password=" + databasePassword;
    }
}
