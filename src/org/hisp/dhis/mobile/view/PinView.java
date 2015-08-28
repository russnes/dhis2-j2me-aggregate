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
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.DataSetRecordStore;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.Properties;
import org.hisp.dhis.mobile.util.RecordStoreUtil;

public class PinView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "PinView";

    private Form pinForm;

    private TextField pinFormTextField;

    private Command pinFormExitCommand;

    private Command pinFormNextCommand;

    private Command pinFormReinitCommand;

    private Alert reinitConfirmAlert;

    public PinView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.pinFormNextCommand )
        {
            this.checkPin();

        }
        else if ( command == this.pinFormExitCommand )
        {
            dhisMIDlet.exitMIDlet();
        }
        else if ( command == this.pinFormReinitCommand )
        {
            this.getDisplay().setCurrent( this.getReinitConfirmAlert() );
        }
        else if ( displayable == reinitConfirmAlert )
        {
            if ( command.getCommandType() == Command.OK )
            {
                RecordStoreUtil.clearAllRecordStore();
                dhisMIDlet.getLoginView().showView();
            }
            else
            {
                this.showView();
            }
        }

    }

    private void checkPin()
    {
        SettingRecordStore settingRecordStore = null;
        try
        {
            settingRecordStore = new SettingRecordStore();
            String currentPin = settingRecordStore.get( SettingRecordStore.PIN );
            String inputPin = this.getPinFormTextField().getString().trim();

            if ( currentPin.equalsIgnoreCase( DHISMIDlet.BLANK ) )
            {
                this.handleInitializationProcess( inputPin );
                DHISMIDlet.setFirstTimeLogIn( true );
            }
            else
            {
                this.handleSecondTimeProcess( currentPin, inputPin );
                DHISMIDlet.setFirstTimeLogIn( false );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }

    }

    private void handleSecondTimeProcess( String currentPin, String inputPin )
        throws Exception
    {
        if ( currentPin.equalsIgnoreCase( inputPin ) )
        {
            Vector orgUnitVector = null;
            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

            dhisMIDlet.getOrgUnitSelectView().setOrgUnitVector( orgUnitVector );

            LogMan.log( LogMan.INFO, "OrgUnit," + CLASS_TAG, "Size: " + orgUnitVector.size() );

            if ( orgUnitVector.size() > 1 )
            {
                this.showOrgUnitSelectScrren( orgUnitVector );
            }
            else
            {
                dhisMIDlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( 0 ) );
                this.update();
            }

        }
        else
        {
            this.getPinFormTextField().setString( DHISMIDlet.BLANK );
            this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), Text.INVALID_PIN() ) );
        }
    }

    private void handleInitializationProcess( String inputPin )
    {
        if ( inputPin.length() < 4 )
        {
            this.getPinFormTextField().setString( DHISMIDlet.BLANK );
            this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), Text.ENTER_PIN() ) );
        }
        else
        {
            Vector orgUnitVector = null;
            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

            ConnectionManager.downloadAllResource( orgUnitVector );
            dhisMIDlet.getWaitingView().showView();
            this.saveInitSetting();
        }
    }

    private void showOrgUnitSelectScrren( Vector orgUnitVector )
    {
        dhisMIDlet.getOrgUnitSelectView().showView();
    }

    private void saveInitSetting()
    {
        try
        {
            Hashtable props = Properties.load( "/properties/app.properties" );
            String serverPhoneNumber = "";
            if ( props.get( "server.phonenumber" ) != null )
            {
                serverPhoneNumber = (String) props.get( "server.phonenumber" );
            }
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            settingRecordStore.put( SettingRecordStore.SERVER_URL, dhisMIDlet.getLoginView()
                .getLoginServerUrlTextField().getString() );
            settingRecordStore.put( SettingRecordStore.USERNAME, dhisMIDlet.getLoginView().getLoginUserNameTextField()
                .getString() );
            settingRecordStore.put( SettingRecordStore.PASSWORD, dhisMIDlet.getLoginView().getLoginPasswordTextField()
                .getString() );
            settingRecordStore.put( SettingRecordStore.PIN, this.getPinFormTextField().getString().trim() );
            settingRecordStore.put( SettingRecordStore.SERVER_PHONE_NUMBER, serverPhoneNumber );
            settingRecordStore.save();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "UI," + CLASS_TAG, e );
        }

    }

    public void prepareView()
    {
    }

    public void navigate()
    {
        if ( DHISMIDlet.isDownloading() )
        {
            dhisMIDlet.getWaitingView().showView();
        }
        else
        {
            if ( dhisMIDlet instanceof FacilityMIDlet )
            {
                try
                {
                    dhisMIDlet.getMainMenuView().showView();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            else
            {
                NameBasedMIDlet namebasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
                namebasedMIDlet.getActivityMainMenuView().showView();
            }
        }
    }

    public void update()
        throws Exception
    {
        SettingRecordStore settingRecordStore = new SettingRecordStore();
        OrgUnit orgUnit = dhisMIDlet.getCurrentOrgUnit();

        if ( this.dhisMIDlet instanceof FacilityMIDlet )
        {
            ConnectionManager.init( dhisMIDlet, orgUnit.getUpdateDataSetsUrl(),
                settingRecordStore.get( SettingRecordStore.USERNAME ),
                settingRecordStore.get( SettingRecordStore.PASSWORD ), DHISMIDlet.DEFAULT_LOCALE, orgUnit );

            ConnectionManager.updateDataSet();
            this.checkOneOrManyDataset();
        }
        else if ( this.dhisMIDlet instanceof NameBasedMIDlet )
        {
            ConnectionManager.init( dhisMIDlet, orgUnit.getUpdateActivityPlanUrl(),
                settingRecordStore.get( SettingRecordStore.USERNAME ),
                settingRecordStore.get( SettingRecordStore.PASSWORD ), DHISMIDlet.DEFAULT_LOCALE, orgUnit );

            DHISMIDlet.setDownloading( true );
            NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
            ConnectionManager.updateActivityPlan();
            nameBasedMIDlet.getActivityMainMenuView().showView();
        }
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI,Authentication" + CLASS_TAG, "Showing " + "PIN" + " Screen..." );
        this.switchDisplayable( null, this.getPinForm() );
    }

    public void preparePinFormForFirstTime()
    {
        this.getPinForm().deleteAll();
        this.getPinForm().append( this.getPinFormTextField() );
        this.getPinForm().append( Text.CREATE_PIN_SUGGESTION() );
    }

    public void preparePinFormForSecondTime()
    {
        this.getPinForm().deleteAll();
        this.getPinForm().addCommand( this.getPinFormReinitCommand() );
        this.getPinForm().append( this.getPinFormTextField() );
        this.getPinForm().append( Text.CHECK_PIN_SUGGESTION() );
    }

    public Form getPinForm()
    {
        if ( pinForm == null )
        {
            pinForm = new Form( Text.ENTER_PIN() );
            pinForm.addCommand( this.getPinFormNextCommand() );
            pinForm.addCommand( this.getPinFormExitCommand() );
            pinForm.setCommandListener( this );
        }
        return pinForm;
    }

    public void setPinForm( Form pinForm )
    {
        this.pinForm = pinForm;
    }

    public TextField getPinFormTextField()
    {
        if ( pinFormTextField == null )
        {
            pinFormTextField = new TextField( Text.PIN(), "", 4, TextField.NUMERIC | TextField.PASSWORD );
        }
        return pinFormTextField;
    }

    public void setPinFormTextField( TextField pinFormTextField )
    {
        this.pinFormTextField = pinFormTextField;
    }

    public Command getPinFormExitCommand()
    {
        if ( pinFormExitCommand == null )
        {
            pinFormExitCommand = new Command( Text.EXIT(), Command.EXIT, 0 );
        }
        return pinFormExitCommand;
    }

    public void setPinFormExitCommand( Command pinFormExitCommand )
    {
        this.pinFormExitCommand = pinFormExitCommand;
    }

    public Command getPinFormNextCommand()
    {
        if ( pinFormNextCommand == null )
        {
            pinFormNextCommand = new Command( Text.NEXT(), Command.SCREEN, 0 );
        }
        return pinFormNextCommand;
    }

    public void setPinFormNextCommand( Command pinFormNextCommand )
    {
        this.pinFormNextCommand = pinFormNextCommand;
    }

    public Command getPinFormReinitCommand()
    {
        if ( pinFormReinitCommand == null )
        {
            pinFormReinitCommand = new Command( Text.REINIT(), Command.SCREEN, 1 );
        }
        return pinFormReinitCommand;
    }

    public void setPinFormReinitCommand( Command pinFormReinitCommand )
    {
        this.pinFormReinitCommand = pinFormReinitCommand;
    }

    public Alert getReinitConfirmAlert()
    {
        if ( this.reinitConfirmAlert == null )
        {
            reinitConfirmAlert = Alerts.getConfirmAlert( Text.MESSAGE(), Text.REINIT_MESSAGE(), this );
        }
        return reinitConfirmAlert;
    }

    public void setReinitConfirmAlert( Alert reinitConfirmAlert )
    {
        this.reinitConfirmAlert = reinitConfirmAlert;
    }

    // Move directly to Period View if there's only one data set
    public void checkOneOrManyDataset()
        throws Exception
    {
        FacilityMIDlet facilityMidlet = (FacilityMIDlet) dhisMIDlet;

        Vector dataSetVector = new Vector();

        dataSetVector = DataSetRecordStore.loadDataSets( dhisMIDlet.getCurrentOrgUnit() );

        if ( dataSetVector.size() > 1 )
        {
            facilityMidlet.getDataSetListView().prepareView();
            facilityMidlet.getDataSetListView().showView();
        }
        else
        {
            facilityMidlet.getPeriodView().setSelectedDataSet( (DataSet) dataSetVector.elementAt( 0 ) );
            facilityMidlet.getPeriodView().showView();
        }
    }
}
