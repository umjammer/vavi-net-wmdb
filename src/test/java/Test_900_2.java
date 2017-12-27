/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import vavi.util.Debug;


/**
 * test cddb.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 031221 nsano initial version <br>
 */
public class Test_900_2 {

    /**
     * usage: t2 url
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL(args[0]);
        URLConnection uc = url.openConnection();
Debug.println("content-type: " + uc.getContentType());

        InputStream is = new BufferedInputStream(uc.getInputStream());
        while (true) {
            int c = is.read();
            if (c == -1)
                break;
            System.err.write(c);
        }
        is.close();
    }
}

/* */
