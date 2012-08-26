package net.pickapack.spider.noJs.crawler.qq;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.pickapack.action.Action1;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class QqNoJsCrawler extends NoJSCrawler {
    public String clientId;

    private String qq;
    private String password;

    private String pSessionId;
    private int msgId;

    public QqNoJsCrawler(String qq, String password, String userAgent) {
        this(qq, password, userAgent, null, -1);
    }

    public QqNoJsCrawler(String qq, String password, String userAgent, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);
        this.clientId = randomClientId();
        this.qq = qq;
        this.password = password;
        this.msgId = new Random().nextInt(10000000);
    }

    public boolean login() {
        try {
            String[] parts = check();
            if (parts[0].equals("0")) {
                if (!login1(parts[2], parts[1])) {
                    return false;
                }

                if (!login2()) {
                    return false;
                }

                return true;
            } else if (parts[0].equals("1")) {
                return false; //TODO
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Header> buildExtraHeaders() {
        List<Header> extraHeaders = new ArrayList<Header>();
        extraHeaders.add(new BasicHeader("Referer", "http://d.web2.qq.com/proxy.html?v=20110331002&callback=1&id=2"));
        extraHeaders.add(new BasicHeader("Origin", "http://d.web2.qq.com"));
        extraHeaders.add(new BasicHeader("Accept", "\"text/html,application/xhtml+xml,application/xml;q=0.9,*/*,q=0.8\""));
        extraHeaders.add(new BasicHeader("Accept-Language", "zh-cn,zh;q=0.5"));
        extraHeaders.add(new BasicHeader("Accept-Charset", "UTF-8,utf-8;q=0.7,*;q=0.7"));
        extraHeaders.add(new BasicHeader("Connection", "Keep-Alive"));
        extraHeaders.add(new BasicHeader("Cache-Control", "no-cache"));
        return extraHeaders;
    }

    private String encodePassword(String ptUin, String captcha) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
            scriptEngine.eval(new InputStreamReader(QqNoJsCrawler.class.getResourceAsStream("/encodepwd.js")));
            Object t = scriptEngine.eval("QXWEncodePwd(\"" + ptUin + "\",\"" + password + "\",\"" + captcha + "\");");
            return t.toString();
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] check() throws IOException, XPathExpressionException, TransformerException {
        Page pageCheck = this.getPage(new URL("http://check.ptlogin2.qq.com/check?appid=1003903&uin=" + qq));
        String text = pageCheck.getText();
        text = text.substring(text.indexOf("(") + 1, text.lastIndexOf(")")).replaceAll("'", "");
        return text.split(",");
    }

    private boolean login1(String ptUin, String captcha) throws IOException, XPathExpressionException, TransformerException {
        String encodedPassword = encodePassword(ptUin, captcha);

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("u", qq));
        requestParameters.add(new BasicNameValuePair("p", encodedPassword));
        requestParameters.add(new BasicNameValuePair("verifycode", captcha));
        requestParameters.add(new BasicNameValuePair("webqq_type", "10"));
        requestParameters.add(new BasicNameValuePair("remember_uin", "1"));
        requestParameters.add(new BasicNameValuePair("login2qq", "0"));
        requestParameters.add(new BasicNameValuePair("aid", "1003903"));
        requestParameters.add(new BasicNameValuePair("u1", "http://web.qq.com/loginproxy.html?login2qq=0&webqq_type=10"));
        requestParameters.add(new BasicNameValuePair("h", "1"));
        requestParameters.add(new BasicNameValuePair("ptredirect", "0"));
        requestParameters.add(new BasicNameValuePair("ptlang", "2052"));
        requestParameters.add(new BasicNameValuePair("from_ui", "1"));
        requestParameters.add(new BasicNameValuePair("pttype", "1"));
        requestParameters.add(new BasicNameValuePair("dumy", ""));
        requestParameters.add(new BasicNameValuePair("fp", "loginerroralert"));
        requestParameters.add(new BasicNameValuePair("action", "1-11-10831"));
        requestParameters.add(new BasicNameValuePair("mibao_css", "m_webqq"));
        requestParameters.add(new BasicNameValuePair("t", "1"));
        requestParameters.add(new BasicNameValuePair("g", "1"));

        List<Header> extraHeaders = buildExtraHeaders();

        Page pageLogin1 = this.getPage(new URL("http://ptlogin2.qq.com/login"), HttpMethod.GET, requestParameters, extraHeaders, "utf-8");

        boolean result = pageLogin1.getText().contains("登录成功！");

        if(result) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 第1次登录QQ成功 (QQ: %s)", DateHelper.toString(new Date()), qq)));
        }
        else {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 第1次登录QQ失败 (QQ: %s)", DateHelper.toString(new Date()), qq)));
        }

        return result;
    }

    private boolean login2() throws IOException, XPathExpressionException, TransformerException {
        String ptWebQq = this.getCookieValue("ptwebqq");

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("r", "{\"status\":\"online\",\"ptwebqq\":\"" + ptWebQq + "\",\"passwd_sig\":\"\",\"clientid\":\"" + clientId + "\",\"psessionid\":null}"));
        requestParameters.add(new BasicNameValuePair("clientid", clientId));
        requestParameters.add(new BasicNameValuePair("psessionid", null));

        List<Header> extraHeaders = buildExtraHeaders();

        Page pageLogin2 = this.getPage(new URL("http://d.web2.qq.com/channel/login2"), HttpMethod.POST, requestParameters, extraHeaders, "utf-8");
        String text = pageLogin2.getText();

        Integer retCode = JsonPath.read(text, "$.retcode");

        if (retCode == 0) {
            pSessionId = JsonPath.read(text, "$.result.psessionid");
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 第2次登录QQ成功 (QQ: %s)", DateHelper.toString(new Date()), qq)));
            return true;
        }

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 第2次登录QQ失败 (QQ: %s)", DateHelper.toString(new Date()), qq)));
        return false;
    }

    public boolean fetchPersonalInfo() {
        try {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("tuin", qq));
            requestParameters.add(new BasicNameValuePair("verifysession", ""));
            requestParameters.add(new BasicNameValuePair("code", ""));
            requestParameters.add(new BasicNameValuePair("vfwebqq", this.getCookieValue("vfwebqq")));
            requestParameters.add(new BasicNameValuePair("t", ""));

            List<Header> extraHeaders = buildExtraHeaders();

            Page pageFetchPersonalInfo = this.getPage(new URL("http://s.web2.qq.com/api/get_friend_info2"), HttpMethod.GET, requestParameters, extraHeaders, "utf-8");
            String text = pageFetchPersonalInfo.getText();
            System.out.println(text);

            Integer retCode = JsonPath.read(text, "$.retcode");

            if (retCode == 0) {
                Integer birthMonth = JsonPath.read(text, "$.result.birthday.month");
                Integer birthYear = JsonPath.read(text, "$.result.birthday.year");
                Integer birthDay = JsonPath.read(text, "$.result.birthday.day");

                Long uin = JsonPath.read(text, "$.result.uin");

                String country = JsonPath.read(text, "$.result.country");
                String city = JsonPath.read(text, "$.result.city");
                String province = JsonPath.read(text, "$.result.province");
                String nick = JsonPath.read(text, "$.result.nick");
                String gender = JsonPath.read(text, "$.result.gender");

                System.out.println(nick + ", " + gender);

                System.out.println("birthday: " + birthYear + "-" + birthMonth + "-" + birthDay);
                System.out.println("uin: " + uin);
                System.out.println("where: " + country + province + city);

                this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 拉取个人资料成功 (QQ: %s)", DateHelper.toString(new Date()), qq)));
                return true;
            }

            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 拉取个人资料失败 (QQ: %s)", DateHelper.toString(new Date()), qq)));
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean fetchAllFriends() {
        try {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("r", "{\"h\":\"hello\",\"vfwebqq\":\"" + this.getCookieValue("vfwebqq") + "\"}"));

            List<Header> extraHeaders = buildExtraHeaders();

            Page pageFetchPersonalInfo = this.getPage(new URL("http://s.web2.qq.com/api/get_user_friends2"), HttpMethod.POST, requestParameters, extraHeaders, "utf-8");
            String text = pageFetchPersonalInfo.getText();
            System.out.println(text);

            Integer retCode = JsonPath.read(text, "$.retcode");

            if (retCode == 0) {
                readFriends(text);
                readMarkNames(text);
                readCategories(text);
                readInfo(text);

                this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 拉取好友列表成功 (QQ: %s)", DateHelper.toString(new Date()), qq)));
                return true;
            }

            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 拉取好友列表失败 (QQ: %s)", DateHelper.toString(new Date()), qq)));
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFriends(String text) {
        int numFriends = ((JSONArray) JsonPath.read(text, "$.result.friends")).size();
        for(int i = 0; i < numFriends; i++) {
            Long uin = JsonPath.read(text, "$.result.friends[" + i + "].uin");
            Integer categories = JsonPath.read(text, "$.result.friends[" + i + "].categories");

            System.out.println("friend uin: " + uin);
            System.out.println("friend categories: " + categories);
        }
    }

    private void readMarkNames(String text) {
        int numMarkNames = ((JSONArray) JsonPath.read(text, "$.result.marknames")).size();
        for(int i = 0; i < numMarkNames; i++) {
            Long uin = JsonPath.read(text, "$.result.marknames[" + i + "].uin");
            String markName = JsonPath.read(text, "$.result.marknames[" + i + "].markname");

            System.out.println("friend uin: " + uin);
            System.out.println("friend markname: " + markName);
        }
    }

    private void readCategories(String text) {
        int numCategories = ((JSONArray) JsonPath.read(text, "$.result.categories")).size();
        for(int i = 0; i < numCategories; i++) {
            Integer index = JsonPath.read(text, "$.result.categories[" + i + "].index");
            String name = JsonPath.read(text, "$.result.categories[" + i + "].name");

            System.out.println("Category index: " + index);
            System.out.println("Category name: " + name);
        }
    }

    private void readInfo(String text) {
        int numInfo = ((JSONArray) JsonPath.read(text, "$.result.info")).size();
        for(int i = 0; i < numInfo; i++) {
            Long uin = JsonPath.read(text, "$.result.info[" + i + "].uin");
            String nickName = JsonPath.read(text, "$.result.info[" + i + "].nick");

            System.out.println("Friend uin: " + uin);
            System.out.println("Friend nickName: " + nickName);
        }
    }

    public boolean fetchOnlineFriends() {
        //TODO
        return true;
    }

    public boolean sendMessage(Long to, String message) {
        try {
            JSONObject r = new JSONObject();
            r.put("to", to);
            r.put("face", 0);

            JSONArray content = new JSONArray();
            content.add(message);

            JSONArray font = new JSONArray();
            font.add("font");

            JSONObject fontValue = new JSONObject();
            fontValue.put("name", "\\u5b8b\\u4f53");
            fontValue.put("size", "10");

            JSONArray style = new JSONArray();
            style.add(0);
            style.add(0);
            style.add(0);
            fontValue.put("style", style);
            fontValue.put("color", "993366");

            font.add(fontValue);
            content.add(font);

            r.put("content", content.toString());
            r.put("msg_id", msgId++);
            r.put("clientid", clientId);
            r.put("psessionid", pSessionId);

            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

            requestParameters.add(new BasicNameValuePair("r", r.toString()));
            requestParameters.add(new BasicNameValuePair("clientid", clientId));
            requestParameters.add(new BasicNameValuePair("psessionid", pSessionId));

            List<Header> extraHeaders = buildExtraHeaders();

            Page pageSendMessage = this.getPage(new URL("http://d.web2.qq.com/channel/send_buddy_msg2"), HttpMethod.POST, requestParameters, extraHeaders, "utf-8");
            String text = pageSendMessage.getText();
            System.out.println(text);

            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s QQ] 发送好友消息成功 (QQ: %s, to: %d)", DateHelper.toString(new Date()), qq, to)));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());
    }

    private static String randomClientId() {
        return new Random(UUID.randomUUID().hashCode()).nextInt(99) + "" + GetTime(new Date().getTime()) / 1000000;
    }

    private static long GetTime(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, Calendar.JANUARY, 1);
        long startDate = calendar.getTimeInMillis();
        return (long) (dateTime - startDate + 0.5);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean useProxy = true;

        QqNoJsCrawler crawler = useProxy ? new QqNoJsCrawler("2437106554", "1026@ustc", NoJSSpider.FIREFOX_3_6, "localhost", 8888) : new QqNoJsCrawler("2437106554", "1026@ustc", NoJSSpider.FIREFOX_3_6);

        crawler.getEventDispatcher().addListener(CrawlerLoggingEvent.class, new Action1<CrawlerLoggingEvent>() {
            @Override
            public void apply(CrawlerLoggingEvent event) {
                System.out.println(event.getMessage());
            }
        });

        if (crawler.login()) {
            crawler.fetchPersonalInfo();
            crawler.fetchAllFriends();
            crawler.sendMessage(2434115987L, "What's wrong?");
        }

        crawler.close();
    }
}
