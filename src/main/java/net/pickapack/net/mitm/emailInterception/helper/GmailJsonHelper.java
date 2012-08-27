package net.pickapack.net.mitm.emailInterception.helper;

import com.jayway.jsonpath.JsonPath;
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
        parseType1Json(FileUtils.readAllText("1.json"));
        parseType2Json(FileUtils.readAllText("2.json"));
    }

    private static void parseType1Json(String json) throws TransformerException, IOException, XPathExpressionException {
        IndentedPrintWriter pw = new IndentedPrintWriter(System.out, true);

        for (Object row : JsonPath.<List<Object>>read(json, "$[0][*]")) {
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

    private static void parseType2Json(String json) throws TransformerException, IOException, XPathExpressionException {
        IndentedPrintWriter pw = new IndentedPrintWriter(System.out, true);

        pw.println("Received email content: ");

        pw.incrementIndentation();

        for (Object row : JsonPath.<List<Object>>read(json, "$[0][*]")) {
            if (row.toString().contains("[\"ms\"")) {
                String id = JsonPath.read(row.toString(), "$[13][0]").toString();
                pw.println("id: " + id);

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
}
