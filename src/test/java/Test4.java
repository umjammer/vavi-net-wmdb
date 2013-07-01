/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.net.URL;
import java.net.URLConnection;

import vavi.util.Debug;


/**
 * test wmdb.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 031220 nsano initial version <br>
 *          0.01 031228 nsano use digester instead of betwixt <br>
 *          0.02 040530 nsano use digester by file configuration <br>
 */
public class Test4 {

    /**
     * usage: t4 path
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL("cddb:" + args[0]);
        URLConnection uc = url.openConnection();
Debug.println("content-type: " + uc.getContentType());
    }
}

/* */
