package net.pickapack.net.mitm.emailInterception.helper;

import com.jayway.jsonpath.JsonPath;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.text.XPathHelper;
import net.pickapack.util.IndentedPrintWriter;
import org.parboiled.common.FileUtils;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

public class GmailJsonHelper {
    public static void main(String[] args) throws TransformerException, IOException, XPathExpressionException {
        parseThreadListResponseJson(FileUtils.readAllText("1.json"));
        parseConversationViewResponseJson(FileUtils.readAllText("2.json"));
    }

    public static void parseThreadListResponseJson(String responseJson) throws TransformerException, IOException, XPathExpressionException {
        IndentedPrintWriter pw = new IndentedPrintWriter(System.out, true);

        pw.println("Received email list: ");

        for (Object row : JsonPath.<List<Object>>read(responseJson, "$[0][*]")) {
            if (row.toString().startsWith("[\"mla\"")) {
                String email = JsonPath.read(row.toString(), "$[1][0]").toString();
                pw.println("email: " + email);

                pw.println();
            } else if (row.toString().startsWith("[\"stu\"")) {
                for (Object subRow : JsonPath.<List<Object>>read(row.toString(), "$[2]")) {
                    pw.println("Received email: ");

                    pw.incrementIndentation();

                    String id = JsonPath.read(subRow.toString(), "$[0]").toString();
                    pw.println("id: " + id);

                    Document documentEmailAndName = XPathHelper.parse(JsonPath.read(subRow.toString(), "$[1][7]").toString());

                    String from = XPathHelper.getFirstByXPath(documentEmailAndName, "//span/@email").getNodeValue();
                    pw.println("from: " + from);

                    String subject = JsonPath.read(subRow.toString(), "$[1][9]").toString();
                    pw.println("subject: " + subject);

                    String contentStart = JsonPath.read(subRow.toString(), "$[1][10]").toString();
                    pw.println("contentStart: " + contentStart);

                    String receiveTime = JsonPath.read(subRow.toString(), "$[1][15]").toString();
                    pw.println("receiveTime: " + receiveTime);

                    pw.println();

                    pw.decrementIndentation();
                }
            }
        }
    }

    public static void parseConversationViewResponseJson(String responseJson) throws TransformerException, IOException, XPathExpressionException {
        IndentedPrintWriter pw = new IndentedPrintWriter(System.out, true);

        pw.println("Received email: ");

        pw.incrementIndentation();

        for (Object row : JsonPath.<List<Object>>read(responseJson, "$[0][*]")) {
            if (row.toString().contains("[\"ms\"")) {
                String id = JsonPath.read(row.toString(), "$[1]").toString();
                pw.println("id: " + id);

                String from = JsonPath.read(row.toString(), "$[6]").toString();
                pw.println("from: " + from);

                List<String> tos = JsonPath.read(row.toString(), "$[13][1]");
                pw.println("tos: " + tos);

                String subject = JsonPath.read(row.toString(), "$[13][5]").toString();
                pw.println("subject: " + subject);

                Long receiveTime = JsonPath.read(row.toString(), "$[7]");
                pw.println("receiveTime: " + DateHelper.toString(receiveTime));

                String contentStart = JsonPath.read(row.toString(), "$[8]").toString();
                pw.println("contentStart: " + contentStart);

                String content = JsonPath.read(row.toString(), "$[13][6]").toString();
                pw.println("content: ");

                pw.incrementIndentation();

                pw.println(content);

                pw.decrementIndentation();
                break;
            }
        }

        pw.decrementIndentation();
    }

    public static void parseSendMailResponseJson(String to, String subject, String content, String responseJson) throws TransformerException, IOException, XPathExpressionException {
        IndentedPrintWriter pw = new IndentedPrintWriter(System.out, true);

        pw.println("Sent email: ");

        pw.incrementIndentation();

        pw.println("to: " + to);

        pw.println("subject: " + subject);

        pw.println("content: ");

        pw.incrementIndentation();

        pw.println(content);

        pw.decrementIndentation();

        for (Object row : JsonPath.<List<Object>>read(responseJson, "$[*]")) {
            if (row.toString().contains("[\"a\"")) {
                String id = JsonPath.read(row.toString(), "$[3][0]").toString();
                pw.println("id: " + id);

                String result = JsonPath.read(row.toString(), "$[2]").toString();
                pw.println("result: " + result);
                break;
            }
        }

        pw.decrementIndentation();
    }
}
