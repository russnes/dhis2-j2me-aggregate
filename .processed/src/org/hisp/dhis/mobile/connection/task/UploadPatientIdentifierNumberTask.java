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
import java.io.IOException;

import javax.microedition.lcdui.Alert;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.ActivityPlan;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class UploadPatientIdentifierNumberTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UploadPatientIdentifierNumberTask";

    private String patientIdentifier;

    public void run()
    {
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream inputStream = null;
        ActivityPlan plan = new ActivityPlan();
        try
        {
            inputStream = this.download( patientIdentifier, "identifier" );
            if ( inputStream != null )
            {
                plan.deSerialize( inputStream );

                if ( plan.getActivities().size() == 0 )
                {
                    nameBasedMIDlet.getFindBeneficiaryView().switchDisplayable(
                        Alerts.getErrorAlert( Text.MESSAGE(), Text.NO_ACTIVITY_FOUND() ),
                        nameBasedMIDlet.getFindBeneficiaryView().getFindBeneficiaryForm() );
                }
                else
                {
                    nameBasedMIDlet.getFindBeneficiaryResultListView().setActivityVector( plan.getActivities() );
                    nameBasedMIDlet.getFindBeneficiaryResultListView().prepareView();
                    nameBasedMIDlet.getFindBeneficiaryResultListView().showView();
                }
            }
        }
        catch ( IOException e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();

            String message = null;

            if ( e.getMessage().equalsIgnoreCase( "NO_BENEFICIARY_FOUND" ) )
            {
                message = Text.NO_BENEFICIARY_FOUND() + " " + patientIdentifier;

            }
            else if ( e.getMessage().equalsIgnoreCase( "NEED_MORE_SPECIFIC" ) )
            {
                message = Text.TOO_MANY_RESULT();
            }

            Alert alert = Alerts.getErrorAlert( Text.ERROR(), message );
            nameBasedMIDlet.getFindBeneficiaryView().switchDisplayable( alert,
                nameBasedMIDlet.getFindBeneficiaryView().getFindBeneficiaryForm() );
        }
    }

    public String getPatientIdentifier()
    {
        return patientIdentifier;
    }

    public void setPatientIdentifier( String patientIdentifier )
    {
        this.patientIdentifier = patientIdentifier;
    }

}
