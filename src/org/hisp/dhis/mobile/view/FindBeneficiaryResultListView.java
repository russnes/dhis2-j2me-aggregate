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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.recordstore.ActivityRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

public class FindBeneficiaryResultListView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "FindBeneficiaryResultListView";
    
    private List findBeneficiaryResultList;

    private Command findBeneficiaryResultListBackCommand;

    private Command findBeneficiaryResultListAddCommand;

    private Command findBeneficiaryResultListDetailCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private Vector activityVector;

    public FindBeneficiaryResultListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
        nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == findBeneficiaryResultListBackCommand )
        {
            nameBasedMIDlet.getFindBeneficiaryView().showView();
        }
        else if ( command == findBeneficiaryResultListDetailCommand )
        {
            if ( activityVector.size() > 0 )
            {
                Activity activity = (Activity) activityVector.elementAt( findBeneficiaryResultList.getSelectedIndex() );
                nameBasedMIDlet.getBeneficiaryDetailView().setSelectedActivity( activity );
                nameBasedMIDlet.getBeneficiaryDetailView().setPreviousDisplayable( this.getFindBeneficiaryResultList() );
                nameBasedMIDlet.getBeneficiaryDetailView().showView();
            }
        }
        else if ( command == findBeneficiaryResultListAddCommand || command == List.SELECT_COMMAND )
        {
            Activity activity = (Activity) activityVector.elementAt( findBeneficiaryResultList.getSelectedIndex() );
            try
            {
                if ( ActivityRecordStore.updateActivity( activity, dhisMIDlet.getCurrentOrgUnit() ) )
                {
                    activityVector.removeElementAt( findBeneficiaryResultList.getSelectedIndex() );
                    findBeneficiaryResultList.delete( findBeneficiaryResultList.getSelectedIndex() );
                    this.getDisplay().setCurrent( Alerts.getInfoAlert( Text.MESSAGE(), Text.ACTIVITY_ADDED_SUCCESS() ) );
                }
                else
                {
                    this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), Text.ACTIVITY_ADDED_FAIL() ) );
                }
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void prepareView()
    {
        this.getFindBeneficiaryResultList().deleteAll();

        for ( int i = 0; i < activityVector.size(); i++ )
        {
            Activity activity = (Activity) activityVector.elementAt( i );

            ProgramStage stage = ProgramRecordStore.getProgramStage( activity.getTask().getProgStageId(), activity
                .getTask().getProgramId() );
            String displayName = stage.getName() + "\n" + PeriodUtil.formatDate( activity.getDueDate() );
            if ( activity.isLate() && activity.getTask().isComplete() == false )
            {
                displayName = "*" + displayName;
            }

            this.getFindBeneficiaryResultList().insert( i, displayName, null );
            activity = null;
            stage = null;
            System.gc();
        }
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Find Beneficiary Result List" + " Screen...");
        this.switchDisplayable( null, this.getFindBeneficiaryResultList() );
    }

    public List getFindBeneficiaryResultList()
    {
        if ( findBeneficiaryResultList == null )
        {
            findBeneficiaryResultList = new List( "", List.IMPLICIT );
            findBeneficiaryResultList.addCommand( this.getFindBeneficiaryResultListAddCommand() );
            findBeneficiaryResultList.addCommand( this.getFindBeneficiaryResultListDetailCommand() );
            findBeneficiaryResultList.addCommand( this.getFindBeneficiaryResultListBackCommand() );
            findBeneficiaryResultList.setCommandListener( this );
        }
        return findBeneficiaryResultList;
    }

    public void setFindBeneficiaryResultList( List findBeneficiaryResultList )
    {
        this.findBeneficiaryResultList = findBeneficiaryResultList;
    }

    public Command getFindBeneficiaryResultListBackCommand()
    {
        if ( findBeneficiaryResultListBackCommand == null )
        {
            findBeneficiaryResultListBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return findBeneficiaryResultListBackCommand;
    }

    public void setFindBeneficiaryResultListBackCommand( Command findBeneficiaryResultListBackCommand )
    {
        this.findBeneficiaryResultListBackCommand = findBeneficiaryResultListBackCommand;
    }

    public Command getFindBeneficiaryResultListAddCommand()
    {
        if ( findBeneficiaryResultListAddCommand == null )
        {
            findBeneficiaryResultListAddCommand = new Command( Text.ADD(), Command.SCREEN, 0 );
        }
        return findBeneficiaryResultListAddCommand;
    }

    public void setFindBeneficiaryResultListAddCommand( Command findBeneficiaryResultListAddCommand )
    {
        this.findBeneficiaryResultListAddCommand = findBeneficiaryResultListAddCommand;
    }

    public Vector getActivityVector()
    {
        return activityVector;
    }

    public void setActivityVector( Vector activityVector )
    {
        this.activityVector = activityVector;
    }

    public Command getFindBeneficiaryResultListDetailCommand()
    {
        if ( findBeneficiaryResultListDetailCommand == null )
        {
            findBeneficiaryResultListDetailCommand = new Command( Text.DETAILS(), Command.SCREEN, 0 );
        }
        return findBeneficiaryResultListDetailCommand;
    }

    public void setFindBeneficiaryResultListDetailCommand( Command findBeneficiaryResultListDetailCommand )
    {
        this.findBeneficiaryResultListDetailCommand = findBeneficiaryResultListDetailCommand;
    }
}
