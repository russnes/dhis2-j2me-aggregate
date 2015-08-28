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
import java.util.Enumeration;
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
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.SMSCode;
import org.hisp.dhis.mobile.model.SMSCommand;
import org.hisp.dhis.mobile.model.Section;
import org.hisp.dhis.mobile.recordstore.DataSetValueRecordStore;
import org.hisp.dhis.mobile.recordstore.SMSCommandRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

public class DataSetEntryView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "DataSetEntryView";

    private FacilityMIDlet facilityMIDlet;

    public static final int EDIT_MODE = 1;

    public static final int NORMAL_MODE = 2;

    private DataSet dataSet;

    private DataValue dataValue;

    private int displayMode;

    private String periodName;

    private Vector formVector;

    private Hashtable dataElementsMappingTable;

    private int currentSession = 0;

    private int emptyValueCounter;

    private DataSetValue dataSetValue;

    private Alert sendDataSetValueConfirmAlert;

    private Command formBackCommand;

    private Command formExitCommand;

    private Command formNextCommand;

    private Command formSendCommand;

    private Command formSaveCommand;

    private Command formEditCommand;

    private Command formSendSMSCommand;

    private String message;

    private StringItem sendMsgLabel;

    private Alert closeAlert;

    public DataSetEntryView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        if ( this.dataSet != null && this.periodName != null )
        {
            this.formVector = null;
            this.dataElementsMappingTable = null;

            if ( this.displayMode != EDIT_MODE )
            {
                this.currentSession = 0;
            }

            System.gc();
            this.formVector = new Vector();
            this.dataElementsMappingTable = new Hashtable();
            this.prepareDataSetEntry();
        }
    }

    private void prepareDataSetEntry()
    {

        DataSetValue dataSetValue = null;

        try
        {
            dataSetValue = DataSetValueRecordStore.getDataSetValue( dataSet.getId(), this.periodName,
                dhisMIDlet.getCurrentOrgUnit() );
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        int sessionSize = dataSet.getSections().size();

        Section section = null;
        Form form = null;
        Vector dataElementVector = null;
        DataElement dataElement = null;
        Vector categoryOptComboVector = null;
        Model categoryOptCombo = null;
        String label = null;

        for ( int i = 0; i < sessionSize; i++ )
        {
            section = (Section) dataSet.getSections().elementAt( i );
            form = new Form( dataSet.getName() );
            dataElementVector = section.getDataElements();
            int dataElementsSize = dataElementVector.size();

            for ( int j = 0; j < dataElementsSize; j++ )
            {
                dataElement = (DataElement) dataElementVector.elementAt( j );
                categoryOptComboVector = dataElement.getCategoryOptionCombos().getModels();
                int categoryOptSize = categoryOptComboVector.size();
                form.append( dataElement.getName() );
                label = "";
                for ( int k = 0; k < categoryOptSize; k++ )
                {
                    categoryOptCombo = (Model) categoryOptComboVector.elementAt( k );
                    if ( categoryOptSize > 1 )
                    {
                        label = categoryOptCombo.getName();
                    }

                    if ( dataSetValue != null )
                    {
                        if ( !dataSetValue.isCompleted() || this.displayMode == EDIT_MODE )
                        {
                            this.insertComponent( form, dataSetValue, categoryOptCombo, dataElement, label );
                        }
                        else
                        {
                            this.insertUneditableComponent( form, dataSetValue, categoryOptCombo, dataElement, label );
                        }
                    }
                    else
                    {
                        this.insertComponent( form, dataSetValue, categoryOptCombo, dataElement, label );
                    }

                    categoryOptCombo = null;
                    System.gc();
                }
                categoryOptComboVector = null;
                dataElement = null;
                System.gc();
            }

            form.setCommandListener( this );

            // Add command for forms

            if ( (this.displayMode == NORMAL_MODE) && (dataSetValue != null && dataSetValue.isCompleted()) )
            {
                form.addCommand( this.getFormEditCommand() );
            }

            else
            {
                form.addCommand( this.getFormSaveCommand() );

            }

            if ( i == 0 )
            {
                if ( sessionSize > 1 )
                {
                    form.addCommand( getFormNextCommand() );
                }
            }
            if ( i > 0 && i < sessionSize - 1 )
            {
                form.addCommand( getFormNextCommand() );
                form.addCommand( getFormBackCommand() );
            }
            if ( i == sessionSize - 1 )
            {
                if ( sessionSize > 1 )
                {
                    form.addCommand( getFormBackCommand() );
                }

                if ( dataSetValue == null || !dataSetValue.isCompleted() || this.displayMode == EDIT_MODE )
                {

                    form.addCommand( getFormSendCommand() );
                    form.addCommand( getFormSendSMSCommand() );
                }

            }

            form.addCommand( getFormExitCommand() );
            formVector.addElement( form );

            // free memory

            dataElementVector = null;
            form = null;
            section = null;
            System.gc();
        }

    }

    private void insertUneditableComponent( Form form, DataSetValue dataSetValue, Model categoryOptCombo,
        DataElement dataElement, String label )
    {
        StringBuffer buffer = new StringBuffer();
        DataValue dataValue = null;
        buffer.append( dataElement.getId() );
        buffer.append( "-" );
        buffer.append( categoryOptCombo.getId() );

        dataValue = (DataValue) dataSetValue.getDataValues().get( buffer.toString() );

        TextField txtField = new TextField( label, "", 32, TextField.ANY | TextField.UNEDITABLE );
        if ( dataElement.getType().equals( "bool" ) )
        {
            if ( dataValue != null )
                if ( dataValue.getVal().equalsIgnoreCase( "true" ) )
                    txtField.setString( Text.YES() );
                else if ( dataValue.getVal().equalsIgnoreCase( "false" ) )
                    txtField.setString( Text.NO() );
                else
                    txtField.setString( Text.PLEASE_SELECT() );
        }
        else
        {
            if ( dataValue != null )
                txtField.setString( dataValue.getVal() );

        }
        form.append( txtField );
        buffer = null;
        dataValue = null;
        System.gc();
    }

    private void insertComponent( Form form, DataSetValue dataSetValue, Model categoryOptCombo,
        DataElement dataElement, String label )
    {
        StringBuffer buffer = new StringBuffer();
        DataValue dataValue = null;
        buffer.append( dataElement.getId() );
        buffer.append( "-" );
        buffer.append( categoryOptCombo.getId() );

        if ( dataSetValue != null )
        {
            dataValue = (DataValue) dataSetValue.getDataValues().get( buffer.toString() );
        }

        // Identify DataElement Type to append suitable Item

        if ( dataElement.getType().equals( "date" ) )
        {
            DateField dateField = new DateField( label, DateField.DATE );
            form.append( dateField );
            dataElementsMappingTable.put( buffer.toString(), dateField );

            if ( !dataValue.getVal().equals( "" ) && dataValue != null )
            {
                dateField.setDate( PeriodUtil.stringToDate( dataValue.getVal() ) );
            }
        }
        else if ( dataElement.getType().equals( "bool" ) )
        {
            ChoiceGroup choiceGroup = new ChoiceGroup( label, Choice.POPUP );
            choiceGroup.append( Text.PLEASE_SELECT(), null );
            choiceGroup.append( Text.YES(), null );
            choiceGroup.append( Text.NO(), null );
            choiceGroup.setFitPolicy( Choice.TEXT_WRAP_ON );
            form.append( choiceGroup );
            dataElementsMappingTable.put( buffer.toString(), choiceGroup );
            if ( dataValue != null )
            {
                if ( dataValue.getVal().equals( "" ) && dataValue != null )
                {
                    choiceGroup.setSelectedIndex( 0, true );
                }
                else if ( dataValue.getVal().equals( "true" ) && dataValue != null )
                {
                    choiceGroup.setSelectedIndex( 1, true );
                }
                else
                {
                    choiceGroup.setSelectedIndex( 2, true );
                }
            }
        }
        else if ( dataElement.getType().equals( "int" ) )
        {
            TextField intField = new TextField( label, "", DHISMIDlet.TEXT_NUMERIC, TextField.NUMERIC );
            form.append( intField );
            dataElementsMappingTable.put( buffer.toString(), intField );
            if ( dataValue != null )
                intField.setString( dataValue.getVal() );
        }
        else
        {
            TextField txtField = new TextField( label, "", DHISMIDlet.TEXT_STRING, TextField.ANY );
            form.append( txtField );
            dataElementsMappingTable.put( buffer.toString(), txtField );
            if ( dataValue != null )
                txtField.setString( dataValue.getVal() );
        }

        buffer = null;
        dataValue = null;
        System.gc();
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Data Set Entry" + " Screen...");
        this.switchDisplayable( null, (Form) this.getFormVector().elementAt( currentSession ) );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.formExitCommand )
        {
            this.getDisplay().setCurrent( this.getCloseAlert() );
        }
        else if ( displayable == this.getCloseAlert() )
        {
            if ( command.getCommandType() == Command.OK )
            {
                this.formVector = null;
                this.dataElementsMappingTable = null;
                System.gc();
                this.facilityMIDlet.getDataSetListView().showView();
            }
            else
            {
                this.showView();
            }
        }
        else if ( command == this.formNextCommand )
        {
            this.switchDisplayable( null, (Displayable) formVector.elementAt( currentSession + 1 ) );
            currentSession++;
        }

        else if ( command == this.formSaveCommand )
        {
            try
            {
                DataSetValue dataSetValue = this.generateDataSetValue();
                if ( DataSetValueRecordStore.isCompleted( dataSet.getId(), periodName ) )
                {
                    dataSetValue.setCompleted( true );
                }
                DataSetValueRecordStore.saveDataSetValue( dataSetValue, dhisMIDlet.getCurrentOrgUnit() );
                this.getDisplay().setCurrent( Alerts.getInfoAlert( Text.MESSAGE(), Text.DATAVALUE_SAVED() ) );
            }
            catch ( Exception e )
            {
                this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), e.getMessage() ) );
                e.printStackTrace();
            }

        }

        else if ( command == this.formBackCommand )
        {
            this.switchDisplayable( null, (Displayable) formVector.elementAt( currentSession - 1 ) );
            currentSession--;
        }

        else if ( command == this.formSendCommand )
        {
            this.dataSetValue = null;
            this.dataSetValue = this.generateDataSetValue();

            if ( this.emptyValueCounter > 0 )
            {
                this.getDisplay().setCurrent( this.getSendDataSetValueConfirmAlert() );
            }
            else
            {
                this.sendDataSetValue();
            }

        }
        else if ( command == this.formSendSMSCommand )
        {

            this.dataSetValue = null;
            String smsString = this.generateSMSValue();
            if ( smsString == null || smsString.trim().equals( "" ) )
            {
                this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), Text.NO_SMSCOMMAND() ) );
            }
            else
            {
                ConnectionManager.sendSMSReport( smsString );
            }
        }
        else if ( command == this.formEditCommand )
        {
            this.setDisplayMode( EDIT_MODE );
            this.prepareView();
            this.showView();
        }
        else if ( displayable == this.sendDataSetValueConfirmAlert )
        {
            if ( command.getCommandType() == Command.OK )
            {
                this.sendDataSetValue();
            }
            else
            {
                this.showView();
            }
        }

    }

    public String generateSMSValue()
    {
        SMSCommand smsCommand = null;
        try
        {
            smsCommand = SMSCommandRecordStore.loadSMSCommand( dataSet.getId() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }

        if ( smsCommand == null )
        {
            return null;
        }

        Hashtable smsCodeMap = this.loadSMSCodeMap( smsCommand );
        StringBuffer sms = new StringBuffer();

        DataSetValue dataSetValue = this.generateDataSetValue();

        Hashtable dataValueHash = dataSetValue.getDataValues();

        sms.append( smsCommand.getName() );
        sms.append( " " );
        sms.append( dataSetValue.getpName() );
        sms.append( "!" );

        if ( dataValueHash == null )
        {
            return "";
        }

        Enumeration elements = dataValueHash.elements();

        while ( elements.hasMoreElements() )
        {
            DataValue dv = (DataValue) elements.nextElement();
            String code = (String) smsCodeMap.get( dv.getId() + "-" + dv.getCategoryOptComboID() );
            if ( code != null && !dv.getVal().trim().equalsIgnoreCase( "" ) )
            {
                sms.append( code + smsCommand.getSeparator() + dv.getVal() + "#" );
            }

        }
        if ( sms.length() > 0 )
        {
            sms.deleteCharAt( sms.length() - 1 );
        }
        return (sms == null ? "" : sms.toString());
    }

    private Hashtable loadSMSCodeMap( SMSCommand smsCommand )
    {
        Hashtable smsCodeTable = new Hashtable();
        Vector smsCodes = smsCommand.getSmsCodes();
        for ( int i = 0; i < smsCodes.size(); i++ )
        {
            SMSCode code = (SMSCode) smsCodes.elementAt( i );
            smsCodeTable.put( code.getDataElementId() + "-" + code.getOptionId(), code.getCode() );
        }
        return smsCodeTable;
    }

    private void sendDataSetValue()
    {
        OrgUnit orgUnit = dhisMIDlet.getCurrentOrgUnit();
        SettingRecordStore settingRecordStore = null;
        try
        {
            settingRecordStore = new SettingRecordStore();
            DataSetValueRecordStore.saveDataSetValue( dataSetValue, dhisMIDlet.getCurrentOrgUnit() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        this.facilityMIDlet.getWaitingView().showView();
        ConnectionManager.init( dhisMIDlet, orgUnit.getUploadFacilityReportUrl(),
            settingRecordStore.get( SettingRecordStore.USERNAME ),
            settingRecordStore.get( SettingRecordStore.PASSWORD ), DHISMIDlet.DEFAULT_LOCALE, orgUnit );
        ConnectionManager.uploadDataSetValue( dataSetValue );
        this.cleanOldDataValue();
    }

    private void cleanOldDataValue()
    {
        try
        {
            if ( dataSet.getPeriodType().equals( "Monthly" ) )
            {
                DataSetValueRecordStore.cleanOldDataSetValues( dataSet.getId(), PeriodUtil.MONTHLY,
                    PeriodUtil.generateMonthlyPeriods() );
            }
            else if ( dataSet.getPeriodType().equals( "Yearly" ) )
            {
                DataSetValueRecordStore.cleanOldDataSetValues( dataSet.getId(), PeriodUtil.YEARLY,
                    PeriodUtil.generateYearlyPeriods() );
            }
            else if ( dataSet.getPeriodType().equals( "Weekly" ) )
            {
                DataSetValueRecordStore.cleanOldDataSetValues( dataSet.getId(), PeriodUtil.WEEKLY, null );
            }
            else if ( dataSet.getPeriodType().equals( "Quarterly" ) )
            {
                DataSetValueRecordStore.cleanOldDataSetValues( dataSet.getId(), PeriodUtil.QUARTERLY,
                    PeriodUtil.generateQuaterlyPeriods() );
            }
            else if ( dataSet.getPeriodType().equals( "Daily" ) )
            {
                DataSetValueRecordStore.cleanOldDataSetValues( dataSet.getId(), PeriodUtil.DAILY, null );
            }
        }
        catch ( RecordStoreException re )
        {
            re.printStackTrace();
            LogMan.log( "RMS," + CLASS_TAG, re );
        }
    }

    public DataSetValue generateDataSetValue()
    {
        this.emptyValueCounter = 0;
        StringBuffer buffer = null;
        Vector des = getAllDataElement( dataSet );
        DataSetValue dsValue = new DataSetValue();
        dsValue.setId( dataSet.getId() );
        dsValue.setName( dataSet.getName() );
        dsValue.setpName( facilityMIDlet.getPeriodView().getSelectedPeriod() );

        String val;
        for ( int i = 0; i < des.size(); i++ )
        {
            DataElement de = (DataElement) des.elementAt( i );
            Vector categoryOptCombos = de.getCategoryOptionCombos().getModels();
            // Loop through all CategoryOptionCombo of the DataElement
            for ( int j = 0; j < categoryOptCombos.size(); j++ )
            {
                DataValue dv = new DataValue();
                Model categoryOptCombo = (Model) categoryOptCombos.elementAt( j );
                buffer = new StringBuffer();
                buffer.append( de.getId() );
                buffer.append( "-" );
                buffer.append( categoryOptCombo.getId() );

                // Identify DataElement Type
                if ( de.getType().equals( "date" ) )
                {
                    DateField dateField = (DateField) dataElementsMappingTable.get( buffer.toString() );
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
                    TextField intField = (TextField) dataElementsMappingTable.get( buffer.toString() );
                    val = intField.getString();
                    if ( val.length() > 1 )
                    {
                        while ( val.indexOf( '0' ) == 0 )
                        {
                            val = val.substring( 1, val.length() );
                        }
                    }
                }
                else if ( de.getType().equals( "bool" ) )
                {
                    ChoiceGroup boolField = (ChoiceGroup) dataElementsMappingTable.get( buffer.toString() );

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
                else
                {
                    TextField txtField = (TextField) dataElementsMappingTable.get( buffer.toString() );
                    val = txtField.getString();
                }

                if ( val.equals( DHISMIDlet.BLANK ) || val == null )
                {
                    emptyValueCounter++;
                }

                dv.setId( de.getId() );
                dv.setCategoryOptComboID( categoryOptCombo.getId() );
                dv.setVal( val );
                dsValue.getDataValues().put( buffer.toString(), dv );
                dv = null;
                buffer = null;
                System.gc();

            }
            de = null;
        }
        System.gc();
        return dsValue;
    }

    // generate sms return string
    // send sms

    private Vector getAllDataElement( DataSet dataSet )
    {
        Vector des = new Vector();
        for ( int i = 0; i < dataSet.getSections().size(); i++ )
        {
            Section section = (Section) dataSet.getSections().elementAt( i );
            for ( int j = 0; j < section.getDataElements().size(); j++ )
            {
                des.addElement( section.getDataElements().elementAt( j ) );
            }
            section = null;
        }
        System.gc();
        return des;
    }

    public Vector getFormVector()
    {
        return formVector;
    }

    public void setFormVector( Vector formVector )
    {
        this.formVector = formVector;
    }

    public int getCurrentSession()
    {
        return currentSession;
    }

    public void setCurrentSession( int currentSession )
    {
        this.currentSession = currentSession;
    }

    public Command getFormBackCommand()
    {
        if ( formBackCommand == null )
        {
            formBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return formBackCommand;
    }

    public void setFormBackCommand( Command formBackCommand )
    {
        this.formBackCommand = formBackCommand;
    }

    public Command getFormExitCommand()
    {
        if ( formExitCommand == null )
        {
            formExitCommand = new Command( Text.CLOSE(), Command.BACK, 0 );
        }
        return formExitCommand;
    }

    public void setFormExitCommand( Command formExitCommand )
    {
        this.formExitCommand = formExitCommand;
    }

    public Command getFormNextCommand()
    {
        if ( formNextCommand == null )
        {
            formNextCommand = new Command( Text.NEXT(), Command.OK, 0 );
        }
        return formNextCommand;
    }

    public void setFormNextCommand( Command formNextCommand )
    {
        this.formNextCommand = formNextCommand;
    }

    public Command getFormSendCommand()
    {
        if ( formSendCommand == null )
        {
            formSendCommand = new Command( Text.SEND(), Command.SCREEN, 0 );
        }
        return formSendCommand;
    }

    public void setFormSendCommand( Command formSendCommand )
    {
        this.formSendCommand = formSendCommand;
    }

    public Command getFormSaveCommand()
    {
        if ( formSaveCommand == null )
        {
            formSaveCommand = new Command( Text.SAVE(), Command.SCREEN, 1 );
        }
        return formSaveCommand;
    }

    public void setFormSaveCommand( Command formSaveCommand )
    {
        this.formSaveCommand = formSaveCommand;
    }

    public Command getFormSendSMSCommand()
    {
        if ( formSendSMSCommand == null )
        {
            formSendSMSCommand = new Command( Text.SENDSMS(), Command.SCREEN, 2 );
        }

        return formSendSMSCommand;
    }

    public void setFormSendSMSCommand( Command formSendSMSCommand )
    {
        this.formSendSMSCommand = formSendSMSCommand;
    }

    public FacilityMIDlet getFacilityMIDlet()
    {
        return facilityMIDlet;
    }

    public void setFacilityMIDlet( FacilityMIDlet facilityMIDlet )
    {
        this.facilityMIDlet = facilityMIDlet;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName( String periodName )
    {
        this.periodName = periodName;
    }

    public Hashtable getDataElementsMappingTable()
    {
        return dataElementsMappingTable;
    }

    public void setDataElementsMappingTable( Hashtable dataElementsMappingTable )
    {
        this.dataElementsMappingTable = dataElementsMappingTable;
    }

    public int getDisplayMode()
    {
        return displayMode;
    }

    public void setDisplayMode( int displayMode )
    {
        this.displayMode = displayMode;
    }

    public Command getFormEditCommand()
    {
        if ( formEditCommand == null )
        {
            formEditCommand = new Command( "Edit", Command.OK, 1 );
        }
        return formEditCommand;
    }

    public void setFormEditCommand( Command formEditCommand )
    {
        this.formEditCommand = formEditCommand;
    }

    public int getEmptyValueCounter()
    {
        return emptyValueCounter;
    }

    public void setEmptyValueCounter( int emptyValueCounter )
    {
        this.emptyValueCounter = emptyValueCounter;
    }

    public Alert getSendDataSetValueConfirmAlert()
    {
        if ( this.sendDataSetValueConfirmAlert == null )
        {
            sendDataSetValueConfirmAlert = Alerts.getConfirmAlert( Text.MESSAGE(),
                this.emptyValueCounter + " " + Text.FIELD_NOT_FILLED(), this );
        }
        return sendDataSetValueConfirmAlert;
    }

    public void setSendDataSetValueConfirmAlert( Alert sendDataSetValueConfirmAlert )
    {
        this.sendDataSetValueConfirmAlert = sendDataSetValueConfirmAlert;
    }

    public DataSetValue getDataSetValue()
    {
        return dataSetValue;
    }

    public void setDataSetValue( DataSetValue dataSetValue )
    {
        this.dataSetValue = dataSetValue;
    }

    public String sendSMS()
    {
        String message;
        message = dataSet.getId() + "#" + dataValue.getId() + "." + dataValue.getCategoryOptComboID() + "," + dataValue;
        return message;
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
