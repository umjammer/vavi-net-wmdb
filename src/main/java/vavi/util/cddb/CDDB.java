/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cddb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import vavi.util.device.cd.CD;


/**
 * CDDB encapsulates the behavior of a CD Database Server
 * that accepts queries via the HTTP protocol.
 * <p>
 * For more information on the CDDB effort visit:<br>
 *	    http://www.cddb.com/
 * <p>
 * Special thanks to:<br>
 *    Ti Kan, author of xmcd and creator of the CDDB concept
 *    Steve Scherf, author of the CDDB server software
 * <p>
 * @author	Keith D. Smith
 * @author	<a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version	0.00 020415 nsano porting <br>
 *	     	1.00 020424 nsano complete <br>
 *	     	1.10 020504 nsano independent of CD class <br>
 *	     	2.00 031220 nsano refine <br>
 */
public class CDDB {

    /** CDDB protocol */
    private static final int PROTOCOL_VERSION = 5;
    /** CDDB termination marker */
//  private static final String TERMINATION_MARKER = ".";
    /** CDDB commands */
    private static final String COMMAND_VERSION  = "ver";
    private static final String COMMAND_READ  = "read";
    private static final String COMMAND_QUERY = "query";

    /** JavaCDDB version */
    private static final String USER_AGENT = "JavaCDDB";

    /** CDDB transfer protocol(for future expansion) */
    private static final String URL_PROTOCOL = "http";

    /** Default site information */
    private static final String DEFAULT_SERVER = "freedb.freedb.org";
    private static final int DEFAULT_PORT = 80;
    private static final String DEFAULT_PATH = "/~cddb/cddb.cgi";
    private static final String DEFAULT_USER = "umjammer";
    private static final String DEFAULT_HOST = "saku2.com";

    /** The default cd class */
    private static final String DEFAULT_CLASS  = "vavi.util.win32.WindowsCD";

    /** The server */
    private static String server;
    /** The default cd class */
    private static int port;
    /** The path */
    private static String path;
    /** The user */
    private static String user;
    /** The host  */
    private static String host;

    /** The cd class */
    private static Class clazz;

    /** */
    private CD cd;

    /**
     * Initializes this with all necessary information to query server
     * and store results.
     * @param	path	cd drive letter
     */
    public CDDB(String path) throws IOException {

        try {
            Constructor cons = clazz.getConstructor(new Class[] { String.class });
            this.cd = (CD) cons.newInstance(new Object[] { path });
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException());
        } catch (Exception e) {
System.err.println("see cddb.properties: cddb.class: " + e);
            throw new IllegalStateException(e);
        }
System.err.println("Server: http://" + server + ":" + port + CDDB.path);
    }

    /** */
    private String getReadString(String uid, String genre) {
        return getCommandString(
            "cmd=" + "cddb" +
            "+" + COMMAND_READ +
            "+" + genre +
            "+" + uid
        );
    }

    /** */
    private String getQueryString() {

        String uid          = CDDBUtil.getUID(cd);
        int[]  trackOffsets = cd.getTrackStarts();
        int    discLength   = CDDBUtil.getLengthAsSecond(cd);

        return getCommandString(
            "cmd=" + "cddb" +
            "+" + COMMAND_QUERY +
            "+" + uid +
            "+" + trackOffsets.length +
            "+" + getOffsetsString(trackOffsets) +
            "+" + discLength
        );
    }

    /** */
    private String getServerVersionString() {
        return getCommandString("cmd=" + COMMAND_VERSION);
    }

    /** */
    private String getCommandString(String command) {
        return CDDB.path +
            "?" + command +

            "&" + "hello=" + user +
            "+" + host +
            "+" + USER_AGENT +
            "+" + "2.0" +

            "&" + "proto=" + PROTOCOL_VERSION;
    }

    /**
     * <pre>
     * 200 "Found exact match"
     * 202 "No match found"
     * 210 "Ok"
     * 211 "Found inexact matches"
     * 401 "Specified CDDB entry not found"
     * 402 "Server error"
     * 403 "Database entry is corrupt"
     * 409 "No handshake"
     * </pre>
     */
    public URLConnection getURLConnection() throws IOException {

        // ver
        String query = getServerVersionString();
System.err.println(query);

        URL url = new URL("http", server, port, query);
        URLConnection uc = url.openConnection();

        BufferedReader br = 
            new BufferedReader(new InputStreamReader(uc.getInputStream()));

        String line = br.readLine();
System.err.println(line);
        StringTokenizer st = new StringTokenizer(line);
        int result = Integer.parseInt(st.nextToken());
        if (result != 200 && result != 201) {
            throw new FileNotFoundException(query);
        }

        // query
        query = getQueryString();
System.err.println(query);
        url = new URL(URL_PROTOCOL, server, port, query);
        uc = url.openConnection();

        br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

        line = br.readLine();
System.err.println(line);
        st = new StringTokenizer(line);
        result = Integer.parseInt(st.nextToken());
        if (result != 200 || result == 202) {
System.err.println("result: " + result);
            throw new FileNotFoundException(query);
        }

        String genre = null;
        String uid = null;
        if ((result % 100) / 10 == 1) {
            Vector proposed = new Vector();
            while (br.ready()) {
                line = br.readLine();
                if (line.equals(".")) {
                    break;
                }
                proposed.addElement(line);
System.err.println(line);
            }
            if (proposed.size() > 1) {
System.err.println("there are multiple proposed title, use first one");
            }
            String target = (String) proposed.elementAt(0);
            st = new StringTokenizer(target);
            genre = st.nextToken();
            uid = st.nextToken();
        } else {
            genre = st.nextToken();
            uid = st.nextToken();
        }

System.err.println("genre: " + genre);
System.err.println("uid: " + uid);

        // read
        query = getReadString(uid, genre);
System.err.println(query);

        url = new URL(URL_PROTOCOL, server, port, query);
        uc = url.openConnection();

        return uc;
    }

    /**
     * Gets the track offsets in s to form a valid CDDB query
     *
     * @param s The array to URL
     * @return  The offsets for CDDB query
     */
    private static String getOffsetsString(int[] s) {
        StringBuffer ret = new StringBuffer();
        int i = 0;
        for (; i < s.length - 1; i++) {
            ret.append(s[i] + "+");
        }
        ret.append(s[i]);
        return ret.toString();
    }

    /** */
    static {
        final String propsPath = "cddb.properties";
        Properties props = new Properties();
        try {
            InputStream is = CDDB.class.getResourceAsStream(propsPath);
            props.load(is);
            is.close();
            
            String value = props.getProperty("cddb.server");
            server = (value == null) ? DEFAULT_SERVER : value;
            value = props.getProperty("cddb.port");
            port = (value == null) ? DEFAULT_PORT : Integer.parseInt(value);
            value = props.getProperty("cddb.path");
            path = (value == null) ? DEFAULT_PATH : value;
            value = props.getProperty("cddb.user");
            user = (value == null) ? DEFAULT_USER : value;
            value = props.getProperty("cddb.host");
            host = (value == null) ? DEFAULT_HOST : value;
            value = props.getProperty("cddb.class");
            clazz = Class.forName((value == null) ? DEFAULT_CLASS : value);
        } catch (Exception e) {
e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }
    }
}

/* */
