package net.pickapack.net;

import org.apache.http.*;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicLineParser;
import org.apache.http.params.BasicHttpParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HttpHelper {
    public static HttpRequest createRequest(final byte[] bytes, final int bytesRead) {
        try {
            SessionInputBuffer inputBuffer = new AbstractSessionInputBuffer() {
                {
                    init(new ByteArrayInputStream(bytes, 0, bytesRead), 10, new BasicHttpParams());
                }

                @Override
                public boolean isDataAvailable(int timeout) throws IOException {
                    throw new RuntimeException("have to override but probably not even called");
                }
            };
            HttpMessageParser parser = new DefaultHttpRequestParser(inputBuffer, new BasicLineParser(new ProtocolVersion("HTTP", 1, 1)), new DefaultHttpRequestFactory(), new BasicHttpParams());
            HttpRequest request = (HttpRequest) parser.parse();
            if (request instanceof BasicHttpEntityEnclosingRequest) {
                EntityDeserializer entityDeserializer = new EntityDeserializer(new LaxContentLengthStrategy());
                HttpEntity entity = entityDeserializer.deserialize(inputBuffer, request);
                ((BasicHttpEntityEnclosingRequest) request).setEntity(entity);
            }
            return request;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (HttpException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse createResponse(final byte[] bytes, final int bytesRead) {
        try {
            SessionInputBuffer inputBuffer = new AbstractSessionInputBuffer() {
                {
                    init(new ByteArrayInputStream(bytes, 0, bytesRead), 10, new BasicHttpParams());
                }

                @Override
                public boolean isDataAvailable(int timeout) throws IOException {
                    throw new RuntimeException("have to override but probably not even called");
                }
            };
            HttpMessageParser parser = new DefaultHttpResponseParser(inputBuffer, new BasicLineParser(new ProtocolVersion("HTTP", 1, 1)), new DefaultHttpResponseFactory(), new BasicHttpParams());
            HttpResponse response = (HttpResponse) parser.parse();
            EntityDeserializer entityDeserializer = new EntityDeserializer(new LaxContentLengthStrategy());
            HttpEntity entity = entityDeserializer.deserialize(inputBuffer, response);
            response.setEntity(entity);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (HttpException e) {
            throw new RuntimeException(e);
        }
    }
}