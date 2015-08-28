package org.hisp.dhis.mobile.imagereports;

import java.io.OutputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.file.CallbackSaveFile;
import org.hisp.dhis.mobile.file.FileManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.json.me.JSONException;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ViewChartPreview
    extends Form
    implements CommandListener, CallbackSaveFile
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ViewChartPreview";

    /**
    *
    */
    private Command cmdBack = new Command( "Back", Command.BACK, 0 );

    /**
     * 
     */
    private Command cmdSave = new Command( "Save", Command.BACK, 1 );

    /**
     * 
     */
    private Command cmdZoom = new Command( "Zoom", Command.BACK, 2 );

    /**
     * 
     */

    private Command cmdInterpretations = new Command( "Interpretations", Command.SCREEN, 0 );

    /**
     * 
     */
    private MIDlet midlet;

    /**
     * 
     */
    private Displayable previousDisplayable;

    /**
     * 
     */
    private ChartData imageFile;

    /**
     * 
     * @param midlet
     */

    public ViewChartPreview( MIDlet midlet, Displayable previousDisplayable, ChartData imageFile )
    {
        super( "Chart" );
        this.midlet = midlet;
        this.previousDisplayable = previousDisplayable;
        this.imageFile = imageFile;

        LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Initializing ViewChartPreview Screen..." );

        setTitle( imageFile.getFileName() );

        append( imageFile.getImageItem() );
        addCommand( cmdBack );
        addCommand( cmdSave );
        addCommand( cmdZoom );
        addCommand( cmdInterpretations );
        setCommandListener( this );
    }

    /**
    * 
    */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == cmdBack )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Back Command Pressed" );

            Display.getDisplay( midlet ).setCurrent( previousDisplayable );
        }
        else if ( c == cmdSave )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Save Command Pressed" );

            FileManager fm = new FileManager( midlet );
            fm.saveFiles( imageFile.getFileName() + " - LowRes" + "." + imageFile.getFileExtension(), this );
        }
        else if ( c == cmdZoom )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Zoom Command Pressed" );

            String url = "";

            try
            {
                url = imageFile.getDataJSON().getString( "href" );
            }
            catch ( JSONException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            new TaskDownloadChartZoom( midlet, this, url ).start();
        }
        else if ( c == cmdInterpretations )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Interpretations Command Pressed" );
            try
            {
                String uId = imageFile.getDataJSON().getString( "id" );

                try
                {
                    StringRecordStore.deleteRecord();
                    StringRecordStore.saveString( uId );

                    ConnectionManager.getDhisMIDlet().getLoadInterpretationView().showView();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }

            }
            catch ( JSONException e )
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * 
     */
    public void saveFile( OutputStream os )
    {
        LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Saving to File..." );

        ProgressAlertFile alert = new ProgressAlertFile( midlet, os, -1 );
        alert.show();
        alert.write( imageFile.getData() );
        alert.closeStream();
    }
}
