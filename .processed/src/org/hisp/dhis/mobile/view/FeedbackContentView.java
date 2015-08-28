package org.hisp.dhis.mobile.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Text;

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

public class FeedbackContentView
    extends AbstractView
    implements CommandListener

{

    private TextBox feedbackTextBox;

    private FacilityMIDlet facilityMIDlet;

    private Command backCommand;

    private Command sendCommand;

    private String subject;

    private Message message;

    public FeedbackContentView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.backCommand = null;
        this.feedbackTextBox = null;
        this.sendCommand = null;
        System.gc();
    }

    public void showView()
    {
        prepareView();

        this.switchDisplayable( null, this.getFeedbackTextBox() );

    }

    public void commandAction( Command command, Displayable displayable )
    {

        if ( command == this.backCommand )
        {
            dhisMIDlet.getFeedbackView().showView();
        }
        else if ( command == this.sendCommand )
        {

            try
            {
                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getSendFeedbackUrl() );
                ConnectionManager.sendFeedback( getMessage() );
            }
            catch ( Exception e )
            {

                e.printStackTrace();
            }

        }

    }

    public TextBox getFeedbackTextBox()
    {
        if ( feedbackTextBox == null )
        {
            feedbackTextBox = new TextBox( "Enter feedback message", "", 1000, 0 );
            feedbackTextBox.addCommand( getBackCommand() );
            feedbackTextBox.addCommand( getSendCommand() );

            feedbackTextBox.setCommandListener( this );
        }
        return feedbackTextBox;
    }

    public void setFeedbackTextBox( TextBox feedbackTextBox )
    {
        this.feedbackTextBox = feedbackTextBox;
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

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND(), Command.SCREEN, 0 );
        }
        return sendCommand;
    }

    public void setSendCommand( Command sendCommand )
    {
        this.sendCommand = sendCommand;
    }

    public String getSubject()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        if ( subject == null )
        {
            subject = StringRecordStore.load();
        }
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public Message getMessage()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        if ( message == null )
        {
            message = new Message();
            message.setSubject( this.getSubject() );
            message.setText( this.getFeedbackTextBox().getString() );

        }
        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

}
