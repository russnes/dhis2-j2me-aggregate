package org.hisp.dhis.mobile.util;

import org.hisp.dhis.mobile.log.LogMan;

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

public class MemoryMonitor
{
    /**
     * 
     */
    private static final String CLASS_TAG = "MemoryMonitor";   
    
    /**
     * 
     */
    public static void outputMemory()
    {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        int usedMemoryPercentage = Utils.getPercentage( usedMemory, totalMemory );

        LogMan.log( LogMan.DEV, "Memory," + CLASS_TAG, convertToReadableBytes( usedMemory ) + " / "
            + convertToReadableBytes( totalMemory ) + " " + convertToTextProgressBar( usedMemory, totalMemory ) + " "
            + usedMemoryPercentage + "%" );
    }

    /**
     * 
     * @param bytes
     * @return
     */
    public static String convertToReadableBytes( long bytes )
    {
        double bytesWithDecimals = (double) bytes;
        String unit = "B";
        if ( bytesWithDecimals >= 1024 )
        {
            bytesWithDecimals /= (double) 1024;
            unit = "KB";
        }
        if ( bytesWithDecimals >= 1024 )
        {
            bytesWithDecimals /= (double) 1024;
            unit = "MB";
        }
        if ( bytesWithDecimals >= 1024 )
        {
            bytesWithDecimals /= (double) 1024;
            unit = "GB";
        }
        return Utils.convertTwoDecimalsOnly( bytesWithDecimals ) + " " + unit;
    }

    /**
     * 
     * @param initialValue
     * @param maxValue
     * @return
     */
    public static String convertToTextProgressBar( long initialValue, long maxValue )
    {
        String progressBar = "[";

        int percentage = (int) (((double) initialValue / (double) maxValue) * (double) 20);
        for ( int i = 0; i <= 20; i++ )
        {
            if ( i <= percentage )
            {
                progressBar += "|";
            }
            else
            {
                progressBar += "-";
            }
        }
        progressBar += "]";
        return progressBar;
    }    
}
