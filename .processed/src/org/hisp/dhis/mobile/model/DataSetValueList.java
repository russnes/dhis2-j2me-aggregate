package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class DataSetValueList
    extends Model
{
    private Vector dataSetValues;

    public DataSetValueList()
    {
    }

    public Vector getDataSetValueList()
    {
        return dataSetValues;
    }

    public void setDataSetValueList( Vector dataSetValues )
    {
        this.dataSetValues = dataSetValues;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( dataSetValues != null )
        {
            dout.writeInt( dataSetValues.size() );
            for ( int i = 0; i < dataSetValues.size(); i++ )
            {
                ((DataSetValue) dataSetValues.elementAt( i )).serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int size = 0;
        size = dataInputStream.readInt();

        if ( size > 0 )
        {

            dataSetValues = new Vector();

            for ( int i = 0; i < size; i++ )
            {
                DataSetValue dataSetValue = new DataSetValue();
                dataSetValue.deSerialize( dataInputStream );
                dataSetValues.addElement( dataSetValue );
            }

        }
    }

}
