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
import javax.microedition.lcdui.Form;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.Beneficiary;
import org.hisp.dhis.mobile.model.PatientIdentifier;
import org.hisp.dhis.mobile.ui.Text;

public class BeneficiaryDetailView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "BeneficiaryDetailView";
    
    private NameBasedMIDlet nameBasedMIDlet;

    private Form beneficiaryDetailForm;

    private Command beneficiaryDetailBackCommand;

    private Activity selectedActivity;

    private Displayable previousDisplayable;

    public BeneficiaryDetailView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        Beneficiary beneficiary = selectedActivity.getBeneficiary();
        StringBuffer buffer = new StringBuffer();
        getBeneficiaryDetailForm().deleteAll();
        getBeneficiaryDetailForm().setTitle( beneficiary.getFullName() );
        buffer.append( "Name:  " );
        buffer.append( beneficiary.getFullName() );
        buffer.append( "\n" );
        buffer.append( "Age: " );
        buffer.append( beneficiary.getAge() );
        buffer.append( "\n" );
        buffer.append( generatePatientIdentifiers( beneficiary.getPatientIdentifier() ) );
        buffer.append( generateStaticPatientAttributes( beneficiary ) );
        buffer.append( generatePatientAtts( beneficiary.getAttsValues() ) );
        getBeneficiaryDetailForm().append( buffer.toString() );
        System.gc();
    }

    private String generatePatientIdentifiers( Vector identifiers )
    {
        if ( identifiers != null )
        {
            String identifierStr = "";
            for ( int i = 0; i < identifiers.size(); i++ )
            {
                identifierStr += ((PatientIdentifier) identifiers.elementAt( i )).getIdentifierType() + ": "
                    + ((PatientIdentifier) identifiers.elementAt( i )).getIdentifier() + "\n";
            }
            return identifierStr;
        }
        else
        {
            return "";
        }

    }

    private String generateStaticPatientAttributes( Beneficiary beneficiary )
    {
        String staticAtts = "";

        if ( beneficiary.getGender() != null )
        {
            if ( beneficiary.getGender().equals( "F" ) )
            {
                staticAtts += "Gender: Female" + "\n";
            }
            else
            {
                staticAtts += "Gender: Male" + "\n";
            }

        }
        if ( beneficiary.getBirthDate() != null )
        {
            String dob = beneficiary.getBirthDate().toString();
            staticAtts += "DOB: " + dob.substring( 4, 10 ) + " " + dob.substring( 23 );
        }
        if ( beneficiary.getDobType() != null )
        {
            if ( beneficiary.getDobType().charValue() == 'V' )
            {
                staticAtts += " (Verified)";
            }
            else if ( beneficiary.getDobType().charValue() == 'D' )
            {
                staticAtts += " (Defined)";
            }
            else
            {
                staticAtts += " (Approximated)";
            }
        }
        if ( beneficiary.getBloodGroup() != null )
        {
            staticAtts += "\nBlood Group: " + beneficiary.getBloodGroup() + "\n";
        }
        if ( beneficiary.getRegistrationDate() != null )
        {
            String regDate = beneficiary.getRegistrationDate().toString();
            staticAtts += "Registration Date: " + regDate.substring( 4, 10 ) + " " + regDate.substring( 23 ) + "\n";
        }

        return staticAtts;
    }

    private String generatePatientAtts( Vector atts )
    {
        String infos = "";
        for ( int i = 0; i < atts.size(); i++ )
        {
            infos += "\n" + atts.elementAt( i ) + "\n";
        }
        return infos;
    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Beneficiary Details" + " Screen...");
        this.prepareView();
        this.switchDisplayable( null, this.getBeneficiaryDetailForm() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.beneficiaryDetailBackCommand )
        {
            this.switchDisplayable( null, this.previousDisplayable );
        }

    }

    public Form getBeneficiaryDetailForm()
    {
        if ( beneficiaryDetailForm == null )
        {
            beneficiaryDetailForm = new Form( Text.DETAILS() );
            beneficiaryDetailForm.setCommandListener( this );
            beneficiaryDetailForm.addCommand( this.getBeneficiaryDetailBackCommand() );
        }
        return beneficiaryDetailForm;
    }

    public void setBeneficiaryDetailForm( Form beneficiaryDetailForm )
    {
        this.beneficiaryDetailForm = beneficiaryDetailForm;
    }

    public Command getBeneficiaryDetailBackCommand()
    {
        if ( beneficiaryDetailBackCommand == null )
        {
            beneficiaryDetailBackCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return beneficiaryDetailBackCommand;
    }

    public void setBeneficiaryDetailBackCommand( Command beneficiaryDetailBackCommand )
    {
        this.beneficiaryDetailBackCommand = beneficiaryDetailBackCommand;
    }

    public Activity getSelectedActivity()
    {
        return selectedActivity;
    }

    public void setSelectedActivity( Activity selectedActivity )
    {
        this.selectedActivity = selectedActivity;
    }

    public Displayable getPreviousDisplayable()
    {
        return previousDisplayable;
    }

    public void setPreviousDisplayable( Displayable previousDisplayable )
    {
        this.previousDisplayable = previousDisplayable;
    }

}
