/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.device.cd;


/**
 * CD ���̃C���^�[�t�F�[�X�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020424 nsano initial version <br>
 */
public interface CD {

    /** �w�肵���g���b�N�̃t���[������Ԃ��܂��D */
    public int getTrackLengthAt(int track);

    /** �e�g���b�N���n�܂�t���[����Ԃ��܂��D */
    public int[] getTrackStarts();

    /** CD �̑��t���[������Ԃ��܂��D */
    public int getLength();

    /** CD �̃g���b�N����Ԃ��܂��D */
    public int getTracksCount();
}

/* */
