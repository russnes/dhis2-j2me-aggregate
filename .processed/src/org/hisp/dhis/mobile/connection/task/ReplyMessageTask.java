package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class ReplyMessageTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "ReplyMessageTask";

    private static final String MESSAGE_SENT = "message_sent";

    private Message message;

    public ReplyMessageTask( Message message )
    {
        this.message = message;
    }

    public ReplyMessageTask()
    {

    }

    public void run()
    {
        final FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();
        Alert alert = null;

        try
        {

            DataInputStream messageStream = this.upload( SerializationUtil.serialize( message ) );

            String message = this.readMessage( messageStream );

            if ( message.equalsIgnoreCase( MESSAGE_SENT ) )
            {
                alert = Alerts.getInfoAlert( Text.MESSAGE(), Text.MESSAGE_SENT_SUCESSFULLY() );
                StringRecordStore.deleteRecord();
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
                facilityMIDlet.getMessagingMenuView().getMenuList() );
        }

    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

}
