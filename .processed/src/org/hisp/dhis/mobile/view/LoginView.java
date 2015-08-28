package org.hisp.dhis.mobile.view;

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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.Properties;

public class LoginView
    extends AbstractView
    implements CommandListener
{
    private static String CLASS_TAG = "LoginView";

    private static String DEFAULT_DHIS_URL = "http://localhost:8080/dhis";

    public static final String MOBILE_PATH = "api/mobile/2.10/";

    private Form loginForm;

    private TextField loginServerUrlTextField;

    private TextField loginUserNameTextField;

    private TextField loginPasswordTextField;

    private Command loginFormExitCommand;

    private Command loginFormLoginCommand;

    private Command logsCommand = new Command( "Logs", Command.OK, 99 );

    private List orgUnitSelectList;

    public LoginView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == loginFormExitCommand )
        {
            dhisMIDlet.exitMIDlet();
        }
        else if ( command == loginFormLoginCommand )
        {
            this.login();
        }
        else if ( command == logsCommand )
        {
            LogMan.showLogMonitorScreen();
        }
    }

    private void login()
    {
        if ( getLoginUserNameTextField().getString().trim().length() == 0
            || getLoginPasswordTextField().getString().trim().length() == 0 )
        {
            switchDisplayable( Alerts.getErrorAlert( Text.INCOMPLETE_FORM(), Text.U_P_MISSING() ), getLoginForm() );
            return;
        }
        else
        {
            dhisMIDlet.getWaitingView().showView();
            ConnectionManager.init( dhisMIDlet, buildURL( this.getLoginServerUrlTextField().getString() ), this
                .getLoginUserNameTextField().getString(), this.getLoginPasswordTextField().getString(), "en-GB", null );
            
            SettingRecordStore settingRecordStore;
            try
            {
                settingRecordStore = new SettingRecordStore();
                settingRecordStore.put( SettingRecordStore.SERVER_URL_BASIC, this.getLoginServerUrlTextField().getString() );
                settingRecordStore.save();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }

            ConnectionManager.login();
        }

    }

    public static String buildURL( String url )
    {
        url += (url.endsWith( "/" ) ? "" : "/") + MOBILE_PATH;
        return url;
    }

    public void resetTextField()
    {
        this.getLoginUserNameTextField().setString( DHISMIDlet.BLANK );
        this.getLoginPasswordTextField().setString( DHISMIDlet.BLANK );
    }

    public void prepareView()
    {
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI,Authentication," + CLASS_TAG, "Showing " + "Login" + " Screen..." );
        this.switchDisplayable( null, this.getLoginForm() );
    }

    public Form getLoginForm()
    {
        if ( loginForm == null )
        {
            loginForm = new Form( Text.LOGIN(), new Item[] { getLoginUserNameTextField(), getLoginPasswordTextField(),
                getLoginServerUrlTextField() } );
            loginForm.addCommand( getLoginFormExitCommand() );
            loginForm.addCommand( getLoginFormLoginCommand() );
            if ( LogMan.isEnabled() )
            {
                loginForm.addCommand( logsCommand );
            }
            loginForm.setCommandListener( this );
        }
        return loginForm;
    }

    public void setLoginForm( Form loginForm )
    {
        this.loginForm = loginForm;
    }

    public TextField getLoginServerUrlTextField()
    {
        if ( loginServerUrlTextField == null )
        {
            try
            {
                Hashtable props = Properties.load( "/properties/app.properties" );
                if ( props.get( "server.url" ) != null )
                {
                    DEFAULT_DHIS_URL = (String) props.get( "server.url" );
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

            loginServerUrlTextField = new TextField( Text.URL(), DEFAULT_DHIS_URL, DHISMIDlet.TEXT_STRING,
                TextField.URL );

        }
        return loginServerUrlTextField;
    }

    public void setLoginServerUrlTextField( TextField loginServerUrlTextField )
    {
        this.loginServerUrlTextField = loginServerUrlTextField;
    }

    public TextField getLoginUserNameTextField()
    {
        if ( loginUserNameTextField == null )
        {
            String defaultStr = "";
            
            if (DHISMIDlet.DEBUG) {
                defaultStr = "admin";
            }
            
            loginUserNameTextField = new TextField( Text.USERNAME(), defaultStr, DHISMIDlet.TEXT_STRING, TextField.ANY
                | TextField.SENSITIVE );
        }
        return loginUserNameTextField;
    }

    public void setLoginUserNameTextField( TextField loginUserNameTextField )
    {
        this.loginUserNameTextField = loginUserNameTextField;
    }

    public TextField getLoginPasswordTextField()
    {
        if ( loginPasswordTextField == null )
        {
            String defaultStr = "";
            
            if (DHISMIDlet.DEBUG) {
                defaultStr = "district";
            }
            loginPasswordTextField = new TextField( Text.PASSWORD(), defaultStr, 32, TextField.ANY | TextField.PASSWORD );
        }
        return loginPasswordTextField;
    }

    public void setLoginPasswordTextField( TextField loginPasswordTextField )
    {
        this.loginPasswordTextField = loginPasswordTextField;
    }

    public Command getLoginFormExitCommand()
    {
        if ( loginFormExitCommand == null )
        {
            loginFormExitCommand = new Command( Text.EXIT(), Command.EXIT, 0 );
        }
        return loginFormExitCommand;
    }

    public void setLoginFormExitCommand( Command loginFormExitCommand )
    {
        this.loginFormExitCommand = loginFormExitCommand;
    }

    public Command getLoginFormLoginCommand()
    {
        if ( loginFormLoginCommand == null )
        {
            loginFormLoginCommand = new Command( Text.LOGIN(), Command.SCREEN, 0 );
        }
        return loginFormLoginCommand;
    }

    public void setLoginFormLoginCommand( Command loginFormLoginCommand )
    {
        this.loginFormLoginCommand = loginFormLoginCommand;
    }

    public List getOrgUnitSelectList()
    {
        return orgUnitSelectList;
    }

    public void setOrgUnitSelectList( List orgUnitSelectList )
    {
        this.orgUnitSelectList = orgUnitSelectList;
    }
}
