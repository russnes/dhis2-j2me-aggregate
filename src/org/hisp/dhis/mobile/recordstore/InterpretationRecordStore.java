package org.hisp.dhis.mobile.recordstore;

import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.Interpretation;
import org.hisp.dhis.mobile.recordstore.filter.InterpretationFiler;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class InterpretationRecordStore
{
    private static final String CLASS_TAG = "InterpretationRecordStore";
    public static final String INTERPRETATION_DB = "INTERPRETATION";

    public InterpretationRecordStore()
    {

    }

    public static void saveInterpretation( Interpretation interpretation )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( INTERPRETATION_DB, true );
            InterpretationFiler interpretationFilter = new InterpretationFiler( interpretation );

            RecordEnumeration recordEnum = recordStore.enumerateRecords( interpretationFilter, null, false );
            byte[] bytes = SerializationUtil.serialize( interpretation );
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

    public static Interpretation load()
        throws RecordStoreNotOpenException, RecordStoreException, IOException
    {
       
        RecordStore rs = null;
        RecordEnumeration re = null;
        
        Interpretation  interpretation = null;

        try
        {
            rs = RecordStore.openRecordStore( INTERPRETATION_DB, true );
            re = rs.enumerateRecords( null, null, false );
      
               interpretation = new Interpretation( );
              SerializationUtil.deSerialize( interpretation, re.nextRecord() );

          
        }
        finally
        {
            if ( re != null )
                re.destroy();
            if ( rs != null )
                rs.closeRecordStore();
            System.gc();
        }

        return interpretation;
    }

    public static void deleteRecord()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
    {
        RecordStore rs = RecordStore.openRecordStore( INTERPRETATION_DB, true );
        RecordEnumeration re = null;
        try
        {
            re = rs.enumerateRecords( null, null, true );
        }
        catch ( RecordStoreNotOpenException ex )
        {
            LogMan.log( "RMS," + CLASS_TAG, ex );
            ex.printStackTrace();
        }

        int rid = 0;

        try
        {
            while ( re.hasNextElement() )
            {
                rid = re.nextRecordId();
                try
                {
                    rs.deleteRecord( rid );
                }
                catch ( RecordStoreNotOpenException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
                catch ( InvalidRecordIDException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
                catch ( RecordStoreException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
            }
        }
        catch ( InvalidRecordIDException ex )
        {
            LogMan.log( "RMS," + CLASS_TAG, ex );
            ex.printStackTrace();
        }
    }

}
