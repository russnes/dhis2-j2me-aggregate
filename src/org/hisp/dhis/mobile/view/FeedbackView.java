package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
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

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class FeedbackView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "FeedbackView";

    private Form feedbackForm;

    private Command backCommand;

    private Command nextCommand;

    private TextField subjectTextField;

    private String subject;

    private FacilityMIDlet facilityMIDlet;

    public FeedbackView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.feedbackForm = null;
        this.backCommand = null;
        this.nextCommand = null;
        System.gc();
    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getFeedbackForm() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.backCommand )
        {
            dhisMIDlet.getMessagingMenuView().showView();
        }
        else if ( command == this.nextCommand )
        {
            try
            {
                StringRecordStore.saveString( this.getSubject() );
                dhisMIDlet.getFeedbackContentView().showView();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public Form getFeedbackForm()
    {
        if ( feedbackForm == null )
        {
            feedbackForm = new Form( Text.FEEDBACK() );
            feedbackForm.addCommand( getBackCommand() );
            feedbackForm.addCommand( getNextCommand() );

            feedbackForm.append( this.getSubjectTextField() );
            feedbackForm.setCommandListener( this );

        }
        return feedbackForm;
    }

    public void setFeedbackForm( Form feedbackForm )
    {
        this.feedbackForm = feedbackForm;
    }

    public Command getNextCommand()
    {
        if ( nextCommand == null )
        {
            nextCommand = new Command( Text.NEXT(), Command.SCREEN, 0 );
        }
        return nextCommand;
    }

    public void setNextCommand( Command nextCommand )
    {
        this.nextCommand = nextCommand;
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

    public TextField getSubjectTextField()
    {
        if ( subjectTextField == null )
        {
            subjectTextField = new TextField( Text.ENTER_SUBJECT(), "", 100, TextField.ANY );
        }
        return subjectTextField;
    }

    public void setSubjectTextField( TextField subjectTextField )
    {
        this.subjectTextField = subjectTextField;
    }

    public String getSubject()
    {
        if ( subject == null )
        {
            subject = getSubjectTextField().getString();
        }
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

}
