package org.karnak.profilepipe.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;


class HMACTest {
    private static HMAC hmac1;
    private static HMAC hmac1_same;
    private static HMAC hmac2;
    private static HMAC hmac2_same;
    private static HMAC hmac3;
    private static HMAC hmac3_same;

    @BeforeAll
    static void beforeAll() {
        final String HMAC_KEY1 = "0123456789";
        hmac1 = new HMAC(HMAC_KEY1);
        hmac1_same = new HMAC(HMAC_KEY1);
        final String HMAC_KEY2 = "CKhP%3E9Ly?h44TLw6ac^HMR%X-E#4reTq6%AP7xxxjB_$ntG9G@Fpb!Y@XEjbKEXnwTV6MTp@puv?_@kfUZhXu7-2ZV6Y*3!tZFqNwY?ung@Q_m?z_?*$4A&DzAht^c";
        hmac2 = new HMAC(HMAC_KEY2);
        hmac2_same = new HMAC(HMAC_KEY2);
        final String HMAC_KEY3 = "#y_dpfGanLUE-Sv+pqFzKLvSbk&UV?xeygWy8RPbQVEZzaAjUn%h%5EAYqmn7zzSWzX$MFYkw5z7?EC*jhn=aRHu_Zz@jXrt+Zft$j9%m@^ssM4B@crD7Nn7&r8RJ4DptzetNvaw$RpGYYb9Qg$5d?yS=gb@xZe&KW@tTjMcXN=aGgmZz*NVY$GKb&e8T9fNx&$H*Yb*hsxZLQHtz-5$dawTC4twKENW@dmE+2c4$DCU*q4K8nLPzUnXY3QzM-wT";
        hmac3 = new HMAC(HMAC_KEY3);
        hmac3_same = new HMAC(HMAC_KEY3);
    }

    @ParameterizedTest
    @MethodSource("providerByteHash")
    void byteHash(HMAC hmac, String input, byte[] output){
        assertArrayEquals(output, hmac.byteHash(input));
    }

    private static Stream<Arguments> providerByteHash() {
        byte[] output1 = {-11, 2, -6, 9, -70, -24, -65, -98, 113, 110, 93, -102, -92, -62, -128, -82, 29, -6, -121, 40, 91, -117, -18, 39, -54, -28, 24, -90, -18, -32, 82, -94};
        byte[] output2 = {82, -80, -100, 113, 72, -43, 91, -117, -102, 4, -25, 29, -31, -22, 40, 9, -110, -106, 44, -78, 25, 125, -21, -113, -114, -20, 8, 29, 0, 38, -118, 81};
        byte[] output3 = {-77, 30, -46, 39, 20, -118, -39, -20, 111, -112, -56, 126, -44, 88, -6, -25, -117, 12, -38, -100, -117, 49, 19, 42, -121, -60, 119, 116, 26, 100, 81, -60};
        byte[] output4 = {-14, -119, -113, 115, -106, -63, -37, -60, 126, 46, -4, -74, 116, -58, -47, -63, 97, -14, -64, 85, -127, -57, -38, 17, 69, -35, -17, -45, -14, -81, 47, -89};
        byte[] output5 = {92, -95, -50, 40, 38, 40, -77, -34, -125, 59, -65, -104, 112, -118, 70, 103, 62, 76, -97, -2, 43, 15, -90, -48, -61, 103, -47, -93, 15, -90, 115, 83};
        byte[] output6 = {-118, 86, -16, 108, 71, -27, 43, -1, 118, 75, 78, -48, 112, 72, -117, 127, 48, -14, 71, -53, 29, -15, 118, 50, 97, 9, 51, 4, -107, 91, 12, 36};
        return Stream.of(
                Arguments.of(hmac1, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#", output1),
                Arguments.of(hmac1, "#Bv=mm683aN", output2),
                Arguments.of(hmac2, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#", output3),
                Arguments.of(hmac2, "#Bv=mm683aN", output4),
                Arguments.of(hmac3, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#", output5),
                Arguments.of(hmac3, "#Bv=mm683aN", output6)
        );
    }

    @ParameterizedTest
    @MethodSource("providerNotSameByteHash")
    void notSameByteHash(HMAC hmac1, HMAC hmac2, String input){
        assertFalse(Arrays.equals(hmac1.byteHash(input), hmac2.byteHash(input)));
    }

    private static Stream<Arguments> providerNotSameByteHash() {
        return Stream.of(
                Arguments.of(hmac1, hmac2, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#"),
                Arguments.of(hmac1, hmac2, "#Bv=mm683aN"),
                Arguments.of(hmac1, hmac3, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#"),
                Arguments.of(hmac1, hmac3, "#Bv=mm683aN"),
                Arguments.of(hmac2, hmac3, "xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#"),
                Arguments.of(hmac2, hmac3, "#Bv=mm683aN")
        );
    }

    @ParameterizedTest
    @MethodSource("providerSameUIDHash")
    void sameUIDHash(HMAC hmac1, HMAC hmac2, String input){
        assertEquals(hmac1.uidHash(input), hmac2.uidHash(input));
    }

    private static Stream<Arguments> providerSameUIDHash() {
        return Stream.of(
                Arguments.of(hmac1, hmac1_same, "2.25.163485808146406487370825160808855144872"),
                Arguments.of(hmac1, hmac1_same, "2.25.94864836973909141411232579544325294158"),
                Arguments.of(hmac2, hmac2_same, "2.25.234378532077629807026582121273495697860"),
                Arguments.of(hmac2, hmac2_same, "2.25.60425845025227825428941166719886325579"),
                Arguments.of(hmac3, hmac3_same, "2.25.174707390929794025815088409892000794305"),
                Arguments.of(hmac3, hmac3_same, "2.25.138226508601833892075134918123442900169")
        );
    }


    @ParameterizedTest
    @MethodSource("providerNotSameUIDHash")
    void notSameUIDHash(HMAC hmac1, HMAC hmac2, String input){
        assertNotEquals(hmac1.uidHash(input), hmac2.uidHash(input));
    }

    private static Stream<Arguments> providerNotSameUIDHash() {
        return Stream.of(
                Arguments.of(hmac1, hmac2, "2.25.163485808146406487370825160808855144872"),
                Arguments.of(hmac1, hmac3, "2.25.94864836973909141411232579544325294158"),
                Arguments.of(hmac2, hmac3, "2.25.234378532077629807026582121273495697860"),
                Arguments.of(hmac1, hmac2, "2.25.60425845025227825428941166719886325579"),
                Arguments.of(hmac1, hmac3, "2.25.174707390929794025815088409892000794305"),
                Arguments.of(hmac2, hmac3, "2.25.138226508601833892075134918123442900169")
        );
    }

    @ParameterizedTest
    @MethodSource("providerUIDHash")
    void UIDHash(HMAC hmac, String input, String output){
        assertEquals(output, hmac.uidHash(input));
    }

    private static Stream<Arguments> providerUIDHash() {
        return Stream.of(
                Arguments.of(hmac1, "2.25.163485808146406487370825160808855144872", "2.25.34814371816139640061947570938091849945"),
                Arguments.of(hmac1, "2.25.94864836973909141411232579544325294158", "2.25.49787065962447474798537541271768822965"),
                Arguments.of(hmac2, "2.25.234378532077629807026582121273495697860", "2.25.287062350997959408263703999455562005373"),
                Arguments.of(hmac2, "2.25.60425845025227825428941166719886325579", "2.25.105268868062495992958668965082825361407"),
                Arguments.of(hmac3, "2.25.174707390929794025815088409892000794305", "2.25.86700382715051525034996851529373166039"),
                Arguments.of(hmac3, "2.25.138226508601833892075134918123442900169", "2.25.200484660542020411291635770434115741618")
        );
    }
    /*
    @ParameterizedTest
    @MethodSource("providerScaleHash")
    void scaleHash(HMAC hmac, String value, int scaledMin, int scaledMax, double output){
        assertEquals(output, hmac.scaleHash(value, scaledMin, scaledMax));
    }

    private static Stream<Arguments> providerScaleHash() {
        return Stream.of(
                Arguments.of(hmac1, "", 10, 89, 1),
                Arguments.of(hmac1, "2.25.94864836973909141411232579544325294158", "2.25.49787065962447474798537541271768822965"),
                Arguments.of(hmac2, "2.25.234378532077629807026582121273495697860", "2.25.287062350997959408263703999455562005373"),
                Arguments.of(hmac2, "2.25.60425845025227825428941166719886325579", "2.25.105268868062495992958668965082825361407"),
                Arguments.of(hmac3, "2.25.174707390929794025815088409892000794305", "2.25.86700382715051525034996851529373166039"),
                Arguments.of(hmac3, "2.25.138226508601833892075134918123442900169", "2.25.200484660542020411291635770434115741618")
        );
    }
    */
}