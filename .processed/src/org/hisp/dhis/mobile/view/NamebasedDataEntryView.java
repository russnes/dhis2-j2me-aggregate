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
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.ActivityValue;
import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.recordstore.ActivityValueRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

public class NamebasedDataEntryView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "NamebasedDataEntryView";
    
    private NameBasedMIDlet nameBasedMIDlet;

    private Activity selectedActivity;

    private ActivityValue activityValue;

    private ProgramStage programStage;

    private Form programStageForm;

    private Command programStageFormBackCommand;

    private Command programStageFormSaveCommand;

    private Command programStageFormSendCommand;

    private Hashtable dataElementMappingTable;

    private Alert closeAlert;

    private static final int TEXT_NUMERIC = 6;

    private int counter;

    public NamebasedDataEntryView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.programStageFormBackCommand )
        {
            this.getDisplay().setCurrent( this.getCloseAlert() );
        }
        else if ( displayable == this.getCloseAlert() )
        {
            if ( command.getCommandType() == Command.OK )
            {
                this.nameBasedMIDlet.getNameListView().showView();
            }
            else
            {
                this.showView();
            }
        }
        else if ( command == this.programStageFormSaveCommand )
        {
            try
            {
                ActivityValueRecordStore.saveActivityValue( this.generateActivityValue() );
                this.getDisplay().setCurrent( Alerts.getInfoAlert( Text.MESSAGE(), Text.DATAVALUE_SAVED() ) );
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        else if ( command == this.programStageFormSendCommand )
        {
            ActivityValue activityValue = this.generateActivityValue();
            if ( this.counter > 0 )
            {
                this.getDisplay().setCurrent(
                    Alerts.getErrorAlert( Text.ERROR(), this.counter + " " + Text.FIELD_NOT_FILLED() ) );
            }
            else
            {

            }
            OrgUnit orgUnit = dhisMIDlet.getCurrentOrgUnit();
            SettingRecordStore settingStore = null;
            try
            {
                settingStore = new SettingRecordStore();
                ActivityValueRecordStore.saveActivityValue( activityValue );
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            ConnectionManager.init( nameBasedMIDlet, orgUnit.getUploadActivityReportUrl(),
                settingStore.get( SettingRecordStore.USERNAME ), settingStore.get( SettingRecordStore.PASSWORD ),
                settingStore.get( SettingRecordStore.LOCALE ), orgUnit );
            ConnectionManager.uploadActivityValue( activityValue, selectedActivity );
        }
    }

    public void prepareView()
    {
        this.getProgramStageForm().deleteAll();
        this.programStage = null;
        this.dataElementMappingTable = null;

        this.programStage = ProgramRecordStore.getProgramStage( selectedActivity.getTask().getProgStageId(),
            selectedActivity.getTask().getProgramId() );

        if ( this.programStage == null )
        {
            this.getProgramStageForm().append( Text.FORM_UNAVAILABLE() );
        }
        else
        {
            this.dataElementMappingTable = new Hashtable();

            try
            {
                this.activityValue = ActivityValueRecordStore.loadActivity( selectedActivity.getTask()
                    .getProgStageInstId() );
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            this.prepareProgramStageForm();
        }
    }

    private void prepareProgramStageForm()
    {
        this.getProgramStageForm().deleteAll();
        this.getProgramStageForm().setTitle( programStage.getName() );
        Vector des = programStage.getDataElements();

        // Get DataValue for each DataElement of the form
        for ( int i = 0; i < des.size(); i++ )
        {
            DataElement de = (DataElement) des.elementAt( i );
            // Get DataValue from Hashtable
            if ( !selectedActivity.getTask().isComplete() )
            {
                this.insertComponent( de );
            }
            else
            {
                this.insertUneditableComponent( de );
            }
            de = null;
            System.gc();
        }
        getProgramStageForm().append( Text.END_OF_FORM() );

        if ( !this.selectedActivity.getTask().isComplete() )
        {
            this.getProgramStageForm().addCommand( this.getProgramStageFormSendCommand() );
            this.getProgramStageForm().addCommand( this.getProgramStageFormSaveCommand() );
        }
        else
        {
            this.getProgramStageForm().removeCommand( this.getProgramStageFormSendCommand() );
            this.getProgramStageForm().removeCommand( this.getProgramStageFormSaveCommand() );
        }
    }

    private void insertComponent( DataElement dataElement )
    {
        DataValue dataValue = null;
        String fieldTitle = dataElement.isCompulsory() ? dataElement.getName() + "*" : dataElement.getName();
        if ( activityValue != null )
        {
            dataValue = (DataValue) activityValue.getDataValues().get( String.valueOf( dataElement.getId() ) );
        }

        if ( dataElement.getType().equals( "date" ) )
        {
            getProgramStageForm().append( fieldTitle + "\n" );
            DateField dateField = new DateField( "", DateField.DATE );
            if ( dataValue != null && !dataValue.getVal().equalsIgnoreCase( "" ) )
            {
                dateField.setDate( PeriodUtil.stringToDate( dataValue.getVal() ) );
            }
            getProgramStageForm().append( dateField );
            dataElementMappingTable.put( String.valueOf( dataElement.getId() ), dateField );
        }
        else if ( dataElement.getType().equals( "int" ) )
        {
            getProgramStageForm().append( fieldTitle + "\n" );
            TextField intField = new TextField( "", "", TEXT_NUMERIC, TextField.NUMERIC );
            if ( dataValue != null )
            {
                intField.setString( dataValue.getVal() );
            }
            getProgramStageForm().append( intField );
            dataElementMappingTable.put( String.valueOf( dataElement.getId() ), intField );
        }
        else if ( dataElement.getType().equals( "bool" ) )
        {
            getProgramStageForm().append( fieldTitle + "\n" );
            ChoiceGroup choiceGroup = new ChoiceGroup( "", Choice.POPUP );
            choiceGroup.append( Text.SELECT_OPTION(), null );
            choiceGroup.append( Text.YES(), null );
            choiceGroup.append( Text.NO(), null );
            choiceGroup.setFitPolicy( Choice.TEXT_WRAP_ON );
            if ( dataValue != null )
            {
                if ( dataValue.getVal().equals( "" ) )
                {
                    choiceGroup.setSelectedIndex( 0, true );
                }
                else if ( dataValue.getVal().equals( "true" ) )
                {
                    choiceGroup.setSelectedIndex( 1, true );
                }
                else
                {
                    choiceGroup.setSelectedIndex( 2, true );
                }
            }
            getProgramStageForm().append( choiceGroup );
            dataElementMappingTable.put( String.valueOf( dataElement.getId() ), choiceGroup );

        }
        else if ( dataElement.getType().equals( "string" ) )
        {
            if ( dataElement.getOptionSet().getOptions().size() <= 1 )
            {
                getProgramStageForm().append( fieldTitle + "\n" );
                TextField txtField = new TextField( "", "", 32, TextField.ANY );
                if ( dataValue != null )
                {
                    txtField.setString( dataValue.getVal() );
                }
                getProgramStageForm().append( txtField );
                dataElementMappingTable.put( String.valueOf( dataElement.getId() ), txtField );
            }

            else
            {
                getProgramStageForm().append( fieldTitle + "\n" );
                ChoiceGroup choiceGroup = new ChoiceGroup( "", ChoiceGroup.POPUP );
                Vector options = dataElement.getOptionSet().getOptions();
                choiceGroup.append( Text.SELECT_OPTION(), null );

                for ( int j = 0; j < options.size(); j++ )
                {
                    String option = (String) options.elementAt( j );
                    choiceGroup.append( option, null );
                    if ( dataValue != null && !dataValue.getVal().equals( "" ) )
                    {

                        choiceGroup.setSelectedIndex( j + 1, true );

                    }

                    option = null;
                }
                getProgramStageForm().append( choiceGroup );
                dataElementMappingTable.put( String.valueOf( dataElement.getId() ), choiceGroup );
                options = null;

            }

        }
    }

    private void insertUneditableComponent( DataElement de )
    {
        DataValue dataValue = null;

        if ( activityValue != null )
        {
            dataValue = (DataValue) activityValue.getDataValues().get( String.valueOf( de.getId() ) );
        }
        getProgramStageForm().append( de.getName() );
        TextField txtField = new TextField( null, "", 30, TextField.ANY | TextField.UNEDITABLE );
        if ( de.getType().equals( "bool" ) && dataValue != null )
        {
            if ( dataValue.getVal().equalsIgnoreCase( "true" ) )
            {
                txtField.setString( Text.YES() );
            }
            else if ( dataValue.getVal().equalsIgnoreCase( "false" ) )
            {
                txtField.setString( Text.NO() );
            }
        }
        else
        {
            if ( dataValue != null )
            {
                if ( de.getOptionSet().getOptions().size() <= 1 )
                {
                    txtField.setString( dataValue.getVal() );
                }
                else
                {
                    Vector options = de.getOptionSet().getOptions();
                    if ( !dataValue.getVal().equals( "" ) )
                    {
                        for ( int j = 0; j < options.size(); j++ )
                        {
                            String option = (String) options.elementAt( j );

                            txtField.setString( option );

                        }
                    }
                }

            }
        }
        getProgramStageForm().append( txtField );
        dataValue = null;
        de = null;
        System.gc();
    }

    private ActivityValue generateActivityValue()
    {
        Vector des = programStage.getDataElements();
        ActivityValue activityValue = new ActivityValue();
        activityValue.setProgramStageInstanceId( (selectedActivity.getTask().getProgStageInstId()) );
        DataElement de;
        String val = null;
        this.counter = 0;
        for ( int i = 0; i < des.size(); i++ )
        {
            de = (DataElement) des.elementAt( i );

            Vector optionSets = de.getOptionSet().getOptions();
            DataValue dv = new DataValue();

            if ( de.getType().equals( "date" ) )
            {
                DateField dateField = (DateField) dataElementMappingTable.get( String.valueOf( de.getId() ) );

                if ( dateField.getDate() != null )
                {
                    val = PeriodUtil.formatDate( dateField.getDate() );
                }
                else
                {
                    val = "";
                }

            }
            else if ( de.getType().equals( "int" ) )
            {
                TextField intField = (TextField) dataElementMappingTable.get( String.valueOf( de.getId() ) );
                val = intField.getString();
            }
            else if ( de.getType().equals( "bool" ) )
            {
                ChoiceGroup boolField = (ChoiceGroup) dataElementMappingTable.get( String.valueOf( de.getId() ) );
                if ( boolField.isSelected( 0 ) )
                {
                    val = "";
                }
                else if ( boolField.isSelected( 1 ) )
                {
                    val = "true";
                }
                else
                {
                    val = "false";
                }
            }
            else if ( de.getType().equals( "string" ) )
            {
                if ( de.getOptionSet().getOptions().size() <= 1 )
                {
                    TextField txtField = (TextField) dataElementMappingTable.get( String.valueOf( de.getId() ) );
                    val = txtField.getString();
                }

                else
                {
                    ChoiceGroup choiceGroup = (ChoiceGroup) dataElementMappingTable.get( String.valueOf( de.getId() ) );
                    int selectedIndex = choiceGroup.getSelectedIndex();
                    if ( selectedIndex == 0 )
                    {
                        val = "";
                    }
                    else
                    {

                        String selectedOption = (String) optionSets.elementAt( selectedIndex - 1 );
                        dv.setOptionName( selectedOption );
                        val = String.valueOf( selectedOption );
                    }
                }
            }
            dv.setId( de.getId() );
            dv.setVal( val );
            if ( val == null || val.trim().equals( DHISMIDlet.BLANK ) )
            {
                if ( de.isCompulsory() )
                {
                    counter++;
                }
            }
            activityValue.getDataValues().put( String.valueOf( de.getId() ), dv );
        }
        return activityValue;
    }



    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Name-Based Data Entry" + " Screen...");
        this.switchDisplayable( null, this.getProgramStageForm() );
    }

    public Activity getSelectedActivity()
    {
        return selectedActivity;
    }

    public void setSelectedActivity( Activity selectedActivity )
    {
        this.selectedActivity = selectedActivity;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public Form getProgramStageForm()
    {
        if ( this.programStageForm == null )
        {
            this.programStageForm = new Form( "" );
            this.programStageForm.setCommandListener( this );
            this.programStageForm.addCommand( this.getProgramStageFormBackCommand() );
        }
        return programStageForm;
    }

    public void setProgramStageForm( Form programStageForm )
    {
        this.programStageForm = programStageForm;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

    public Command getProgramStageFormBackCommand()
    {
        if ( this.programStageFormBackCommand == null )
        {
            this.programStageFormBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return programStageFormBackCommand;
    }

    public void setProgramStageFormBackCommand( Command programStageFormBackCommand )
    {
        this.programStageFormBackCommand = programStageFormBackCommand;
    }

    public Command getProgramStageFormSaveCommand()
    {
        if ( programStageFormSaveCommand == null )
        {
            programStageFormSaveCommand = new Command( Text.SAVE(), Command.SCREEN, 0 );
        }
        return programStageFormSaveCommand;
    }

    public void setProgramStageFormSaveCommand( Command programStageFormSaveCommand )
    {
        this.programStageFormSaveCommand = programStageFormSaveCommand;
    }

    public Command getProgramStageFormSendCommand()
    {
        if ( programStageFormSendCommand == null )
        {
            programStageFormSendCommand = new Command( Text.SEND(), Command.SCREEN, 0 );
        }
        return programStageFormSendCommand;
    }

    public void setProgramStageFormSendCommand( Command programStageFormSendCommand )
    {
        this.programStageFormSendCommand = programStageFormSendCommand;
    }

    public Alert getCloseAlert()
    {
        if ( this.closeAlert == null )
        {
            closeAlert = Alerts.getConfirmAlert( Text.MESSAGE(), Text.CLOSE_CONFIRM(), this );
        }
        return closeAlert;
    }

    public void setCloseAlert( Alert closeAlert )
    {

        this.closeAlert = closeAlert;
    }
}
