package com.nilscoding.maven.mvndlwsdl.utils;

import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.maven.plugin.logging.Log;

/**
 * Download utils
 * @author NilsCoding
 */
public class DownloadUtils {
    
    private DownloadUtils() { }
    
    /**
     * Downloads the content from the given URL to a string
     * @param url   URL
     * @param log   logging
     * @return  URL content as string or null
     */
    public static String download(String url, Log log) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(AcceptAllCertificatesTrustManager.INSTANCE), AcceptAllCertificatesTrustManager.INSTANCE)
                    .hostnameVerifier((host, sess) -> true)
                    .build();
            Request request = new Request.Builder().url(url).build();
            String contentStr;
            try (Response response = client.newCall(request).execute()) {
                contentStr = response.body().string();
            }
            return contentStr;
        } catch (Exception ex) {
            log.error("could not fetch file from '" + url + "': " + ex, ex);
            return null;
        }
    }
    
    /**
     * Creates an SSLSocketFactory
     * @param tm    trust manager, can be null
     * @return  SSL socket factory or null on error
     */
    public static SSLSocketFactory createSSLSocketFactory(TrustManager tm) {
        try {
            final String PROTOCOL = "SSL";
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
            KeyManager[] keyManagers = null;
            SecureRandom secureRandom = new SecureRandom();
            TrustManager[] tms = (tm == null) ? null : new TrustManager[] { tm };
            sslContext.init(keyManagers, tms, secureRandom);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception ex) {
            return null;
        }
    }
    
}
