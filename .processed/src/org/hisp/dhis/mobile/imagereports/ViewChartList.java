package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ViewChartList
    extends List
    implements CommandListener
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ViewChartList";

    /**
     * 
     */
    JSONArray charts;

    /**
     * 
     */
    Command cmdExit = new Command( "Exit", Command.OK, 0 );

    /**
     * 
     * @param charts
     */
    public ViewChartList( JSONArray charts )
    {
        super( "Chart List", IMPLICIT );
        this.charts = charts;

        LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Initializing ViewChartList Screen..." );
        try
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "charts: " + charts.toString( 5 ) );
        }
        catch ( Throwable e )
        {
        }

        for ( int i = 0; i < charts.length(); i++ )
        {
            try
            {
                JSONObject chart = charts.getJSONObject( i );
                String name = chart.getString( "name" );

                append( name, null );
            }
            catch ( Exception e )
            {
                LogMan.log( "Network," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }

        addCommand( cmdExit );
        setCommandListener( this );
    }

    /**
     * 
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == SELECT_COMMAND )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Select Command Pressed" );

            try
            {
                JSONObject chart = charts.getJSONObject( getSelectedIndex() );

                new TaskDownloadChartPreview( chart, this ).start();
            }
            catch ( JSONException e )
            {
                LogMan.log( "Network," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        else if ( c == cmdExit )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Exit Command Pressed" );

            ConnectionManager.getDhisMIDlet().getMainMenuView().showView();
        }
    }

}
