package org.hisp.dhis.mobile.connection.task;

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
import java.io.IOException;

import javax.microedition.lcdui.Displayable;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.MobileWrapper;
import org.hisp.dhis.mobile.recordstore.DataSetRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.RecordStoreUtil;

public class UpdateContentLanguageTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UpdateContentLanguageTask";
    
    private MobileWrapper mobileWrapper;

    public UpdateContentLanguageTask()
    {
        this.mobileWrapper = new MobileWrapper();
    }

    public void run()
    {

        DataInputStream inputStream = null;
        Displayable nextDisplayable = null;
        try
        {
            inputStream = this.download();
            // SettingRecordStore settingRecordStore = new SettingRecordStore();
            mobileWrapper.deSerialize( inputStream );

            // String localesCSV = "";
            //
            // for ( int i = 0; i < mobileWrapper.getLocales().size(); i++ )
            // {
            // localesCSV += "," + mobileWrapper.getLocales().elementAt( i );
            // }

            // localesCSV = localesCSV.substring( localesCSV.indexOf( "," ) + 1
            // );
            //
            // settingRecordStore.put( SettingRecordStore.LOCALE, localesCSV );

            if ( ConnectionManager.getDhisMIDlet() instanceof FacilityMIDlet )
            {
                FacilityMIDlet midlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();
                nextDisplayable = midlet.getDataSetListView().getDataSetList();

                RecordStoreUtil.clearRecordStore( DataSetRecordStore.DATASET_DB );
                //DataSetRecordStore.saveDataSets( mobileWrapper.getDataSets() );

            }
            else if ( ConnectionManager.getDhisMIDlet() instanceof NameBasedMIDlet )
            {
                NameBasedMIDlet midlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
                nextDisplayable = midlet.getActivityMainMenuView().getActivityMainMenuList();

                RecordStoreUtil.clearRecordStore( ProgramRecordStore.PROGRAM_DB );
                //ProgramRecordStore.savePrograms( mobileWrapper.getPrograms() );
            }

            ConnectionManager
                .getDhisMIDlet()
                .getSettingView()
                .switchDisplayable( Alerts.getInfoAlert( Text.MESSAGE(), Text.SETTING_SAVED_MESSAGE() ),
                    nextDisplayable );

        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
//        catch ( RecordStoreException e )
//        {
//            e.printStackTrace();
//            DHISMIDlet.debug( e.getMessage() );
//        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            System.gc();
        }

    }

}
