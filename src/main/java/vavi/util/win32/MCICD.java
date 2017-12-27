/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.win32;

import java.io.IOException;
import vavi.util.device.cd.CD;
import vavi.util.win32.MSF;


/**
 * CD Information using MCI.
 * 
 * @author Keith D. Smith
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 020415 nsano porting <br>
 *          1.00 020424 nsano complete <br>
 *          1.01 020424 nsano refine <br>
 */
public class MCICD implements CD {

    /** number of tracks on current disc */
    private int numTracks;
    /** total length of current disc in "mm:ss:ff" format */
    private String cdLength;
    /** start times of each track in "mm:ss:ff" format */
    private String[] trackStarts;
    /** track lengths of each track in "mm:ss:ff" format */
    private String[] trackLengths;

    /** */
    public MCICD() throws IOException {
        init(null);
    }

    /**
     * @param    drive    ie "d:", "e:"
     */
    public MCICD(String drive) throws IOException {
        init(drive);
    }

    /** */
    private void init(String drive) throws IOException {
        if (drive == null) {
            MCICDControl.open();
        } else {
            MCICDControl.open(drive);
        }
        MCICDControl.listDevices();
//      MCICDControl.stop();
//      MCICDControl.refresh();

        numTracks   = MCICDControl.getNumTracks();
        cdLength    = MCICDControl.getCDLength();
//System.err.println("cd length: " + cdLength);
        trackStarts = MCICDControl.getTrackStartTimes();

        trackLengths = new String[numTracks];

        for (int i = 0; i < numTracks; i++) {
            trackLengths[i] = MCICDControl.getTrackLength(i + 1);
        }

//      MCICDControl.stop();
        MCICDControl.close();
    }

    /** */
    public int getTrackLengthAt(int track) {
        return new MSF(trackLengths[track]).toFrames();
    }

    /** */
    public int[] getTrackStarts() {
        int[] ret = new int[trackStarts.length];
        for (int i = 0; i < trackStarts.length; i++) {
            ret[i] = new MSF(trackStarts[i]).toFrames();
        }
        return ret;
    }

    /** */
    public int getLength() {
        return new MSF(cdLength).toFrames();
    }

    /** */
    public int getTracksCount() {
        return numTracks;
    }
}

/* */
