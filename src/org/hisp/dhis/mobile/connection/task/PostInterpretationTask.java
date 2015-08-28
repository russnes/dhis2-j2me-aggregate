package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Interpretation;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class PostInterpretationTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "PostInterpretationTask";

    private static final String INTERPRETATION_SENT = "interpretation_sent";

    private String data;

    public PostInterpretationTask( String data )
    {
        super();
        this.data = data;
    }

    public void run()
    {
        final FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();
        Alert alert = null;
        try
        {
            DataInputStream messageStream = this.download( data, "data" );
            Interpretation interpretation = new Interpretation();
            interpretation.deSerialize( messageStream );
            String message = interpretation.getText();
            if ( message.equalsIgnoreCase( INTERPRETATION_SENT ) )
            {
                alert = Alerts.getInfoAlert( "Interpretation sent", "Interpretation sent sucessfully" );
            }
            else
            {
                alert = Alerts.getErrorAlert( Text.ERROR(), Text.SEND_FAIL() );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            alert = Alerts.getErrorAlert( Text.ERROR(), e.getMessage() );
        }

        finally
        {
            facilityMIDlet.getWaitingView().switchDisplayable( alert,
                facilityMIDlet.getMainMenuView().getMainMenuList() );

        }

    }

    public String getData()
    {
        return data;
    }

    public void setData( String data )
    {
        this.data = data;
    }

}
