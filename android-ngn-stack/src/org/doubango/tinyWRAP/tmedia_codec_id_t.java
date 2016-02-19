/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public enum tmedia_codec_id_t {
    tmedia_codec_id_none(0x00000000),
    tmedia_codec_id_amr_nb_oa(0x00000001 << 0),
    tmedia_codec_id_amr_nb_be(0x00000001 << 1),
    tmedia_codec_id_amr_wb_oa(0x00000001 << 2),
    tmedia_codec_id_amr_wb_be(0x00000001 << 3),
    tmedia_codec_id_gsm(0x00000001 << 4),
    tmedia_codec_id_pcma(0x00000001 << 5),
    tmedia_codec_id_pcmu(0x00000001 << 6),
    tmedia_codec_id_ilbc(0x00000001 << 7),
    tmedia_codec_id_speex_nb(0x00000001 << 8),
    tmedia_codec_id_speex_wb(0x00000001 << 9),
    tmedia_codec_id_speex_uwb(0x00000001 << 10),
    tmedia_codec_id_bv16(0x00000001 << 11),
    tmedia_codec_id_bv32(0x00000001 << 12),
    tmedia_codec_id_opus(0x00000001 << 13),
    tmedia_codec_id_g729ab(0x00000001 << 14),
    tmedia_codec_id_g722(0x00000001 << 15),
    tmedia_codec_id_h261(0x00010000 << 0),
    tmedia_codec_id_h263(0x00010000 << 1),
    tmedia_codec_id_h263p(0x00010000 << 2),
    tmedia_codec_id_h263pp(0x00010000 << 3),
    tmedia_codec_id_h264_bp(0x00010000 << 4),
    tmedia_codec_id_h264_mp(0x00010000 << 5),
    tmedia_codec_id_h264_hp(0x00010000 << 6),
    tmedia_codec_id_h264_bp10(tmedia_codec_id_h264_bp),
    tmedia_codec_id_h264_bp20(tmedia_codec_id_h264_bp),
    tmedia_codec_id_h264_bp30(tmedia_codec_id_h264_bp),
    tmedia_codec_id_h264_svc(0x00010000 << 7),
    tmedia_codec_id_theora(0x00010000 << 8),
    tmedia_codec_id_mp4ves_es(0x00010000 << 9),
    tmedia_codec_id_vp8(0x00010000 << 10),
    tmedia_codec_id_t140(0x00010000 << 14),
    tmedia_codec_id_red(0x00010000 << 15);

    public final int swigValue() {
        return swigValue;
    }

    public static tmedia_codec_id_t swigToEnum(int swigValue) {
        tmedia_codec_id_t[] swigValues = tmedia_codec_id_t.class.getEnumConstants();
        if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
            return swigValues[swigValue];
        for (tmedia_codec_id_t swigEnum : swigValues)
            if (swigEnum.swigValue == swigValue)
                return swigEnum;
        throw new IllegalArgumentException("No enum " + tmedia_codec_id_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private tmedia_codec_id_t() {
        this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private tmedia_codec_id_t(int swigValue) {
        this.swigValue = swigValue;
        SwigNext.next = swigValue + 1;
    }

    @SuppressWarnings("unused")
    private tmedia_codec_id_t(tmedia_codec_id_t swigEnum) {
        this.swigValue = swigEnum.swigValue;
        SwigNext.next = this.swigValue + 1;
    }

    private final int swigValue;

    private static class SwigNext {
        private static int next = 0;
    }
}

