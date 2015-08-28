package org.hisp.dhis.mobile.connection.task;

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

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.ActivityValue;
import org.hisp.dhis.mobile.recordstore.ActivityRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class UploadActivityValueTask
    extends AbstractTask
{
    private static final String ACTIVITY_REPORT_UPLOADED = "activity_report_uploaded";

    private ActivityValue activityValue;

    private Activity activity;

    public void run()
    {
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        Alert alert = null;
        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( this.activityValue ) );
            String message = this.readMessage( messageStream );

            if ( message.equalsIgnoreCase( ACTIVITY_REPORT_UPLOADED ) )
            {
                alert = Alerts.getInfoAlert( Text.MESSAGE(), Text.UPLOADED_SUCCESSFULLY() );
                this.activity.getTask().setComplete( true );
                ActivityRecordStore.saveActivity( this.activity, ConnectionManager.getOrgUnit() );
                int selectedIndex = nameBasedMIDlet.getActivityPlanListView().getActivityPlanList().getSelectedIndex();
                nameBasedMIDlet.getActivityPlanListView().getPatientActivityVector().removeElementAt( selectedIndex );
                nameBasedMIDlet.getActivityPlanListView().getActivityPlanList().delete( selectedIndex );
            }
            else
            {
                alert = Alerts.getErrorAlert( Text.ERROR(), Text.UPLOAD_FAIL() );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            alert = Alerts.getErrorAlert( Text.ERROR(), Text.UPLOAD_FAIL() );
        }
        finally
        {
            nameBasedMIDlet.getActivityPlanListView().switchDisplayable( alert,
                nameBasedMIDlet.getActivityPlanListView().getActivityPlanList() );
        }

    }

    public ActivityValue getActivityValue()
    {
        return activityValue;
    }

    public void setActivityValue( ActivityValue activityValue )
    {
        this.activityValue = activityValue;
    }

    public Activity getActivity()
    {
        return activity;
    }

    public void setActivity( Activity activity )
    {
        this.activity = activity;
    }

}
