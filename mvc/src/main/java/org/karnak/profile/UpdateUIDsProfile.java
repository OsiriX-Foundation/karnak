package org.karnak.profile;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.internal.VRType;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.UUID;

import java.util.ArrayList;
import java.util.Optional;

public class UpdateUIDsProfile implements ProfileChain{
    private ProfileChain parent;
    private HMAC hmac;{
        hmac = AppConfig.getInstance().getHmac();
    }

    @Override
    public KeepEnum isKeep(DicomElement dcmElem) {
            if(dcmElem.vr() == VR.UI) {
                return parent.isKeep(dcmElem); //ask profile parent
            } else {
                return KeepEnum.noKeep;
            }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if(isKeep(dcmElem)== KeepEnum.keepNoChange){
            return new UUID();
        } else {
          return null;
        }
    }
}
