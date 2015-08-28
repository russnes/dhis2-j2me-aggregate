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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.recordstore.DataSetValueRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class UploadDataSetValueTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UploadDataSetValueTask";
    
    private static final String DATASET_REPORT_UPLOADED = "dataset_report_uploaded";

    private DataSetValue dataSetValue;

    public void run()
    {
        final FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();
        Alert alert = null;
        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( dataSetValue ) );
            String message = this.readMessage( messageStream );
            if ( message.equalsIgnoreCase( DATASET_REPORT_UPLOADED ) )
            {
                alert = Alerts.getInfoAlert( Text.MESSAGE(), Text.UPLOADED_SUCCESSFULLY() );
                dataSetValue.setCompleted( true );
                DataSetValueRecordStore.saveDataSetValue( dataSetValue, facilityMIDlet.getCurrentOrgUnit() );
            }
            else
            {
                alert = Alerts.getErrorAlert( Text.ERROR(), Text.UPLOAD_FAIL() );
            }

        }
        catch ( IOException e )
        {
            String message = e.getMessage();
            LogMan.log( "Network," + CLASS_TAG, e );

            if ( message.equalsIgnoreCase( "DATASET_LOCKED" ) )
            {
                alert = Alerts.getErrorAlert( Text.UPLOAD_FAIL(), Text.DATASET_LOCKED() );
            }
            else
            {
                alert = Alerts.getErrorAlert( Text.ERROR(), Text.UPLOAD_FAIL() );
                alert = Alerts.getConfirmAlert( Text.MESSAGE(), "Fail to send with GPRS, do you want to try SMS",
                    new CommandListener()
                    {
                        public void commandAction( Command command, Displayable displayable )
                        {
                            if ( command.getCommandType() == Command.OK )
                            {
                                String smsString = facilityMIDlet.getDataSetEntryView().generateSMSValue();
                                if ( smsString == null || smsString.trim().equals( "" ) )
                                {
                                    Form form = (Form) facilityMIDlet.getDataSetEntryView().getFormVector().elementAt(  facilityMIDlet.getDataSetEntryView().getCurrentSession() );
                                    facilityMIDlet.getDataSetEntryView().switchDisplayable( Alerts.getErrorAlert( Text.ERROR(), Text.NO_SMSCOMMAND() ), form );
                                }
                                else
                                {
                                    ConnectionManager.sendSMSReport( smsString );
                                }
                            }
                        }
                    } );
            }
        }
        catch ( RecordStoreException re )
        {
            re.printStackTrace();
            alert = Alerts.getErrorAlert( Text.ERROR(), re.getMessage() );
        }
        finally
        {
            facilityMIDlet.getDataSetListView().prepareView();
            facilityMIDlet.getWaitingView().switchDisplayable( alert,
                facilityMIDlet.getDataSetListView().getDataSetList() );
            System.gc();
        }

    }

    public DataSetValue getDataSetValue()
    {
        return dataSetValue;
    }

    public void setDataSetValue( DataSetValue dataSetValue )
    {
        this.dataSetValue = dataSetValue;
    }

}
