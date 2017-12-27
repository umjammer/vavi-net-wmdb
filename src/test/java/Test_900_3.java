/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.cddb.CDDBUtil;
import vavi.util.device.cd.CD;
import vavi.util.win32.WindowsCD;


/**
 * test wmdb.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 031220 nsano initial version <br>
 *          0.01 031228 nsano use digester instead of betwixt <br>
 *          0.02 040530 nsano use digester by file configuration <br>
 */
public class Test_900_3 {

    /**
     * usage: t3 drive_letter
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL("wmdb:" + args[0]);
        URLConnection uc = url.openConnection();
Debug.println("content-type: " + uc.getContentType());

        //----

        // rule ファイルを指定して digester を作成
        Digester d = DigesterLoader.createDigester(new File("digester.xml").toURI().toURL());

        // 読み込み
        Album album = (Album) d.parse(uc.getInputStream());
Debug.println(StringUtil.paramString(album));

        //----

        CD cd = new WindowsCD(args[0]);
        String uid = CDDBUtil.getUID(cd);

        //----

        CDDBWriter cw = new CDDBWriter(new BufferedWriter(new FileWriter(uid)));
        cw.writeCDDB(cd, album);
        cw.flush();
        cw.close();
    }
}

/* */
