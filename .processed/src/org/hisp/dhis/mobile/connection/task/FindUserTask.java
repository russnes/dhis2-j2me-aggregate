package org.hisp.dhis.mobile.connection.task;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
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
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Recipient;
import org.hisp.dhis.mobile.recordstore.UserRecordStore;

public class FindUserTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "FindUserTask";

    private String keyword;

    public FindUserTask( String keyword )
    {
        super();
        this.keyword = keyword;
    }

    public void run()
    {
        FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();

        DataInputStream inputStream = null;
        Recipient recipient = new Recipient();

        try
        {
            inputStream = this.download( keyword, "name" );

            recipient.deSerialize( inputStream );

            Vector userVector = recipient.getUsers();

            if ( userVector != null )
            {
                UserRecordStore.deleteRecordStore();
                UserRecordStore.saveUsers( userVector );
            }

            userVector = null;

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            facilityMIDlet.getUserListView().showView();
        }

    }

}
