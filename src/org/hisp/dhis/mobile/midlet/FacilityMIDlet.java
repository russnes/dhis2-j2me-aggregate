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
import org.hisp.dhis.mobile.view.DataSetEntryView;
import org.hisp.dhis.mobile.view.DataSetListView;
import org.hisp.dhis.mobile.view.PeriodView;

public class FacilityMIDlet
    extends DHISMIDlet
{
    private static final String CLASS_TAG = "FacilityMIDlet";
    
    private DataSetListView dataSetListView;

    private PeriodView periodView;

    private DataSetEntryView dataSetEntryView;

    public FacilityMIDlet()
    {
        LogMan.log( LogMan.DEV, CLASS_TAG, "Initializing Facility Reporting Application" );
    }

    public DataSetListView getDataSetListView()
    {
        if ( dataSetListView == null )
        {
            dataSetListView = new DataSetListView( this );
        }
        return dataSetListView;
    }

    public void setDataSetListView( DataSetListView dataSetListView )
    {
        this.dataSetListView = dataSetListView;
    }

    public PeriodView getPeriodView()
    {
        if ( this.periodView == null )
        {
            this.periodView = new PeriodView( this );
        }
        return periodView;
    }

    public void setPeriodView( PeriodView periodView )
    {
        this.periodView = periodView;
    }

    public DataSetEntryView getDataSetEntryView()
    {
        if ( this.dataSetEntryView == null )
        {
            dataSetEntryView = new DataSetEntryView( this );
        }
        return dataSetEntryView;
    }

    public void setDataSetEntryView( DataSetEntryView dataSetEntryView )
    {
        this.dataSetEntryView = dataSetEntryView;
    }

}
