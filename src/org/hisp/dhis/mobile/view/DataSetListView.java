package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2013, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.recordstore.DataSetRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class DataSetListView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "DataSetListView";

    private FacilityMIDlet facilityMIDlet;

    private List dataSetList;

    private Command dataSetListBackCommand;

    private Vector dataSetVector;

    public Alert exitAlert;

    public DataSetListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        facilityMIDlet = (FacilityMIDlet) this.dhisMIDlet;
        dataSetVector = new Vector();
    }

    public void prepareView()
    {
        try
        {
            this.dataSetVector = DataSetRecordStore.loadDataSets( dhisMIDlet.getCurrentOrgUnit() );
            this.getDataSetList().deleteAll();
            for ( int i = 0; i < dataSetVector.size(); i++ )
            {
                DataSet dataSet = (DataSet) dataSetVector.elementAt( i );
                this.getDataSetList().append( dataSet.getName(), null );
            }
        }
        catch ( RecordStoreException e )
        {
            getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), "Cannot load DataSet" ) );
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), "Cannot deserialize DataSet" ) );
            e.printStackTrace();
        }
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Data Set List" + " Screen...");
        prepareView();
        switchDisplayable( null, this.getDataSetList() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == List.SELECT_COMMAND )
        {
            DataSet selectedDataSet = (DataSet) dataSetVector.elementAt( this.getDataSetList().getSelectedIndex() );
            this.facilityMIDlet.getPeriodView().setSelectedDataSet( selectedDataSet );
            this.facilityMIDlet.getPeriodView().showView();
        }
        else if ( command == this.dataSetListBackCommand )
        {
            this.facilityMIDlet.getMainMenuView().showView();
        }

    }

    public FacilityMIDlet getFacilityMIDlet()
    {
        return facilityMIDlet;
    }

    public void setFacilityMIDlet( FacilityMIDlet facilityMIDlet )
    {
        this.facilityMIDlet = facilityMIDlet;
    }

    public List getDataSetList()
    {
        if ( dataSetList == null )
        {
            dataSetList = new List( Text.SELECT_FORM(), Choice.IMPLICIT );
            dataSetList.setFitPolicy( List.TEXT_WRAP_ON );
            dataSetList.addCommand( this.getDataSetListBackCommand() );

            dataSetList.setCommandListener( this );
        }
        return dataSetList;
    }

    public void setDataSetList( List dataSetList )
    {
        this.dataSetList = dataSetList;
    }

    public Command getDataSetListBackCommand()
    {
        if ( dataSetListBackCommand == null )
        {
            dataSetListBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return dataSetListBackCommand;
    }

    public void setDataSetListBackCommand( Command dataSetListBackCommand )
    {
        this.dataSetListBackCommand = dataSetListBackCommand;
    }

    public Vector getDataSetVector()
    {
        return dataSetVector;
    }

    public void setDataSetVector( Vector dataSetVector )
    {
        this.dataSetVector = dataSetVector;
    }

    public Alert getExitAlert()
    {
        if ( this.exitAlert == null )
        {
            exitAlert = Alerts.getConfirmAlert( Text.MESSAGE(), Text.EXIT_CONFIRM(), this );
        }
        return exitAlert;
    }

    public void setExitAlert( Alert exitAlert )
    {
        this.exitAlert = exitAlert;
    }

}
