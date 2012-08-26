package net.pickapack.spider.noJs.crawler.media;

import net.pickapack.captcha.DeathByCaptchaSolver;
import net.pickapack.captcha.ManualCaptchaSolver;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.Page;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class MediaFileDownloader {
    private String host;
    private NoJSCrawler crawler;
    private DeathByCaptchaSolver deathByCaptchaSolver;
    private ExecutorService downloadMediaFilesService;

    public MediaFileDownloader(String host, NoJSCrawler crawler) {
        this.host = host;
        this.crawler = crawler;
        this.deathByCaptchaSolver = new DeathByCaptchaSolver(crawler, "fifoxbnc", "quskyoi2580");
        this.downloadMediaFilesService = Executors.newFixedThreadPool(5);
    }

    protected String solveRecaptchaChallenge(String challenge) throws IOException, XPathExpressionException, TransformerException {
        return this.solveCaptcha(this.crawler.urlToBytes("http://www.google.com/recaptcha/api/image?c=" + challenge));
    }

    protected String getRecaptchaChallenge(String recaptchaScriptSrc) throws IOException, XPathExpressionException, TransformerException {
        Page pageRecaptchaScript = this.crawler.getPage(new URL(recaptchaScriptSrc));

        String scriptText = pageRecaptchaScript.getText();
        String[] lines = scriptText.split("\n");

        String challenge = null;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("challenge")) {
                challenge = line.substring(line.indexOf("'") + 1, line.lastIndexOf("'"));
                break;
            }
        }

        if (challenge == null) {
            throw new IllegalArgumentException();
        }
        return challenge;
    }

    protected String solveCaptcha(byte[] data) {
        return solveCaptchaByDeathByCaptcha(data);
//        return solveCaptchaByManual(data);
    }

    public static String solveCaptchaByManual(byte[] data) {
        return new ManualCaptchaSolver().solveCaptcha(data).getCaptcha().getText();
    }

    protected String solveCaptchaByDeathByCaptcha(byte[] data) {
        return this.deathByCaptchaSolver.solveCaptcha(data).getCaptcha().getText();
    }

    public void downloadMediaFile(final String storageFolder, final URL url) {
        this.downloadMediaFilesService.submit(new Runnable() {
            @Override
            public void run() {
                doDownloadMediaFile(storageFolder, url);
            }
        });
    }

    public void close() {
        this.downloadMediaFilesService.shutdown();
        while (!this.downloadMediaFilesService.isTerminated()) {
            try {
                this.downloadMediaFilesService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract void doDownloadMediaFile(String storageFolder, URL url);

    public String getHost() {
        return host;
    }

    public NoJSCrawler getCrawler() {
        return crawler;
    }
}
