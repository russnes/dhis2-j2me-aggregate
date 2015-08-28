package org.hisp.dhis.mobile.recordstore.filter;

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
import java.util.Date;

import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.util.PeriodUtil;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class ActivityFilter
    implements RecordFilter
{
    private String patientName;

    private String groupingValue;

    private Integer programStageInstID;

    private Boolean isCompleted;

    private Date currentDate;

    public boolean matches( byte[] candidate )
    {
        Activity candidateActivity = new Activity();
        try
        {
            SerializationUtil.deSerialize( candidateActivity, candidate );
            if ( this.programStageInstID != null )
            {
                if ( candidateActivity.getTask().getProgStageInstId() == this.programStageInstID.intValue() )
                {
                    return true;
                }
            }
            else if ( this.patientName != null && this.isCompleted != null )
            {
                if ( this.isCompleted.booleanValue() == candidateActivity.getTask().isComplete()
                    && candidateActivity.getBeneficiary().getFullName().equals( this.patientName ) )
                {
                    return true;
                }
            }
            else if ( this.groupingValue != null && this.isCompleted != null )
            {
                String groupByValue = candidateActivity.getBeneficiary().getGroupByAttribute().getValue();
                if ( this.groupingValue.equals( groupByValue ) )
                {
                    return true;
                }
            }
            else if ( this.isCompleted != null )
            {
                if ( candidateActivity.getTask().isComplete() == this.isCompleted.booleanValue() )
                {
                    return true;
                }
            }
            else if ( this.currentDate != null )
            {
                if ( PeriodUtil.isExpired( currentDate, candidateActivity.getExpireDate() ) )
                {
                   return true;
                }
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        return false;
    }

    public void setCriteria( Integer programStageInstID, Boolean isCompleted, String patientName, String groupingValue, Date currentDate )
    {
        this.programStageInstID = programStageInstID;
        this.isCompleted = isCompleted;
        this.patientName = patientName;
        this.groupingValue = groupingValue;
        this.currentDate = currentDate;
    }

    public Integer getProgramStageInstID()
    {
        return programStageInstID;
    }

    public void setProgramStageInstID( Integer programStageInstID )
    {
        this.programStageInstID = programStageInstID;
    }

    public Boolean getIsCompleted()
    {
        return isCompleted;
    }

    public void setIsCompleted( Boolean isCompleted )
    {
        this.isCompleted = isCompleted;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName( String patientName )
    {
        this.patientName = patientName;
    }

    public String getGroupingValue()
    {
        return groupingValue;
    }

    public void setGroupingValue( String groupingValue )
    {
        this.groupingValue = groupingValue;
    }

    public Date getCurrentDate()
    {
        return currentDate;
    }

    public void setCurrentDate( Date currentDate )
    {
        this.currentDate = currentDate;
    }

}
