package net.pickapack.net.mitm.emailInterception.client;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import net.pickapack.net.mitm.emailInterception.util.EmailInterceptionContainer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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

        Thread threadRunContainer = new Thread(){
            @Override
            public void run() {
                try {
                    EmailInterceptionContainer.main(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        threadRunContainer.setDaemon(true);
        threadRunContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    emailInterceptionTask.setEndTime(DateHelper.toTick(new Date()));
                    ServiceManager.getEmailInterceptionService().updateEmailInterceptionTask(emailInterceptionTask);

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

        emailInterceptionTask.setBeginTime(DateHelper.toTick(new Date()));
        ServiceManager.getEmailInterceptionService().updateEmailInterceptionTask(emailInterceptionTask);

        ServiceManager.getEmailInterceptionService().runEmailInterceptionTask(emailInterceptionTask);
    }
}
