package org.hisp.dhis.mobile.midlet;

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

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.view.ActivityMainMenuView;
import org.hisp.dhis.mobile.view.ActivityPlanListView;
import org.hisp.dhis.mobile.view.BeneficiaryDetailView;
import org.hisp.dhis.mobile.view.FindBeneficiaryResultListView;
import org.hisp.dhis.mobile.view.FindBeneficiaryView;
import org.hisp.dhis.mobile.view.GroupingView;
import org.hisp.dhis.mobile.view.NameListView;
import org.hisp.dhis.mobile.view.NamebasedDataEntryView;

public class NameBasedMIDlet
    extends DHISMIDlet
{
    private static final String CLASS_TAG = "NameBasedMIDlet";

    private ActivityMainMenuView activityMainMenuView;

    private ActivityPlanListView activityPlanListView;

    private GroupingView groupingView;

    private NameListView nameListView;

    private NamebasedDataEntryView nameBasedDataEntryView;

    private BeneficiaryDetailView beneficiaryDetailView;

    private FindBeneficiaryView findBeneficiaryView;

    private FindBeneficiaryResultListView findBeneficiaryResultListView;

    public NameBasedMIDlet()
    {
        LogMan.log( LogMan.DEV, CLASS_TAG, "Initializing Name-Based Reporting Application" );
    }

    public ActivityMainMenuView getActivityMainMenuView()
    {
        if ( this.activityMainMenuView == null )
        {
            this.activityMainMenuView = new ActivityMainMenuView( this );
        }
        return activityMainMenuView;
    }

    public void setActivityMainMenuView( ActivityMainMenuView activityMainMenuView )
    {
        this.activityMainMenuView = activityMainMenuView;
    }

    public ActivityPlanListView getActivityPlanListView()
    {
        if ( activityPlanListView == null )
        {
            activityPlanListView = new ActivityPlanListView( this );
        }
        return activityPlanListView;
    }

    public void setActivityPlanListView( ActivityPlanListView activityPlanListView )
    {
        this.activityPlanListView = activityPlanListView;
    }

    public GroupingView getGroupingView()
    {
        if ( groupingView == null )
        {
            groupingView = new GroupingView( this );
        }
        return groupingView;
    }

    public void setGroupingView( GroupingView groupingView )
    {
        this.groupingView = groupingView;
    }

    public NameListView getNameListView()
    {
        if ( nameListView == null )
        {
            nameListView = new NameListView( this );
        }
        return nameListView;
    }

    public void setNameListView( NameListView nameListView )
    {
        this.nameListView = nameListView;
    }

    public NamebasedDataEntryView getNameBasedDataEntryView()
    {
        if ( this.nameBasedDataEntryView == null )
        {
            this.nameBasedDataEntryView = new NamebasedDataEntryView( this );
        }
        return nameBasedDataEntryView;
    }

    public void setNameBasedDataEntryView( NamebasedDataEntryView nameBasedDataEntryView )
    {
        this.nameBasedDataEntryView = nameBasedDataEntryView;
    }

    public BeneficiaryDetailView getBeneficiaryDetailView()
    {
        if ( beneficiaryDetailView == null )
        {
            beneficiaryDetailView = new BeneficiaryDetailView( this );
        }
        return beneficiaryDetailView;
    }

    public void setBeneficiaryDetailView( BeneficiaryDetailView beneficiaryDetailView )
    {
        this.beneficiaryDetailView = beneficiaryDetailView;
    }

    public FindBeneficiaryView getFindBeneficiaryView()
    {
        if ( findBeneficiaryView == null )
        {
            findBeneficiaryView = new FindBeneficiaryView( this );
        }
        return findBeneficiaryView;
    }

    public void setFindBeneficiaryView( FindBeneficiaryView findBeneficiaryView )
    {
        this.findBeneficiaryView = findBeneficiaryView;
    }

    public FindBeneficiaryResultListView getFindBeneficiaryResultListView()
    {
        if ( findBeneficiaryResultListView == null )
        {
            findBeneficiaryResultListView = new FindBeneficiaryResultListView( this );
        }
        return findBeneficiaryResultListView;
    }

    public void setFindBeneficiaryResultListView( FindBeneficiaryResultListView findBeneficiaryResultListView )
    {
        this.findBeneficiaryResultListView = findBeneficiaryResultListView;
    }
}
