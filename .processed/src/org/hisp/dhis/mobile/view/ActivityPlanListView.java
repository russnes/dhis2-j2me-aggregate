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

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

public class ActivityPlanListView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "ActivityPlanListView";

    private NameBasedMIDlet nameBasedMIDlet;

    private String patientName;

    private Vector patientActivityVector;

    private List activityPlanList;

    private Command activityPlanListBackCommand;

    private Command activityPlanListDetailCommand;

    public ActivityPlanListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        this.getActivityPlanList().deleteAll();

        for ( int i = 0; i < patientActivityVector.size(); i++ )
        {
            Activity activity = (Activity) patientActivityVector.elementAt( i );

            ProgramStage stage = ProgramRecordStore.getProgramStage( activity.getTask().getProgStageId(), activity
                .getTask().getProgramId() );
            String displayName = stage.getName() + "\n" + PeriodUtil.formatDate( activity.getDueDate() );
            if ( activity.isLate() && activity.getTask().isComplete() == false )
            {
                displayName = "*" + displayName;
            }

            this.getActivityPlanList().insert( i, displayName, null );
            activity = null;
            stage = null;
            System.gc();
        }
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Activity Plan List" + " Screen...");
        this.switchDisplayable( null, this.activityPlanList );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == activityPlanListBackCommand )
        {
            nameBasedMIDlet.getNameListView().showView();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            Activity selectedActivity = (Activity) patientActivityVector
                .elementAt( activityPlanList.getSelectedIndex() );
            nameBasedMIDlet.getNameBasedDataEntryView().setSelectedActivity( selectedActivity );
            nameBasedMIDlet.getNameBasedDataEntryView().prepareView();
            nameBasedMIDlet.getNameBasedDataEntryView().showView();
        }
        else if ( command == activityPlanListDetailCommand )
        {
            Activity selectedActivity = (Activity) patientActivityVector
                .elementAt( activityPlanList.getSelectedIndex() );
            if ( selectedActivity != null )
            {
                nameBasedMIDlet.getBeneficiaryDetailView().setSelectedActivity( selectedActivity );
                nameBasedMIDlet.getBeneficiaryDetailView().setPreviousDisplayable( this.getActivityPlanList() );
                nameBasedMIDlet.getBeneficiaryDetailView().showView();
            }
        }

    }

    public List getActivityPlanList()
    {
        if ( activityPlanList == null )
        {
            activityPlanList = new List( Text.SELECT_ACTIVITY(), Choice.IMPLICIT );
            activityPlanList.setFitPolicy( Choice.TEXT_WRAP_ON );
            activityPlanList.addCommand( getActivityPlanListBackCommand() );
            activityPlanList.addCommand( getActivityPlanListDetailCommand() );
            activityPlanList.setCommandListener( this );
        }
        return activityPlanList;
    }

    public void setActivityPlanList( List activityPlanList )
    {
        this.activityPlanList = activityPlanList;
    }

    public Command getActivityPlanListBackCommand()
    {
        if ( activityPlanListBackCommand == null )
        {
            activityPlanListBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return activityPlanListBackCommand;
    }

    public void setActivityPlanListBackCommand( Command activityPlanListBackCommand )
    {
        this.activityPlanListBackCommand = activityPlanListBackCommand;
    }

    public Vector getPatientActivityVector()
    {
        return patientActivityVector;
    }

    public void setPatientActivityVector( Vector patientActivityVector )
    {
        this.patientActivityVector = patientActivityVector;
    }

    public Command getActivityPlanListDetailCommand()
    {
        if ( activityPlanListDetailCommand == null )
        {
            activityPlanListDetailCommand = new Command( Text.DETAILS(), Command.SCREEN, 0 );
        }
        return activityPlanListDetailCommand;
    }

    public void setActivityPlanListDetailCommand( Command activityPlanListDetailCommand )
    {
        this.activityPlanListDetailCommand = activityPlanListDetailCommand;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName( String patientName )
    {
        this.patientName = patientName;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }
}
