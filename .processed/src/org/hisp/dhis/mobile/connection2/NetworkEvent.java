package org.hisp.dhis.mobile.connection2;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;

import org.hisp.dhis.mobile.imagereports.ProgressAlertNetwork;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.util.MemoryMonitor;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class NetworkEvent
    extends Thread
{
    /**
     * 
     */
    private static final String CLASS_TAG = "NetworkEvent";

    /**
     * 
     */
    ManagerHTTP httpMan;

    /**
     * 
     */
    HttpConnection hc;

    /**
     * 
     */
    ProgressConn progressConn;

    /**
     * 
     */
    NetworkResponse response;

    /**
     * 
     */
    boolean canceled = false;

    /**
     * 
     */
    private ParamsHTTP params;

    /**
     * 
     */
    private NetworkRequest request;

    /**
     * 
     */
    private ProgressAlertNetwork progressAlert;

    /**
     * 
     * @param params
     * @param request
     */
    public NetworkEvent( ParamsHTTP params, NetworkRequest request )
    {
        this.params = params;
        this.request = request;
        this.progressAlert = request.getProgressAlert();
        this.progressConn = progressAlert.getProgress();
    }

    /**
     * 
     */
    public void run()
    {
        httpMan = new ManagerHTTP();
        response = new NetworkResponse();
        try
        {
            hc = httpMan.connect( params, progressConn );

            if ( !canceled )
            {
                int code = hc.getResponseCode();
                String message = hc.getResponseMessage();
                long lenght = hc.getLength();

                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + code );
                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Message: " + message );
                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Length: " + lenght );

                response.setResponseCode( code );
                response.setResponseDescription( message );

                int i = 0;
                String key;
                Hashtable responseHeaders = new Hashtable();
                do
                {
                    key = hc.getHeaderFieldKey( i );
                    if ( key != null )
                    {
                        String value = hc.getHeaderField( key );
                        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response HTTP Header: " + key + ": " + value );
                        responseHeaders.put( key, value );
                    }
                    i++;
                }
                while ( key != null );

                response.setResponseHeaders( responseHeaders );
            }

            if ( !canceled )
            {
                byte[] b = httpMan.downloadBinary( hc, progressConn );

                if ( b != null )
                {
                    LogMan.log( LogMan.DEV, "Network," + CLASS_TAG,
                        "Actual Content Length: " + MemoryMonitor.convertToReadableBytes( b.length ) );
                }

                response.setContent( b );
            }

            if ( !canceled )
            {
                disconnect( false );

                response.setLastStatus( progressConn.getState() );

                progressAlert.dismissAlert();
            }
        }
        catch ( Exception e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();
            if ( !canceled )
            {
                progressConn.setState( ConnectionStates.ERROR );
                response.setLastStatus( ConnectionStates.ERROR );
                response.setException( e );
            }
            progressAlert.showOkCommand();
        }

        request.getResponseCallback().networkResponded( response );
    }

    /**
     * @throws IOException
     * 
     */
    public void disconnect( boolean canceled )
    {
        try
        {
            httpMan.disconnect( hc, progressConn );
        }
        catch ( IOException e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();
        }

        if ( canceled )
        {
            this.canceled = true;
            progressConn.setState( ConnectionStates.CANCELED );
            response.setLastStatus( ConnectionStates.CANCELED );
            progressAlert.showOkCommand();
        }
    }
}
