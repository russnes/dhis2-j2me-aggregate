package org.hisp.dhis.mobile.recordstore;

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
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.model.SMSCommand;
import org.hisp.dhis.mobile.recordstore.filter.SMSCommandFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class SMSCommandRecordStore
{
    public static final String SMSCOMMAND_DB = "SMSCOMMAND";

    public static boolean saveSMSCommands( Vector smsCommandVector )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < smsCommandVector.size(); i++ )
        {
            SMSCommand smsCommand = (SMSCommand) smsCommandVector.elementAt( i );
            saveSMSCommand( smsCommand );
        }
        return true;
    }

    public static void saveSMSCommand( SMSCommand smsCommand )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( SMSCOMMAND_DB, true );
            SMSCommandFilter filter = new SMSCommandFilter();
            filter.setDataSetId( smsCommand.getDataSetId() );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( filter, null, false );
            byte[] bytes = SerializationUtil.serialize( smsCommand );
            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.setRecord( id, bytes, 0, bytes.length );
            }
            else
            {
                recordStore.addRecord( bytes, 0, bytes.length );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }

    }

    public static SMSCommand loadSMSCommand( int dataSetID )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {

        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        SMSCommandFilter filter = new SMSCommandFilter();
        filter.setDataSetId( dataSetID );

        try
        {
            recordStore = RecordStore.openRecordStore( SMSCOMMAND_DB, true );
            recordEnumeration = recordStore.enumerateRecords( filter, null, false );
            
            if ( recordEnumeration.numRecords() > 0 )
            {
                SMSCommand smsCommand = new SMSCommand();
                SerializationUtil.deSerialize( smsCommand, recordEnumeration.nextRecord() );
                return smsCommand;
            }

        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return null;
    }
}
