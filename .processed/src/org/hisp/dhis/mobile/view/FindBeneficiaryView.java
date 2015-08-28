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
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class FindBeneficiaryView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "FindBeneficiaryView";
    
    private Form findBeneficiaryForm;

    private TextField keywordTextField;

    private Command findBeneficiaryFindCommand;

    private Command findBeneficiaryBackCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindBeneficiaryView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {

    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Find Beneficiary" + " Screen...");
        this.switchDisplayable( null, this.getFindBeneficiaryForm() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.findBeneficiaryFindCommand )
        {
            nameBasedMIDlet.getWaitingView().showView();

            OrgUnit orgUnit = dhisMIDlet.getCurrentOrgUnit();
            SettingRecordStore settingStore = null;

            try
            {
                settingStore = new SettingRecordStore();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }

            ConnectionManager.init( nameBasedMIDlet, orgUnit.getSearchUrl(),
                settingStore.get( SettingRecordStore.USERNAME ), settingStore.get( SettingRecordStore.PASSWORD ),
                settingStore.get( SettingRecordStore.LOCALE ), orgUnit );
            ConnectionManager.uploadBeneficiaryIdentifier( this.getKeywordTextField().getString().trim() );
        }
        else if ( command == this.findBeneficiaryBackCommand )
        {
            nameBasedMIDlet.getActivityMainMenuView().showView();
        }

    }

    public Form getFindBeneficiaryForm()
    {
        if ( findBeneficiaryForm == null )
        {
            findBeneficiaryForm = new Form( Text.FIND() );
            findBeneficiaryForm.addCommand( this.getFindBeneficiaryFindCommand() );
            findBeneficiaryForm.addCommand( this.getFindBeneficiaryBackCommand() );
            findBeneficiaryForm.append( this.getKeywordTextField() );
            findBeneficiaryForm.append( Text.SEARCH_HINT() );
            findBeneficiaryForm.setCommandListener( this );
        }
        return findBeneficiaryForm;
    }

    public void setFindBeneficiaryForm( Form findBenficiaryForm )
    {
        this.findBeneficiaryForm = findBenficiaryForm;
    }

    public TextField getKeywordTextField()
    {
        if ( keywordTextField == null )
        {
            keywordTextField = new TextField( Text.ENTER_KEY_WORD(), "", 32, TextField.ANY );
        }
        return keywordTextField;
    }

    public void setKeywordTextField( TextField keywordTextField )
    {
        this.keywordTextField = keywordTextField;
    }

    public Command getFindBeneficiaryFindCommand()
    {
        if ( findBeneficiaryFindCommand == null )
        {
            findBeneficiaryFindCommand = new Command( Text.FIND(), Command.SCREEN, 0 );
        }
        return findBeneficiaryFindCommand;
    }

    public void setFindBeneficiaryFindCommand( Command findBeneficiaryFindCommand )
    {
        this.findBeneficiaryFindCommand = findBeneficiaryFindCommand;
    }

    public Command getFindBeneficiaryBackCommand()
    {
        if ( findBeneficiaryBackCommand == null )
        {
            findBeneficiaryBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return findBeneficiaryBackCommand;
    }

    public void setFindBeneficiaryBackCommand( Command findBeneficiaryBackCommand )
    {
        this.findBeneficiaryBackCommand = findBeneficiaryBackCommand;
    }

}
