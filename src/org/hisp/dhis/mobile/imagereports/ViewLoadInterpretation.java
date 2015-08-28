package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.view.AbstractView;

public class ViewLoadInterpretation
    extends AbstractView
    implements CommandListener
{
    private Command loadCommand;

    private Command backCommand;

    private FacilityMIDlet facilityMIDlet;

    private Form form;

    public ViewLoadInterpretation( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.loadCommand = null;
        this.backCommand = null;
        this.form = null;

        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getForm() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            dhisMIDlet.getMainMenuView().showView();
        }
        else if ( command == this.getLoadCommand() )
        {
            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getDownloadInterpretationUrl() );
            try
            {
                ConnectionManager.downloadInterpretation( StringRecordStore.load() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public Form getForm()
    {
        if ( form == null )
        {
            form = new Form( "Load interpretation..." );
            form.append( "Load interpretation" );
            form.addCommand( this.getBackCommand() );
            form.addCommand( this.getLoadCommand() );

            form.setCommandListener( this );
        }
        return form;
    }

    public void setForm( Form form )
    {
        this.form = form;
    }

    public Command getLoadCommand()
    {
        if ( loadCommand == null )
        {
            loadCommand = new Command( "Load", Command.SCREEN, 0 );
        }
        return loadCommand;
    }

    public void setLoadCommand( Command loadCommand )
    {
        this.loadCommand = loadCommand;
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

}
