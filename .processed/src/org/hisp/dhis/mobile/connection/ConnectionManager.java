package org.hisp.dhis.mobile.connection;

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

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.task.AbstractTask;
import org.hisp.dhis.mobile.connection.task.DownloadAllResourceTask;
import org.hisp.dhis.mobile.connection.task.DownloadDataSetValuesTask;
import org.hisp.dhis.mobile.connection.task.DownloadInterpretationTask;
import org.hisp.dhis.mobile.connection.task.DownloadMessageConversationTask;
import org.hisp.dhis.mobile.connection.task.FindUserTask;
import org.hisp.dhis.mobile.connection.task.GetMessageTask;
import org.hisp.dhis.mobile.connection.task.LoginTask;
import org.hisp.dhis.mobile.connection.task.PostCommentTask;
import org.hisp.dhis.mobile.connection.task.PostInterpretationTask;
import org.hisp.dhis.mobile.connection.task.ReplyMessageTask;
import org.hisp.dhis.mobile.connection.task.SendFeedbackTask;
import org.hisp.dhis.mobile.connection.task.SendMessageTask;
import org.hisp.dhis.mobile.connection.task.SendSMSReportTask;
import org.hisp.dhis.mobile.connection.task.UpdateActivityPlanTask;
import org.hisp.dhis.mobile.connection.task.UpdateContentLanguageTask;
import org.hisp.dhis.mobile.connection.task.UpdateDataSetTask;
import org.hisp.dhis.mobile.connection.task.UpdateNewVersionTask;
import org.hisp.dhis.mobile.connection.task.UploadActivityValueTask;
import org.hisp.dhis.mobile.connection.task.UploadDataSetValueTask;
import org.hisp.dhis.mobile.connection.task.UploadPatientIdentifierNumberTask;
import org.hisp.dhis.mobile.connection2.ManagerNetwork;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.ActivityValue;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

public class ConnectionManager
{
    private static final String CLASS_TAG = "Network,ConnectionManager";

    public static final String MEDIATYPE_MOBILE_SERIALIZED = "application/vnd.org.dhis2.mobile+serialized";

    // Class variables

    private static DHISMIDlet dhisMIDlet;

    private static String url;

    private static String userName;

    private static String password;

    private static String ua;

    private static String locale;

    private static OrgUnit orgUnit;

    public static void init( org.hisp.dhis.mobile.midlet.DHISMIDlet dhisMIDlet, String url, String userName,
        String password, String locale, OrgUnit orgUnit )
    {
        LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Initializing Connection Manager..." );

        ConnectionManager.dhisMIDlet = dhisMIDlet;
        ConnectionManager.url = url;
        ConnectionManager.userName = userName;
        ConnectionManager.password = password;
        ConnectionManager.locale = locale;
        ConnectionManager.orgUnit = orgUnit;

        ua = "Profile/" + System.getProperty( "microedition.profiles" ) + " Configuration/"
            + System.getProperty( "microedition.configuration" );

        LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Connection Settings: URL=" + url );
        LogMan.log( LogMan.DEV, "Network,Authentication," + CLASS_TAG, "Connection Settings: Username=" + userName );
        LogMan.log( LogMan.DEV, "Network,Authentication," + CLASS_TAG, "Connection Settings: Password=" + password );
        LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Connection Settings: Locale=" + locale );
        LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Connection Settings: User-Agent=" + ua );

        ManagerNetwork netman = ManagerNetwork.getInstance();

        SettingRecordStore settingRecordStore;
        try
        {
            settingRecordStore = new SettingRecordStore();
            String basicUrl = settingRecordStore.get( SettingRecordStore.SERVER_URL_BASIC );
            netman.setServerURL( basicUrl );
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }

        netman.setMidlet( dhisMIDlet );
        netman.setUserAgent( ConnectionManager.getUa() );
        netman.setAcceptedLanguage( ConnectionManager.getLocale() );
        netman.setContentType( ConnectionManager.MEDIATYPE_MOBILE_SERIALIZED );
        netman.setAccept( ConnectionManager.MEDIATYPE_MOBILE_SERIALIZED );

        netman.setUsername( ConnectionManager.getUserName() );
        netman.setPassword( ConnectionManager.getPassword() );

        byte[] auth = (netman.getUsername() + ":" + netman.getPassword()).getBytes();
        String encoded = Base64.encode( auth, 0, auth.length );
        netman.setAuthorization( "Basic " + encoded );
    }

    /**
     * 
     * @return
     */
    public static HttpConnection createConnection()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Creating connection..." );
        HttpConnection connection = null;
        try
        {
            connection = getConnection( url );
            connection.setRequestProperty( "User-Agent", ua );
            connection.setRequestProperty( "Accept-Language", locale );
            connection.setRequestProperty( "Content-Type", MEDIATYPE_MOBILE_SERIALIZED );
            connection.setRequestProperty( "Accept", MEDIATYPE_MOBILE_SERIALIZED );

            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: User-Agent: " + ua );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Accept-Language: " + locale );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Content-Type: "
                + MEDIATYPE_MOBILE_SERIALIZED );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Accept: "
                + MEDIATYPE_MOBILE_SERIALIZED );

            // set HTTP basic authentication
            if ( ConnectionManager.userName != null && password != null )
            {
                LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Encoding... " + userName + ":" + password );
                byte[] auth = (userName + ":" + password).getBytes();
                String encoded = Base64.encode( auth, 0, auth.length );

                LogMan.log( LogMan.INFO, "Network,HTTPHeader,Authentication," + CLASS_TAG,
                    "HTTP Header: Authorization: " + "Basic " + encoded );
                connection.setRequestProperty( "Authorization", "Basic " + encoded );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return connection;
    }

    public static HttpConnection getConnection( String url )
        throws IOException
    {
        if ( url.startsWith( "https://" ) )
        {
            LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Opening HTTPS Connection: " + url );
            return (HttpsConnection) Connector.open( url, Connector.READ_WRITE, true );
        }
        else
        {
            LogMan.log( LogMan.DEV, "Network" + CLASS_TAG, "Opening HTTP Connection: " + url );
            return (HttpConnection) Connector.open( url, Connector.READ_WRITE, true );
        }
    }

    public static void login()
    {
        runTask( new LoginTask() );
    }

    public static void downloadAllResource( Vector orgUnitVector )
    {
        runTask( new DownloadAllResourceTask( orgUnitVector ) );
    }

    public static void updateDataSet()
    {
        runTask( new UpdateDataSetTask() );
    }

    public static void uploadDataSetValue( DataSetValue dataSetValue )
    {
        UploadDataSetValueTask uploadDataSetValueTask = new UploadDataSetValueTask();
        uploadDataSetValueTask.setDataSetValue( dataSetValue );
        runTask( uploadDataSetValueTask );
    }

    public static void downloadDataSetValues()
    {
        DownloadDataSetValuesTask downloadDataSetValuesTask = new DownloadDataSetValuesTask();
        runTask( downloadDataSetValuesTask );
    }

    public static void uploadActivityValue( ActivityValue activityValue, Activity activity )
    {
        UploadActivityValueTask uploadActivityValueTask = new UploadActivityValueTask();
        uploadActivityValueTask.setActivity( activity );
        uploadActivityValueTask.setActivityValue( activityValue );
        runTask( uploadActivityValueTask );
    }

    public static void updateActivityPlan()
    {
        runTask( new UpdateActivityPlanTask() );
    }

    public static void uploadBeneficiaryIdentifier( String identifier )
    {
        UploadPatientIdentifierNumberTask uploadPatientIdentifierNumberTask = new UploadPatientIdentifierNumberTask();
        uploadPatientIdentifierNumberTask.setPatientIdentifier( identifier );
        runTask( uploadPatientIdentifierNumberTask );
    }

    public static void updateContentLanguage()
    {
        runTask( new UpdateContentLanguageTask() );
    }

    public static void updateNewVersion()
    {
        runTask( new UpdateNewVersionTask() );
    }

    public static void sendSMSReport( String smsString )
    {
        SendSMSReportTask sendSMSReportTask = new SendSMSReportTask();
        sendSMSReportTask.setSmsString( smsString );
        runTask( sendSMSReportTask );
    }

    public static void sendFeedback( Message message )
    {
        SendFeedbackTask sendFeedbackTask = new SendFeedbackTask();
        sendFeedbackTask.setMessage( message );
        runTask( sendFeedbackTask );
    }

    public static void findUser( String keyword )
    {
        FindUserTask findUserTask = new FindUserTask( keyword );
        runTask( findUserTask );
    }

    public static void sendMessage( Message message )
    {
        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.setMessage( message );
        runTask( sendMessageTask );
    }

    public static void downloadMessageConversation()
    {
        DownloadMessageConversationTask downloadMessageConversationTask = new DownloadMessageConversationTask();
        runTask( downloadMessageConversationTask );
    }

    public static void getMessage( int conversationId )
    {
        GetMessageTask getMessageTask = new GetMessageTask( conversationId );
        runTask( getMessageTask );
    }

    public static void replyMessage( Message message )
    {
        ReplyMessageTask replyMessageTask = new ReplyMessageTask();
        replyMessageTask.setMessage( message );
        runTask( replyMessageTask );
    }

    public static void downloadInterpretation( String uId )
    {
        DownloadInterpretationTask dowTask = new DownloadInterpretationTask( uId );
        runTask( dowTask );
    }

    public static void postNewInterpretation( String data )
    {
        PostInterpretationTask postInterpretationTask = new PostInterpretationTask( data );
        runTask( postInterpretationTask );
    }

    public static void postComment( String data )
    {
        PostCommentTask postCommentTask = new PostCommentTask( data );
        runTask( postCommentTask );
    }

    public static void runTask( AbstractTask task )
    {
        new Thread( task ).start();
    }

    public static DHISMIDlet getDhisMIDlet()
    {
        return dhisMIDlet;
    }

    public static void setDhisMIDlet( DHISMIDlet dhisMIDlet )
    {
        ConnectionManager.dhisMIDlet = dhisMIDlet;
    }

    public static String getUrl()
    {
        return url;
    }

    public static void setUrl( String url )
    {
        ConnectionManager.url = url;
    }

    public static String getUserName()
    {
        return userName;
    }

    public static void setUserName( String userName )
    {
        ConnectionManager.userName = userName;
    }

    public static String getPassword()
    {
        return password;
    }

    public static void setPassword( String password )
    {
        ConnectionManager.password = password;
    }

    public static String getUa()
    {
        return ua;
    }

    public static void setUa( String ua )
    {
        ConnectionManager.ua = ua;
    }

    public static String getLocale()
    {
        return locale;
    }

    public static void setLocale( String locale )
    {
        ConnectionManager.locale = locale;
    }

    public static OrgUnit getOrgUnit()
    {
        return orgUnit;
    }

    public static void setOrgUnit( OrgUnit orgUnit )
    {
        ConnectionManager.orgUnit = orgUnit;
    }

    public static String getMediatypeMobileSerialized()
    {
        return MEDIATYPE_MOBILE_SERIALIZED;
    }
}
