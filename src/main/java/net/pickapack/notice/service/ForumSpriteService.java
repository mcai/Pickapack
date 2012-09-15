package net.pickapack.notice.service;

import net.pickapack.notice.model.forum.*;
import net.pickapack.service.Service;

import java.util.List;

public interface ForumSpriteService extends Service {
    List<ForumUser> getAllForumUsers();

    ForumUser getForumUserById(long forumUserId);

    ForumUser getForumUserByName(String name);

    ForumUser getForumUserByEmail(String email);

    void addForumUser(ForumUser forumUser);

    void removeForumUserById(long forumUserId);

    void updateForumUser(ForumUser forumUser);

    List<Forum> getAllForums();

    List<Forum> getForumsByParent(Forum parent);

    Forum getForumById(long forumId);

    Forum getForumByName(String name);

    Forum getForumByLabel(String label);

    void addForum(Forum forum);

    void removeForumById(long forumId);

    void updateForum(Forum forum);

    List<ForumThread> getForumThreadsByParent(Forum parent);

    List<ForumThread> getForumThreadsBySubject(String subject);

    ForumThread getForumThreadById(long forumThreadId);

    ForumThread getForumThreadByParentAndLabel(Forum parent, String label);

    void addForumThread(ForumThread forumThread);

    void removeForumThreadById(long forumThreadId);

    void updateForumThread(ForumThread forumThread);

    List<ForumThreadMessage> getForumThreadMessagesByParent(ForumThread parent);

    List<ForumThreadMessage> getForumThreadMessagesBySubject(String subject);

    List<ForumThreadMessage> getForumThreadMessagesByUserId(long forumUserId);

    ForumThreadMessage getForumThreadMessageById(long forumThreadMessageId);

    void addForumThreadMessage(ForumThreadMessage forumThreadMessage);

    void removeForumThreadMessageById(long forumThreadMessageId);

    void updateForumThreadMessage(ForumThreadMessage forumThreadMessage);

    ForumUserPermission getForumUserPermissionByParent(ForumUser parent);

    void addForumUserPermission(ForumUserPermission forumUserPermission);

    void removeForumUserPermissionByParent(ForumUser parent);

    void updateForumUserPermission(ForumUserPermission forumUserPermission);
}
