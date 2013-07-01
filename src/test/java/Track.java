/*
 * Copyright (c) 2003 by Naohide Sano, All Rights Reserved.
 *
 * Programmed by Naohide Sano
 */

import vavi.util.CharNormalizerJa;


/**
 * Track.
 *
 * @author	<a href=mailto:vavivavi@yahoo.co.jp>Naohide Sano</a> (nsano)
 * @version	0.00	031221	vavi	initial version <br>
 */
public class Track {

    /** */
    private int number;

    /** */
    public void setNumber(int number) {
        this.number = number;
    }

    /** */
    public int getNumber() {
        return number;
    }

    /** */
    private String title;

    /** */
    public void setTitle(String title) {
        this.title = CharNormalizerJa.ToHalfAns2.normalize(title);
    }

    /** */
    public String getTitle() {
        return title;
    }
}

/* */
