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

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.view.LoginView;

public class LoginTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "LoginTask";
    
    public static OrgUnit orgUnit;

    public static DataInputStream inputStream;

    public static String updateNewVersionUrl;

    public static double serverVersion;

    public static Vector orgUnitVector;

    /**
     * 
     */
    public LoginTask()
    {
        this.orgUnit = new OrgUnit();
        this.orgUnitVector = new Vector();
    }

    /**
     * 
     */
    public void run()
    {
        try
        {
            inputStream = this.download();
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            String clientVersion = settingRecordStore.get( SettingRecordStore.CLIENT_VERSION );
            
            LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Client Version: " + clientVersion);

            if ( clientVersion == "" )
            {
                LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Setting Client Version to: " + "2.8");
                settingRecordStore.put( SettingRecordStore.CLIENT_VERSION, "2.8" );
                settingRecordStore.save();
            }
            
            serverVersion = getServerVersion();
            
            boolean isNewVersionAvailable = orgUnit.checkNewVersion( serverVersion );

            if ( isNewVersionAvailable == true )
            {
                ConnectionManager.getDhisMIDlet().getUpdateNewVersionView().showView();
            }
            else
            {
                handleLogIn( inputStream );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network,Authentication," + CLASS_TAG, e );
            LoginView loginView = ConnectionManager.getDhisMIDlet().getLoginView();
            loginView.resetTextField();
            loginView
                .switchDisplayable( Alerts.getErrorAlert( Text.ERROR(), e.getMessage() ), loginView.getLoginForm() );
        }
        finally
        {
            try
            {
                if ( inputStream != null )
                    inputStream.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            System.gc();
        }
    }

    public static void handleLogIn( DataInputStream dis )
        throws Exception
    {
        try
        {
            int size = dis.readInt();

            if ( size == 0 )
            {
                throw new IOException( Text.NO_ORGUNIT_ERROR() );
            }
            else
            {
                for ( int i = 0; i < size; i++ )
                {
                    OrgUnit orgUnit = new OrgUnit();
                    orgUnit.deSerialize( dis );
                    orgUnitVector.addElement( orgUnit );
                    OrgUnitRecordStore.saveOrgUnit( orgUnit );
                }
                ConnectionManager.getDhisMIDlet().getPinView().preparePinFormForFirstTime();
                ConnectionManager.getDhisMIDlet().getPinView().showView();
                //ConnectionManager.downloadAllResource( orgUnitVector );
            }
        }
        finally
        {
            try
            {
                if ( dis != null )
                    dis.close();
                System.gc();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
                LogMan.log( "Network,Login," + CLASS_TAG, ioe );
            }

        }

    }

    public OrgUnit getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( OrgUnit orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    public static DataInputStream getInputStream()
    {
        return inputStream;
    }

    public static void setInputStream( DataInputStream inputStream )
    {
        LoginTask.inputStream = inputStream;
    }

    /**
     * 
     * @return
     */
    public double getServerVersion()
    {
        double result = 0;
        try
        {
            result = inputStream.readDouble();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Server Version: " + result);
        
        return result;
    }
    
    

}
