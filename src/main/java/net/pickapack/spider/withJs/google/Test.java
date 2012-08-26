package net.pickapack.spider.withJs.google;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws IOException {
        PostMethod post = new PostMethod("http://jakarata.apache.org/");
        NameValuePair[] data = {
                new NameValuePair("user", "joe"),
                new NameValuePair("password", "bloggs")
        };
        post.setRequestBody(data);
        // execute method and handle any error responses.
        InputStream in = post.getResponseBodyAsStream();
    }
}
