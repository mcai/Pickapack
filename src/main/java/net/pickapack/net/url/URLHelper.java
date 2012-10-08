package net.pickapack.net.url;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class URLHelper {
    /**
     *
     * @param url
     * @param key
     * @return
     */
    public static String getQueryParameterFromUrl(String url, String key) {
        NameValuePair pair = getQueryParameterPairFromUrl(url, key);
        return pair != null ? pair.getValue() : null;
    }
    /**
     *
     * @param url
     * @param key
     * @return
     */
    public static NameValuePair getQueryParameterPairFromUrl(String url, String key) {
        List<NameValuePair> nameValuePairList = URLEncodedUtils.parse(URI.create(url), "ISO-8859-1");
        for(NameValuePair pair : nameValuePairList) {
            if(pair.getName().equals(key)) {
                return pair;
            }
        }

        return null;
    }

    /**
     *
     * @param url
     * @return
     */
    public static List<NameValuePair> getQueryParametersFromUrl(String url) {
        return URLEncodedUtils.parse(URI.create(url), "ISO-8859-1");
    }
}
