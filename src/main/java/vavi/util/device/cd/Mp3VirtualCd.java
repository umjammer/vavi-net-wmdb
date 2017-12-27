/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.device.cd;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vavi.util.tag.TagException;
import vavi.util.tag.id3.MP3File;


/**
 * Mp3VirtualCd.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/05/29 umjammer initial version <br>
 */
public class Mp3VirtualCd implements CD {

    /** */
    List<MP3File> tracks = new ArrayList<>();

    /**
     * @param path directory
     */
    public Mp3VirtualCd(String path) throws IOException {
        File[] files = new File(path).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".mp3");
            }
        });
        for (File file : files) {
            try {
                MP3File mp3file = new MP3File(file.getPath());
                tracks.add(mp3file);
            } catch (TagException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public int getTrackLengthAt(int track) {
        return (int) (long) (Long) tracks.get(track).getProperty("Length") * 75;
    }

    public int[] getTrackStarts() {
        int[] starts = new int[getTracksCount()];
        int total = 0;
        for (int i = 0; i < starts.length; i++) {
            int length = getTrackLengthAt(i);
            starts[i] = total + length;
            total += length;
        }
        return starts;
    }

    public int getLength() {
        int total = 0;
        for (int i = 0; i < getTracksCount(); i++) {
            total += getTrackLengthAt(i);
        }
        return total;
    }

    public int getTracksCount() {
        return tracks.size();
    }
}

/* */
