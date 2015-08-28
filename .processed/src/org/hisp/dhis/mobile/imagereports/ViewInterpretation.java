package org.hisp.dhis.mobile.imagereports;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.recordstore.InterpretationRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.view.AbstractView;

public class ViewInterpretation
    extends AbstractView
    implements CommandListener
{
    private FacilityMIDlet facilityMIDlet;

    private Command backCommand;

    private Command commentCommand;

    private Form form;

    public ViewInterpretation( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        backCommand = null;
        commentCommand = null;
        form = null;
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
        else if ( command == this.getCommentCommand() )
        {
            facilityMIDlet.getViewInterpretationComment().showView();
        }

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

    public Command getCommentCommand()
    {
        if ( commentCommand == null )
        {
            commentCommand = new Command( "Comment", Command.SCREEN, 0 );
        }
        return commentCommand;
    }

    public void setCommentCommand( Command commentCommand )
    {
        this.commentCommand = commentCommand;
    }

    public Form getForm()
        throws RecordStoreNotOpenException, RecordStoreException, IOException
    {
        if ( form == null )
        {
            form = new Form( "Interpretation" );
            form.append( InterpretationRecordStore.load().getText() );
            form.addCommand( this.getBackCommand() );
            form.addCommand( this.getCommentCommand() );
            form.setCommandListener( this );

        }
        return form;
    }

    public void setForm( Form form )
    {
        this.form = form;
    }

}
