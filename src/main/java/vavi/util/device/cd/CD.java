/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.device.cd;


/**
 * CD 情報のインターフェースです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020424 nsano initial version <br>
 */
public interface CD {

    /** 指定したトラックのフレーム数を返します． */
    public int getTrackLengthAt(int track);

    /** 各トラックが始まるフレームを返します． */
    public int[] getTrackStarts();

    /** CD の総フレーム数を返します． */
    public int getLength();

    /** CD のトラック数を返します． */
    public int getTracksCount();
}

/* */
