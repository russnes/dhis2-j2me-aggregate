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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.connection.task.LoginTask;
import org.hisp.dhis.mobile.connection.task.UpdateNewVersionTask;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class UpdateNewVersionView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "UpdateNewVersionView";

    private Form updateNewVersionForm;

    private Command updateVersionFormYesCommand;

    private Command updateVersionFormNoCommand;

    public UpdateNewVersionView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        // TODO Auto-generated constructor stub
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == updateVersionFormYesCommand )
        {
            try
            {
                this.dhisMIDlet.getWaitingView().showView();

                int size = LoginTask.inputStream.readInt();

                LoginTask.orgUnit.deSerialize( LoginTask.inputStream );

                ConnectionManager.setUrl( LoginTask.orgUnit.getUpdateNewVersionUrl() );

                ConnectionManager.updateNewVersion();

                boolean temp = true;

                if ( UpdateNewVersionTask.isDownloadSuccessfully == true )
                {
                    // Save the new version value
                    SettingRecordStore settingRecordStore = null;
                    settingRecordStore = new SettingRecordStore();
                    settingRecordStore.put( SettingRecordStore.CLIENT_VERSION, LoginTask.orgUnit.serverVersion + "" );
                    settingRecordStore.save();

                    temp = false;
                }
            }

            catch ( Exception e )
            {
                e.printStackTrace();
            }
            this.dhisMIDlet.getSplashScreenView().showView();
        }
        else
        {
            try
            {
                LoginTask.handleLogIn( LoginTask.inputStream );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

    }

    public void prepareView()
    {
        // TODO Auto-generated method stub

    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Update New Version" + " Screen...");
        this.switchDisplayable( null, this.getUpdateNewVersionForm() );
    }

    public Form getUpdateNewVersionForm()
    {

        if ( updateNewVersionForm == null )
        {
            updateNewVersionForm = new Form( Text.UPDATE_NEW_VERSION(), new Item[] {} );

            updateNewVersionForm.append( Text.UPDATE_NEW_NOTIFICATION() );

            updateNewVersionForm.addCommand( this.getUpdateVersionFormNoCommand() );

            updateNewVersionForm.addCommand( this.getUpdateVersionFormYesCommand() );

            updateNewVersionForm.setCommandListener( this );
        }

        return updateNewVersionForm;
    }

    public void setUpdateNewVersionForm( Form updateNewVersionForm )
    {
        this.updateNewVersionForm = updateNewVersionForm;
    }

    public Command getUpdateVersionFormYesCommand()
    {
        if ( updateVersionFormYesCommand == null )
        {
            updateVersionFormYesCommand = new Command( Text.YES(), Command.OK, 0 );
        }
        return updateVersionFormYesCommand;
    }

    public void setUpdateVersionFormYesCommand( Command updateVersionFormYesCommand )
    {
        this.updateVersionFormYesCommand = updateVersionFormYesCommand;
    }

    public Command getUpdateVersionFormNoCommand()
    {
        if ( updateVersionFormNoCommand == null )
        {
            updateVersionFormNoCommand = new Command( Text.NO(), Command.CANCEL, 0 );
        }
        return updateVersionFormNoCommand;
    }

    public void setUpdateVersionFormNoCommand( Command updateVersionFormNoCommand )
    {
        this.updateVersionFormNoCommand = updateVersionFormNoCommand;
    }

}
