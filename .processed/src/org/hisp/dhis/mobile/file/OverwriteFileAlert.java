package org.hisp.dhis.mobile.file;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/**
 * 
 * @author Paul Mark Castillo
 *
 */
public class OverwriteFileAlert
    extends Alert
    implements CommandListener
{
    /**
     * 
     */
    Command okCommand = new Command( "OK", Command.OK, 0 );

    /**
     * 
     */
    Command cancelCommand = new Command( "Cancel", Command.CANCEL, 1 );

    /**
     * 
     */
    CallbackOverwrite callback;
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
    public OverwriteFileAlert( MIDlet midlet, Displayable previousDisplayable, CallbackOverwrite callback )
    {
        super( "Overwrite File", "Are you sure you want to overwrite the existing file?", null, AlertType.WARNING );
        this.callback = callback;
        this.midlet = midlet;
        this.previousDisplayable = previousDisplayable;
        setTimeout( FOREVER );

        addCommand( okCommand );
        addCommand( cancelCommand );
        setCommandListener( this );
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == okCommand )
        {
            Display.getDisplay( midlet ).setCurrent( previousDisplayable );
            callback.okOverwrite();
        }
        else if ( c == cancelCommand )
        {
            Display.getDisplay( midlet ).setCurrent( previousDisplayable );
            callback.cancelOverwrite();
        }
    }
}
