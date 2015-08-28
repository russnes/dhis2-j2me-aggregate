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
import java.util.Vector;


public class DataSetList
    extends Model
{
    private Vector addedDataSets;

    private Vector deletedDataSets;

    private Vector modifiedDataSets;
    
    private Vector currentDataSets;

    public DataSetList()
    {
    }

    public Vector getAddedDataSets()
    {
        return addedDataSets;
    }

    public void setAddedDataSets( Vector addedDataSets )
    {
        this.addedDataSets = addedDataSets;
    }

    public Vector getDeletedDataSets()
    {
        return deletedDataSets;
    }

    public void setDeletedDataSets( Vector deletedDataSets )
    {
        this.deletedDataSets = deletedDataSets;
    }

    public Vector getModifiedDataSets()
    {
        return modifiedDataSets;
    }

    public void setModifiedDataSets( Vector modifiedDataSets )
    {
        this.modifiedDataSets = modifiedDataSets;
    }

    public Vector getCurrentDataSets()
    {
        return currentDataSets;
    }

    public void setCurrentDataSets( Vector currentDataSets )
    {
        this.currentDataSets = currentDataSets;
    }
    
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( addedDataSets != null )
        {
            dout.writeInt( addedDataSets.size() );
            for ( int i = 0; i <  addedDataSets.size(); i++){
                ((DataSet)addedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( deletedDataSets != null )
        {
            dout.writeInt( deletedDataSets.size() );
            for ( int i = 0; i <  deletedDataSets.size(); i++){
                ((DataSet)deletedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( modifiedDataSets != null )
        {
            dout.writeInt( modifiedDataSets.size() );
            for ( int i = 0; i <  modifiedDataSets.size(); i++){
                ((DataSet)modifiedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( currentDataSets != null )
        {
            dout.writeInt( currentDataSets.size() );
            for ( int i = 0; i <  currentDataSets.size(); i++){
                ((DataSet)currentDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
    }
    
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int temp = 0;
        temp = dataInputStream.readInt();
        if(temp > 0){
            addedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                addedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            deletedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                deletedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            modifiedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                modifiedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            currentDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                currentDataSets.addElement( dataSet );
            }
        }
    }
}
