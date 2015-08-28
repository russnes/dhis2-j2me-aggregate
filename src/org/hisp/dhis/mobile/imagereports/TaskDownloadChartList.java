package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.connection2.CallbackNetworkResponse;
import org.hisp.dhis.mobile.connection2.ConnectionStates;
import org.hisp.dhis.mobile.connection2.ManagerNetwork;
import org.hisp.dhis.mobile.connection2.NetworkRequest;
import org.hisp.dhis.mobile.connection2.NetworkResponse;
import org.hisp.dhis.mobile.log.LogMan;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class TaskDownloadChartList
    extends Thread
    implements CallbackNetworkResponse
{
    /**
     * 
     */
    private static final String CLASS_TAG = "TaskDownloadChartList";

    /**
     * 
     */
    public void run()
    {
        LogMan.log( LogMan.INFO, "Network,ImageReports" + CLASS_TAG, "Running TaskDownloadChartList..." );
        
        NetworkRequest request = new NetworkRequest();
        request.setPath( "/api/charts.json?paging=false" );
        request.setResponseCallback( this );

        ProgressAlertNetwork alert = new ProgressAlertNetwork( true );
        alert.setIdleString( "" );
        alert.setConnectingString( "Connecting to the DHIS 2 Web API..." );
        alert.setConnectedString( "Connecting to the DHIS 2 Web API..." );
        alert.setSendingString( "" );
        alert.setSentString( "" );
        alert.setReceivingString( "Downloading Chart List" );
        alert.setReceivedString( "Chart list sucessfully downloaded!" );
        alert.setDisconnectingString( "Chart list sucessfully downloaded!" );
        alert.setDisconnectedString( "Chart list sucessfully downloaded!" );
        alert.setErrorString( "Oops! An error occurred!" );
        alert.setCanceledString( "Canceled" );

        alert.getProgress().setRealtimeUpdate( false );

        request.setProgressAlert( alert );

        ManagerNetwork.getInstance().addEvent( request );
    }

    /**
     * 
     */
    public void networkResponded( NetworkResponse response )
    {
        LogMan.log( LogMan.INFO, "Network,ImageReports," + CLASS_TAG, "Network Response Received" );

        if ( response.getLastStatus() != ConnectionStates.ERROR
            && response.getLastStatus() != ConnectionStates.CANCELED )
        {

            try
            {
                String responseStr = new String( response.getContent() );
                JSONObject responseJSON = new JSONObject( responseStr );

                LogMan.log( LogMan.INFO, "Network,ImageReports" + CLASS_TAG, "Response: " + responseJSON.toString( 5 ) );

                JSONArray charts = responseJSON.getJSONArray( "charts" );

                MIDlet midlet = ManagerNetwork.getMidlet();
                ViewChartList chartsView = new ViewChartList( charts );

                Display.getDisplay( midlet ).setCurrent( chartsView );
            }
            catch ( JSONException e )
            {
                LogMan.log( "Network," + CLASS_TAG, e );
                e.printStackTrace();

                MIDlet midlet = ManagerNetwork.getMidlet();

                Alert alert = new Alert( "Error", "Unable to parse chart list!", null, AlertType.ERROR );
                alert.setTimeout( Alert.FOREVER );

                Display.getDisplay( midlet ).setCurrent( alert );
            }
        }
    }
}
