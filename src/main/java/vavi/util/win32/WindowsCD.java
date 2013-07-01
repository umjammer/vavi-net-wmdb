/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.win32;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vavi.util.device.cd.CD;


/**
 * .cda file から CD 情報を得るクラスです．
 * 
 * TODO 最適化しろ!
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020424 nsano initial version <br>
 *          0.01 031220 nsano clean imports <br>
 */
public class WindowsCD implements CD {

    /** */
    private List<CDDA> tracks = new ArrayList<CDDA>();

    /** TODO */
    public int getTrackLengthAt(int track) {
        return tracks.get(track).getLengthMSF().toFrames();
    }

    /** TODO */
    public int[] getTrackStarts() {
        int[] begins = new int[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            begins[i] = tracks.get(i).getBeginMSF().toFrames();
        }
        return begins;
    }

    /** TODO */
    public int getLength() {
        int length = 0;
        for (int i = 0; i < tracks.size(); i++) {
            length += tracks.get(i).getLengthMSF().toFrames();
        }
        return length;
    }

    /** TODO */
    public int getTracksCount() {
        return tracks.size();
    }

    /**
     * @param drive ie "d:", "e:"
     */
    public WindowsCD(String drive) throws IOException {

        if (drive == null) {
            drive = "d:";
        }

        for (int i = 1; ; i++) {

//System.err.println(drive + System.getProperty("file.separator") + "Track" + toInt2(i) + "." + CDDA.getExtention());

            File file = new File(drive +
                                 System.getProperty("file.separator") +
                                 "Track" + toInt2(i) +
                                 "." + CDDA.getExtention());
            if (!file.exists()) {
                break;
            }

            FileInputStream is = new FileInputStream(file);
            tracks.add(CDDA.class.cast(CDDA.readFrom(is)));
        }

        if (tracks.size() == 0) {
            throw new FileNotFoundException(drive);
        }
    }

    /** */
    private static final String toInt2(int i) {
        String s = "0" + i;
        return s.substring(s.length() - 2);
    }
}

/* */
