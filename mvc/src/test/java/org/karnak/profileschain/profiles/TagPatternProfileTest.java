package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagPatternProfileTest {
    private static final String CURVES = "50xxxxxx";
    private static final String OVERLAYS_DATA = "60xx3000";
    private static final String OVERLAYS_COMMENTS = "60xx4000";
    private static final String TRAILING_PADDING = "fffcfffx";
    private static final String TRAILING_PADDING2 = "FFFCFFFX";

    private static DicomObject dataset = DicomObject.newDicomObject();

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        dataset.setString(Tag.CurveLabel, VR.LO, "curve label");
        dataset.newDicomSequence(Tag.CurveReferencedOverlaySequence);
        dataset.setNull(Tag.OverlayData | (1 << 17), VR.OB);
        dataset.setNull(Tag.OverlayData | (2 << 17), VR.OB);
        dataset.setNull(Tag.OverlayData | (5 << 17), VR.OB);
        dataset.setString(Tag.OverlayComments, VR.LT, "overlay comments");
    }

    @Test
    void isValid() {
        assertEquals(true, TagPatternProfile.isValid(CURVES));
        assertEquals(true, TagPatternProfile.isValid(OVERLAYS_DATA));
        assertEquals(true, TagPatternProfile.isValid(OVERLAYS_COMMENTS));
        assertEquals(true, TagPatternProfile.isValid(TRAILING_PADDING));
        assertEquals(true, TagPatternProfile.isValid(TRAILING_PADDING2));

        assertEquals(false, TagPatternProfile.isValid(null));
        assertEquals(false, TagPatternProfile.isValid(""));
        assertEquals(false, TagPatternProfile.isValid("fffcfffc"), "must be invalid when no x");
        assertEquals(false, TagPatternProfile.isValid("00181000"), "must be invalid when no x");
        assertEquals(false, TagPatternProfile.isValid("60xx3000A"));
        assertEquals(false, TagPatternProfile.isValid("G0xx3000"));
        assertEquals(false, TagPatternProfile.isValid("60xx-3000"));
        assertEquals(false, TagPatternProfile.isValid("(60003000)"));
    }

    @Test
    void getAction() {
        // WHITELIST policy for curves pattern with an exception to keep (CurveReferencedOverlaySequence)
        TagPatternProfile curves = buildTagPatternProfile(CURVES);
        curves.put(Tag.CurveReferencedOverlaySequence, Action.KEEP);
        Assertions.assertThrows(IllegalStateException.class, () -> {
            curves.put(Tag.CurveReferencedOverlaySequence, Action.REMOVE);
        });
        Assertions.assertThrows(IllegalStateException.class, () -> {
            curves.put(Tag.CurveReferencedOverlaySequence, Action.REPLACE_NULL);
        });
        assertEquals(Action.REMOVE, curves.getAction(dataset.get(Tag.CurveLabel).orElse(null)));
        assertEquals(Action.KEEP, curves.getAction(dataset.get(Tag.CurveReferencedOverlaySequence).orElse(null)));
        assertEquals(null, curves.getAction(dataset.get(Tag.OverlayData | (1 << 17)).orElse(null)));

        // WHITELIST policy for overlay pattern with an exception to keep (first layer of OverlayData)
        TagPatternProfile overlayData = buildTagPatternProfile(OVERLAYS_DATA);
        overlayData.put(Tag.OverlayData | (1 << 17), Action.KEEP);
        assertEquals(Action.KEEP, overlayData.getAction(dataset.get(Tag.OverlayData | (1 << 17)).orElse(null)));
        assertEquals(Action.REMOVE, overlayData.getAction(dataset.get(Tag.OverlayData | (2 << 17)).orElse(null)));
        assertEquals(Action.REMOVE, overlayData.getAction(dataset.get(Tag.OverlayData | (5 << 17)).orElse(null)));
        assertEquals(null, overlayData.getAction(dataset.get(Tag.CurveLabel).orElse(null)));

        assertEquals(Action.REMOVE, buildTagPatternProfile(OVERLAYS_COMMENTS).getAction(dataset.get(Tag.OverlayComments).orElse(null)));

        // BLACKLIST policy for curves pattern with parent profile
        TagPatternProfile curves2 = new TagPatternProfile("", CURVES, overlayData);
        curves2.put(Tag.CurveReferencedOverlaySequence, Action.REMOVE);
        curves2.put(Tag.CurveLabel, Action.DEFAULT_DUMMY);
        Assertions.assertThrows(IllegalStateException.class, () -> {
            curves2.put(Tag.CurveReferencedOverlaySequence, Action.KEEP);
        });
        assertEquals(Action.DEFAULT_DUMMY, curves2.getAction(dataset.get(Tag.CurveLabel).orElse(null)));
        assertEquals(Action.REMOVE, curves2.getAction(dataset.get(Tag.CurveReferencedOverlaySequence).orElse(null)));
        assertEquals(Action.KEEP, curves2.getAction(dataset.get(Tag.OverlayData | (1 << 17)).orElse(null)));
    }

    @Test
    void buildProfile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            buildTagPatternProfile(Integer.toHexString(Tag.OverlayData));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            buildTagPatternProfile("5518100");
        });
    }

    private static TagPatternProfile buildTagPatternProfile(String pattern) {
        return new TagPatternProfile("", pattern, null);
    }
}