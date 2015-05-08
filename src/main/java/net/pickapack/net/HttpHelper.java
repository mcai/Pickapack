/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

/**
 * HTTP helper.
 *
 * @author Min Cai
 */
public class HttpHelper {
    /**
     * Create an HTTP request from the specified byte array.
     *
     * @param bytes the byte array
     * @param bytesRead the number of bytes that is to be read
     * @return a newly created HTTP request from the specified byte array
     */
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

    /**
     * Create an HTTP response from the specified byte array.
     *
     * @param bytes the byte array
     * @param bytesRead the number of bytes that is to be read
     * @return a newly created HTTP response from the specified byte array
     */
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