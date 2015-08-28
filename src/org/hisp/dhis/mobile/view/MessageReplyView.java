package org.hisp.dhis.mobile.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class MessageReplyView
    extends AbstractView
    implements CommandListener
{
    public static final String CLASS_TAG = "MessageReplyView";

    private TextBox messageReplyTextbox;

    private FacilityMIDlet facilityMIDlet;

    private Command sendCommand;

    private Command backCommand;

    private Message message;

    public MessageReplyView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.messageReplyTextbox = null;
        this.backCommand = null;
        this.sendCommand = null;
        this.message = null;
        System.gc();
    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getMessageReplyTextbox() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            dhisMIDlet.getMessageConversationView().showView();
        }
        else if ( command == this.getSendCommand() )
        {
            try
            {
                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getReplyMessageUrl() );
                ConnectionManager.replyMessage( getMessage() );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public TextBox getMessageReplyTextbox()
    {
        if ( messageReplyTextbox == null )
        {
            messageReplyTextbox = new TextBox( "Enter message", "", 9999, 0 );
            messageReplyTextbox.addCommand( this.getBackCommand() );
            messageReplyTextbox.addCommand( this.getSendCommand() );

            messageReplyTextbox.setCommandListener( this );
        }

        return messageReplyTextbox;
    }

    public void setMessageReplyTextbox( TextBox messageReplyTextbox )
    {
        this.messageReplyTextbox = messageReplyTextbox;
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

    public Message getMessage()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        if ( message == null )
        {
            message = new Message();
            message.setSubject( StringRecordStore.load() );
            message.setText( this.getMessageReplyTextbox().getString() );

        }
        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

}
