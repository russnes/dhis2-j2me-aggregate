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
import org.json.me.JSONObject;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class TaskDownloadChartPreview
    extends Thread
    implements CallbackNetworkResponse
{
    /**
     * 
     */
    private static final String CLASS_TAG = "TaskDownloadChartPreview";

    /**
     * 
     */
    JSONObject chart;

    /**
     * 
     */
    private Displayable previousDisplayable;

    /**
     * 
     */
    public TaskDownloadChartPreview( JSONObject chart, Displayable previousDisplayable )
    {
        this.chart = chart;
        this.previousDisplayable = previousDisplayable;
    }

    /**
     * 
     */
    public void run()
    {
        LogMan.log( LogMan.INFO, "Network,ImageReports" + CLASS_TAG, "Running TaskDownloadChartPreview..." );

        try
        {
            String href = chart.getString( "href" );

            NetworkRequest request = new NetworkRequest();

            int origScreenWidth = UtilUI.getScreenWidth();
            int origScreenHeight = UtilUI.getScreenHeight();

            long screenWidthFraction = UtilUI.fraction( origScreenWidth, 5 );
            long screenHeightFraction = UtilUI.fraction( origScreenHeight, 2 );

            long finalWidth = (origScreenWidth - screenWidthFraction);
            long finalHeight = (origScreenHeight - screenHeightFraction);

            LogMan.log( LogMan.DEBUG, "Network,ImageReports" + CLASS_TAG, "Final Image to be downloaded: " + finalWidth
                + "x" + finalHeight );

            request.setCompletePath( href + "/data?width=" + finalWidth + "&height=" + finalHeight );

            request.setResponseCallback( this );

            ProgressAlertNetwork alert = new ProgressAlertNetwork( true );
            alert.setIdleString( "" );
            alert.setConnectingString( "Connecting to the DHIS 2 Web API..." );
            alert.setConnectedString( "Connecting to the DHIS 2 Web API..." );
            alert.setSendingString( "" );
            alert.setSentString( "" );
            alert.setReceivingString( "Downloading Image" );
            alert.setReceivedString( "Image sucessfully downloaded!" );
            alert.setDisconnectingString( "Image sucessfully downloaded!" );
            alert.setDisconnectedString( "Image sucessfully downloaded!" );
            alert.setErrorString( "Oops! An error occurred!" );
            alert.setCanceledString( "Canceled" );

            alert.getProgress().setRealtimeUpdate( false );

            request.setProgressAlert( alert );

            ManagerNetwork.getInstance().addEvent( request );
        }
        catch ( Exception e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    public void networkResponded( NetworkResponse response )
    {
        LogMan.log( LogMan.INFO, "Network,ImageReports" + CLASS_TAG, "Network Response Received" );

        if ( response.getLastStatus() != ConnectionStates.ERROR
            && response.getLastStatus() != ConnectionStates.CANCELED )
        {
            try
            {
                String content_disposition = (String) response.getResponseHeaders().get( "content-disposition" );

                ChartData file = new ChartData( "image", "png" );
                file.setDataJSON( chart );
                file.setData( response.getContent() );
                file.generateImage();
                file.parseContentDisposition( content_disposition );

                MIDlet midlet = ManagerNetwork.getMidlet();
                ViewChartPreview iv = new ViewChartPreview( midlet, previousDisplayable, file );
                Display.getDisplay( midlet ).setCurrent( iv );
            }
            catch ( Exception e )
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
