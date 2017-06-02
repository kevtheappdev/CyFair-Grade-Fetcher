package com.kevinturner.jv;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import apns.ApnsConnection;
import apns.ApnsConnectionFactory;
import apns.ApnsConnectionPool;
import apns.ApnsException;
import apns.ApnsRuntimeException;
import apns.CannotOpenConnectionException;
import apns.CannotUseConnectionException;
import apns.DefaultApnsConnectionFactory;
import apns.DefaultPushNotificationService;
import apns.PayloadException;
import apns.PushNotification;
import apns.PushNotificationService;
import apns.keystore.KeyStoreProvider;
import apns.keystore.WrapperKeyStoreProvider;
import com.google.appengine.api.taskqueue.DeferredTask;

public class SendPushNotification implements DeferredTask {
  

		  private static final long serialVersionUID = 1L;

		  private static volatile ApnsConnectionFactory sApnsConnectionFactory;
		  private static volatile ApnsConnectionPool sApnsConnectionPool;
		  private static volatile PushNotificationService sPushNotificationService;
		  private static final int APNS_CONNECTION_POOL_CAPACITY = 5;

		  private final PushNotification mPushNotification;

		  public SendPushNotification(PushNotification pushNotification) {
		    mPushNotification = pushNotification;
		   
		  }

		  @Override
		  public void run() {
		    try {
		      trySendingPushNotification();
		    } catch (CannotOpenConnectionException e) {
		      throw new RuntimeException("Could not connect to APNS", e);
		    } catch (CannotUseConnectionException e) {
		       throw new RuntimeException("Could not send: " + mPushNotification, e);
		    } catch (PayloadException e) {
		        //getLogger().error("Could not send push notification (dropping task)", e);
		    } catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }

		  private void trySendingPushNotification() throws CannotOpenConnectionException, CannotUseConnectionException, PayloadException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		    ApnsConnection apnsConnection = getApnsConnectionPool().obtain();
		    if (apnsConnection == null) {
		      apnsConnection = openConnection();
		    }

		    try {
		      //getLogger().debug("Sending push notification: {}", mPushNotification);
		      getPushNotificationService().send(mPushNotification, apnsConnection);
		      getApnsConnectionPool().put(apnsConnection);
		    } catch (CannotUseConnectionException e) {
		      //getLogger().debug("Could not send push notification - opening new connection");
		      apnsConnection = openConnection();
		      //getLogger().debug("Retrying sending push notification");
		      getPushNotificationService().send(mPushNotification, apnsConnection);
		      getApnsConnectionPool().put(apnsConnection);
		    }
		  }

		  private static ApnsConnectionPool getApnsConnectionPool() {
		    if (sApnsConnectionPool == null) {
		      synchronized (SendPushNotification.class) {
		        if (sApnsConnectionPool == null) {
		          sApnsConnectionPool = new ApnsConnectionPool(APNS_CONNECTION_POOL_CAPACITY);
		        }        
		      }
		    }  
		    return sApnsConnectionPool;
		  }

		  private static PushNotificationService getPushNotificationService() {
		    if (sPushNotificationService == null) {
		      synchronized (SendPushNotification.class) {
		        if (sPushNotificationService == null) {
		          sPushNotificationService = new DefaultPushNotificationService();
		        }      
		      }
		    }  
		    return sPushNotificationService;
		  }

		  private static ApnsConnection openConnection() throws CannotOpenConnectionException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		    //getLogger().debug("Connecting to APNS");
		    return getApnsConnectionFactory().openPushConnection();
		  }  

		  private static ApnsConnectionFactory getApnsConnectionFactory() throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException {
		    if (sApnsConnectionFactory == null) {
		    	synchronized (SendPushNotification.class) {
		        if (sApnsConnectionFactory == null) {
		          DefaultApnsConnectionFactory.Builder builder = DefaultApnsConnectionFactory.Builder.get();
		          String certPath = SendPushNotification.class.getResource("Production.p12").getPath();
		          //KeyStoreProvider ksp = new ClassPathResourceKeyStoreProvider(certPath, KeyStoreType.PKCS12, new char[0]);
		            System.out.println("Creating apns connection, sending notification");      
		          KeyStore p12 = KeyStore.getInstance("pkcs12");
		          p12.load(new FileInputStream(certPath), "".toCharArray());
		          KeyStoreProvider ksp = new WrapperKeyStoreProvider(p12, "".toCharArray());
		          builder.setSandboxKeyStoreProvider(ksp);
		          try {
		            sApnsConnectionFactory = builder.build();
		          } catch (ApnsException e) {
		        	  System.out.println("It didnt work");
		            throw new ApnsRuntimeException("Could not create APNS connection factory", e);
		          
		          }
		      
		        }  
		    	 }
		    	
		    }
		    return sApnsConnectionFactory;
		  
		    
		 } 
	
}
