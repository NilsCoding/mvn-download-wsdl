package com.nilscoding.maven.mvndlwsdl.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * Trust Manager to accept all certificates
 * @author NilsCoding
 */
public class AcceptAllCertificatesTrustManager implements X509TrustManager {

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
