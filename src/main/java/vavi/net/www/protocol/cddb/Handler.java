/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.www.protocol.cddb;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import vavi.util.cddb.CDDB;


/**
 * Protocol:
 * 
 * <pre>
 *  cddb:c:
 * </pre>
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 031221 nsano initial version <br>
 */
public final class Handler extends URLStreamHandler {

    /** currently drive letter */
    private String path;

    /** */
    protected void parseURL(URL url, String spec, int start, int limit) {
System.err.println("spec: " + spec);
        String protocol = spec.substring(0, start - 1);
System.err.println("protocol: " + protocol);
        if ("cddb".equals(protocol)) {
            path = spec.substring(start, limit);
            start = limit;
        }
        super.parseURL(url, spec, start, limit);
    }

    /** */
    protected URLConnection openConnection(URL url) throws IOException {

        CDDB cddb = new CDDB(path);
        URLConnection uc = cddb.getURLConnection();

        return uc;
    }
}

/* */
