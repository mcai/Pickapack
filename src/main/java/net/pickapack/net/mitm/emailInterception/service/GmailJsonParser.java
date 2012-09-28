package net.pickapack.net.mitm.emailInterception.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.pickapack.JsonSerializationHelper;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.parboiled.common.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GmailJsonParser {
    public static void main(String[] args) throws Exception {
        parseReceivedEmails(null, "", extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/1.gmail.html"))));
        parseReceivedEmails(null, "", extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/1.gmail_with_attachments.html"))));
        parseReceivedEmails(null, "", extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/2.gmail_with_attachments.html"))));
        parseReceivedEmails(null, "", extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/3.gmail_with_attachments.html"))));

        parseAttachmentUploaded(null, extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/1.gmail_upload_attachment.html"))));
        parseSentEmails(null, "", extractJson(FileUtils.readAllText(new File("/home/itecgo/Desktop/1.gmail_send_email.html"))), null, null, null);
    }

    public static List<ReceivedEmailEvent> parseReceivedEmails(EmailInterceptionTask emailInterceptionTask, String email, String json) throws IOException {
        json = JsonSerializationHelper.prettyPrint(json);

        ObjectMapper m = new ObjectMapper();
        JsonNode rootNode = m.readValue(json, JsonNode.class);

        List<ReceivedEmailEvent> receivedEmailEvents = new ArrayList<ReceivedEmailEvent>();

        for(JsonNode node1 : rootNode) {
            if(node1.size() > 0) {
                for(final JsonNode childNode : node1) {
                    if (childNode.size() > 0 && childNode.get(0).asText().equals("ms")) {
                        System.out.println(childNode);
                        System.out.println(childNode.size());

                        if (childNode.size() == 47 || childNode.size() == 48) {
                            String no = childNode.get(1).asText();
                            String from = childNode.get(6).asText();
                            String subject = childNode.get(12).asText();
                            long receiveTime = childNode.get(7).asLong();
                            String content = childNode.get(13).isNull() || StringUtils.isBlank(childNode.get(13).asText()) ? "" : childNode.get(13).get(6).asText();
                            List<String> attachmentNames = childNode.get(21).isNull() || StringUtils.isBlank(childNode.get(21).asText()) ? new ArrayList<String>() : new ArrayList<String>() {{
                                for(String attachmentName : childNode.get(21).asText().split(",")) {
                                    add(attachmentName);
                                }
                            }};

                            ReceivedEmailEvent receivedEmailEvent = new ReceivedEmailEvent(emailInterceptionTask, no, email, from, subject, content, attachmentNames);
                            receivedEmailEvent.setReceiveTime(receiveTime);
                            receivedEmailEvents.add(receivedEmailEvent);
                        }
                    }
                }
            }
        }

        return receivedEmailEvents;
    }

    public static String parseAttachmentUploaded(EmailInterceptionTask emailInterceptionTask, String json) throws IOException {
        json = JsonSerializationHelper.prettyPrint(json);

        ObjectMapper m = new ObjectMapper();
        JsonNode rootNode = m.readValue(json, JsonNode.class);

        if(rootNode.size() == 2) {
            JsonNode childNode = rootNode.get(0);
            if(childNode.size() == 13) {
                JsonNode childChildNode = childNode.get(0);
                if(childChildNode.size() == 4) {
                    return childChildNode.get(3).asText();
                }
            }
        }

        return null;
    }

    public static SentEmailEvent parseSentEmails(EmailInterceptionTask emailInterceptionTask, String email, String json, List<String> tos, String subject, String content) throws IOException {
        json = JsonSerializationHelper.prettyPrint(json);

        ObjectMapper m = new ObjectMapper();
        JsonNode rootNode = m.readValue(json, JsonNode.class);

        String no = null;
        String result = null;

        for(JsonNode node1 : rootNode) {
            if(node1.size() > 0) {
                for(final JsonNode childNode : node1) {
                    if (childNode.size() > 0 && childNode.get(0).asText().equals("v")) {
                        System.out.println(childNode);
                        System.out.println(childNode.size());

                        if (childNode.size() == 4) {
                            no = childNode.get(3).asText();
                        }
                    }
                    else if (childNode.size() > 0 && childNode.get(0).asText().equals("a")) {
                        System.out.println(childNode);
                        System.out.println(childNode.size());

                        if (childNode.size() == 4) {
                            result = childNode.get(2).asText();
                        }
                    }
                }
            }
        }

        if(no != null && result != null) {
            return new SentEmailEvent(emailInterceptionTask, no, email, tos, subject, content, result);
        }

        return null;
    }

    public static String extractJson(String str) throws IOException {
        String text = StringUtils.substringAfter(str, "while(1);").trim();

        List<String> lines = IOUtils.readLines(new StringReader(text));

        List<String> resultLines = new ArrayList<String>();

        int i = 0;

        boolean numericFound = false;

        for(String line : lines) {
            if(!StringUtils.isNumeric(line.trim())) {
                if(i > 0 && line.trim().startsWith("[[")) {
                    line = "," + line.trim();
                }

                resultLines.add(line);
                i++;
            }
            else {
                numericFound = true;
            }
        }

        String joinedResultLines = StringUtils.join(resultLines, "\n");

        return numericFound ? "[" + joinedResultLines + "]" : joinedResultLines;
    }
}