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
import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.recordstore.ActivityRecordStore;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class ActivityMainMenuView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "ActivityMainMenuView";
    
    public static final int CURRENT_ACTIVITIES = 0;

    public static final int SENT_ACTIVITIES = 1;

    private NameBasedMIDlet nameBasedMIDlet;

    private List activityMainMenuList;

    private Command activityMainMenuListExitCommand;

    private Command activityMainMenuListSettingCommand;

    private Command orgUnitSelectCommand;

    public ActivityMainMenuView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        // TODO Auto-generated method stub
    }

    /**
     * 
     */
    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Activity Main Menu" + " Screen...");
        this.switchDisplayable( null, this.getActivityMainMenuList() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.activityMainMenuListExitCommand )
        {
            dhisMIDlet.exitMIDlet();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            try
            {
                int selectedIndex = this.activityMainMenuList.getSelectedIndex();

                if ( selectedIndex == 2 )
                {
                    this.nameBasedMIDlet.getFindBeneficiaryView().showView();
                }
                else
                {
                    if ( NameBasedMIDlet.isDownloading )
                    {
                        this.switchDisplayable( Alerts.getErrorAlert( Text.ERROR(), Text.UPDATING() ),
                            this.getActivityMainMenuList() );
                    }
                    else
                    {
                        ActivityRecordStore.deleteExpiredActivities( new Date() );
                        Vector activityVector = null;
                        if ( selectedIndex == CURRENT_ACTIVITIES )
                        {
                            activityVector = ActivityRecordStore.loadActivityByCompleteStatus( false,
                                dhisMIDlet.getCurrentOrgUnit() );
                        }
                        else if ( selectedIndex == SENT_ACTIVITIES )
                        {
                            activityVector = ActivityRecordStore.loadActivityByCompleteStatus( true,
                                dhisMIDlet.getCurrentOrgUnit() );
                        }

                        Activity activity = null;
                        PatientAttribute patientAttribute = null;

                        if ( activityVector.size() > 0 )
                        {
                            activity = (Activity) activityVector.elementAt( 0 );
                            patientAttribute = activity.getBeneficiary().getGroupByAttribute();
                        }

                        if ( activity != null && patientAttribute != null )
                        {
                            this.nameBasedMIDlet.getGroupingView().setActivityVector( activityVector );
                            this.nameBasedMIDlet.getGroupingView().prepareView();
                            this.nameBasedMIDlet.getGroupingView().showView();
                        }
                        else
                        {
                            this.nameBasedMIDlet.getNameListView().setActivityVector( activityVector );
                            this.nameBasedMIDlet.getNameListView().prepareView();
                            this.nameBasedMIDlet.getNameListView().showView();
                        }
                    }

                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                LogMan.log( "UI," + CLASS_TAG, e );
            }
        }
        else if ( command == activityMainMenuListSettingCommand )
        {
            nameBasedMIDlet.getSettingView().showView();
        }
        else if ( command == orgUnitSelectCommand )
        {
            nameBasedMIDlet.getOrgUnitSelectView().showView();
        }

    }

    public List getActivityMainMenuList()
    {
        if ( activityMainMenuList == null )
        {
            activityMainMenuList = new List( Text.MAIN_MENU(), Choice.IMPLICIT );
            activityMainMenuList.append( Text.CURRENT_ACTIVITY_PLAN(), null );
            activityMainMenuList.append( Text.COMPLETED_ACTIVITIES(), null );
            activityMainMenuList.append( Text.FIND_BENEFICIARY(), null );
            activityMainMenuList.addCommand( getActivityMainMenuListSettingCommand() );
            activityMainMenuList.addCommand( getActivityMainMenuListExitCommand() );

            Vector orgUnitvector = null;
            try
            {
                orgUnitvector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            if ( orgUnitvector.size() > 1 )
            {
                activityMainMenuList.addCommand( this.getOrgUnitSelectCommand() );
                orgUnitvector = null;
            }

            activityMainMenuList.setCommandListener( this );
            activityMainMenuList.setFitPolicy( List.TEXT_WRAP_ON );
            activityMainMenuList.setSelectedFlags( new boolean[] { false, false, false, false } );
        }
        return activityMainMenuList;
    }

    public void setActivityMainMenuList( List activityMainMenuList )
    {
        this.activityMainMenuList = activityMainMenuList;
    }

    public Command getActivityMainMenuListExitCommand()
    {
        if ( activityMainMenuListExitCommand == null )
        {
            activityMainMenuListExitCommand = new Command( Text.EXIT(), Command.EXIT, 0 );
        }
        return activityMainMenuListExitCommand;
    }

    public void setActivityMainMenuListExitCommand( Command activityMainMenuListExitCommand )
    {
        this.activityMainMenuListExitCommand = activityMainMenuListExitCommand;
    }

    public Command getActivityMainMenuListSettingCommand()
    {
        if ( activityMainMenuListSettingCommand == null )
        {
            activityMainMenuListSettingCommand = new Command( Text.SETTINGS(), Command.SCREEN, 0 );
        }
        return activityMainMenuListSettingCommand;
    }

    public void setActivityMainMenuListSettingCommand( Command activityMainMenuListSettingCommand )
    {
        this.activityMainMenuListSettingCommand = activityMainMenuListSettingCommand;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

    public Command getOrgUnitSelectCommand()
    {
        if ( orgUnitSelectCommand == null )
        {
            orgUnitSelectCommand = new Command( Text.SELECT_ORGUNIT(), Command.SCREEN, 0 );
        }
        return orgUnitSelectCommand;
    }

    public void setOrgUnitSelectCommand( Command orgUnitSelectCommand )
    {
        this.orgUnitSelectCommand = orgUnitSelectCommand;
    }

}
