/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.www.protocol.wmdb;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import vavi.util.cddb.WMDB;


/**
 * Protocol:
 * 
 * <pre>
 *  wmdb:c:
 * </pre>
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 031220 nsano initial version <br>
 */
public final class Handler extends URLStreamHandler {

    /** currently drive letter */
    private String path;

    /** */
    protected void parseURL(URL url, String spec, int start, int limit) { 
System.err.println("spec: " + spec);
        String protocol = spec.substring(0, start - 1);
System.err.println("protocol: " + protocol);
        if ("wmdb".equals(protocol)) {
            path = spec.substring(start, limit); 
            start = limit;
        }
        super.parseURL(url, spec, start, limit); 
    } 

    /** */
    protected URLConnection openConnection(URL url) 
        throws IOException {

        WMDB wmdb = new WMDB(path);
        URLConnection uc = wmdb.getURLConnection();

        return uc;
    }
}

/* */
