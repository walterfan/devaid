package com.github.walterfan.util;


/*
   Copyright (c) 2011 IETF Trust and the persons identified
   as authors of the code.  All rights reserved.

   Redistribution and use in source and binary forms, with
   or without modification, is permitted pursuant to, and subject
   to the license terms contained in, the Simplified BSD License
   set forth in Section 4.c of the IETF Trust's Legal Provisions
   Relating to IETF Documents (http://trustee.ietf.org/license-info).
*/

/**
 * Calculates the audio level of specific samples of a signal
 * relative to overload.
 */
public class AudioLevelCalculator
{

    /**
     * Calculates the audio level of a signal with specific
     * <tt>samples</tt>.
     *
     * @param samples  the samples whose audio level we need to
     * calculate.  The samples are specified as an <tt>int</tt>
     * array starting at <tt>offset</tt>, extending <tt>length</tt>
     * number of elements, and each <tt>int</tt> element in the
     * specified range representing a sample whose audio level we
     * need to calculate.  Though a sample is provided in the
     * form of an <tt>int</tt> value, the sample size in bits
     * is determined by the caller via <tt>overload</tt>.
     *
     * @param offset  the offset in <tt>samples</tt> at which the
     * samples start.
     *
     * @param length  the length of the signal specified in
     * <tt>samples<tt>, starting at <tt>offset</tt>.
     *
     * @param overload  the overload (point) of <tt>signal</tt>.
     * For example, <tt>overload</tt> can be {@link Byte#MAX_VALUE}
     * for 8-bit signed samples or {@link Short#MAX_VALUE} for
     * 16-bit signed samples.
     *
     * @return  the audio level of the specified signal.
     */
    public static int calculateAudioLevel(
        int[] samples, int offset, int length,
        int overload)
    {
        /*
         * Calculate the root mean square (RMS) of the signal.
         */
        double rms = 0;

        for (; offset < length; offset++)
        {
            double sample = samples[offset];

            sample /= overload;
            rms += sample * sample;
        }
        rms = (length == 0) ? 0 : Math.sqrt(rms / length);

        /*
         * The audio level is a logarithmic measure of the
         * rms level of an audio sample relative to a reference
         * value and is measured in decibels.
         */
        double db;

        /*
         * The minimum audio level permitted.
         */
        final double MIN_AUDIO_LEVEL = -127;

        /*
         * The maximum audio level permitted.
         */
        final double MAX_AUDIO_LEVEL = 0;

        if (rms > 0)
        {
            /*
             * The "zero" reference level is the overload level,
             * which corresponds to 1.0 in this calculation, because
             * the samples are normalized in calculating the RMS.
             */
            db = 20 * Math.log10(rms);

            /*
             * Ensure that the calculated level is within the minimum
             * and maximum range permitted.
             */
            if (db < MIN_AUDIO_LEVEL)
                db = MIN_AUDIO_LEVEL;
            else if (db > MAX_AUDIO_LEVEL)
                db = MAX_AUDIO_LEVEL;
        }
        else
        {
            db = MIN_AUDIO_LEVEL;
        }

        return (int)Math.round(db);
    }
}