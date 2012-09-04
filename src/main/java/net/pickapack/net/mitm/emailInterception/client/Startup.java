package net.pickapack.net.mitm.emailInterception.client;

import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class Startup {
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            System.out.println("Usage: java -cp ./pickapack.jar net.pickapack.net.mitm.emailInterception.client.Startup <db-user> <db-password> <path-to-task-file>");
            return;
        }

        ServiceManager.databaseUser = args[0];
        ServiceManager.databasePassword = args[1];

        String fileNameEmailInterceptionTask = args[2];

        File file = new File(fileNameEmailInterceptionTask);
        if(!file.exists()) {
            System.out.println("Email interception task definition file \"" + fileNameEmailInterceptionTask + "\" do not exist!");
            return;
        }

        EmailInterceptionTask emailInterceptionTask = new Persister().read(EmailInterceptionTask.class, file);

        if(emailInterceptionTask == null) {
            System.out.println("Failed to parse email interception task definition file \"" + fileNameEmailInterceptionTask + "\"!");
            return;
        }

        ServiceManager.getEmailInterceptionService().addEmailInterceptionTask(emailInterceptionTask);
        ServiceManager.getEmailInterceptionService().runEmailInterceptionTask(emailInterceptionTask);
    }
}
