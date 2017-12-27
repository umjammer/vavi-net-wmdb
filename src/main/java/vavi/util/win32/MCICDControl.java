/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.win32;

import java.io.IOException;
import com.ms.win32.Winmm;


/**
 * CDPlayer encapsulates the behavior and control of a computer CD-ROM drive
 * used to play audio CDs.
 * 
 * @author Keith D. Smith
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 020415 nsano porting <br>
 *          0.01 031220 nsano clean impoerts <br>
 */
public class MCICDControl {

    /** length of maximum error returned by mciSendString() */
    public static final int MAX_ERROR_SIZE = 64;

    /** current track */
    private static int numTracks = -1;
    /** track start times in mm:ss:ff forma */
    private static String[] trackStartTimes = null;

    // MCI device command strings/templates for CD player
    // used in conjunction with com.ms.win32.Winmm.mciSendString()
    // Constants ending with _HEADER require additional data to be
    // tacked to the end
    private static final String OPEN = "open cdaudio type cdaudio alias cdaudio wait";

    private static final String CLOSE = "close cdaudio wait";

    private static final String PLAY_HEADER = "play cdaudio ";

    private static final String SET_TIME_FORMAT = "set cdaudio time format msf wait";

    private static final String STOP = "stop cdaudio wait";

    private static final String PAUSE = "pause cdaudio wait";

    private static final String CD_LENGTH = "status cdaudio length wait";

    private static final String TRACK_LENGTH_HEADER = "status cdaudio length track ";

    private static final String NUMBER_OF_TRACKS = "status cdaudio number of tracks wait";

//  private static final String TIME_FORMAT = "status cdaudio time format wait";

    private static final String TRACK_BEGINNING_HEADER = "status cdaudio position track ";

    private static final String CURRENT_TRACK = "status cdaudio current track ";

    private static final String MEDIA_PRESENT = "status cdaudio media present wait";

    private static final String MODE = "status cdaudio mode wait";

    private static final String CURRENT_POSITION = "status cdaudio position wait";

//  private static final String CD_REWIND = "seek cdaudio to start wait";

//  private static final String GOTO_TRACK_HEADER = "seek cdaudio to ";

    private static final String SYS_QUANTITY = "sysinfo all quantity";

    private static final String SYS_NAME = "sysinfo all name ";

    /** buffer to hold return values/errors */
    private static StringBuffer message = new StringBuffer(MAX_ERROR_SIZE);

    /** */
    private MCICDControl() {}

    /**
     * @throws IOException If an IO error occurs.
     */
    public static void listDevices() throws IOException {
        int count;
        Winmm.mciSendString(SYS_QUANTITY, message, MAX_ERROR_SIZE, 0);
        try {
            count = Integer.valueOf(message.toString().trim()).intValue();
        } catch (NumberFormatException e) {
            throw new IOException(message.toString());
        }

        for (int i = 1; i <= count; i++) {
            Winmm.mciSendString(SYS_NAME + i, message, MAX_ERROR_SIZE, 0);
System.err.println("MCI Device " + i + ": " + message);
        }
    }

    /**
     * Checks that there is media present in the CD drive
     * Pops up a dialog to allow retry
     *
     * @return true if playable media is present false otherwise
     */
    public static boolean isCDInDrive() {
        Winmm.mciSendString(MEDIA_PRESENT, message, MAX_ERROR_SIZE, 0);
        return !message.toString().startsWith("f");
    }

    /**
     * Returns true if CD player is paused. Else false.
     */
    public static boolean isPaused() {
        Winmm.mciSendString(MODE, message, MAX_ERROR_SIZE, 0);
        return message.toString().equals("paused");
    }

    /**
     * Returns true if CD player is stopped. Else false.
     */
    public static boolean isStopped() {
        Winmm.mciSendString(MODE, message, MAX_ERROR_SIZE, 0);
        return message.toString().equals("stopped");
    }

    /**
     * Returns true if CD player is ready to receive a command. Else false.
     */
    public static boolean isReady() {
        Winmm.mciSendString(MODE, message, MAX_ERROR_SIZE, 0);
        return !message.toString().equals("not ready");
    }
    
    /**
     * Returns true if CD player is playing. Else false.
     */
    public static boolean isPlaying() {
        Winmm.mciSendString(MODE, message, MAX_ERROR_SIZE, 0);
        return message.toString().equals("playing");
    }
    
    /**
     * Refreshes status of CD player.
     * NOTE: To be used when CD is replaced by user.
     *
     * @throws IOException If an IO error occurs.
     */
    public static void refresh() throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("CD is not in drive");
        }
        if (isPlaying()) {
            stop();
        }
        
        numTracks = getNumTracks();
        trackStartTimes = getTrackStartTimes();
        if (trackStartTimes == null || trackStartTimes.length != numTracks) {
            throw new IOException("bad CD");
        }
    }

    /**
     * Issues a play command to the CD Player.
     * NOTE: Arguments are in the "mm:ss:ff" format
     *
     * @param from  Location on disk to start playing from
     * @param to    Location on disk to stop playing
     * @throws  IOException If an IO error occurs
     */
    public static void play(String from, String to) throws IOException {
        if (from.length() != 8 || to.length() != 8) {
            throw new IllegalArgumentException(
                                               "not in MSF format: " + from + " or " + to);
        }
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        if (isPlaying()) {
            stop();
        }
        
        StringBuffer sb = new StringBuffer(PLAY_HEADER);
        sb.append("from ");
        sb.append(from);
        sb.append(" to ");
        sb.append(to);
        
        Winmm.mciSendString(sb.toString(), message, MAX_ERROR_SIZE, 0);
        if (message.toString().trim().length() != 0) {
            throw new IOException(message.toString());
        }
    }

    /**
     * Issues a play command to the CD Player.  Plays from 'from' to end
     * of disc.
     * NOTE: Arguments are in the "mm:ss:ff" format
     *
     * @param    from    Location on disk to start playing from
     * @throws  IOException If an IO error occurs
     */
    public static void play(String from) throws IOException {
        if (from.length() != 8) {
            throw new IllegalArgumentException("not in MSF format: " + from);
        }
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        if (isPlaying()) {
            stop();
        }
        
        StringBuffer sb = new StringBuffer(PLAY_HEADER);
        sb.append("from ");
        sb.append(from);
        
        Winmm.mciSendString(sb.toString(), message, MAX_ERROR_SIZE, 0);
        if (message.toString().trim().length() != 0) {
            throw new IOException(message.toString());
        }
    }

    /**
     * Issues a play command to the CD Player. Starts playback from
     * beginning of disc.
     * NOTE: Arguments are in the "mm:ss:ff" format
     *
     * @throws  IOException If an IO error occurs
     */
    public static void play() throws IOException {
        String times = trackStartTimes[getCurrentTrack() - 1];
        try {
            play(times);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not in MSF format: " + times);
        }
    }

    /**
     * Gets to total length of CD.
     *
     * @return  Length of CD in "mm:ss:ff" format.
     * @throws  IOException If an IO error occurs
     */
    public static String getCDLength() throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        Winmm.mciSendString(CD_LENGTH, message, MAX_ERROR_SIZE, 0);
        if (message.toString().indexOf(':') == -1) {
            throw new IOException(message.toString());
        }
        return message.toString();
    }

    /**
     * Gets current track.
     * NOTE: Track numbers start at 1.
     *
     * @return  The current track
     * @throws  IOException If an IO error occurs
     */
    public static int getCurrentTrack() throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        Winmm.mciSendString(CURRENT_TRACK, message, MAX_ERROR_SIZE, 0);
        try {
            return Integer.valueOf(message.toString()).intValue();
        } catch (NumberFormatException e) {
            throw new IOException(message.toString());
        }
    }

    /**
     * Gets the number of tracks on the current disc.
     *
     * @return  The number of tracks
     * @throws  IOException If an IO error occurs
     */
    public static int getNumTracks() throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        Winmm.mciSendString(NUMBER_OF_TRACKS, message, MAX_ERROR_SIZE, 0);
        try {
            return Integer.valueOf(message.toString()).intValue();
        } catch (NumberFormatException e) {
            throw new IOException(message.toString());
        }
    }

    /**
     * Gets the start times of each track on current disc.
     * NOTE: times are in "mm:ss:ff" format
     *
     * @return  An array containing the start time of each track
     *          on the current disc
     * @throws  IOException If an IO error occurs
     */
    public static String[] getTrackStartTimes() throws IOException {
        if (!isCDInDrive() || !isReady()) {
            throw new IOException("Drive is not ready.");
        }
        
        String[] ret = new String[numTracks];
        for (int i = 1; i <= numTracks; i++) {
            StringBuffer sb = new StringBuffer(TRACK_BEGINNING_HEADER);
            sb.append(i);
            Winmm.mciSendString(sb.toString(), message, MAX_ERROR_SIZE, 0);
            ret[i - 1] = message.toString();
            if (ret[i - 1].indexOf(':') == -1) {
                throw new IOException(ret[i - 1]);
            }
        }
        return ret;
    }

    /**
     * Gets the current position.
     *
     * @return  The current position in "mm:ss:ff" format
     * @throws  IOException If an IO error occurs
     */
    public static String getCurrentPosition() throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        Winmm.mciSendString(CURRENT_POSITION, message, MAX_ERROR_SIZE, 0);
        if (message.toString().trim().length() == 0) {
            throw new IOException(message.toString());
        }
        return message.toString();
    }

    /**
     * Gets the length of a specific track.
     *
     * @param    track    Track to lookup
     * @return    The length of 'track' in "mm:ss:ff" format
     * @throws  IOException If an IO error occurs
     */
    public static String getTrackLength(int track) throws IOException {
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        StringBuffer sb = new StringBuffer(TRACK_LENGTH_HEADER);
        sb.append(track);
        
        Winmm.mciSendString(sb.toString(), message, MAX_ERROR_SIZE, 0);
        if (message.toString().indexOf(':') == -1) {
            throw new IOException(message.toString());
        }
        return message.toString();
    }

    /**
     * Advances CD to a location and begins playing at that
     * position.
     *
     * @param track The track to begin playing
     * @throws  IOException If an IO error occurs
     */
    public static void gotoTrack(int track) throws IOException {
        String times = trackStartTimes[track - 1];
        try {
            play(times);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not in MSF format: " + times);
        }
    }

    /**
     * Begins playing next track.  If CD is currently
     * playing the last track nothing happens.
     *
     * @throws  IOException If an IO error occurs
     */
    public static void nextTrack() throws IOException {
        if (!isReady()) {
            throw new IOException("Drive is not ready.");
        }
        
        int track = getCurrentTrack();
        if (track == numTracks) {
            return;
        }
        try {
            gotoTrack(track + 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Bad track number: " + (track + 1));
        }
    }

    /**
     * Begins playing previous track.  If CD is currently
     * playing first track nothing happens.
     *
     * @throws  IOException If an IO error occurs
     */
    public static void prevTrack() throws IOException {
    if (!isReady()) {
            throw new IOException("Drive is not ready.");
        }

        int track = getCurrentTrack();
        if (track == 1) {
            return;
        }
        try {
            gotoTrack(track - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Bad track number: " + (track - 1));
        }
    }

    /**
     * Stops the CD.
     */
    public static void stop() {
        if (isReady() && (!isStopped() || isPlaying())) {
            Winmm.mciSendString(STOP, message, MAX_ERROR_SIZE, 0);
        }
    }

    /**
     * Pauses the CD.
     *
     * @throws  IOException    If an IO error occurs.
     */
    public static void pause() throws IOException {
        if (isReady() && (!isPaused() || isPlaying())) {
            Winmm.mciSendString(PAUSE, message, MAX_ERROR_SIZE, 0);
            if (message.toString().trim().length() != 0) {
                throw new IOException(message.toString());
            }
        }
    }

    /**
     * Opens the CD to receive commands.
     * NOTE: This must be done at the beginning of a new session.
     *
     * @throws  IOException    If an IO error occurs.
     */
    public static void open() throws IOException {
        Winmm.mciSendString(OPEN, message, MAX_ERROR_SIZE, 0);
        if (message.toString().trim().length() != 0) {
            throw new IOException(message.toString());
        }
        openImpl();
    }

    /**
     * Opens the CD to receive commands.
     * NOTE: This must be done at the beginning of a new session.
     *
     * @param    drive    ie "d:", "e:"
     * @throws  IOException    If an IO error occurs.
     */
    public static void open(String drive) throws IOException {
        String command = "open cdaudio!" + drive + " alias cdaudio wait";
//System.err.println(command);
        int r = Winmm.mciSendString(command, message, MAX_ERROR_SIZE, 0);
//System.err.println(r);
        if (r != 0) {
            throw new IOException(message.toString());
        }
        openImpl();
    }

    /**
     * @throws  IOException    If an IO error occurs.
     */
    private static void openImpl() throws IOException {
        
        Winmm.mciSendString(SET_TIME_FORMAT, message, MAX_ERROR_SIZE, 0);
        if (message.toString().trim().length() != 0) {
            throw new IOException(message.toString());
        }
        
        if (!isCDInDrive()) {
            throw new IOException("Drive is not ready.");
        }
        
        numTracks = getNumTracks();
        trackStartTimes = getTrackStartTimes();
        if (trackStartTimes == null || trackStartTimes.length != numTracks) {
            throw new IOException("bad CD");
        }
    }

    /**
     * Closes the CD (ends current session).
     * NOTE: This should be done at the end of each session.
     */
    public static void close() {
        Winmm.mciSendString(CLOSE, message, MAX_ERROR_SIZE, 0);
    }
}

/* */
