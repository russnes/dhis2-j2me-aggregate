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

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.recordstore.DataSetValueRecordStore;
import org.hisp.dhis.mobile.ui.CalendarCanvas;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

public class PeriodView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "PeriodView";

    private FacilityMIDlet facilityMIDlet;

    private DataSet selectedDataSet;

    private Form periodForm;

    private List periodList;

    private ChoiceGroup periodChoice;

    private Command periodFormNextCommand;

    private Command periodFormBackCommand;

    private Command openCalendarCommand;

    private DateField dailyPeriodDateField;

    private String selectedPeriod;

    public PeriodView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.getPeriodForm().deleteAll();
        this.getPeriodList().deleteAll();
        // if ( selectedDataSet.getPeriodType().equals( "Daily" ) ||
        // selectedDataSet.getPeriodType().equals( "Weekly" ) )
        if ( selectedDataSet.getPeriodType().equals( "Daily" ) )
        {
            getPeriodForm().append( getDailyPeriodDateField() );
            getPeriodForm().addCommand( getOpenCalendarCommand() );
        }
        else
        {
            getPeriodForm().removeCommand( this.getOpenCalendarCommand() );
            getPeriodForm().append( getPeriodChoice() );
            Vector periods = new Vector();

            if ( selectedDataSet.getPeriodType().equals( "Monthly" ) )
            {
                periods = PeriodUtil.generateMonthlyPeriods();
            }
            else if ( selectedDataSet.getPeriodType().equals( "Weekly" ) )
            {
                // periods = PeriodUtil.generateWeeklyPeriods();
                periods = PeriodUtil.generateWeekRange();
            }
            else if ( selectedDataSet.getPeriodType().equals( "Yearly" ) )
            {
                periods = PeriodUtil.generateYearlyPeriods();
            }
            else if ( selectedDataSet.getPeriodType().equals( "Quarterly" ) )
            {
                periods = PeriodUtil.generateQuaterlyPeriods();
            }

            for ( int i = 0; i < periods.size(); i++ )
            {
                String period = (String) periods.elementAt( i );
                short state = DataSetValueRecordStore.getDataSetState( selectedDataSet, period,
                    dhisMIDlet.getCurrentOrgUnit() );
                try
                {
                    if ( state == DataSetValueRecordStore.STATE_SAVED )
                    {
                        getPeriodList().append( period, Image.createImage( "/save.png" ) );
                    }
                    else if ( state == DataSetValueRecordStore.STATE_COMPLETED )
                    {
                        getPeriodList().append( period, Image.createImage( "/sent.gif" ) );
                    }
                    else
                    {
                        getPeriodList().append( period, null );
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                period = null;
            }
            System.gc();
        }

    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Period" + " Screen..." );
        this.prepareView();
        // if ( selectedDataSet.getPeriodType().equals( "Daily" ) ||
        // selectedDataSet.getPeriodType().equals( "Weekly" ) )
        if ( selectedDataSet.getPeriodType().equals( "Daily" ) )
        {
            this.switchDisplayable( null, this.getPeriodForm() );
        }
        else
        {
            this.switchDisplayable( null, this.getPeriodList() );
        }

    }

    public FacilityMIDlet getFacilityMIDlet()
    {
        return facilityMIDlet;
    }

    public void setFacilityMIDlet( FacilityMIDlet facilityMIDlet )
    {
        this.facilityMIDlet = facilityMIDlet;
    }

    public Form getPeriodForm()
    {
        if ( periodForm == null )
        {
            periodForm = new Form( Text.SELECT_PERIOD(), new Item[] { getPeriodChoice() } );
            periodForm.addCommand( getPeriodFormNextCommand() );
            periodForm.addCommand( getPeriodFormBackCommand() );
            periodForm.setCommandListener( this );
        }
        return periodForm;
    }

    public void setPeriodForm( Form periodForm )
    {
        this.periodForm = periodForm;
    }

    public Command getPeriodFormNextCommand()
    {
        if ( periodFormNextCommand == null )
        {
            periodFormNextCommand = new Command( Text.NEXT(), Command.SCREEN, 0 );
        }
        return periodFormNextCommand;
    }

    public void setPeriodFormNextCommand( Command periodFormNextCommand )
    {
        this.periodFormNextCommand = periodFormNextCommand;
    }

    public Command getPeriodFormBackCommand()
    {
        if ( periodFormBackCommand == null )
        {
            periodFormBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return periodFormBackCommand;
    }

    public void setPeriodFormBackCommand( Command periodFormBackCommand )
    {
        this.periodFormBackCommand = periodFormBackCommand;
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == periodFormNextCommand )
        {
            this.facilityMIDlet.getWaitingView().showView();
            String periodName = null;

            if ( !selectedDataSet.getPeriodType().equals( "Daily" ) )
            {
                periodName = getPeriodChoice().getString( getPeriodChoice().getSelectedIndex() );
                if ( periodName.indexOf( " * " ) != -1 )
                {
                    periodName = periodName.substring( 0, periodName.indexOf( " * " ) );
                }
            }

            if ( selectedDataSet.getPeriodType().equals( "Daily" ) )
            {
                selectedPeriod = PeriodUtil.formatDate( dailyPeriodDateField.getDate() );
            }

            else if ( selectedDataSet.getPeriodType().equals( "Monthly" ) )
            {
                selectedPeriod = PeriodUtil.formatMonthlyPeriod( periodName );
            }

            else if ( selectedDataSet.getPeriodType().equals( "Weekly" ) )
            {
                selectedPeriod = (String) PeriodUtil.dateOfWeekTable.get( periodName );
            }
            else
            {
                selectedPeriod = periodName;
            }

            this.facilityMIDlet.getDataSetEntryView().setDataSet( selectedDataSet );
            this.facilityMIDlet.getDataSetEntryView().setPeriodName( selectedPeriod );
            this.facilityMIDlet.getDataSetEntryView().setDisplayMode( DataSetEntryView.NORMAL_MODE );
            this.facilityMIDlet.getDataSetEntryView().prepareView();
            this.facilityMIDlet.getDataSetEntryView().showView();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            String periodName = getPeriodList().getString( getPeriodList().getSelectedIndex() );
            if ( periodName.indexOf( " * " ) != -1 )
            {
                periodName = periodName.substring( 0, periodName.indexOf( " * " ) );
            }
            else if ( selectedDataSet.getPeriodType().equals( "Monthly" ) )
            {
                selectedPeriod = PeriodUtil.formatMonthlyPeriod( periodName );
            }
            else if ( selectedDataSet.getPeriodType().equals( "Weekly" ) )
            {
                selectedPeriod = (String) PeriodUtil.dateOfWeekTable.get( periodName );
            }
            else
            {
                selectedPeriod = periodName;
            }
            this.facilityMIDlet.getDataSetEntryView().setDataSet( selectedDataSet );
            this.facilityMIDlet.getDataSetEntryView().setPeriodName( selectedPeriod );
            this.facilityMIDlet.getDataSetEntryView().setDisplayMode( DataSetEntryView.NORMAL_MODE );
            this.facilityMIDlet.getDataSetEntryView().prepareView();
            this.facilityMIDlet.getDataSetEntryView().showView();
        }
        else if ( command == this.periodFormBackCommand )
        {
            this.facilityMIDlet.getDataSetListView().showView();
        }
        else if ( command == this.openCalendarCommand )
        {
            this.switchDisplayable( null, new CalendarCanvas( this.getFacilityMIDlet() ) );
        }
    }

    public ChoiceGroup getPeriodChoice()
    {
        if ( periodChoice == null )
        {
            periodChoice = new ChoiceGroup( Text.PERIOD(), Choice.POPUP );
            periodChoice.setFitPolicy( Choice.TEXT_WRAP_DEFAULT );
        }
        return periodChoice;
    }

    public void setPeriodChoice( ChoiceGroup periodChoice )
    {
        this.periodChoice = periodChoice;
    }

    public DataSet getSelectedDataSet()
    {
        return selectedDataSet;
    }

    public void setSelectedDataSet( DataSet selectedDataSet )
    {
        this.selectedDataSet = selectedDataSet;
    }

    public DateField getDailyPeriodDateField()
    {
        if ( dailyPeriodDateField == null )
        {
            dailyPeriodDateField = new DateField( "Select Date:", DateField.DATE );
            dailyPeriodDateField.setDate( new java.util.Date( System.currentTimeMillis() ) );
        }
        return dailyPeriodDateField;
    }

    public void setDailyPeriodDateField( DateField dailyPeriodDateField )
    {
        this.dailyPeriodDateField = dailyPeriodDateField;
    }

    public Command getOpenCalendarCommand()
    {
        if ( openCalendarCommand == null )
        {
            openCalendarCommand = new Command( "Select Date", Command.OK, 1 );
        }
        return openCalendarCommand;
    }

    public void setOpenCalendarCommand( Command openCalendarCommand )
    {
        this.openCalendarCommand = openCalendarCommand;
    }

    public String getSelectedPeriod()
    {
        return selectedPeriod;
    }

    public void setSelectedPeriod( String selectedPeriod )
    {
        this.selectedPeriod = selectedPeriod;
    }

    public List getPeriodList()
    {
        if ( this.periodList == null )
        {
            this.periodList = new List( Text.PERIOD() + " of " + this.selectedDataSet.getName(), List.IMPLICIT );
            this.periodList.addCommand( this.getPeriodFormBackCommand() );
            this.periodList.setCommandListener( this );
        }
        else
            this.periodList.setTitle( Text.PERIOD() + this.selectedDataSet.getName() );

        return this.periodList;
    }

    public void setPeriodList( List periodList )
    {
        this.periodList = periodList;
    }

}
