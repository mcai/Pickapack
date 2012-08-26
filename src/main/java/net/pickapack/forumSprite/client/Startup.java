package net.pickapack.forumSprite.client;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.forumSprite.model.forum.Forum;
import net.pickapack.forumSprite.model.forum.ForumThread;
import net.pickapack.forumSprite.model.forum.ForumThreadMessage;
import net.pickapack.forumSprite.service.ServiceManager;

import java.util.Date;
import java.util.List;

public class Startup {
    public static void main(String[] args) {
        List<Forum> forums = ServiceManager.getForumSpriteService().getAllForums();
        for(Forum forum : forums) {
            List<ForumThread> forumThreads = ServiceManager.getForumSpriteService().getForumThreadsByParent(forum);
            for(ForumThread forumThread : forumThreads) {
                List<ForumThreadMessage> forumThreadMessages = ServiceManager.getForumSpriteService().getForumThreadMessagesByParent(forumThread);
                for(ForumThreadMessage forumThreadMessage : forumThreadMessages) {
                    System.out.println(forumThreadMessage);
                }
            }
        }

        if(ServiceManager.getForumSpriteService().getForumByName("hello") == null) {
            ServiceManager.getForumSpriteService().addForum(new Forum(null, "hello", "world", true, DateHelper.toTick(new Date())));
        }
    }
}
