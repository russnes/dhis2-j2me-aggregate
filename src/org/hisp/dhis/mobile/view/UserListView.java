package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.recordstore.RecipientRecordStore;
import org.hisp.dhis.mobile.recordstore.UserRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class UserListView
    extends AbstractView
    implements CommandListener
{

    private static final String CLASS_TAG = "UserListView";

    private FacilityMIDlet facilityMIDlet;

    private Vector userVector;

    private Command backCommand;

    private Command addCommand;

    private Command addAllCommand;

    private List userList;

    public Alert exitAlert;

    public UserListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
        userVector = new Vector();

    }

    public void commandAction( Command command, Displayable displayable )
    {
        Vector pendingUserVector = new Vector();

        if ( command == this.getBackCommand() )
        {
            this.facilityMIDlet.getFindUserView().showView();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            try
            {
                User user = (User) this.getUserVector().elementAt( this.getUserList().getSelectedIndex() );

                pendingUserVector.addElement( user );

                RecipientRecordStore.saveRecipients( pendingUserVector );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            finally
            {
                dhisMIDlet.getMessageSubjectView().showView();
            }

        }
        else if ( command == this.getAddCommand() )
        {
            try
            {
                User user = (User) this.getUserVector().elementAt( this.getUserList().getSelectedIndex() );

                pendingUserVector.addElement( user );

                RecipientRecordStore.saveRecipients( pendingUserVector );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    UserRecordStore.deleteRecordStore();
                    dhisMIDlet.getMessageOptionView().showView();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }

            }

        }
        else if ( command == this.getAddAllCommand() )
        {
            try
            {
                RecipientRecordStore.saveRecipients( this.getUserVector() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();

            }
            finally
            {
                try
                {
                    UserRecordStore.deleteRecordStore();
                    dhisMIDlet.getMessageOptionView().showView();
                }
                catch ( Exception e2 )
                {
                    e2.printStackTrace();
                }

            }
        }

    }

    public void prepareView()
    {
        try
        {
            this.userVector = UserRecordStore.loadUsers();

            this.getUserList().deleteAll();

            for ( int i = 0; i < userVector.size(); i++ )
            {
                User user = (User) userVector.elementAt( i );
                this.getUserList().append( user.getFirstName() + " " + user.getSurname(), null );
            }

        }
        catch ( RecordStoreException e )
        {
            getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), "Cannot load user" ) );
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), "Cannot deserialize User" ) );

        }
        System.gc();
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "User List" + " Screen..." );
        prepareView();
        switchDisplayable( null, this.getUserList() );
    }

    public Vector getUserVector()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( userVector == null )
        {
            userVector = UserRecordStore.loadUsers();

        }
        return userVector;
    }

    public void setUserVector( Vector userVector )
    {
        this.userVector = userVector;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public List getUserList()
    {
        if ( this.userList == null )
        {
            this.userList = new List( Text.SELECT_USER(), List.IMPLICIT );
            this.userList.setFitPolicy( List.TEXT_WRAP_ON );
            this.userList.addCommand( this.getBackCommand() );
            this.userList.addCommand( this.getAddCommand() );
            this.userList.addCommand( this.getAddAllCommand() );
            this.userList.setCommandListener( this );
        }
        return userList;
    }

    public void setUserList( List userList )
    {
        this.userList = userList;
    }

    public Alert getExitAlert()
    {
        if ( this.exitAlert == null )
        {
            exitAlert = Alerts.getConfirmAlert( Text.MESSAGE(), Text.EXIT_CONFIRM(), this );
        }
        return exitAlert;
    }

    public void setExitAlert( Alert exitAlert )
    {
        this.exitAlert = exitAlert;
    }

    public Command getAddCommand()
    {
        if ( addCommand == null )
        {
            addCommand = new Command( Text.ADD(), Command.SCREEN, 0 );
        }
        return addCommand;
    }

    public void setAddCommand( Command addCommand )
    {
        this.addCommand = addCommand;
    }

    public Command getAddAllCommand()
    {
        if ( addAllCommand == null )
        {
            addAllCommand = new Command( Text.ADD_ALL(), Command.SCREEN, 0 );

        }
        return addAllCommand;
    }

    public void setAddAllCommand( Command addAllCommand )
    {
        this.addAllCommand = addAllCommand;
    }

}
