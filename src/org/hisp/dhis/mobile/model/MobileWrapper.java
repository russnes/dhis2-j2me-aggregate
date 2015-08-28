package org.hisp.dhis.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.hisp.dhis.mobile.log.LogMan;

public class MobileWrapper
{
    private static final String CLASS_TAG = "MobileWrapper";
	
    private Vector programs;

    private ActivityPlan activityPlan;

    private Date serverCurrentDate;

    private Vector dataSets;

    private Vector locales;

    private Vector smsCommands;

    public Vector getLocales()
    {
        return locales;
    }

    public void setLocales( Vector locales )
    {
        this.locales = locales;
    }

    public Vector getPrograms()
    {
        return programs;
    }

    public void setPrograms( Vector programs )
    {
        this.programs = programs;
    }

    public ActivityPlan getActivityPlan()
    {
        return activityPlan;
    }

    public void setActivityPlan( ActivityPlan activityPlan )
    {
        this.activityPlan = activityPlan;
    }

    public Date getServerCurrentDate()
    {
        return serverCurrentDate;
    }

    public void setServerCurrentDate( Date serverCurrentDate )
    {
        this.serverCurrentDate = serverCurrentDate;
    }

    public Vector getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Vector dataSets )
    {
        this.dataSets = dataSets;
    }

    public Vector getSmsCommands()
    {
        return smsCommands;
    }

    public void setSmsCommands( Vector smsCommands )
    {
        this.smsCommands = smsCommands;
    }

    public void serialize( DataOutputStream din )
        throws IOException
    {
        // Fixme: implement
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
    	LogMan.log(LogMan.DEBUG, "Network," + CLASS_TAG + ",deSerialize(DataInputStream)", "Method Call. din=" + din);
    	
        this.programs = new Vector();

        int numbProgram = din.readInt();
        for ( int i = 0; i < numbProgram; i++ )
        {
            Program program = new Program();
            program.deSerialize( din );
            programs.addElement( program );
        }

        this.activityPlan = new ActivityPlan();

        activityPlan.deSerialize( din );

        this.serverCurrentDate = new Date( din.readLong() );

        this.dataSets = new Vector();
        int numbDs = din.readInt();
        for ( int j = 0; j < numbDs; j++ )
        {
            DataSet ds = new DataSet();
            ds.deSerialize( din );
            dataSets.addElement( ds );
        }

        this.locales = new Vector();
        int numbLocales = din.readInt();

        for ( int k = 0; k < numbLocales; k++ )
        {
            String locale = din.readUTF();
            locales.addElement( locale );
        }

        this.smsCommands = new Vector();
        int numbSMSCommands = din.readInt();
        for ( int l = 0; l < numbSMSCommands; l++ )
        {
            SMSCommand smsCommand = new SMSCommand();
            smsCommand.deSerialize( din );
            smsCommands.addElement( smsCommand );
        }
    }

}
