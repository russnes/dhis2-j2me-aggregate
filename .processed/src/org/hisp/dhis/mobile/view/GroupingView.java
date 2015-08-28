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

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.ui.Text;

public class GroupingView
    extends AbstractView
    implements CommandListener
{
    public static final String CLASS_TAG = "GroupingView";
    
    private NameBasedMIDlet nameBasedMIDlet;

    private Vector groupingItemVector;

    private Vector activityVector;

    private List groupingItemList;

    private Command groupingItemListBackCommand;

    public GroupingView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.groupingItemListBackCommand )
        {
            nameBasedMIDlet.getActivityMainMenuView().showView();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            String selectedGroupingValue = ((PatientAttribute) groupingItemVector.elementAt( groupingItemList
                .getSelectedIndex() )).getValue();
            Vector activities = this.filterActivity( selectedGroupingValue );
            nameBasedMIDlet.getNameListView().setActivityVector( activities );
            nameBasedMIDlet.getNameListView().setGroupingValue( selectedGroupingValue );
            nameBasedMIDlet.getNameListView().prepareView();
            nameBasedMIDlet.getNameListView().showView();
        }
    }

    private Vector filterActivity( String selectedGroupingValue )
    {
        Vector filteredActivities = new Vector();
        Activity activity = null;
        for ( int i = 0; i < this.activityVector.size(); i++ )
        {
            activity = null;
            activity = (Activity) this.activityVector.elementAt( i );
            if ( activity.getBeneficiary().getGroupByAttribute().getValue().equals( selectedGroupingValue ) )
            {
                filteredActivities.addElement( activity );
            }
            System.gc();
        }
        return filteredActivities;
    }

    public void prepareView()
    {
        this.groupingItemVector = this.loadGroupingItemVector();
        for ( int i = 0; i < groupingItemVector.size(); i++ )
        {
            String itemValue = ((PatientAttribute) groupingItemVector.elementAt( i )).getValue();
            this.getGroupingItemList().append( itemValue, null );
        }

        String itemName = ((PatientAttribute) groupingItemVector.elementAt( 0 )).getName();
        this.getGroupingItemList().setTitle( itemName );
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Grouping" + " Screen...");
        this.switchDisplayable( null, this.getGroupingItemList() );
    }

    private Vector loadGroupingItemVector()
    {
        groupingItemVector = null;
        groupingItemVector = new Vector();

        Hashtable temp = new Hashtable();
        this.getGroupingItemList().deleteAll();

        for ( int i = 0; i < activityVector.size(); i++ )
        {
            Activity activity = (Activity) activityVector.elementAt( i );

            // Extract group factor attribute of beneficiary from
            // activity

            PatientAttribute patientAttribute = activity.getBeneficiary().getGroupByAttribute();

            if ( patientAttribute != null && temp.get( patientAttribute.getValue().trim() ) == null )
            {
                groupingItemVector.addElement( patientAttribute );
                temp.put( patientAttribute.getValue().trim(), patientAttribute );
            }

            // Sort Group-Factor Attribute List
            if ( groupingItemVector.size() > 1 )
            {
                sortAttributeList( groupingItemVector );
            }

        }
        temp.clear();
        temp = null;
        System.gc();
        return groupingItemVector;
    }

    private void sortAttributeList( Vector attributes )
    {
        PatientAttribute temp;
        int size = attributes.size();
        int i = 0;
        while ( i < size )
        {
            for ( int j = 0; j < size - 1; j++ )
            {
                if ( ((PatientAttribute) attributes.elementAt( j )).compareTo( (PatientAttribute) attributes
                    .elementAt( j + 1 ) ) > 0 )
                {
                    temp = new PatientAttribute( ((PatientAttribute) attributes.elementAt( j )).getName(),
                        ((PatientAttribute) attributes.elementAt( j )).getValue() );
                    attributes.setElementAt( attributes.elementAt( j + 1 ), j );
                    attributes.setElementAt( new PatientAttribute( temp.getName(), temp.getValue() ), j + 1 );
                    temp = null;
                }
                i++;
            }
        }
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

    public Vector getGroupingItemVector()
    {
        return groupingItemVector;
    }

    public void setGroupingItemVector( Vector groupingItemVector )
    {
        this.groupingItemVector = groupingItemVector;
    }

    public List getGroupingItemList()
    {
        if ( groupingItemList == null )
        {
            groupingItemList = new List( "", Choice.IMPLICIT );
            groupingItemList.addCommand( getGroupingItemListBackCommand() );
            groupingItemList.setCommandListener( this );
        }
        return groupingItemList;
    }

    public void setGroupingItemList( List groupingItemList )
    {
        this.groupingItemList = groupingItemList;
    }

    public Vector getActivityVector()
    {
        return activityVector;
    }

    public void setActivityVector( Vector activityVector )
    {
        this.activityVector = activityVector;
    }

    public Command getGroupingItemListBackCommand()
    {
        if ( groupingItemListBackCommand == null )
        {
            groupingItemListBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return groupingItemListBackCommand;
    }

    public void setGroupingItemListBackCommand( Command groupingItemListBackCommand )
    {
        this.groupingItemListBackCommand = groupingItemListBackCommand;
    }
}
