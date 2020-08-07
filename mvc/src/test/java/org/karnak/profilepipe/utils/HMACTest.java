package org.karnak.profilepipe.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

import java.util.Arrays;


class HMACTest {
    private static final String HMAC_KEY1 = "0123456789";
    private static final String HMAC_KEY2 = "CKhP%3E9Ly?h44TLw6ac^HMR%X-E#4reTq6%AP7xxxjB_$ntG9G@Fpb!Y@XEjbKEXnwTV6MTp@puv?_@kfUZhXu7-2ZV6Y*3!tZFqNwY?ung@Q_m?z_?*$4A&DzAht^c";
    private static final String HMAC_KEY3 = "#y_dpfGanLUE-Sv+pqFzKLvSbk&UV?xeygWy8RPbQVEZzaAjUn%h%5EAYqmn7zzSWzX$MFYkw5z7?EC*jhn=aRHu_Zz@jXrt+Zft$j9%m@^ssM4B@crD7Nn7&r8RJ4DptzetNvaw$RpGYYb9Qg$5d?yS=gb@xZe&KW@tTjMcXN=aGgmZz*NVY$GKb&e8T9fNx&$H*Yb*hsxZLQHtz-5$dawTC4twKENW@dmE+2c4$DCU*q4K8nLPzUnXY3QzM-wT";
    private static final HMAC hmac1 = new HMAC(HMAC_KEY1);
    private static final HMAC hmac2 = new HMAC(HMAC_KEY2);
    private static final HMAC hmac3 = new HMAC(HMAC_KEY3);

    @BeforeAll
    static void beforeAll() {
    }

    @Test
    void byteHash() {
        byte[] bytehash1hmac1 = hmac1.byteHash("xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#");
        byte[] bytehash2hmac1 = hmac1.byteHash("#Bv=mm683aN");
        byte[] bytehash1hmac1result = {-11, 2, -6, 9, -70, -24, -65, -98, 113, 110, 93, -102, -92, -62, -128, -82, 29, -6, -121, 40, 91, -117, -18, 39, -54, -28, 24, -90, -18, -32, 82, -94};
        byte[] bytehash2hmac1result = {82, -80, -100, 113, 72, -43, 91, -117, -102, 4, -25, 29, -31, -22, 40, 9, -110, -106, 44, -78, 25, 125, -21, -113, -114, -20, 8, 29, 0, 38, -118, 81};

        byte[] bytehash1hmac2 = hmac2.byteHash("xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#");
        byte[] bytehash2hmac2 = hmac2.byteHash("#Bv=mm683aN");
        byte[] bytehash1hmac2result = {-77, 30, -46, 39, 20, -118, -39, -20, 111, -112, -56, 126, -44, 88, -6, -25, -117, 12, -38, -100, -117, 49, 19, 42, -121, -60, 119, 116, 26, 100, 81, -60};
        byte[] bytehash2hmac2result = {-14, -119, -113, 115, -106, -63, -37, -60, 126, 46, -4, -74, 116, -58, -47, -63, 97, -14, -64, 85, -127, -57, -38, 17, 69, -35, -17, -45, -14, -81, 47, -89};


        byte[] bytehash1hmac3 = hmac3.byteHash("xbnMRs3W-6KM*sbM#hs8sCZrgbBPSw2CE?rnnUF=thnfG7bFw_fZvHe!Ka&B#");
        byte[] bytehash2hmac3 = hmac3.byteHash("#Bv=mm683aN");
        byte[] bytehash1hmac3result = {92, -95, -50, 40, 38, 40, -77, -34, -125, 59, -65, -104, 112, -118, 70, 103, 62, 76, -97, -2, 43, 15, -90, -48, -61, 103, -47, -93, 15, -90, 115, 83};
        byte[] bytehash2hmac3result = {-118, 86, -16, 108, 71, -27, 43, -1, 118, 75, 78, -48, 112, 72, -117, 127, 48, -14, 71, -53, 29, -15, 118, 50, 97, 9, 51, 4, -107, 91, 12, 36};


        //###############################TEST HMAC KEY 1#############################################
        assertArrayEquals(bytehash1hmac1, bytehash1hmac1result);
        assertArrayEquals(bytehash2hmac1, bytehash2hmac1result);
        //###############################TEST HMAC KEY 2#############################################
        assertArrayEquals(bytehash1hmac2, bytehash1hmac2result);
        assertArrayEquals(bytehash2hmac2, bytehash2hmac2result);
        //###############################TEST HMAC KEY 3#############################################
        assertArrayEquals(bytehash1hmac3, bytehash1hmac3result);
        assertArrayEquals(bytehash2hmac3, bytehash2hmac3result);

        //######################TEST same bytehash BUT DIFFERENT HMAC KEY#################################
        assertNotEquals(Arrays.toString(bytehash1hmac1), Arrays.toString(bytehash1hmac3));
        assertNotEquals(Arrays.toString(bytehash2hmac1), Arrays.toString(bytehash2hmac2));
        assertNotEquals(Arrays.toString(bytehash2hmac1), Arrays.toString(bytehash2hmac3));
    }

    @Test
    void scaleHash() {
    }

    @Test
    void uidHash() {
        String uid1hmackey1 = hmac1.uidHash("toto","2.25.3333838383838838338");
        String uid2hmackey1 = hmac1.uidHash("argwf efwf /doiqjd !+]]","1.2.840.113704.1.111.5224.1217330018.18");
        String uid3hmackey1 = hmac1.uidHash("AXETK.*sEE","2.16.840.1.113669.632.20.121711.10000158860");
        String uid4hmackey1 = hmac1.uidHash("DKDJCCDOEL456L","2.16.840.1.113669.632.20.1211.10000502993");
        String uid5hmackey1 = hmac1.uidHash("QPEOD;CODKEJSOEöEKOCOELüXüDKDKDKDPFK","1.3.12.2.1107.5.2.31.30222.20070720081336125.0.0.0");
        String uid6hmackey1 = hmac1.uidHash("P235)(*/°.2édlcjcoeie","1.2.276.0.7230010.3.1.4.296485376.1.1484917366.62821");

        String uid1hmackey2 = hmac2.uidHash("toto","2.25.3333838383838838338");
        String uid2hmackey2 = hmac2.uidHash("argwf efwf /doiqjd !+]]","1.2.840.113704.1.111.5224.1217330018.18");
        String uid3hmackey2 = hmac2.uidHash("AXETK.*sEE","2.16.840.1.113669.632.20.121711.10000158860");
        String uid4hmackey2 = hmac2.uidHash("DKDJCCDOEL456L","2.16.840.1.113669.632.20.1211.10000502993");
        String uid5hmackey2 = hmac2.uidHash("QPEOD;CODKEJSOEöEKOCOELüXüDKDKDKDPFK","1.3.12.2.1107.5.2.31.30222.20070720081336125.0.0.0");
        String uid6hmackey2 = hmac2.uidHash("P235)(*/°.2édlcjcoeie","1.2.276.0.7230010.3.1.4.296485376.1.1484917366.62821");

        String uid1hmackey3 = hmac3.uidHash("toto","2.25.3333838383838838338");
        String uid2hmackey3 = hmac3.uidHash("argwf efwf /doiqjd !+]]","1.2.840.113704.1.111.5224.1217330018.18");
        String uid3hmackey3 = hmac3.uidHash("AXETK.*sEE","2.16.840.1.113669.632.20.121711.10000158860");
        String uid4hmackey3 = hmac3.uidHash("DKDJCCDOEL456L","2.16.840.1.113669.632.20.1211.10000502993");
        String uid5hmackey3 = hmac3.uidHash("QPEOD;CODKEJSOEöEKOCOELüXüDKDKDKDPFK","1.3.12.2.1107.5.2.31.30222.20070720081336125.0.0.0");
        String uid6hmackey3 = hmac3.uidHash("P235)(*/°.2édlcjcoeie","1.2.276.0.7230010.3.1.4.296485376.1.1484917366.62821");

        //###############################TEST HMAC KEY 1#############################################
        assertEquals(uid1hmackey1, "2.25.233671738038250046940083120998001891688");
        assertEquals(uid2hmackey1, "2.25.172550896703359720929648304016925264366");
        assertEquals(uid3hmackey1, "2.25.317916216572342520508348932664357311304");
        assertEquals(uid4hmackey1, "2.25.305352237750264180482573594023536757574");
        assertEquals(uid5hmackey1, "2.25.185342349041919775496768421292028706658");
        assertEquals(uid6hmackey1, "2.25.201449837558210293540447302415033181281");

        //###############################TEST HMAC KEY 2#############################################
        assertEquals(uid1hmackey2, "2.25.107036922797957086874016528925179122321");
        assertEquals(uid2hmackey2, "2.25.332130428373182134326050754292921637695");
        assertEquals(uid3hmackey2, "2.25.27837535495561615733695225322222044339");
        assertEquals(uid4hmackey2, "2.25.105069769489781949621945064952514161755");
        assertEquals(uid5hmackey2, "2.25.3895808070345353484473779524627424041");
        assertEquals(uid6hmackey2, "2.25.93223529085704809974848196727169512366");

        //###############################TEST HMAC KEY 3#############################################
        assertEquals(uid1hmackey3, "2.25.72329494149846071487686972988265303397");
        assertEquals(uid2hmackey3, "2.25.36116369642956423323025358198962638414");
        assertEquals(uid3hmackey3, "2.25.69851453623563018609886426683947643206");
        assertEquals(uid4hmackey3, "2.25.302749414304211782279329719127978366673");
        assertEquals(uid5hmackey3, "2.25.299874919431962127836450890483172001122");
        assertEquals(uid6hmackey3, "2.25.31690164370947223296814393878836358746");

        //######################TEST SAME UID BUT DIFFERENT HMAC KEY#################################
        assertNotEquals(uid1hmackey1, uid1hmackey2);
        assertNotEquals(uid1hmackey2, uid1hmackey3);
        assertNotEquals(uid2hmackey1, uid2hmackey2);
        assertNotEquals(uid2hmackey2, uid2hmackey3);
        assertNotEquals(uid3hmackey1, uid3hmackey2);
        assertNotEquals(uid3hmackey2, uid3hmackey3);
        assertNotEquals(uid4hmackey1, uid4hmackey2);
        assertNotEquals(uid4hmackey2, uid4hmackey3);
        assertNotEquals(uid5hmackey1, uid5hmackey2);
        assertNotEquals(uid5hmackey2, uid5hmackey3);
        assertNotEquals(uid6hmackey1, uid6hmackey2);
        assertNotEquals(uid6hmackey2, uid6hmackey3);

    }
}