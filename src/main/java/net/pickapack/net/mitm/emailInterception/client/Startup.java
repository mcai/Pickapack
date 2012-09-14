package net.pickapack.net.mitm.emailInterception.client;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

        final EmailInterceptionTask emailInterceptionTask = new Persister().read(EmailInterceptionTask.class, file);

        if(emailInterceptionTask == null) {
            System.out.println("Failed to parse email interception task definition file \"" + fileNameEmailInterceptionTask + "\"!");
            return;
        }

        ServiceManager.getEmailInterceptionService().addEmailInterceptionTask(emailInterceptionTask);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    PrintWriter pw = new PrintWriter("emailInterception.log", "gb2312");

                    for (ReceivedEmailEvent receivedEmailEvent : ServiceManager.getEmailInterceptionService().getReceivedEmailEvents()) {
                        if (receivedEmailEvent.getParentId() == emailInterceptionTask.getId()) {
                            pw.println(receivedEmailEvent);
                        }
                    }

                    for (SentEmailEvent sentEmailEvent : ServiceManager.getEmailInterceptionService().getSentEmailEvents()) {
                        if(sentEmailEvent.getParentId() == emailInterceptionTask.getId()) {
                            pw.println(sentEmailEvent);
                        }
                    }

                    pw.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ServiceManager.getEmailInterceptionService().runEmailInterceptionTask(emailInterceptionTask);
    }
}
