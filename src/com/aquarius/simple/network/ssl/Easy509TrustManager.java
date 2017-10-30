package com.aquarius.simple.network.ssl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by aquarius on 2017/10/30.
 *
 * @see @link https://github.com/smanikandan14/Volley-demo
 */
public class Easy509TrustManager implements X509TrustManager{


    private X509TrustManager standardTrustManager = null;

    public Easy509TrustManager(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keyStore);
        TrustManager[] trustManagers = factory.getTrustManagers();
        if (trustManagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found!");
        }
        this.standardTrustManager = (X509TrustManager) trustManagers[0];
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[] certificates, String authType)
     */
    @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        standardTrustManager.checkClientTrusted(x509Certificates, authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[] certificates, String authType)
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        standardTrustManager.checkServerTrusted(x509Certificates, authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
