package net.pickapack.net.mitm.emailInterception.util;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import net.pickapack.util.IndentedPrintWriter;
import org.parboiled.common.FileUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;

public class EmailInterceptionContainer implements Container {
    public void handle(Request request, Response response) {
        try {
            PrintStream body = new PrintStream(response.getPrintStream(), true, "utf-8");

            long time = System.currentTimeMillis();

            response.set("Content-Type", "text/html;charset=utf-8");
            response.set("Server", "Notice/1.0");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            body.println("<html><head><title>Welcome to Gmail Interception Service</title></head><body><pre>");

            body.printf("[%s] Welcome to Gmail Interception Service!%n", DateHelper.toString(new Date()));

            body.println();

            try {
                body.println("README ");
                body.println("########################");
                body.println();
                body.println(FileUtils.readAllText(EmailInterceptionContainer.class.getResourceAsStream("GmailInterception.README.txt")));
                body.println();

                body.println();

                IndentedPrintWriter indentedBody = new IndentedPrintWriter(body, true);

                indentedBody.println("Email Interception Tasks ");
                body.println("########################");

                indentedBody.incrementIndentation();

                for(EmailInterceptionTask emailInterceptionTask : ServiceManager.getEmailInterceptionService().getEmailInterceptionTasks()) {
                    indentedBody.println(emailInterceptionTask.getTitle());

                    indentedBody.incrementIndentation();

                    indentedBody.println("port: " + emailInterceptionTask.getPort());

                    indentedBody.println("Rules for intercepting received emails: " + emailInterceptionTask.getReceivedEmailRule());
                    indentedBody.println("Rules for intercepting sent emails: " + emailInterceptionTask.getSentEmailRule());

                    indentedBody.println("received email events: ");
                    indentedBody.incrementIndentation();
                    for(ReceivedEmailEvent receivedEmailEvent : ServiceManager.getEmailInterceptionService().getReceivedEmailEventsByParent(emailInterceptionTask)) {
                        indentedBody.println(receivedEmailEvent);
                    }
                    indentedBody.decrementIndentation();

                    indentedBody.println("sent email events: ");
                    indentedBody.incrementIndentation();
                    for(SentEmailEvent sentEmailEvent : ServiceManager.getEmailInterceptionService().getSentEmailEventsByParent(emailInterceptionTask)) {
                        indentedBody.println(sentEmailEvent);
                    }
                    indentedBody.decrementIndentation();

                    indentedBody.decrementIndentation();

                    body.println();
                }

                indentedBody.decrementIndentation();

                body.println();
                body.println("########################");

                body.println("</pre></body></html>");
            } catch (Exception e) {
                e.printStackTrace(body);
            }

            body.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Container container = new EmailInterceptionContainer();
        Connection connection = new SocketConnection(container);
        SocketAddress listen = new InetSocketAddress(3736);
        System.out.printf("[%s] Gmail interception web admin server started listening at: %s\n", DateHelper.toString(new Date()), listen.toString());
        connection.connect(listen);
    }
}