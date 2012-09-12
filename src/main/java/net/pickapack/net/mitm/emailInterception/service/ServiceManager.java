package net.pickapack.net.mitm.emailInterception.service;

public class ServiceManager {
    public static String databaseUser;
    public static String databasePassword;

    private static EmailInterceptionService emailInterceptionService;

    public static EmailInterceptionService getEmailInterceptionService() {
        if(emailInterceptionService == null) {
            emailInterceptionService = new EmailInterceptionServiceImpl();
        }

        return emailInterceptionService;
    }

    public static String getDatabaseUrl() {
        return "jdbc:mysql://localhost/email_interception?user=" + databaseUser + "&password=" + databasePassword;
    }
}
