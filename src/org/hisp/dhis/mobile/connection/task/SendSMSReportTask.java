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

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class SendSMSReportTask
    extends AbstractTask
{
    private String smsString;

    public void run()
    {
        final FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();

        SettingRecordStore settingRecordStore = null;

        try
        {
            settingRecordStore = new SettingRecordStore();
            MessageConnection smsConn = (MessageConnection) Connector.open( "sms://"
                + settingRecordStore.get( SettingRecordStore.SERVER_PHONE_NUMBER ) );

            TextMessage sms = (TextMessage) smsConn.newMessage( MessageConnection.TEXT_MESSAGE );

            sms.setPayloadText( smsString );

            smsConn.send( sms );
            smsConn.close();

        }
        catch ( IOException ie )
        {
            ie.printStackTrace();
        }
        catch ( SecurityException se )
        {
            se.printStackTrace();
        }
        catch ( NullPointerException ne )
        {
            ne.printStackTrace();
        }
        catch ( IllegalArgumentException ile )
        {
            ile.printStackTrace();
            facilityMIDlet.getDataSetEntryView().getDisplay()
                .setCurrent( Alerts.getErrorAlert( Text.ERROR(), Text.INVALID_PHONE_NUMBER() ) );
        }
        catch ( RecordStoreException re )
        {
            re.printStackTrace();
        }

    }

    public String getSmsString()
    {
        return smsString;
    }

    public void setSmsString( String smsString )
    {
        this.smsString = smsString;
    }

}
