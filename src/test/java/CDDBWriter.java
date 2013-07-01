/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import vavi.util.cddb.CDDBUtil;
import vavi.util.device.cd.CD;


/**
 * CDDBWriter.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040606 nsano initial version <br>
 */
public class CDDBWriter {

    /** */
    private PrintWriter pw;

    /** */
    public CDDBWriter(Writer writer) {
        pw = new PrintWriter(writer);
    }

    /** */
    private static final String NAME = "wmdb";
    /** */
    private static final String VERSION = "v0.01";
    /** */
    private static final String COPY = "Copyright (c) Vavi.";

    /** */
    public void writeCDDB(CD cd, Album album) throws IOException {

        String uid          = CDDBUtil.getUID(cd);
        int[]  trackOffsets = cd.getTrackStarts();
        int    discLength   = CDDBUtil.getLengthAsSecond(cd);

        //----

        pw.println("# xmcd");
        pw.println("#");
        pw.println("# Track frame offsets:");
        for (int i = 0; i < trackOffsets.length; i++) {
            pw.println("#       " + trackOffsets[i]);
        }
        pw.println("#");
        pw.println("# Disc length: " + discLength + " seconds");
        pw.println("#");
        pw.println("# Revision:" + 0);
        pw.println("# Processed by: " + NAME + " " + VERSION + " " + COPY);
        pw.println("# Submitted via: " + NAME + " " + VERSION);
        pw.println("#");
        pw.println("DISCID=" + uid);
        pw.println("DTITLE=" + album.getArtist() + " / " +
                               album.getTitle());
        pw.println("DYEAR=");
        pw.println("DGENRE=" + album.getGenre());
        for (int i = 0; i < album.getTracks().size(); i++) {
            Track track = ((List<Track>) album.getTracks()).get(i);
            pw.println("TTITLE" + i + "=" + track.getTitle());
        }
        pw.println("EXTD=");
        for (int i = 0; i < album.getTracks().size(); i++) {
            pw.println("EXTT" + i + "=");
        }
        pw.println("PLAYORDER=");
        pw.println(".");
    }

    /** */
    public void flush() throws IOException {
        pw.flush();
    }

    /** */
    public void close() throws IOException {
        pw.close();
    }
}

/* */
