/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLConnection;

import vavi.util.cddb.CDDB;


/**
 * TestCDDB. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/07 umjammer initial version <br>
 */
public class TestCDDB {
    
    /**
     * JavaCDDB is a Win95/NT-like CD Player with a special twist.
     * Instead of relegating the task of providing disc title/artist/track
     * data to the user, JavaCDDB connects to a CD Database Server and attempts
     * to lookup the information automatically.
     * <p>
     * This requires an Internet connection and the ability to receive results
     * generated by data submitted using forms.
     * <pre>
     * + how to compile
     *
     *  % javacms *.java
     *
     * + how to run
     *
     *  % jview /cp . JavaCDDB [drive]
     *
     *  drive: "d:", "e:"
     * </pre>
     * @param args Array of parameters passed to the application
     * via the command line.
     */
    public static void main(String[] args) throws Exception {

        String drive = args.length == 0 ? null : args[0];

        CDDB db = new CDDB(drive);
        URLConnection uc = db.getURLConnection();

        InputStream is = new BufferedInputStream(uc.getInputStream());
        while (true) {
            int c = is.read();
            if (c == -1) {
                break;
            }
            System.err.write(c);
        }
        is.close();

//  System.err.println("Artist: " + data.getProperty("DTITLE"));

//      for (int i = 0; ; i++) {
//          String value = data.getProperty("TTITLE" + i);
//          if (value != null) {
//  System.err.println("Track " + (i + 1) + ": " + value);
//          } else {
//              break;
//          }
//      }
    }
}

/* */
