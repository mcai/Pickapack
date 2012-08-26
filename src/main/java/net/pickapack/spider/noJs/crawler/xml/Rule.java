package net.pickapack.spider.noJs.crawler.xml;

public abstract class Rule {
    private String id;
    private String path;
    private String[] applyUrlPatternIds;
    private boolean follow;
    private boolean download;
    private boolean print;

    public Rule(String id, String path, String applyUrlPatternIds, boolean follow, boolean download, boolean print) {
        this.id = id;
        this.path = path;
        this.applyUrlPatternIds = applyUrlPatternIds.split(",");
        this.follow = follow;
        this.download = download;
        this.print = print;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String[] getApplyUrlPatternIds() {
        return applyUrlPatternIds;
    }

    public boolean isFollow() {
        return follow;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isPrint() {
        return print;
    }
}
