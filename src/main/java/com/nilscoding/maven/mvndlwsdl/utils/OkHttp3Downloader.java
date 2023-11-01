package com.nilscoding.maven.mvndlwsdl.utils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.maven.plugin.logging.Log;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provided downloader implementation using OkHttp 3.
 * @author NilsCoding
 */
public class OkHttp3Downloader implements IDownloader {

    /**
     * Minimum proxy port number.
     */
    protected static final int PROXY_MIN_PORT = 1;
    /**
     * Maximum proxy port number.
     */
    protected static final int PROXY_MAX_PORT = 65535;

    /**
     * Creates a new instance.
     */
    public OkHttp3Downloader() {
    }

    /**
     * Downloads the text file from given URL.
     * @param url        URL to download from
     * @param log        logging
     * @param optionsStr options string (specific to implementation)
     * @return downloaded text file content or null on error
     */
    @Override
    public String downloadFile(String url, Log log, String optionsStr) {
        try {
            Map<String, String> options = StringUtils.parseOptions(optionsStr);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(AcceptAllCertificatesTrustManager.INSTANCE),
                            AcceptAllCertificatesTrustManager.INSTANCE)
                    .hostnameVerifier((host, sess) -> true);

            // optional: proxy
            String proxyName = options.get("proxyHost");
            int proxyPort = StringUtils.parseToInt(options.get("proxyPort"), -1);
            if ((StringUtils.isEmpty(proxyName) == false)
                    && (proxyPort >= PROXY_MIN_PORT)
                    && (proxyPort <= PROXY_MAX_PORT)) {
                clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyName, proxyPort)));
            }
            // optional: authentication
            Map<String, String> optionalHeaders = new LinkedHashMap<>();
            String authType = options.get("authType");
            if ("header".equalsIgnoreCase(authType)) {
                String headerValue = options.get("authValue");
                if (StringUtils.isEmpty(headerValue) == false) {
                    optionalHeaders.put("Authorization", headerValue.trim());
                }
            } else if ("basic".equalsIgnoreCase(authType)) {
                String authValue = options.get("authValue");
                if (StringUtils.isEmpty(authValue) == false) {
                    optionalHeaders.put("Authorization", "Basic " + authValue.trim());
                } else {
                    String authUser = options.get("authUser");
                    String authPass = options.get("authPass");
                    if ((StringUtils.isEmpty(authUser) == false) && (StringUtils.isEmpty(authPass) == false)) {
                        authValue = Credentials.basic(authUser, authPass);
                        optionalHeaders.put("Authorization", authValue);
                    }
                }
            }

            OkHttpClient client = clientBuilder.build();
            Request.Builder requestBuilder = new Request.Builder();
            if (optionalHeaders.isEmpty() == false) {
                optionalHeaders.forEach(requestBuilder::header);
            }
            Request request = requestBuilder.url(url).build();
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
     * Creates an SSLSocketFactory.
     * @param tm trust manager, can be null
     * @return SSL socket factory or null on error
     */
    public static SSLSocketFactory createSSLSocketFactory(TrustManager tm) {
        try {
            final String protocol = "SSL";
            SSLContext sslContext = SSLContext.getInstance(protocol);
            KeyManager[] keyManagers = null;
            SecureRandom secureRandom = new SecureRandom();
            TrustManager[] tms = (tm == null) ? null : new TrustManager[]{tm};
            sslContext.init(keyManagers, tms, secureRandom);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception ex) {
            return null;
        }
    }
}
