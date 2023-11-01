package com.nilscoding.maven.mvndlwsdl.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Trust Manager to accept all certificates.
 * @author NilsCoding
 */
public final class AcceptAllCertificatesTrustManager implements X509TrustManager {

    /**
     * Instance.
     */
    public static final AcceptAllCertificatesTrustManager INSTANCE = new AcceptAllCertificatesTrustManager();

    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
