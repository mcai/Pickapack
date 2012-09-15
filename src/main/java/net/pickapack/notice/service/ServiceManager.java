package net.pickapack.notice.service;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.notice.model.forum.*;

import java.util.Date;

public class ServiceManager {
    public static final String DATABASE_URL = "jdbc:mysql://localhost/forum_sprite?user=root&password=1026@ustc";

    private static ForumSpriteService forumSpriteService;

    static {
        forumSpriteService = new ForumSpriteServiceImpl();

        for(Forum forum : forumSpriteService.getAllForums()) {
            forumSpriteService.removeForumById(forum.getId());
        }

        for(int i = 0; i < 5; i++) {
            Forum forum = new Forum(null, "论坛#" + i, "论坛标题#" + i, true, DateHelper.toTick(new Date()));
            forumSpriteService.addForum(forum);

            for(int j = 0; j < 5; j++) {
                ForumThread forumThread = new ForumThread(forum, "主题#" + j, "主题标题#" + j, DateHelper.toTick(new Date()));
                forumSpriteService.addForumThread(forumThread);

                for(int k = 0; k < 2; k++) {
                    ForumThreadMessage forumThreadMessage = new ForumThreadMessage(forumThread, "帖子#" + k, "帖子标题#" + k, DateHelper.toTick(new Date()), ForumThreadMessageAccessType.PUBLIC, "admin", false);
                    forumSpriteService.addForumThreadMessage(forumThreadMessage);
                }
            }
        }
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
