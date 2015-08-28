package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.ui.Text;

public class FindUserView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "FindUserView";

    private Form findUserForm;

    private Command backCommand;

    private Command findCommand;

    private TextField findUserTextField;

    private String keyword;

    private FacilityMIDlet facilityMIDlet;

    public FindUserView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.findUserForm = null;
        this.backCommand = null;
        this.findCommand = null;
        this.findUserTextField = null;
        this.keyword = null;
        System.gc();

    }

    public void showView()
    {
        this.prepareView();
        this.switchDisplayable( null, this.getFindUserForm() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            dhisMIDlet.getMessagingMenuView().showView();
        }
        else if ( command == this.getFindCommand() )
        {
            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getFindUserUrl() );
            ConnectionManager.findUser( getKeyword() );
            
            findCommand = null;
            backCommand = null;
            keyword = null;
            System.gc();
                
        }

    }

    public Form getFindUserForm()
    {
        if ( findUserForm == null )
        {
            findUserForm = new Form( Text.FIND_USER() );
            findUserForm.addCommand( getBackCommand() );
            findUserForm.addCommand( getFindCommand() );

            findUserForm.append( this.getFindUserTextField() );

            findUserForm.setCommandListener( this );
        }

        return findUserForm;
    }

    public void setFindUserForm( Form findUserForm )
    {
        this.findUserForm = findUserForm;
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

    public Command getFindCommand()
    {
        if ( findCommand == null )
        {
            findCommand = new Command( Text.FIND(), Command.SCREEN, 0 );
        }
        return findCommand;
    }

    public void setFindCommand( Command findCommand )
    {
        this.findCommand = findCommand;
    }

    public TextField getFindUserTextField()
    {
        if ( findUserTextField == null )
        {
            findUserTextField = new TextField( Text.ENTER_KEY_WORD(), "", 100, TextField.ANY );
        }

        return findUserTextField;
    }

    public void setFindUserTextField( TextField findUserTextField )
    {
        this.findUserTextField = findUserTextField;
    }

    public String getKeyword()
    {
        if ( keyword == null )
        {
            keyword = getFindUserTextField().getString();
        }
        return keyword;
    }

    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }

}
