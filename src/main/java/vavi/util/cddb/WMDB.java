/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cddb;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import vavi.util.Debug;
import vavi.util.device.cd.CD;
import vavi.util.win32.MSF;


/**
 * WMDB riper for the Windows Media Player.
 *
 * <pre>
 * services.windowsmedia.com:80
 *
 * GET /cdinfo/querytoc.asp?cd=曲数+$2+$3+...+$n+$LEADOUT HTTP/1.1
 * Accept: *／*
 * User-Agent: Windows-Media-Player/7.01.00.3055
 * Accept-Encoding: gzip, deflate
 * Cookie:
 * Connection: Keep-Alive
 * Cache-Control: no-cache
 * Host: services.windowsmedia.com
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 020418 nsano initial version <br>
 *          0.10 020504 nsano independent of CD class <br>
 *          0.11 020510 nsano add cookie finder <br>
 *          1.00 031220 nsano refine <br>
 */
public class WMDB {

    /** The default cddb server */
    private static final String DEFAULT_SERVER = "services.windowsmedia.com";
    /** The default cddb server port */
    private static final int    DEFAULT_PORT   = 80;
    /** The default cddb server path */
    private static final String DEFAULT_PATH   = "/cdinfo/GetMDRCD.asp";
    /** The default cd class, must be sub class of vavi.util.device.cd.CD */
    private static final String DEFAULT_CLASS  = "vavi.util.win32.WindowsCD";

    /** The cddb server */
    private static String server;
    /** The cddb server port */
    private static int port;
    /** The cddb server path */
    private static String path;
    /** The cddb server query */
    private String query;

    /** The cd class */
    private static Class<?> clazz;

    /**
     * Creates an WMDB object.
     *
     * @param path 今のところドライブレター (i.e. c:, d:)
     */
    public WMDB(String path) {

        CD cd = null;

        try {
            Constructor<?> cons = clazz.getConstructor(String.class);
            cd = (CD) cons.newInstance(path);
        } catch (InvocationTargetException e) {
Debug.printStackTrace(e);
            throw (RuntimeException) new IllegalStateException().initCause(e.getTargetException());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        //----

        // creates query string
        StringBuffer sb = new StringBuffer("?cd=");

        // number of tracks
        int[] ts = cd.getTrackStarts();
        sb.append(Integer.toHexString(ts.length).toUpperCase() + "+");

        // each track's start frame
        for (int i = 0; i < ts.length; i++) {
            sb.append(Integer.toHexString(ts[i]).toUpperCase() + "+");
        }

        // lead-out frame (+1 ???)
        int lastTrack = cd.getTracksCount() - 1;
        int lengthInFrames = cd.getTrackLengthAt(lastTrack);
        int startTimeInFrames = ts[lastTrack];
        int leadOutStartTimeInFrames = startTimeInFrames + lengthInFrames;
        int lo = new MSF(leadOutStartTimeInFrames).toFrames() + 1;
        sb.append(Integer.toHexString(lo).toUpperCase());

        this.query = WMDB.path + sb.toString();
System.err.println("GET " + query + " HTTP/1.0");
    }

    /**
     * Gets connection from the cddb server.
     */
    public URLConnection getURLConnection() throws IOException {

        URL url = new URL("http", server, port, query);
        URLConnection uc = url.openConnection();

        // add base request header
        Properties requestProps = getRequestProperties();
        Enumeration<?> e = requestProps.propertyNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = requestProps.getProperty(name);
            uc.setRequestProperty(name, value);
        }

        return uc;
    }

    /** */
    private Properties getRequestProperties() throws IOException {

        Properties requestProps = new Properties();

        // sets HTTP headers
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if (name.startsWith("wmdb")) {
                // ignore
            } else if (name.equals("Cookie")) {
                String base = props.getProperty(name);
                String filename = null;

                int i = 1;
                while (true) {
                    filename = MessageFormat.format(
                        base,
                        new Object[] {
                            System.getProperty("user.name"),
                            new Integer(i)
                        });
                    if (new File(filename).exists()) {
System.err.println(filename + " exists");
                        break;
                    }
                    i++;
                    if (i > 9) {
                        throw new FileNotFoundException(base);
                    }
                }

                Cookie[] cookies = getCookies(filename);
                String value = getCookieString(cookies);
                requestProps.put(name, value);
System.err.println(name + ": " + value);
            } else {
                String value = props.getProperty(name);
                requestProps.put(name, value);
System.err.println(name + ": " + value);
            }
        }
System.err.println();

        return requestProps;
    }

    /** The properties file name */
    private static final String propsPath = "wmdb.properties";
    /** The properties for this application */
    private static Properties props = new Properties();

    /** Initializes. */
    static {
        try {
            InputStream is = WMDB.class.getResourceAsStream(propsPath);
            props.load(is);
            is.close();

            String value = props.getProperty("wmdb.server");
            server = (value == null) ? DEFAULT_SERVER : value;
            value = props.getProperty("wmdb.port");
            port = (value == null) ? DEFAULT_PORT : Integer.parseInt(value);
            value = props.getProperty("wmdb.path");
            path = (value == null) ? DEFAULT_PATH : value;
            value = props.getProperty("wmdb.class");
            clazz = Class.forName((value == null) ? DEFAULT_CLASS : value);
        } catch (Exception e) {
e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * InputStream for IE Cookie file, each part terminated by "0x0a".
     */
    class IECookieInputStream extends FilterInputStream {
        IECookieInputStream(InputStream in) {
            super(in);
        }
        String readLine() throws IOException {
            StringBuffer sb = new StringBuffer();
            while (true) {
                int c = in.read();
                if (c == -1 || c == 0x0a) {
                    break;
                }
                sb.append((char) c);
            }
            return sb.toString();
        }
    }

    /** Makes a string for HTTP header of "Cookie:". */
    String getCookieString(Cookie[] cookies) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.length; i++) {
            if (i != 0 && cookies.length > 1) {
                sb.append("; ");
            }
            sb.append(cookies[i].getName());
            sb.append("=");
            sb.append(cookies[i].getValue());
        }
        return sb.toString();
    }

    /** This class represents HTTP cookie. */
    class Cookie {
        String name;
        String value;
        String domain;
        Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }
        void setDomain(String domain) {
            this.domain = domain;
        }
        String getName() {
            return name;
        }
        String getValue() {
            return value;
        }
    }

    /**
     * Gets cookies from IE cookie file.
     *
     * [TOF]
     * name1 LS value1 LS domain1 LS ?? LS ?? LS ?? LS ?? LS ?? LS "*" LS
     * name2 LS value2 LS domain2 LS ?? LS ?? LS ?? LS ?? LS ?? LS "*" LS
     *  ...
     * [EOF]
     *
     * LS: 0x0a
     *
     * @param fileName IE cookie file name
     */
    Cookie[] getCookies(String fileName) throws IOException {
        IECookieInputStream is =
            new IECookieInputStream(new FileInputStream(fileName));

        List<Cookie> tmp = new ArrayList<>();

        while (is.available() > 0) {
            String name = is.readLine();

            String value = is.readLine();

            Cookie cookie = new Cookie(name, value);

            value = is.readLine();
            cookie.setDomain(value);

            value = is.readLine();

            while (true) {
                value = is.readLine();
                if (value.equals("*")) {
                    break;
                }
            }

            tmp.add(cookie);
        }
        is.close();

        Cookie[] cookies = new Cookie[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            cookies[i] = tmp.get(i);
        }

        return cookies;
    }

    //-------------------------------------------------------------------------

    /**
     * The main entry point for the application.
     * @param args drive letter.
     */
    public static void main(String[] args) throws Exception {

        String drive = args.length == 0 ? null : args[0];

        WMDB db = new WMDB(drive);
        URLConnection uc = db.getURLConnection();

        BufferedInputStream is = new BufferedInputStream(uc.getInputStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (true) {
            int c = is.read();
            if (c == -1) {
                break;
            }
            os.write(c);
        }
        is.close();
        os.close();

        String data = os.toString("UTF8");
        String dc =
            "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ" +
            "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ" +
            "０１２３４５６７８９" +
            "　（）“”";
        String sc =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            " ()\"\"";

        for (int i = 0; i < dc.length(); i++) {
            data = data.replace(dc.charAt(i), sc.charAt(i));
        }
System.out.println(data);
    }
}

/* */
