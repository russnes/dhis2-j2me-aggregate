package org.hisp.dhis.mobile.recordstore.filter;

import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.Interpretation;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class InterpretationFiler implements RecordFilter
{
    private static final String CLASS_TAG = "InterpretationFiler";

    private Interpretation interpretation;

    public InterpretationFiler( Interpretation interpretation )
    {
        this.interpretation = interpretation;
    }

    public Interpretation getInterpretation()
    {
        return interpretation;
    }

    public void setInterpretation( Interpretation interpretation )
    {
        this.interpretation = interpretation;
    }

    public boolean matches( byte[] suspect )
    {
        if ( interpretation == null )
        {
            return false;
        }
        else
        {
            try
            {
                Interpretation suspectInterpretation = new Interpretation();
                SerializationUtil.deSerialize( suspectInterpretation, suspect );
                return interpretation.getText() == suspectInterpretation.getText();

            }
            catch ( Exception e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        return false;
    }

}
