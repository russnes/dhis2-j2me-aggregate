package org.hisp.dhis.mobile.connection2;

import java.util.Hashtable;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.imagereports.ProgressAlertNetwork;
import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ManagerNetwork
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ManagerNetwork";

    /**
     * 
     */
    private static MIDlet midlet;

    /**
    * 
    */
    private String serverURL, userAgent, acceptedLanguage, contentType, accept, username, password, authorization;

    /**
    *
    */
    private static ManagerNetwork instance;

    /**
     * 
     */
    private ManagerNetwork()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Initializing Network Manager..." );
    }

    /**
     * 
     * @return
     */
    public static ManagerNetwork getInstance()
    {
        if ( instance == null )
        {
            instance = new ManagerNetwork();
        }
        return instance;
    }

    /**
     * 
     * @param request
     */
    public void addEvent( NetworkRequest request )
    {
        ParamsHTTP params = getDefaultParameters();

        if ( request.getCompletePath() != null )
        {
            params.setURI( request.getCompletePath() );
        }
        else
        {
            params.setURI( getServerURL() + request.getPath() );
        }

        ProgressAlertNetwork progress = request.getProgressAlert();
        progress.setMidlet( midlet );
        progress.setPreviousDisplayable( Display.getDisplay( midlet ).getCurrent() );

        NetworkEvent event = new NetworkEvent( params, request );

        progress.setNetworkEvent( event );
        Display.getDisplay( midlet ).setCurrent( progress );

        event.start();
    }

    /**
     * 
     * @return
     */
    private ParamsHTTP getDefaultParameters()
    {
        ParamsHTTP params = new ParamsHTTP();

        Hashtable httpHeaders = new Hashtable();
        httpHeaders.put( "User-Agent", getUserAgent() );
        httpHeaders.put( "Accept-Language", getAcceptedLanguage() );
        httpHeaders.put( "Content-Type", getContentType() );
        httpHeaders.put( "Accept", getAccept() );
        httpHeaders.put( "Authorization", getAuthorization() );

        params.setRequestProperty( httpHeaders );
        params.setURI( getServerURL() );
        params.setRequestMethod( HttpConnection.GET );

        return params;
    }

    /**
     * @return the serverURL
     */
    public String getServerURL()
    {
        return serverURL;
    }

    /**
     * @param serverURL the serverURL to set
     */
    public void setServerURL( String serverURL )
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Setting Server URL: " + serverURL );
        this.serverURL = serverURL;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent()
    {
        return userAgent;
    }

    /**
     * @param userAgent the userAgent to set
     */
    public void setUserAgent( String userAgent )
    {
        this.userAgent = userAgent;
    }

    /**
     * @return the acceptedLanguage
     */
    public String getAcceptedLanguage()
    {
        return acceptedLanguage;
    }

    /**
     * @param acceptedLanguage the acceptedLanguage to set
     */
    public void setAcceptedLanguage( String acceptedLanguage )
    {
        this.acceptedLanguage = acceptedLanguage;
    }

    /**
     * @return the contentType
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType( String contentType )
    {
        this.contentType = contentType;
    }

    /**
     * @return the accepted
     */
    public String getAccept()
    {
        return accept;
    }

    /**
     * @param accepted the accepted to set
     */
    public void setAccept( String accept )
    {
        this.accept = accept;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername( String username )
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * @return the authorization
     */
    public String getAuthorization()
    {
        return authorization;
    }

    /**
     * @param authorization the authorization to set
     */
    public void setAuthorization( String authorization )
    {
        this.authorization = authorization;
    }

    /**
     * 
     * @return
     */
    public static MIDlet getMidlet()
    {
        return midlet;
    }

    /**
     * 
     * @param midlet
     */
    public static void setMidlet( MIDlet midlet )
    {
        ManagerNetwork.midlet = midlet;
    }
}
