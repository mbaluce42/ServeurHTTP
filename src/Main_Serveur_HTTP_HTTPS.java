import com.sun.net.httpserver.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

import MODEL.handler.AuthorsHandler;
import MODEL.handler.BooksHandler;
import MODEL.handler.SubjectsHandler;

public class Main_Serveur_HTTP_HTTPS
{
    private static final int PORT_HTTP = 8080;
    private static final int PORT_HTTPS = 8443;
    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEYSTORE_PASSWORD = "password";

    public static void main(String[] args)
    {
        try
        {
            startHttpServer();
            startHttpsServer();
            System.out.println("Serveur HTTP démarré sur le port " + PORT_HTTP);
            System.out.println("Serveur HTTPS démarré sur le port " + PORT_HTTPS);
        }
        catch (Exception e)
        {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startHttpServer() throws IOException
    {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT_HTTP), 0);
        configureContexts(httpServer);
        httpServer.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        httpServer.start();
    }

    private static void startHttpsServer() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        //chargement du keystore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fileInputStream = new FileInputStream("keystore.jks"))
        {
            keyStore.load(fileInputStream,KEYSTORE_PASSWORD.toCharArray());
        }

        //config gestionnaire de clés
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());//permet de charger les clés

        //config gestionnaire de confiance
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        //config SSLContext
        SSLContext SsIC = SSLContext.getInstance("TLSv1.3");
        SsIC.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),null);

        //création du serveur
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(PORT_HTTPS), 0);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(SsIC)//application de la configuration SSL
        {
            public void configure(HttpsParameters params)
            {
                try
                {
                    //SSL pour le serveur
                    SSLContext context = getSSLContext();//récupération du contexte SSL
                    SSLParameters sslParameters = context.getDefaultSSLParameters();//récupération des paramètres SSL par défaut
                    sslParameters.setNeedClientAuth(false);//pas besoin d'authentification client
                    params.setSSLParameters(sslParameters);//application des paramètres SSL
                }
                catch (Exception e)
                {
                    System.err.println("Erreur lors de la configuration SSL: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        configureContexts(httpsServer);
        httpsServer.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        httpsServer.start();
    }

    private static void configureContexts(HttpServer server)
    {
        server.createContext("/api/subjects", new SubjectsHandler());
        server.createContext("/api/authors", new AuthorsHandler());
        server.createContext("/api/books", new BooksHandler());
    }
}