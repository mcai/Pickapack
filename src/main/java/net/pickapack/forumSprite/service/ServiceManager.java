package net.pickapack.forumSprite.service;

public class ServiceManager {
    public static final String DATABASE_URL = "jdbc:mysql://localhost/forum_sprite?user=root&password=1026@ustc";

    private static ForumSpriteService forumSpriteService;

    static {
        forumSpriteService = new ForumSpriteServiceImpl();
    }

    public static ForumSpriteService getForumSpriteService() {
        return forumSpriteService;
    }
}
