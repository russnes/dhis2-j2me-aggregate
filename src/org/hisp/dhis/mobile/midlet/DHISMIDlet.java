package org.hisp.dhis.mobile.midlet;

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
import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.imagereports.ViewInterpretation;
import org.hisp.dhis.mobile.imagereports.ViewInterpretationComment;
import org.hisp.dhis.mobile.imagereports.ViewLoadInterpretation;
import org.hisp.dhis.mobile.imagereports.ViewPostNewInterpretation;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.log.LogUtils;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.TextMapping;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.ui.TextMappingLoader;
import org.hisp.dhis.mobile.util.Properties;
import org.hisp.dhis.mobile.view.FeedbackContentView;
import org.hisp.dhis.mobile.view.FeedbackView;
import org.hisp.dhis.mobile.view.FindUserView;
import org.hisp.dhis.mobile.view.LoginView;
import org.hisp.dhis.mobile.view.MainMenuView;
import org.hisp.dhis.mobile.view.MessageConversationView;
import org.hisp.dhis.mobile.view.MessageDetailView;
import org.hisp.dhis.mobile.view.MessageOptionView;
import org.hisp.dhis.mobile.view.MessageReplyView;
import org.hisp.dhis.mobile.view.MessageSubjectView;
import org.hisp.dhis.mobile.view.MessageView;
import org.hisp.dhis.mobile.view.MessagingMenuView;
import org.hisp.dhis.mobile.view.OrgUnitSelectView;
import org.hisp.dhis.mobile.view.PinView;
import org.hisp.dhis.mobile.view.SettingView;
import org.hisp.dhis.mobile.view.SplashScreenView;
import org.hisp.dhis.mobile.view.UpdateNewVersionView;
import org.hisp.dhis.mobile.view.UserListView;
import org.hisp.dhis.mobile.view.WaitingView;

public class DHISMIDlet
    extends MIDlet
{
    private static String CLASS_TAG = "DHISMIDlet";

    // Debug Mode
    public static final boolean DEBUG = true;

    public static boolean DISABLE_IMAGE_REPORTS = false;

    // Flag

    public static boolean isDownloading;

    public static boolean isFirstTimeLogIn;

    // Common MIDlet Variables

    private SplashScreenView splashScreenView;

    private WaitingView waitingView;

    private LoginView loginView;

    private OrgUnitSelectView orgUnitSelectView;

    private PinView pinView;

    private SettingView settingView;

    private UpdateNewVersionView updateNewVersionView;

    private OrgUnit currentOrgUnit;

    private MainMenuView mainMenuView;

    private MessagingMenuView messagingMenuView;

    private FeedbackView feedbackView;

    private FeedbackContentView feedbackContentView;

    private FindUserView findUserView;

    private UserListView userListView;

    private MessageSubjectView messageSubjectView;

    private MessageView messageView;

    private MessageOptionView messageOptionView;

    private MessageConversationView messageConversationView;

    private MessageDetailView messageDetailView;

    private MessageReplyView messageReplyView;

    private ViewLoadInterpretation loadInterpretationView;

    private ViewInterpretationComment viewInterpretationComment;

    private ViewInterpretation viewInterpretation;

    private ViewPostNewInterpretation viewPostNewInterpretation;

    // Others

    public static String DEFAULT_LOCALE = "en-GB";

    public static final String BLANK = "";

    // For text value

    public static final int TEXT_NUMERIC = 8;

    public static final int TEXT_STRING = 64;

    /**
     * 
     */
    public DHISMIDlet()
    {
        System.out.println( "Setting Up Logger..." );
        setupLogger();

        if ( DEBUG )
        {
            System.out.println( "Overriding Logger Settings..." );
            LogMan.setEnabled( true );
            LogMan.setEnableSaveToFile( true );
            LogMan.setLevel( LogMan.DEBUG );
            LogMan.setBuffSize( 20 );
            LogMan.setIncludeTags( "" );
            LogMan.setExcludeTags( "" );
            LogMan.setIncludeMessages( "" );
            LogMan.setExcludeMessages( "" );
        }

        String jadImageReports = getAppProperty( "DISABLE_IMAGE_REPORTS" );
        if ( jadImageReports != null && jadImageReports.equals( "true" ) )
        {
            LogMan.log( LogMan.DEV, "JAD,ImageReports," + CLASS_TAG, "Overriding, disabling image reports..." );
            DISABLE_IMAGE_REPORTS = true;
        }
    }

    protected void destroyApp( boolean b )
        throws MIDletStateChangeException
    {
        // TODO Auto-generated method stub
    }

    protected void pauseApp()
    {
        // TODO Auto-generated method stub
    }

    protected void startApp()
        throws MIDletStateChangeException
    {
        try
        {
            this.loadLanguageLocale();
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        this.getSplashScreenView().showView();
    }

    private void loadLanguageLocale()
        throws RecordStoreException, IOException
    {
        SettingRecordStore settingRecordStore = new SettingRecordStore();
        String locale = settingRecordStore.get( SettingRecordStore.LOCALE );

        try
        {
            Hashtable props = Properties.load( "/properties/app.properties" );
            if ( props.get( "default.locale" ) != null )
            {
                DEFAULT_LOCALE = (String) props.get( "default.locale" );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        if ( locale.equals( "" ) )
        {
            TextMapping textMapping = TextMappingLoader.load( DEFAULT_LOCALE );
            Text.setTextMapping( textMapping );
        }
        else
        {
            TextMapping textMapping = TextMappingLoader.load( locale );
            Text.setTextMapping( textMapping );
        }

    }

    public void exitMIDlet()
    {
        try
        {
            this.destroyApp( true );
        }
        catch ( MIDletStateChangeException e )
        {
            e.printStackTrace();
        }
        notifyDestroyed();
    }

    /**
    *
    */
    public void setupLogger()
    {
        LogMan.initialize( this );

        String jadEnabled = getAppProperty( "LOG_ENABLED" );
        if ( jadEnabled != null )
        {
            if ( jadEnabled.toLowerCase().equals( "enabled" ) || jadEnabled.toLowerCase().equals( "enable" ) )
            {
                LogMan.setEnabled( true );
            }
            else
            {
                LogMan.setEnabled( false );
            }
        }
        else
        {
            LogMan.setEnabled( false );
        }

        String jadEnableSaveToFile = getAppProperty( "LOG_ENABLE_SAVE_TO_FILE" );
        if ( jadEnableSaveToFile != null )
        {
            if ( jadEnableSaveToFile.toLowerCase().equals( "enabled" )
                || jadEnableSaveToFile.toLowerCase().equals( "enable" ) )
            {
                LogMan.setEnableSaveToFile( true );
            }
            else
            {
                LogMan.setEnableSaveToFile( false );
            }
        }
        else
        {
            LogMan.setEnableSaveToFile( false );
        }

        String jadBuffer = getAppProperty( "LOG_BUFFER" );
        if ( jadBuffer != null )
        {
            if ( !jadBuffer.equals( "" ) )
            {
                LogMan.setBuffSize( Integer.parseInt( jadBuffer ) );
            }
            else
            {
                LogMan.setBuffSize( 20 );
            }
        }
        else
        {
            LogMan.setBuffSize( 20 );
        }

        String jadLevel = getAppProperty( "LOG_LEVEL" );
        if ( jadLevel != null )
        {
            LogMan.setLevel( LogUtils.getLevelInt( jadLevel.toUpperCase() ) );
        }
        else
        {
            LogMan.setLevel( LogMan.LEVEL );
        }

        String jadIncludeTags = getAppProperty( "LOG_INCLUDE_TAGS" );
        if ( jadIncludeTags != null )
        {
            LogMan.setIncludeTags( jadIncludeTags );
        }
        else
        {
            LogMan.setIncludeTags( "" );
        }

        String jadExcludeTags = getAppProperty( "LOG_EXCLUDE_TAGS" );
        if ( jadExcludeTags != null )
        {
            LogMan.setExcludeTags( jadExcludeTags );
        }
        else
        {
            LogMan.setExcludeTags( "" );
        }

        String jadIncludeMessages = getAppProperty( "LOG_INCLUDE_MESSAGES" );
        if ( jadIncludeMessages != null )
        {
            LogMan.setIncludeMessages( jadIncludeMessages );
        }
        else
        {
            LogMan.setIncludeMessages( "" );
        }

        String jadExcludeMessages = getAppProperty( "LOG_EXCLUDE_MESSAGES" );
        if ( jadExcludeMessages != null )
        {
            LogMan.setExcludeMessages( jadExcludeMessages );
        }
        else
        {
            LogMan.setExcludeMessages( "" );
        }
    }

    public SplashScreenView getSplashScreenView()
    {
        if ( splashScreenView == null )
        {
            splashScreenView = new SplashScreenView( this );
        }
        return splashScreenView;
    }

    public void setSplashScreenView( SplashScreenView splashScreenView )
    {
        this.splashScreenView = splashScreenView;
    }

    public WaitingView getWaitingView()
    {
        if ( waitingView == null )
        {
            waitingView = new WaitingView( this );
        }
        return waitingView;
    }

    public void setWaitingView( WaitingView waitingView )
    {
        this.waitingView = waitingView;
    }

    public LoginView getLoginView()
    {
        if ( loginView == null )
        {
            loginView = new LoginView( this );
        }
        return loginView;
    }

    public OrgUnitSelectView getOrgUnitSelectView()
    {
        if ( orgUnitSelectView == null )
        {
            orgUnitSelectView = new OrgUnitSelectView( this );
        }
        return orgUnitSelectView;
    }

    public void setOrgUnitSelectView( OrgUnitSelectView orgUnitSelectView )
    {
        this.orgUnitSelectView = orgUnitSelectView;
    }

    public void setLoginView( LoginView loginView )
    {
        this.loginView = loginView;
    }

    public PinView getPinView()
    {
        if ( pinView == null )
        {
            pinView = new PinView( this );
        }
        return pinView;
    }

    public void setPinView( PinView pinView )
    {
        this.pinView = pinView;
    }

    public SettingView getSettingView()
    {
        if ( settingView == null )
        {
            settingView = new SettingView( this );
        }
        return settingView;
    }

    public void setSettingView( SettingView settingView )
    {
        this.settingView = settingView;
    }

    public MessagingMenuView getMessagingMenuView()
    {
        if ( messagingMenuView == null )
        {
            messagingMenuView = new MessagingMenuView( this );
        }
        return messagingMenuView;
    }

    public void setMessagingMenuView( MessagingMenuView messagingMenuView )
    {
        this.messagingMenuView = messagingMenuView;
    }

    public FeedbackView getFeedbackView()
    {
        if ( feedbackView == null )
        {
            feedbackView = new FeedbackView( this );
        }
        return feedbackView;
    }

    public void setFeedbackView( FeedbackView feedbackView )
    {
        this.feedbackView = feedbackView;
    }

    public FeedbackContentView getFeedbackContentView()
    {
        if ( feedbackContentView == null )
        {
            feedbackContentView = new FeedbackContentView( this );
        }
        return feedbackContentView;
    }

    public void setFeedbackContentView( FeedbackContentView feedbackContentView )
    {
        this.feedbackContentView = feedbackContentView;
    }

    public FindUserView getFindUserView()
    {
        if ( findUserView == null )
        {
            findUserView = new FindUserView( this );
        }
        return findUserView;
    }

    public void setFindUserView( FindUserView findUserView )
    {
        this.findUserView = findUserView;
    }

    public UserListView getUserListView()
    {
        if ( userListView == null )
        {
            userListView = new UserListView( this );
        }
        return userListView;
    }

    public void setUserListView( UserListView userListView )
    {
        this.userListView = userListView;
    }

    public MessageSubjectView getMessageSubjectView()
    {
        if ( messageSubjectView == null )
        {
            messageSubjectView = new MessageSubjectView( this );
        }
        return messageSubjectView;
    }

    public void setMessageSubjectView( MessageSubjectView messageSubjectView )
    {
        this.messageSubjectView = messageSubjectView;
    }

    public MessageView getMessageView()
    {
        if ( messageView == null )
        {
            messageView = new MessageView( this );
        }
        return messageView;
    }

    public void setMessageView( MessageView messageView )
    {
        this.messageView = messageView;
    }

    public MessageOptionView getMessageOptionView()
    {
        if ( messageOptionView == null )
        {
            messageOptionView = new MessageOptionView( this );
        }
        return messageOptionView;
    }

    public void setMessageOptionView( MessageOptionView messageOptionView )
    {
        this.messageOptionView = messageOptionView;
    }

    public MessageConversationView getMessageConversationView()
    {
        if ( messageConversationView == null )
        {
            messageConversationView = new MessageConversationView( this );
        }
        return messageConversationView;
    }

    public void setMessageConversationView( MessageConversationView messageConversationView )
    {
        this.messageConversationView = messageConversationView;
    }

    public MessageDetailView getMessageDetailView()
    {
        if ( messageDetailView == null )
        {
            messageDetailView = new MessageDetailView( this );
        }
        return messageDetailView;
    }

    public void setMessageDetailView( MessageDetailView messageDetailView )
    {
        this.messageDetailView = messageDetailView;
    }

    public MessageReplyView getMessageReplyView()
    {
        if ( messageReplyView == null )
        {
            messageReplyView = new MessageReplyView( this );
        }
        return messageReplyView;
    }

    public void setMessageReplyView( MessageReplyView messageReplyView )
    {
        this.messageReplyView = messageReplyView;
    }

    public ViewLoadInterpretation getLoadInterpretationView()
    {
        if ( loadInterpretationView == null )
        {
            loadInterpretationView = new ViewLoadInterpretation( this );
        }
        return loadInterpretationView;
    }

    public void setLoadInterpretationView( ViewLoadInterpretation loadInterpretationView )
    {
        this.loadInterpretationView = loadInterpretationView;
    }

    public ViewInterpretationComment getViewInterpretationComment()
    {
        if ( viewInterpretationComment == null )
        {
            viewInterpretationComment = new ViewInterpretationComment( this );
        }
        return viewInterpretationComment;
    }

    public void setViewInterpretationComment( ViewInterpretationComment viewInterpretationComment )
    {
        this.viewInterpretationComment = viewInterpretationComment;
    }

    public ViewInterpretation getViewInterpretation()
    {
        if ( viewInterpretation == null )
        {
            viewInterpretation = new ViewInterpretation( this );
        }
        return viewInterpretation;
    }

    public void setViewInterpretation( ViewInterpretation viewInterpretation )
    {
        this.viewInterpretation = viewInterpretation;
    }

    public ViewPostNewInterpretation getViewPostNewInterpretation()
    {
        if ( viewPostNewInterpretation == null )
        {
            viewPostNewInterpretation = new ViewPostNewInterpretation( this );
        }
        return viewPostNewInterpretation;
    }

    public void setViewPostNewInterpretation( ViewPostNewInterpretation viewPostNewInterpretation )
    {
        this.viewPostNewInterpretation = viewPostNewInterpretation;
    }

    public UpdateNewVersionView getUpdateNewVersionView()
    {
        if ( updateNewVersionView == null )
        {
            updateNewVersionView = new UpdateNewVersionView( this );
        }
        return updateNewVersionView;
    }

    public void setUpdateNewVersionView( UpdateNewVersionView updateNewVersionView )
    {
        this.updateNewVersionView = updateNewVersionView;
    }

    public MainMenuView getMainMenuView()
    {
        if ( mainMenuView == null )
        {
            mainMenuView = new MainMenuView( this );
        }
        return mainMenuView;
    }

    public void setMainMenuView( MainMenuView mainMenuView )
    {
        this.mainMenuView = mainMenuView;
    }

    public static boolean isDownloading()
    {
        return isDownloading;
    }

    public static void setDownloading( boolean isDownloading )
    {
        DHISMIDlet.isDownloading = isDownloading;
    }

    public OrgUnit getCurrentOrgUnit()
    {
        return currentOrgUnit;
    }

    public void setCurrentOrgUnit( OrgUnit currentOrgUnit )
    {
        this.currentOrgUnit = currentOrgUnit;
    }

    public static boolean isFirstTimeLogIn()
    {
        return isFirstTimeLogIn;
    }

    public static void setFirstTimeLogIn( boolean isFirstTimeLogIn )
    {
        DHISMIDlet.isFirstTimeLogIn = isFirstTimeLogIn;
    }

}
