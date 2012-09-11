package net.pickapack.web;

import com.mysql.jdbc.StringUtils;
import net.pickapack.dateTime.DateHelper;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;

public class NoticeContainer implements Container {
   public void handle(Request request, Response response) {
       try {
           PrintStream body = new PrintStream(response.getPrintStream(), true, "utf-8");

           long time = System.currentTimeMillis();

           response.set("Content-Type", "text/plain;charset=utf-8");
           response.set("Server", "Notice/1.0");
           response.setDate("Date", time);
           response.setDate("Last-Modified", time);

           if(request.getMethod().equals("POST")) {
               String userId = request.getForm().get("userId");
               String password = request.getForm().get("password");

               if(StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(password)) {
                   body.printf("[%s 用户登录] 用户名和密码不能为空!%n", DateHelper.toString(new Date()));
               }
               else {
                   body.printf("[%s 用户登录] %s您好, 欢迎使用notice!%n", DateHelper.toString(new Date()), userId);
               }

               response.setCode(HttpURLConnection.HTTP_OK);
           }

           body.close();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   } 

   public static void main(String[] list) throws Exception {
      Container container = new NoticeContainer();
      Connection connection = new SocketConnection(container);
      SocketAddress address = new InetSocketAddress(3721);
      connection.connect(address);
   }
}