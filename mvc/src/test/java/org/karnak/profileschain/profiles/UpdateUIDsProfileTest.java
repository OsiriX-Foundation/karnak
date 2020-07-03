package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateUIDsProfileTest {
    /*
    private static DicomObject dataset = DicomObject.newDicomObject();
    private static UpdateUIDsProfile uidProfile;

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        TagPatternProfile curves = new TagPatternProfile("", "50xxxxxx", null);
        curves.put(Tag.CurveReferencedOverlaySequence, Action.KEEP);
        uidProfile = new UpdateUIDsProfile("", AbstractProfileItem.Type.REPLACE_UID.getClassAlias(), curves);
        dataset.setNull(Tag.OverlayData | (1 << 17), VR.OB);
        dataset.setString(Tag.CurveLabel, VR.LO, "curve label");
        dataset.newDicomSequence(Tag.CurveReferencedOverlaySequence);
        dataset.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.randomUID());
        dataset.setString(Tag.UID, VR.UI, UIDUtils.randomUID());
    }

    @Test
    void getAction() {
        uidProfile.clearTagMap();
        uidProfile.put(Tag.SOPInstanceUID, Action.UID);
        assertEquals(Action.UID, uidProfile.getAction(dataset.get(Tag.SOPInstanceUID).orElse(null)));
        assertEquals(null, uidProfile.getAction(dataset.get(Tag.UID).orElse(null)));

        assertEquals(Action.KEEP, uidProfile.getAction(dataset.get(Tag.CurveReferencedOverlaySequence).orElse(null)));
        assertEquals(Action.REMOVE, uidProfile.getAction(dataset.get(Tag.CurveLabel).orElse(null)));
        assertEquals(null, uidProfile.getAction(dataset.get(Tag.OverlayData | (1 << 17)).orElse(null)));
    }

    @Test
    void put() {
        uidProfile.clearTagMap();
        assertEquals(0, uidProfile.tagMap.size());

        uidProfile.put(Tag.SOPInstanceUID, Action.UID);
        uidProfile.put(Tag.UID, Action.REMOVE);

        assertEquals(Action.REMOVE, uidProfile.remove(Tag.UID));

        Assertions.assertThrows(IllegalStateException.class, () -> {
            uidProfile.put(Tag.StudyInstanceUID, Action.KEEP);
        });

        // TODO UI options, should give the list of all UIDs and not allowed other tags
        // TODO Generate random UIDs
        // TODO Generate random UIDs with consistency
    }
     */
}