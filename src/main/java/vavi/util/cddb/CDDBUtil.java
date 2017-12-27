/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cddb;

import vavi.util.device.cd.CD;
import vavi.util.win32.MSF;


/**
 * UIDGenerator generates the "unique" IDs used by
 * CDDB systems to identify individual discs.  It
 * does so using the algorithm found in the CDDB
 * Specification found at one of the following:
 * <p>
 * <a href="ftp://ftp.netcom.com/pub/sc/scherf/cddb-docs/cddb.howto">
 * ftp://ftp.netcom.com/pub/sc/scherf/cddb-docs/cddb.howto
 * </a><br>
 * <a href="http://www.cddb.com">
 * http://www.cddb.com
 * </a>
 * <p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 020424 nsano initial version <br>
 *          1.00 020424 nsano separate from CDDB <br>
 *          1.10 020504 nsano independent of CD <br>
 */
public final class CDDBUtil {

    /** */
    private CDDBUtil() {
    }

    /**
     * Generates a UID based on the information stored in 'toc'
     *
     * @param toc CDDBStruct containing track/lead-out info
     * @return  UID as 8-character hex String
     */
    private static String generateUID(MSF[] toc) {
        // NOTE: toc includes songs plus the lead-out (track 0xAA)
        int numTracks = toc.length - 1;
        int discLengthInSeconds = 0;
        int n = 0;
        // for each SONG,
        // get its offset in seconds and sum up the digits
        // then add to running total
        for (int i = 0; i < numTracks; i++) {
            n += cddbSum(toc[i].min * 60 + toc[i].sec);
        }

        // compute the length of the disc (in seconds)
        // by taking the start time
        // of the lead-out track (in seconds) and subtracting from this
        // value the start time of the first track (in seconds)
        discLengthInSeconds =
            ((toc[numTracks].min * 60) + toc[numTracks].sec) -
            ((toc[0].min * 60) + toc[0].sec);
        // now create the UID as a 32-bit integer
        int value = ((n % 0xFF) << 24 | discLengthInSeconds << 8 | numTracks);
        // convert to hex String
        String ret =  Integer.toHexString(value);
        // pad short IDs with zeros
        while (ret.length() < 8) {
            ret = "0" + ret;
        }
        return ret;
    }

    /**
     * Sums the digits of n
     *
     * @param n Number whose digits are to be summed
     * @return  The sum of the digits
     */
    private static int cddbSum(int n) {
        int ret = 0;
        while (n > 0) {
            ret += (n % 10);
            n /= 10;
        }
        return ret;
    }

    /**
     * Generates the CDDB UID for the current disc
     *
     * @return  The 8-character, hex string representing
     *      the UID of the current disc
     */
    public static String getUID(CD cd) {
        // make an array of msf structs for all tracks + lead-out
        MSF[] toc = new MSF[cd.getTracksCount() + 1];

        // first do each song
        for (int i = 0; i < cd.getTracksCount(); i++) {
            toc[i] = new MSF(cd.getTrackStarts()[i]);
        }

        // now for the lead-out
        // compute it's start time
        // by taking the start time of the last track
        // and adding to it its length
        int lastTrack = cd.getTracksCount() - 1;
        int lengthInFrames = cd.getTrackLengthAt(lastTrack);
        int startTimeInFrames = toc[lastTrack].toFrames();
        int leadOutStartTimeInFrames = startTimeInFrames + lengthInFrames;
        toc[toc.length - 1] = new MSF(leadOutStartTimeInFrames);

        // finally, get UID
        return generateUID(toc);
    }

    /** */
    public static int getLengthAsSecond(CD cd) {
        return cd.getLength() / 75;
    }
}

/* */
