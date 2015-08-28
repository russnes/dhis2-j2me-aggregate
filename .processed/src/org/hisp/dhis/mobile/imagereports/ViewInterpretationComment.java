package org.hisp.dhis.mobile.imagereports;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.InterpretationComment;
import org.hisp.dhis.mobile.recordstore.InterpretationRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.view.AbstractView;

public class ViewInterpretationComment
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "ViewInterpretationComment";

    private Command backCommand;

    private Command sendCommand;

    private Form form;

    private TextField commentTextField;

    private FacilityMIDlet facilityMIDlet;

    public ViewInterpretationComment( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        backCommand = null;
        sendCommand = null;
        form = null;
        commentTextField = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        try
        {
            this.switchDisplayable( null, this.getForm() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

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
                String data = InterpretationRecordStore.load().getId() + this.getCommentTextField().getString();

                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getPostComment() );
                ConnectionManager.postComment( data );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public Form getForm()
        throws RecordStoreNotOpenException, RecordStoreException, IOException
    {
        if ( form == null )
        {
            Vector commentVector = InterpretationRecordStore.load().getInterComments();

            String text = "";
            for ( int i = 0; i < commentVector.size(); i++ )
            {
                InterpretationComment interComment = (InterpretationComment) commentVector.elementAt( i );
                text += interComment.getText() + " / -";
            }

            form = new Form( "Comment" );
            form.append( text.substring( 0, text.length() - 3 ) );
            form.append( this.getCommentTextField() );
            form.addCommand( this.getBackCommand() );
            form.addCommand( this.getSendCommand() );

            form.setCommandListener( this );

        }
        return form;
    }

    public void setForm( Form form )
    {
        this.form = form;
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

    public TextField getCommentTextField()
    {
        if ( commentTextField == null )
        {
            commentTextField = new TextField( "enter comment", "", 1000, TextField.ANY );
        }
        return commentTextField;
    }

    public void setCommentTextField( TextField commentTextField )
    {
        this.commentTextField = commentTextField;
    }

}
