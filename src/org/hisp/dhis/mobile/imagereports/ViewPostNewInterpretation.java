package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.view.AbstractView;

public class ViewPostNewInterpretation
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "ViewPostNewInterpretation";

    private FacilityMIDlet facilityMIDlet;

    private TextBox interTextbox;

    private Command backCommand;

    private Command sendCommand;

    public ViewPostNewInterpretation( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        interTextbox = null;
        backCommand = null;
        sendCommand = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getInterTextbox() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            dhisMIDlet.getMainMenuView().showView();
        }
        else if ( command == this.getSendCommand() )
        {

            try
            {
                String data = StringRecordStore.load() + this.getInterTextbox().getString();
                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getPostInterpretationUrl() );
                ConnectionManager.postNewInterpretation( data );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public TextBox getInterTextbox()
    {
        if ( interTextbox == null )
        {
            interTextbox = new TextBox( "Enter interpretation", "", 99999, 0 );
            interTextbox.addCommand( this.getBackCommand() );
            interTextbox.addCommand( this.getSendCommand() );
            interTextbox.setCommandListener( this );
        }
        return interTextbox;
    }

    public void setInterTextbox( TextBox interTextbox )
    {
        this.interTextbox = interTextbox;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND(), Command.SCREEN, 0 );
        }
        return sendCommand;
    }

    public void setSendCommand( Command sendCommand )
    {
        this.sendCommand = sendCommand;
    }

}
