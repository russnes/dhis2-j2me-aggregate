package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Conversation;
import org.hisp.dhis.mobile.model.MessageConversation;
import org.hisp.dhis.mobile.recordstore.MessageConversationRecordStore;

public class DownloadMessageConversationTask
    extends AbstractTask
{
    public static final String CLASS_TAG = "DownloadMessageConversationTask";

    public DownloadMessageConversationTask()
    {
    }

    public void run()
    {
        FacilityMIDlet facilityMIDlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();

        DataInputStream inputStream = null;

        Conversation conversation = new Conversation();

        try
        {
            inputStream = this.download();
            conversation.deSerialize( inputStream );

            Vector messageConversationsVector = conversation.getMessageConversations();

            if ( messageConversationsVector != null )
            {
                MessageConversationRecordStore.deleteRecordStore();
                MessageConversationRecordStore.saveConversations( messageConversationsVector );
            }

            messageConversationsVector = null;

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            facilityMIDlet.getMessageConversationView().showView();
        }

    }

}
