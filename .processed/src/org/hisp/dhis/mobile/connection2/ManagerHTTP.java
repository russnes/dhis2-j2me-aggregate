package org.hisp.dhis.mobile.connection2;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpConnection;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ManagerHTTP
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ManagerHTTP";

    /**
     *
     */
    private ManagerConn connMan;

    /**
     *
     */
    public ManagerHTTP()
    {
        ManagerIO ioMan = new ManagerIO();
        ManagerConn connMan = new ManagerConn( ioMan );
        setConnMan( connMan );
    }

    /**
     * 
     * @param connMan
     */
    public ManagerHTTP( ManagerConn connMan )
    {
        setConnMan( connMan );
    }

    /**
     * 
     * @param httpParams
     * @return
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public HttpConnection connect( ParamsHTTP httpParams, ProgressConn progress )
        throws IllegalArgumentException, ConnectionNotFoundException, IOException, SecurityException
    {
        HttpConnection httpConnection = (HttpConnection) connMan.connect( httpParams, progress );
        httpConnection.setRequestMethod( httpParams.getRequestMethod() );

        Hashtable requestProperties = httpParams.getRequestProperty();
        Enumeration enu = requestProperties.keys();
        while ( enu.hasMoreElements() )
        {
            Object keyObj = enu.nextElement();
            String keyStr = (String) keyObj;
            String valueStr = (String) requestProperties.get( keyObj );

            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Adding Request Property: " + keyStr + "=" + valueStr );
            httpConnection.setRequestProperty( keyStr, valueStr );
        }

        return httpConnection;
    }

    /**
     * 
     * @param httpConnection
     * @throws IOException
     */
    public void disconnect( HttpConnection httpConnection, ProgressConn progress )
        throws IOException
    {
        connMan.disconnect( httpConnection, progress );
    }

    /**
     * 
     * @param httpConnection
     * @param request
     * @throws IOException
     */
    public void upload( HttpConnection httpConnection, String request, ProgressConn progress )
        throws IOException
    {
        if ( request == null )
        {
            request = "";
        }

        char c[] = request.toCharArray();
        int charLength = c.length;

        httpConnection.setRequestProperty( "Content-Length", charLength + "" );
        connMan.send( httpConnection, c, progress );
    }

    /**
     * 
     * @param httpConnection
     * @return
     * @throws IOException
     */
    public String downloadTexts( HttpConnection httpConnection, ProgressConn progress )
        throws IOException
    {
        String str = connMan.receiveTexts( httpConnection, progress );
        return str;
    }

    /**
     * 
     * @param httpConnection
     * @return
     * @throws IOException
     */
    public byte[] downloadBinary( HttpConnection httpConnection, ProgressConn progress )
        throws IOException
    {
        byte b[] = connMan.receiveBinary( httpConnection, progress );
        return b;
    }

    /**
     * @return the connMan
     */
    public ManagerConn getConnMan()
    {
        return connMan;
    }

    /**
     * @param connMan the connMan to set
     */
    public void setConnMan( ManagerConn connMan )
    {
        this.connMan = connMan;
    }
}
