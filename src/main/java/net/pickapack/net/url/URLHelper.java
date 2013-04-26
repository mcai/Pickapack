package net.pickapack.net.url;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

/**
 * URL helper.
 *
 * @author Min Cai
 */
public class URLHelper {
    /**
     * Get a specific query parameter from the specified URL.
     *
     * @param url the URL
     * @param key the key of the query parameter
     * @return the specific query parameter parsed from the specified URL
     */
    public static String getQueryParameterFromUrl(String url, String key) {
        NameValuePair pair = getQueryParameterPairFromUrl(url, key);
        return pair != null ? pair.getValue() : null;
    }

    /**
     * Get a specific query parameter key/value pair from the specified URL.
     *
     * @param url the URL
     * @param key the key of the query parameter
     * @return the specific query parameter key/value pair from the specified URL
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
     * Get the list of query parameter key/value pairs from the specified URL.
     *
     * @param url the URL
     * @return the list of query parameter key/value pairs from the specified URL
     */
    public static List<NameValuePair> getQueryParametersFromUrl(String url) {
        return URLEncodedUtils.parse(URI.create(url), "ISO-8859-1");
    }
}
