package net.pickapack.notice.rest;

import com.mysql.jdbc.StringUtils;
import net.pickapack.JsonSerializationHelper;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.notice.model.forum.Forum;
import net.pickapack.notice.model.forum.ForumThread;
import net.pickapack.notice.model.forum.ForumThreadMessage;
import net.pickapack.notice.service.ServiceManager;
import org.apache.http.HttpStatus;
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

public class NoticeContainer implements Container {
    public void handle(Request request, Response response) {
        try {
            PrintStream body = new PrintStream(response.getPrintStream(), true, "utf-8");

            long time = System.currentTimeMillis();

            response.set("Content-Type", "text/plain;charset=utf-8");
            response.set("Server", "Notice/1.0");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            if (request.getMethod().equals("POST")) {
                String action = request.getForm().get("action");
                String userId = request.getForm().get("userId");

                if (action.equals("login")) {
                    String password = request.getForm().get("password");
                    if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(password)) {
                        body.printf("[%s 用户登录] 用户名和密码不能为空!%n", DateHelper.toString(new Date()));
                    } else {
                        body.printf("[%s 用户登录] %s您好, 欢迎使用notice!%n", DateHelper.toString(new Date()), userId);
                    }
                } else if (action.equals("getAllForums")) {
                    body.println(JsonSerializationHelper.serialize(ServiceManager.getForumSpriteService().getAllForums().toArray()));
                } else if (action.equals("getForumThreadsByParentId")) {
                    long parentId = Long.parseLong(request.getForm().get("parentId"));
                    Forum forum = ServiceManager.getForumSpriteService().getForumById(parentId);

                    if(forum != null) {
                        body.println(JsonSerializationHelper.serialize(ServiceManager.getForumSpriteService().getForumThreadsByParent(forum).toArray()));
                    }
                } else if (action.equals("getForumThreadMessagesByParentId")) {
                    long parentId = Long.parseLong(request.getForm().get("parentId"));
                    ForumThread forumThread = ServiceManager.getForumSpriteService().getForumThreadById(parentId);

                    if(forumThread != null) {
                        body.println(JsonSerializationHelper.serialize(ServiceManager.getForumSpriteService().getForumThreadMessagesByParent(forumThread).toArray()));
                    }
                } else if (action.equals("getParentByForumId")) {
                    long forumId = Long.parseLong(request.getForm().get("forumId"));
                    Forum forum = ServiceManager.getForumSpriteService().getForumById(forumId);

                    if(forum != null) {
                        body.println(JsonSerializationHelper.serialize(ServiceManager.getParent(forum)));
                    }
                } else if (action.equals("getParentByForumThreadId")) {
                    long forumThreadId = Long.parseLong(request.getForm().get("forumThreadId"));
                    ForumThread forumThread = ServiceManager.getForumSpriteService().getForumThreadById(forumThreadId);

                    if(forumThread != null) {
                        body.println(JsonSerializationHelper.serialize(ServiceManager.getParent(forumThread)));
                    }
                } else if (action.equals("getParentByForumThreadMessageId")) {
                    long forumThreadMessageId = Long.parseLong(request.getForm().get("forumThreadMessageId"));
                    ForumThreadMessage forumThreadMessage = ServiceManager.getForumSpriteService().getForumThreadMessageById(forumThreadMessageId);

                    if(forumThreadMessage != null) {
                        body.println(JsonSerializationHelper.serialize(ServiceManager.getParent(forumThreadMessage)));
                    }
                }

                response.setCode(HttpStatus.SC_OK);
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