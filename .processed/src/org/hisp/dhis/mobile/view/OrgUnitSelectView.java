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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.ui.Text;

public class OrgUnitSelectView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "OrgUnitSelectView";

    private Vector orgUnitVector;

    private List orgUnitSelectList;

    public OrgUnitSelectView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.orgUnitVector = new Vector();
    }

    public void prepareView()
    {
        this.getOrgUnitSelectList().deleteAll();;
        for ( int i = 0; i < orgUnitVector.size(); i++ )
        {
            OrgUnit unit = (OrgUnit) orgUnitVector.elementAt( i );
            this.getOrgUnitSelectList().append( unit.getName(), null );
        }
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Organization Unit Selection" + " Screen...");
        this.prepareView();
        this.switchDisplayable( null, this.getOrgUnitSelectList() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == List.SELECT_COMMAND )
        {
            OrgUnit orgUnit = (OrgUnit) this.getOrgUnitVector().elementAt( this.getOrgUnitSelectList().getSelectedIndex() );
            dhisMIDlet.setCurrentOrgUnit( orgUnit );
            
            if ( DHISMIDlet.isFirstTimeLogIn )
            {
                dhisMIDlet.getPinView().navigate();
            }
            else
            {
                try
                {
                    dhisMIDlet.getPinView().update();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {

        }
    }

    public Vector getOrgUnitVector()
    {
        return orgUnitVector;
    }

    public void setOrgUnitVector( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }

    public List getOrgUnitSelectList()
    {
        if ( this.orgUnitSelectList == null )
        {
            this.orgUnitSelectList = new List( Text.SELECT_ORG_UNIT(), List.IMPLICIT );
            this.orgUnitSelectList.setFitPolicy( List.TEXT_WRAP_ON );
            this.orgUnitSelectList.setCommandListener( this );
        }
        return orgUnitSelectList;
    }

    public void setOrgUnitSelectList( List orgUnitSelectList )
    {
        this.orgUnitSelectList = orgUnitSelectList;
    }

}
