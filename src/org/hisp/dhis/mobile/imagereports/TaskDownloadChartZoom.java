package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.connection2.CallbackNetworkResponse;
import org.hisp.dhis.mobile.connection2.ConnectionStates;
import org.hisp.dhis.mobile.connection2.ManagerNetwork;
import org.hisp.dhis.mobile.connection2.NetworkRequest;
import org.hisp.dhis.mobile.connection2.NetworkResponse;
import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class TaskDownloadChartZoom
    extends Thread
    implements CallbackNetworkResponse
{
    /**
     * 
     */
    private static final String CLASS_TAG = "TaskDownloadChartZoom";

    /**
     * 
     */
    MIDlet midlet;

    /**
     * 
     */
    Displayable previousDisplayable;

    /**
     * 
     */
    String url;

    /**
     * 
     * @param url
     */
    public TaskDownloadChartZoom( MIDlet midlet, Displayable previousDisplayable, String url )
    {
        this.midlet = midlet;
        this.previousDisplayable = previousDisplayable;
        this.url = url;
        
    }

    /**
     * 
     */
    public void run()
    {
        LogMan.log( LogMan.INFO, "Network,ImageReports" + CLASS_TAG, "Running TaskDownloadChartZoom..." );
        
        ProgressAlertNetwork alert = new ProgressAlertNetwork( true );
        alert.setIdleString( "" );
        alert.setConnectingString( "Connecting to the DHIS 2 Web API..." );
        alert.setConnectedString( "Connecting to the DHIS 2 Web API..." );
        alert.setSendingString( "" );
        alert.setSentString( "" );
        alert.setReceivingString( "Downloading Full-Sized Image" );
        alert.setReceivedString( "Image sucessfully downloaded!" );
        alert.setDisconnectingString( "Image sucessfully downloaded!" );
        alert.setDisconnectedString( "Image sucessfully downloaded!" );
        alert.setErrorString( "Oops! An error occurred!" );
        alert.setCanceledString( "Canceled" );
        alert.getProgress().setRealtimeUpdate( false );

        NetworkRequest request = new NetworkRequest();
        request.setCompletePath( url + "/data?width=640&height=400" );
        request.setResponseCallback( this );
        request.setProgressAlert( alert );

        ManagerNetwork.getInstance().addEvent( request );
    }

    /**
     * 
     */
    public void networkResponded( NetworkResponse response )
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Network Response Received" );

        if ( response.getLastStatus() != ConnectionStates.ERROR
            && response.getLastStatus() != ConnectionStates.CANCELED )
        {

            try
            {
                String content_disposition = (String) response.getResponseHeaders().get( "content-disposition" );

                ChartData file = new ChartData( "image", "png" );
                file.setData( response.getContent() );
                file.generateImage();
                file.parseContentDisposition( content_disposition );

                MIDlet midlet = ManagerNetwork.getMidlet();
                ViewChartZoom vcz = new ViewChartZoom( midlet, previousDisplayable, file );
                Display.getDisplay( midlet ).setCurrent( vcz );
            }
            catch ( Throwable e )
            {
                LogMan.log( "Network," + CLASS_TAG, e );
                e.printStackTrace();

                MIDlet midlet = ManagerNetwork.getMidlet();

                Alert alert = new Alert( "Error", "Unable to render image!", null, AlertType.ERROR );
                alert.setTimeout( Alert.FOREVER );

                Display.getDisplay( midlet ).setCurrent( alert );
            }
        }

    }

}
