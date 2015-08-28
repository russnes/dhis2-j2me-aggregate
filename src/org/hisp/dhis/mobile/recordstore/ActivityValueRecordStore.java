package org.hisp.dhis.mobile.recordstore;

import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.model.ActivityValue;
import org.hisp.dhis.mobile.recordstore.filter.ActivityValueFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

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

public class ActivityValueRecordStore
{
    public static final String ACTIVITYVALUE_DB = "ACTIVITYVALUE";

    public static boolean saveActivityValue( ActivityValue activityValue )
        throws RecordStoreException, IOException
    {
        ActivityValueFilter activityValueFilter = new ActivityValueFilter();
        activityValueFilter.setCriteria( activityValue.getProgramStageInstanceId() );
        RecordEnumeration re = null;
        RecordStore rs = null;
        try
        {
            rs = RecordStore.openRecordStore( ACTIVITYVALUE_DB, true );
            re = rs.enumerateRecords( activityValueFilter, null, true );
            byte[] bytes = SerializationUtil.serialize( activityValue );

            if ( re.numRecords() > 0 )
            {
                int id = re.nextRecordId();
                rs.setRecord( id, bytes, 0, bytes.length );
            }
            else
            {
                rs.addRecord( bytes, 0, bytes.length );
            }
            activityValueFilter = null;
        }
        finally
        {
            re.destroy();
            rs.closeRecordStore();
            System.gc();

        }
        System.gc();
        return true;
    }

    public static ActivityValue loadActivity( int proStageInsID )
        throws RecordStoreException, IOException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        ActivityValueFilter activityValueFilter = new ActivityValueFilter();
        activityValueFilter.setCriteria( proStageInsID );
        ActivityValue activityValue = new ActivityValue();
        try
        {
            rs = RecordStore.openRecordStore( ACTIVITYVALUE_DB, true );
            re = rs.enumerateRecords( activityValueFilter, null, true );
            if ( re.numRecords() > 0 )
            {
                SerializationUtil.deSerialize( activityValue, re.nextRecord() );
                return activityValue;
            }
            activityValueFilter = null;
            System.gc();
        }
        finally
        {
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
        return null;
    }

    public static boolean deleteActivityValue( int proStageInsID )
        throws RecordStoreException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        ActivityValueFilter activityValueFilter = new ActivityValueFilter();
        activityValueFilter.setCriteria( proStageInsID );
        try
        {
            rs = RecordStore.openRecordStore( ACTIVITYVALUE_DB, true );
            re = rs.enumerateRecords( activityValueFilter, null, true );
            if ( re.numRecords() > 0 )
            {
                rs.deleteRecord( re.nextRecordId() );
            }
            activityValueFilter = null;
            System.gc();
        }
        finally
        {
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
        return true;

    }
}
