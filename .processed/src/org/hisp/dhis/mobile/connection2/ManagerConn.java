package org.hisp.dhis.mobile.connection2;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ManagerConn
{

    /**
     * 
     */
    private static final String CLASS_TAG = "ManagerConn";

    /**
     *
     */
    private ManagerIO ioMan;

    /**
     *
     */
    public ManagerConn( ManagerIO ioMan )
    {
        setIoMan( ioMan );
    }

    /**
     * 
     * @param connParams
     * @return
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public Connection connect( ParamsConn connParams, ProgressConn progress )
        throws IllegalArgumentException, ConnectionNotFoundException, IOException, SecurityException
    {
        String uri = connParams.getURI();
        int mode = connParams.getMode();
        boolean timeouts = connParams.isTimeouts();

        if ( progress != null )
        {
            progress.setState( ConnectionStates.CONNECTING );
        }

        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Connecting: " + uri );
        Connection conn = Connector.open( uri, mode, timeouts );

        if ( progress != null )
        {
            progress.setState( ConnectionStates.CONNECTED );
        }

        return conn;
    }

    /**
     * 
     * @param conn
     * @throws IOException
     */
    public void disconnect( Connection conn, ProgressConn progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setState( ConnectionStates.DISCONNECTING );
        }

        conn.close();
        conn = null;

        if ( progress != null )
        {
            progress.setState( ConnectionStates.DISCONNECTED );
        }
        else
        {
        }
    }

    /**
     * 
     * @param conn
     * @param request
     * @throws IOException
     */
    public void send( OutputConnection conn, byte request[], ProgressConn progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setState( ConnectionStates.SENDING );
        }

        OutputStream os = conn.openOutputStream();
        ioMan.write( os, request );
        ioMan.close( os );

        if ( progress != null )
        {
            progress.setState( ConnectionStates.SENT );
        }
    }

    /**
     * 
     * @param conn
     * @param request
     * @throws IOException
     */
    public void send( OutputConnection conn, char request[], ProgressConn progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setState( ConnectionStates.SENDING );
        }

        OutputStream os = conn.openOutputStream();
        ioMan.write( os, request );
        ioMan.close( os );

        if ( progress != null )
        {
            progress.setState( ConnectionStates.SENT );
        }
    }

    /**
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    public byte[] receiveBinary( InputConnection conn, ProgressConn progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setState( ConnectionStates.RECEIVING );
        }

        InputStream is = conn.openInputStream();
        byte b[] = ioMan.readBinary( is, progress );
        ioMan.close( is );

        if ( progress != null )
        {
            progress.setState( ConnectionStates.RECEIVED );
        }
        return b;
    }

    /**
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    public String receiveTexts( ContentConnection conn, ProgressConn progress )
        throws IOException
    {
        DataInputStream dis = conn.openDataInputStream();

        // String encoding = conn.getEncoding();
        long length = conn.getLength();
        // String type = conn.getType();

        if ( progress != null )
        {
            progress.setState( ConnectionStates.RECEIVING );
        }

        String str;
        if ( length > 0 )
        {
            str = ioMan.readTextsFully( dis, length, progress );
        }
        else
        {
            str = ioMan.readTexts( dis, progress );
        }

        if ( progress != null )
        {
            progress.setState( ConnectionStates.RECEIVED );
        }

        ioMan.close( dis );
        return str;
    }

    /**
     * @return the ioMan
     */
    public ManagerIO getIoMan()
    {
        return ioMan;
    }

    /**
     * @param ioMan the ioMan to set
     */
    public void setIoMan( ManagerIO ioMan )
    {
        this.ioMan = ioMan;
    }
}
