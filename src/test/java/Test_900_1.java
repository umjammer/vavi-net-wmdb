/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.digester.Digester;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
 */
public class Test_900_1 {

    /**
     * usage: t1 drive_letter
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL("wmdb:" + args[0]);
        URLConnection uc = url.openConnection();
        Debug.println("content-type: " + uc.getContentType());

        // ----

        // メソッドを使用して digester を作成
        XMLReader reader = XMLReaderFactory.createXMLReader();
        Digester d = new Digester(reader);

        d.addObjectCreate("METADATA/MDR-CD", Album.class);

        d.addBeanPropertySetter("METADATA/MDR-CD/albumTitle", "title");
        d.addBeanPropertySetter("METADATA/MDR-CD/albumArtist", "artist");
        d.addBeanPropertySetter("METADATA/MDR-CD/genre");

        d.addObjectCreate("METADATA/MDR-CD/track", Track.class);
        d.addSetNext("METADATA/MDR-CD/track", "addTrack");

        d.addBeanPropertySetter("METADATA/MDR-CD/track/trackNumber", "number");
        d.addBeanPropertySetter("METADATA/MDR-CD/track/trackTitle", "title");

        // 読み込み
        Album album = (Album) d.parse(uc.getInputStream());
        Debug.println(StringUtil.paramString(album));

        // ----

        CD cd = new WindowsCD(args[0]);
        String uid = CDDBUtil.getUID(cd);

        // ----

        CDDBWriter cw = new CDDBWriter(new BufferedWriter(new FileWriter(uid)));
        cw.writeCDDB(cd, album);
        cw.flush();
        cw.close();
    }
}

/* */
