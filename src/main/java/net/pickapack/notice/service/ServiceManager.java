package net.pickapack.notice.service;

import net.pickapack.notice.model.forum.*;

public class ServiceManager {
    public static final String DATABASE_URL = "jdbc:mysql://localhost/forum_sprite?user=root&password=######"; //TODO: supply password here

    private static ForumSpriteService forumSpriteService;

    static {
        forumSpriteService = new ForumSpriteServiceImpl();
    }

    public static ForumSpriteService getForumSpriteService() {
        return forumSpriteService;
    }

    public static Forum getParent(Forum forum) {
        return forum.getParentId() == -1 ? null : ServiceManager.getForumSpriteService().getForumById(forum.getParentId());
    }

    public static Forum getParent(ForumThread forumThread) {
        return forumThread.getParentId() == -1 ? null : ServiceManager.getForumSpriteService().getForumById(forumThread.getParentId());
    }

    public static ForumThread getParent(ForumThreadMessage forumThreadMessage) {
        return forumThreadMessage.getParentId() == -1 ? null : ServiceManager.getForumSpriteService().getForumThreadById(forumThreadMessage.getParentId());
    }

    public ForumUser getParent(ForumUserPermission forumUserPermission) {
        return forumUserPermission.getParentId() == -1 ? null : ServiceManager.getForumSpriteService().getForumUserById(forumUserPermission.getParentId());
    }
}
