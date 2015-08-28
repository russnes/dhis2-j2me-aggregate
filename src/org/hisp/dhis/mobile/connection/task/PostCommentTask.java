package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Interpretation;
import org.hisp.dhis.mobile.model.InterpretationComment;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class PostCommentTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "PostCommentTask";

    private static final String COMMENT_SENT = "comment_sent";

    private String data;

    public PostCommentTask( String data )
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
            InterpretationComment interpretationComment = new InterpretationComment();
            interpretationComment.deSerialize( messageStream );
            String message = interpretationComment.getText();
            if ( message.equalsIgnoreCase( COMMENT_SENT ) )
            {
                alert = Alerts.getInfoAlert( "Comment sent", "Comment sent sucessfully" );
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
