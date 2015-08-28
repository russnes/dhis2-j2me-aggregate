package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Interpretation;
import org.hisp.dhis.mobile.recordstore.InterpretationRecordStore;

public class DownloadInterpretationTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "DownloadInterpretationTask";

    private String uId;

    public DownloadInterpretationTask( String uId )
    {
        super();
        this.uId = uId;
    }

    public void run()
    {
        FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();

        DataInputStream inputStream = null;
        Interpretation interpretation = new Interpretation();

        try
        {
            inputStream = this.download( uId, "uId" );
            interpretation.deSerialize( inputStream );

            if ( interpretation != null )
            {

                InterpretationRecordStore.deleteRecord();
                InterpretationRecordStore.saveInterpretation( interpretation );

            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }

        finally
        {
            try
            {
                if ( InterpretationRecordStore.load() != null )
                {
                    facilityMIDlet.getViewInterpretation().showView();
                }
                else
                {
                    facilityMIDlet.getViewPostNewInterpretation().showView();

                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

}
