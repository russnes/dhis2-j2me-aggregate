package org.hisp.dhis.mobile.imagereports;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.util.Utils;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ProgressAlertFile
    extends Alert
    implements CommandListener
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ProgressAlertFile";

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
    OutputStream os;

    /**
     * 
     */
    int initialLength = 0, totalLength;

    /**
     * 
     */
    Gauge gauge;

    /**
     * 
     */
    boolean errorOccured, canceled;

    /**
     * 
     */
    static String savingFileStr = "Saving File";

    /**
     * 
     */
    Command cancelCommand = new Command( "Cancel", Command.CANCEL, 0 );

    /**
     * 
     */
    Command okCommand = new Command( "Ok", Command.OK, 0 );

    /**
     * 
     * @param midlet
     */
    public ProgressAlertFile( MIDlet midlet, OutputStream os, int totalLength )
    {
        super( "Please wait...", savingFileStr, null, AlertType.INFO );
        this.midlet = midlet;
        this.os = os;
        this.totalLength = totalLength;

        if ( totalLength > 0 )
        {
            gauge = new Gauge( null, false, totalLength, 0 );
            setIndicator( gauge );
        }

        addCommand( cancelCommand );
        setCommandListener( this );
    }

    /**
     * 
     */
    public void show()
    {
        previousDisplayable = Display.getDisplay( midlet ).getCurrent();
        Display.getDisplay( midlet ).setCurrent( this );
    }

    /**
     * 
     * @param b
     */
    public void write( byte[] b )
    {
        if ( b != null )
        {
            try
            {
                if ( os != null )
                {
                    os.write( b );
                    initialLength++;
                    update();
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "File," + CLASS_TAG, e );
                e.printStackTrace();
                showError();
            }
        }
    }

    /**
     * 
     */
    private void update()
    {
        if ( totalLength > 0 )
        {
            gauge.setValue( initialLength );

            String str = savingFileStr + ": " + initialLength + " / " + totalLength + " "
                + Utils.getPercentage( initialLength, totalLength ) + "%";

            setString( str );
        }
    }

    /**
     * 
     */
    private void showError()
    {
        LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "Showing an error..." );

        errorOccured = true;
        setString( "An error occurred!" );
        closeStream();
    }

    /**
     * 
     */
    public void closeStream()
    {
        LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "Closing OutputStream..." );

        removeAllCommands();
        addCommand( okCommand );

        if ( !errorOccured && !canceled )
        {
            setString( "Save File Successful!" );
        }

        try
        {
            if ( os != null )
            {
                os.close();
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            os = null;
        }
    }

    /**
     * 
     */
    private void removeAllCommands()
    {
        removeCommand( cancelCommand );
        removeCommand( okCommand );
    }

    /**
     * 
     */
    public void dismiss()
    {
        Display.getDisplay( midlet ).setCurrent( previousDisplayable );
    }

    /**
     * 
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == cancelCommand )
        {
            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Cancel command pressed" );

            canceled = true;
            setString( "User canceled" );
            closeStream();
        }
        else if ( c == okCommand )
        {
            LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "Ok command pressed" );

            dismiss();
        }
    }
}
