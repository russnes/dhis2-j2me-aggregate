package org.hisp.dhis.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

public class OrgUnit
    implements DataStreamSerializable
{
    private static final String CLASS_TAG = "OrgUnit";

    private int id;

    private String name;

    private String downloadAllUrl;

    private String updateActivityPlanUrl;

    private String uploadFacilityReportUrl;

    private String downloadFacilityReportUrl;

    private String uploadActivityReportUrl;

    private String updateDataSetsUrl;

    private String changeDataSetLangUrl;

    private String searchUrl;

    private String updateNewVersionUrl;

    private String sendFeedbackUrl;

    private String findUserUrl;

    private String sendMessageUrl;

    private String downloadMessageConversationUrl;

    private String getMessageUrl;

    private String replyMessageUrl;

    private String downloadInterpretationUrl;

    private String postInterpretationUrl;

    private String postComment;

    public static double serverVersion;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDownloadAllUrl()
    {
        return downloadAllUrl;
    }

    public void setDownloadAllUrl( String downloadAllUrl )
    {
        this.downloadAllUrl = downloadAllUrl;
    }

    public String getUploadFacilityReportUrl()
    {
        return uploadFacilityReportUrl;
    }

    public void setUploadFacilityReportUrl( String uploadFacilityReportUrl )
    {
        this.uploadFacilityReportUrl = uploadFacilityReportUrl;
    }

    public String getDownloadFacilityReportUrl()
    {
        return downloadFacilityReportUrl;
    }

    public void setDownloadFacilityReportUrl( String downloadFacilityReportUrl )
    {
        this.downloadFacilityReportUrl = downloadFacilityReportUrl;
    }

    public String getUploadActivityReportUrl()
    {
        return uploadActivityReportUrl;
    }

    public void setUploadActivityReportUrl( String uploadActivityReportUrl )
    {
        this.uploadActivityReportUrl = uploadActivityReportUrl;
    }

    public String getUpdateDataSetsUrl()
    {
        return updateDataSetsUrl;
    }

    public void setUpdateDataSetsUrl( String updateDataSetsUrl )
    {
        this.updateDataSetsUrl = updateDataSetsUrl;
    }

    public String getUpdateActivityPlanUrl()
    {
        return updateActivityPlanUrl;
    }

    public void setUpdateActivityPlanUrl( String updateActivityPlanUrl )
    {
        this.updateActivityPlanUrl = updateActivityPlanUrl;
    }

    public String getChangeDataSetLangUrl()
    {
        return changeDataSetLangUrl;
    }

    public void setChangeDataSetLangUrl( String changeDataSetLangUrl )
    {
        this.changeDataSetLangUrl = changeDataSetLangUrl;
    }

    public String getSearchUrl()
    {
        return searchUrl;
    }

    public void setSearchUrl( String searchUrl )
    {
        this.searchUrl = searchUrl;
    }

    public String getUpdateNewVersionUrl()
    {
        return updateNewVersionUrl;
    }

    public void setUpdateNewVersionUrl( String updateNewVersionUrl )
    {
        this.updateNewVersionUrl = updateNewVersionUrl;
    }

    public String getSendFeedbackUrl()
    {
        return sendFeedbackUrl;
    }

    public void setSendFeedbackUrl( String sendFeedbackUrl )
    {
        this.sendFeedbackUrl = sendFeedbackUrl;
    }

    public String getFindUserUrl()
    {
        return findUserUrl;
    }

    public void setFindUserUrl( String findUserUrl )
    {
        this.findUserUrl = findUserUrl;
    }

    public String getSendMessageUrl()
    {
        return sendMessageUrl;
    }

    public void setSendMessageUrl( String sendMessageUrl )
    {
        this.sendMessageUrl = sendMessageUrl;
    }

    public String getDownloadMessageConversationUrl()
    {
        return downloadMessageConversationUrl;
    }

    public void setDownloadMessageConversationUrl( String downloadMessageConversationUrl )
    {
        this.downloadMessageConversationUrl = downloadMessageConversationUrl;
    }

    public String getGetMessageUrl()
    {
        return getMessageUrl;
    }

    public void setGetMessageUrl( String getMessageUrl )
    {
        this.getMessageUrl = getMessageUrl;
    }

    public String getReplyMessageUrl()
    {
        return replyMessageUrl;
    }

    public void setReplyMessageUrl( String replyMessageUrl )
    {
        this.replyMessageUrl = replyMessageUrl;
    }

    public String getDownloadInterpretationUrl()
    {
        return downloadInterpretationUrl;
    }

    public void setDownloadInterpretationUrl( String downloadInterpretationUrl )
    {
        this.downloadInterpretationUrl = downloadInterpretationUrl;
    }

    public String getPostInterpretationUrl()
    {
        return postInterpretationUrl;
    }

    public void setPostInterpretationUrl( String postInterpretationUrl )
    {
        this.postInterpretationUrl = postInterpretationUrl;
    }

    public String getPostComment()
    {
        return postComment;
    }

    public void setPostComment( String postComment )
    {
        this.postComment = postComment;
    }

    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {

        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.name );
        dataOutputStream.writeUTF( this.downloadAllUrl );
        dataOutputStream.writeUTF( this.updateActivityPlanUrl );
        dataOutputStream.writeUTF( this.uploadFacilityReportUrl );
        dataOutputStream.writeUTF( this.downloadFacilityReportUrl );
        dataOutputStream.writeUTF( this.uploadActivityReportUrl );
        dataOutputStream.writeUTF( this.updateDataSetsUrl );
        dataOutputStream.writeUTF( this.changeDataSetLangUrl );
        dataOutputStream.writeUTF( this.searchUrl );
        dataOutputStream.writeUTF( this.updateNewVersionUrl );
        dataOutputStream.writeUTF( this.sendFeedbackUrl );
        dataOutputStream.writeUTF( this.findUserUrl );
        dataOutputStream.writeUTF( this.sendMessageUrl );
        dataOutputStream.writeUTF( this.downloadMessageConversationUrl );
        dataOutputStream.writeUTF( this.getMessageUrl );
        dataOutputStream.writeUTF( this.replyMessageUrl );
        dataOutputStream.writeUTF( this.downloadInterpretationUrl );
        dataOutputStream.writeUTF( this.postInterpretationUrl );
        dataOutputStream.writeUTF( this.postComment );
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.id = dataInputStream.readInt();
        this.name = dataInputStream.readUTF();
        this.downloadAllUrl = dataInputStream.readUTF();
        this.updateActivityPlanUrl = dataInputStream.readUTF();
        this.uploadFacilityReportUrl = dataInputStream.readUTF();
        this.downloadFacilityReportUrl = dataInputStream.readUTF();
        this.uploadActivityReportUrl = dataInputStream.readUTF();
        this.updateDataSetsUrl = dataInputStream.readUTF();
        this.changeDataSetLangUrl = dataInputStream.readUTF();
        this.searchUrl = dataInputStream.readUTF();
        this.updateNewVersionUrl = dataInputStream.readUTF();
        this.sendFeedbackUrl = dataInputStream.readUTF();
        this.findUserUrl = dataInputStream.readUTF();
        this.sendMessageUrl = dataInputStream.readUTF();
        this.downloadMessageConversationUrl = dataInputStream.readUTF();
        this.getMessageUrl = dataInputStream.readUTF();
        this.replyMessageUrl = dataInputStream.readUTF();
        this.downloadInterpretationUrl = dataInputStream.readUTF();
        this.postInterpretationUrl = dataInputStream.readUTF();
        this.postComment = dataInputStream.readUTF();

    }

    /**
     * 
     * @param serverVersion
     * @return
     */
    public boolean checkNewVersion( double serverVersion )
    {
        double clientVersion;
        boolean result = false;
        try
        {
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            clientVersion = Double.parseDouble( settingRecordStore.get( SettingRecordStore.CLIENT_VERSION ) );
            this.serverVersion = serverVersion;

            LogMan.log( LogMan.INFO, CLASS_TAG, "CheckVersion: Server Version: " + this.serverVersion );
            LogMan.log( LogMan.INFO, CLASS_TAG, "CheckVersion: Client Version: " + clientVersion );

            if ( this.serverVersion > clientVersion )
            {
                result = true;
                LogMan.log( LogMan.DEBUG, CLASS_TAG, "CheckVersion: New version found" );
            }
            else
            {
                LogMan.log( LogMan.DEBUG, CLASS_TAG, "CheckVersion: No new version found" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }

}
