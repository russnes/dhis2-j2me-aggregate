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

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.ui.Text;

public class NameListView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "NameListView";
    
    private NameBasedMIDlet nameBasedMIDlet;

    private Vector activityVector;

    private Vector nameVector;

    private String groupingValue;

    private List nameList;

    private Command nameListBackCommand;

    public NameListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.collectNameVector();
        this.getNameList().deleteAll();
        for ( int i = 0; i < nameVector.size(); i++ )
        {
            this.getNameList().append( (String) nameVector.elementAt( i ), null );
        }

    }

    private void collectNameVector()
    {
        nameVector = null;
        nameVector = new Vector();
        Hashtable tempTable = new Hashtable();
        this.getNameList().deleteAll();
        for ( int i = 0; i < activityVector.size(); i++ )
        {
            Activity activity = (Activity) activityVector.elementAt( i );
            if ( this.groupingValue != null )
            {

                if ( activity.getBeneficiary().getGroupByAttribute().getValue().equals( groupingValue )
                    && tempTable.get( new Integer( activity.getBeneficiary().getId() ) ) == null )
                {
                    this.getNameList().append( activity.getBeneficiary().getFullName(), null );
                    tempTable.put( new Integer( activity.getBeneficiary().getId() ), "1" );
                    nameVector.addElement( activity.getBeneficiary().getFullName() );
                }

            }
            else
            {
                if ( tempTable.get( new Integer( activity.getBeneficiary().getId() ) ) == null )
                {
                    this.getNameList().append( activity.getBeneficiary().getFullName(), null );
                    tempTable.put( new Integer( activity.getBeneficiary().getId() ), "1" );
                    nameVector.addElement( activity.getBeneficiary().getFullName() );
                }
            }
            activity = null;
        }
        tempTable = null;
        System.gc();
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Name List" + " Screen...");
        this.switchDisplayable( null, this.nameList );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == nameListBackCommand )
        {
            if ( groupingValue == null )
            {
                nameBasedMIDlet.getActivityMainMenuView().showView();
            }
            else
            {
                nameBasedMIDlet.getGroupingView().showView();
            }

        }
        else if ( command == List.SELECT_COMMAND )
        {
            ActivityPlanListView activityPlanListView = nameBasedMIDlet.getActivityPlanListView();
            String selectedPatientName = (String) nameVector.elementAt( nameList.getSelectedIndex() );
            Vector patientActivityVector = this.filterActivityByPatient( this.activityVector, selectedPatientName );
            // activityPlanListView.setActivityPlanMode(
            // nameBasedMIDlet.getActivityMainMenuView()
            // .getActivityMainMenuList().getSelectedIndex() );
            activityPlanListView.setPatientActivityVector( patientActivityVector );
            activityPlanListView.setPatientName( selectedPatientName );
            activityPlanListView.prepareView();
            activityPlanListView.showView();

        }

    }

    private Vector filterActivityByPatient( Vector activityVector, String selectedPatientName )
    {
        Vector patientActivityVector = new Vector();
        for ( int i = 0; i < activityVector.size(); i++ )
        {
            String patientName = ((Activity) activityVector.elementAt( i )).getBeneficiary().getFullName();
            if ( patientName.equals( selectedPatientName ) )
            {
                patientActivityVector.addElement( activityVector.elementAt( i ) );
            }
        }
        return patientActivityVector;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

    public Vector getActivityVector()
    {
        return activityVector;
    }

    public void setActivityVector( Vector activityVector )
    {
        this.activityVector = activityVector;
    }

    public String getGroupingValue()
    {
        return groupingValue;
    }

    public void setGroupingValue( String groupingValue )
    {
        this.groupingValue = groupingValue;
    }

    public Command getNameListBackCommand()
    {

        if ( this.nameListBackCommand == null )
        {
            this.nameListBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return nameListBackCommand;
    }

    public void setNameListBackCommand( Command nameListBackCommand )
    {
        this.nameListBackCommand = nameListBackCommand;
    }

    public Vector getNameVector()
    {
        return nameVector;
    }

    public void setNameVector( Vector nameVector )
    {
        this.nameVector = nameVector;
    }

    public List getNameList()
    {
        if ( this.nameList == null )
        {
            this.nameList = new List( Text.NAME_LIST(), List.IMPLICIT );
            this.nameList.setFitPolicy( List.TEXT_WRAP_ON );
            this.nameList.addCommand( this.getNameListBackCommand() );
            this.nameList.setCommandListener( this );

        }
        return nameList;
    }

    public void setNameList( List nameList )
    {
        this.nameList = nameList;
    }

}
