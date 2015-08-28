/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.mobile.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.imagereports.TaskDownloadChartList;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.ui.Text;

/**
 * @author Nguyen Kim Lai
 * 
 * @version $ MainMenuView.java Sep 10, 2012 $
 */
public class MainMenuView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "MainMenuView";

    private FacilityMIDlet facilityMIDlet;

    private Command mainMenuExitCommand;

    private Command logsCommand = new Command( "Logs", Command.OK, 0 );

    private List mainMenuList;

    public MainMenuView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.mainMenuExitCommand )
        {
            dhisMIDlet.exitMIDlet();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            int index = getMainMenuList().getSelectedIndex();
            String item = getMainMenuList().getString( index );

            if ( item.equals( "Data Set List" ) )
            {
                try
                {
                    ConnectionManager.getDhisMIDlet().getPinView().checkOneOrManyDataset();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            else if ( item.equals( "Org Unit List" ) )
            {
                dhisMIDlet.getOrgUnitSelectView().showView();
            }
            else if ( item.equals( "Chart List" ) )
            {
                new TaskDownloadChartList().start();
            }
            else if ( item.equals( "Setting" ) )
            {
                dhisMIDlet.getSettingView().showView();
            }
            else if ( item.equals( "Messaging" ) )
            {
                dhisMIDlet.getMessagingMenuView().showView();
            }
        }
        else if ( command == logsCommand )
        {
            LogMan.showLogMonitorScreen();
        }

    }

    public void prepareView()
    {

    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Main Menu" + " Screen..." );
        this.switchDisplayable( null, this.getMainMenuList() );
    }

    public List getMainMenuList()
    {
        if ( this.mainMenuList == null )
        {
            this.mainMenuList = new List( "Main Menu", List.IMPLICIT );

            mainMenuList.append( "Data Set List", null );
            mainMenuList.append( "Org Unit List", null );

            if ( !DHISMIDlet.DISABLE_IMAGE_REPORTS )
            {
                mainMenuList.append( "Chart List", null );
            }

            mainMenuList.append( "Messaging", null );

            mainMenuList.append( "Setting", null );

            mainMenuList.addCommand( getMainMenuExitCommand() );

            if ( LogMan.isEnabled() )
            {
                mainMenuList.addCommand( logsCommand );
            }

            mainMenuList.setCommandListener( this );
        }
        return this.mainMenuList;
    }

    public void setMainMenuList( List mainMenuList )
    {
        this.mainMenuList = mainMenuList;
    }

    public Command getMainMenuExitCommand()
    {
        if ( mainMenuExitCommand == null )
        {
            mainMenuExitCommand = new Command( Text.EXIT(), Command.EXIT, 0 );
        }
        return mainMenuExitCommand;
    }

    public void setMainMenuExitCommand( Command mainMenuExitCommand )
    {
        this.mainMenuExitCommand = mainMenuExitCommand;
    }
}
