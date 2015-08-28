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
import java.util.Date;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.filter.ActivityFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class ActivityRecordStore
{
    public static final String ACTIVITY_DB = "ACTIVITY";

    public static boolean saveActivities( Vector activityVector, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < activityVector.size(); i++ )
        {
            Activity activity = (Activity) activityVector.elementAt( i );
            if ( !saveActivity( activity, orgUnit ) )
            {
                return false;
            }
        }
        return true;
    }

    public static boolean saveActivity( Activity activity, OrgUnit orgUnit )
    throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
{
    RecordStore recordStore = null;
    RecordEnumeration recordEnumeration = null;
    ActivityFilter activityFilter = new ActivityFilter();
    activityFilter.setCriteria( new Integer( activity.getTask().getProgStageInstId() ), null, null, null, null );

    try
    {
        recordStore = RecordStore.openRecordStore( ACTIVITY_DB + orgUnit.getId(), true );
        recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
        byte[] bite = SerializationUtil.serialize( activity );
        if ( recordEnumeration.numRecords() > 0 )
        {
            int id = recordEnumeration.nextRecordId();
            recordStore.setRecord( id, bite, 0, bite.length );
        }
        else
        {
            recordStore.addRecord( bite, 0, bite.length );
        }
    }
    finally
    {
        recordEnumeration.destroy();
        recordStore.closeRecordStore();
        System.gc();
    }

    return true;
}

    public static Vector loadActivityByCompleteStatus( boolean isCompleted, OrgUnit orgUnit )
        throws RecordStoreException, IOException
    {
        Vector activityVector = new Vector();
        RecordStore recordStore = null;
        Activity activity = null;
        RecordEnumeration recordEnumeration = null;
        ActivityFilter activityFilter = new ActivityFilter();
        activityFilter.setCriteria( null, new Boolean( isCompleted ), null, null, null );

        try
        {
            recordStore = RecordStore.openRecordStore( ACTIVITY_DB + orgUnit.getId(), true );
            recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                activity = null;
                activity = new Activity();
                SerializationUtil.deSerialize( activity, recordEnumeration.nextRecord() );
                activityVector.addElement( activity );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return activityVector;
    }

    public static Vector loadActivityByPatient( String patientName, boolean isCompleted )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector activityVector = new Vector();
        RecordStore recordStore = null;
        Activity activity = null;
        RecordEnumeration recordEnumeration = null;
        ActivityFilter activityFilter = new ActivityFilter();
        activityFilter.setCriteria( null, new Boolean( isCompleted ), patientName, null, null );

        try
        {
            recordStore = RecordStore.openRecordStore( ACTIVITY_DB, true );
            recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                activity = null;
                activity = new Activity();
                SerializationUtil.deSerialize( activity, recordEnumeration.nextRecord() );
                activityVector.addElement( activity );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return activityVector;
    }

    public static Vector loadActivityByGroupingValue( String groupingValue, boolean isComplete )
        throws RecordStoreException, IOException
    {
        Vector activityVector = new Vector();
        RecordStore recordStore = null;
        Activity activity = null;
        RecordEnumeration recordEnumeration = null;
        ActivityFilter activityFilter = new ActivityFilter();
        activityFilter.setCriteria( null, new Boolean( isComplete ), null, groupingValue, null );

        try
        {
            recordStore = RecordStore.openRecordStore( ACTIVITY_DB, true );
            recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                activity = null;
                activity = new Activity();
                SerializationUtil.deSerialize( activity, recordEnumeration.nextRecord() );
                activityVector.addElement( activity );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return activityVector;
    }

    public static boolean updateActivities( Vector activityVector, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < activityVector.size(); i++ )
        {
            Activity activity = (Activity) activityVector.elementAt( i );
            if ( !updateActivity( activity, orgUnit ) )
            {
                return false;
            }
        }
        return true;
    }

    public static boolean updateActivity( Activity activity, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        ActivityFilter activityFilter = new ActivityFilter();
        activityFilter.setCriteria( new Integer( activity.getTask().getProgStageInstId() ), null, null, null, null );

        try
        {
            recordStore = RecordStore.openRecordStore( ACTIVITY_DB + orgUnit.getId(), true );
            recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
            byte[] bite = SerializationUtil.serialize( activity );
            if ( recordEnumeration.numRecords() <= 0 )
            {
                recordStore.addRecord( bite, 0, bite.length );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return true;
    }

    public static void deleteExpiredActivities( Date currentDate )
        throws RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        ActivityFilter activityFilter = new ActivityFilter();
        activityFilter.setCriteria( null, null, null, null, currentDate );

        try
        {
            recordStore = RecordStore.openRecordStore( ACTIVITY_DB, true );
            recordEnumeration = recordStore.enumerateRecords( activityFilter, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                int recordID = recordEnumeration.nextRecordId();
                Activity activity = new Activity();
                SerializationUtil.deSerialize( activity, recordStore.getRecord( recordID ) );
                recordStore.deleteRecord( recordID );
                ActivityValueRecordStore.deleteActivityValue( activity.getTask().getProgStageInstId() );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
    }
}
