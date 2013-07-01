/*
 * Copyright (c) 2003 by Naohide Sano, All Rights Reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vavi.util.CharNormalizerJa;


/**
 * Album.
 *
 * @author	<a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version	0.00	031221	nsano	initial version <br>
 */
public class Album {

    /** */
    private String title;

    /** */
    public void setTitle(String title) {
        this.title = CharNormalizerJa.ToHalfAns2.normalize(title);
    }

    /** */
    public String getTitle() {
        return title;
    }

    /** */
    private String artist;

    /** */
    public void setArtist(String artist) {
        this.artist = CharNormalizerJa.ToHalfAns2.normalize(artist);
    }

    /** */
    public String getArtist() {
        return artist;
    }

    /** */
    private String genre;

    /** */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /** */
    public String getGenre() {
        return genre;
    }

    /** */
    private List<Track> tracks = new ArrayList<Track>();

    /** */
    public void addTrack(Track track) {
//Debug.println(track);
        this.tracks.add(track);
    }

    /** */
    public Collection<Track> getTracks() {
        return tracks;
    }
}

/* */
