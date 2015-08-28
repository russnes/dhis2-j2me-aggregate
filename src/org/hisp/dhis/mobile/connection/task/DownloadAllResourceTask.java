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
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.MobileWrapper;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.ActivityRecordStore;
import org.hisp.dhis.mobile.recordstore.DataSetRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.SMSCommandRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

public class DownloadAllResourceTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "DownloadAllResourceTask";

    private MobileWrapper mobileWrapper;

    private Vector orgUnitVector;

    public DownloadAllResourceTask( Vector orgUnitVector )
    {
        this.mobileWrapper = new MobileWrapper();
        this.orgUnitVector = orgUnitVector;
    }

    public void run()
    {
        DHISMIDlet.setDownloading( true );

        for ( int i = 0; i < orgUnitVector.size(); i++ )
        {
            OrgUnit orgUnit = (OrgUnit) orgUnitVector.elementAt( i );
            this.processDownload( orgUnit );
        }

        DHISMIDlet.setDownloading( false );

        if ( ConnectionManager.getDhisMIDlet() instanceof FacilityMIDlet )
        {
            FacilityMIDlet facilityMidlet = (FacilityMIDlet) ConnectionManager.getDhisMIDlet();
            facilityMidlet.getOrgUnitSelectView().setOrgUnitVector( orgUnitVector );
            if ( orgUnitVector.size() > 1 )
            {
                facilityMidlet.getOrgUnitSelectView().showView();
            }
            else
            {
                facilityMidlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( 0 ) );

                try
                {
                    ConnectionManager.getDhisMIDlet().getPinView().checkOneOrManyDataset();
                }
                catch ( Exception e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if ( mobileWrapper.getDataSets() != null )
            {
                try
                {
                    SettingRecordStore settingRecordStore = new SettingRecordStore();
                    for ( int i = 0; i < orgUnitVector.size(); i++ )
                    {
                        OrgUnit orgUnit = (OrgUnit) orgUnitVector.elementAt( i );
                        ConnectionManager.init( ConnectionManager.getDhisMIDlet(),
                            orgUnit.getDownloadFacilityReportUrl(),
                            settingRecordStore.get( SettingRecordStore.USERNAME ),
                            settingRecordStore.get( SettingRecordStore.PASSWORD ), DHISMIDlet.DEFAULT_LOCALE, orgUnit );
                        ConnectionManager.downloadDataSetValues();
                    }
                }
                catch ( RecordStoreException e )
                {
                    e.printStackTrace();
                }

            }
        }
        else if ( ConnectionManager.getDhisMIDlet() instanceof NameBasedMIDlet )
        {
            NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
            if ( orgUnitVector.size() > 1 )
            {
                nameBasedMIDlet.getOrgUnitSelectView().setOrgUnitVector( orgUnitVector );
                nameBasedMIDlet.getOrgUnitSelectView().showView();
            }
            else
            {
                nameBasedMIDlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( 0 ) );
                nameBasedMIDlet.getActivityMainMenuView().showView();
            }

        }

    }

    private void processDownload( OrgUnit orgUnit )
    {
    	LogMan.log(LogMan.DEBUG, "Network," + CLASS_TAG + ",processDownload(OrgUnit)", "Method Call. orgUnit=" + orgUnit);
    	
        DataInputStream inputStream = null;
        ConnectionManager.setUrl( orgUnit.getDownloadAllUrl() );
        try
        {
            inputStream = this.download();            
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            this.handleDownloadAllResource( inputStream );
            String localesCSV = "";

            for ( int i = 0; i < mobileWrapper.getLocales().size(); i++ )
            {
                localesCSV += "," + mobileWrapper.getLocales().elementAt( i );
            }

            localesCSV = localesCSV.substring( localesCSV.indexOf( "," ) + 1 );

            settingRecordStore.put( SettingRecordStore.LOCALE, localesCSV );

            if ( ConnectionManager.getDhisMIDlet() instanceof FacilityMIDlet )
            {
                DataSetRecordStore.saveDataSets( mobileWrapper.getDataSets(), orgUnit );
                SMSCommandRecordStore.saveSMSCommands( mobileWrapper.getSmsCommands() );
            }
            else if ( ConnectionManager.getDhisMIDlet() instanceof NameBasedMIDlet )
            {
                ProgramRecordStore.savePrograms( mobileWrapper.getPrograms() );
                ActivityRecordStore.saveActivities( mobileWrapper.getActivityPlan().getActivities(), orgUnit );
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
            LogMan.log( "RMS," + CLASS_TAG, e );
        }
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

    private void handleDownloadAllResource( DataInputStream inputStream )
        throws IOException
    {
    	LogMan.log(LogMan.DEBUG, "Network," + CLASS_TAG + ",handleDownloadAllResource(DataInputStream)", "Method Call. inputStream=" + inputStream);
    	
        mobileWrapper.deSerialize( inputStream );
    }

    public MobileWrapper getMobileWrapper()
    {
        return mobileWrapper;
    }

    public void setMobileWrapper( MobileWrapper mobileWrapper )
    {
        this.mobileWrapper = mobileWrapper;
    }

    public Vector getOrgUnitVector()
    {
        return orgUnitVector;
    }

    public void setOrgUnitVector( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }
}
