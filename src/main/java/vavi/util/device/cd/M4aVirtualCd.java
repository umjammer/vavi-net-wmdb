/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.device.cd;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import vavi.util.Debug;
import vavi.util.tag.TagException;
import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;
import vavi.util.tag.mp4.____;


/**
 * M4aVirtualCd. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/05/29 umjammer initial version <br>
 */
public class M4aVirtualCd implements CD {

    /** */
    int[] tracks;

    /** */
    int total;
    
    /**
     * @param path directory
     */
    public M4aVirtualCd(String path) throws IOException {
        File[] files = new File(path).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".m4a");
            }
        });
        if (files == null) {
            throw new IllegalArgumentException("no m4a files in teh directory.");
        }
        String cddb = null;
        for (File file : files) {
            MP4File mp4file = new MP4File(file.getPath());
            cddb = findCDDB(mp4file);
            if (cddb != null) {
                break;
            }
        }
        // 0011E913+344082+19+150+14674+31196+51256+69901+89203+107592+127125+142847+159686+175556+192008+209797+229751+247597+264348+281514+300174+319631
        // 18+0FD200A8C19B77F67DF748F805FBBBD5+32164
        String[] parts = cddb.split("\\+");
        if (parts.length == 3) {
            this.total = Integer.valueOf(parts[2]);
System.err.println("total: " + total);
            int tracks = Integer.valueOf(parts[0]);
            this.tracks = new int[tracks];
System.err.println("tracks: " + tracks);
        } else {
            this.total = Integer.valueOf(parts[1]);
System.err.println("total: " + total);
            int tracks = Integer.valueOf(parts[2]);
            this.tracks = new int[tracks];
System.err.println("tracks: " + tracks);
            for (int i = 0; i < tracks; i++) {
                int length = Integer.valueOf(parts[i + 3]);
System.err.println("track" + (i + 1) + ": " + length);
                this.tracks[i] = length; 
            }
        }
    }

    String findCDDB(MP4File mp4File) {
        try {
            MP4Tag mp4Tag = MP4Tag.class.cast(mp4File.getTag());
            @SuppressWarnings("unchecked")
            List<MP4Tag> results = List.class.cast(mp4Tag.getTag("----"));
            for (Object o : results) {
                if (____.class.isInstance(o)) {
                    ____ box = ____.class.cast(o);
Debug.println(box);
                    if (box.getName().equals("iTunes_CDDB_1")) {
                        byte[] data = box.getData();
                        return new String(data, 4, data.length - 4); // TODO fixed value 4
                    }
                }
            }
        } catch (TagException e) {
        }
        return null;
    }

    public int getTrackLengthAt(int track) {
        return tracks[track];
    }

    public int[] getTrackStarts() {
        return tracks;
    }

    public int getLength() {
        return total;
    }

    public int getTracksCount() {
        return tracks.length;
    }
}

/* */
