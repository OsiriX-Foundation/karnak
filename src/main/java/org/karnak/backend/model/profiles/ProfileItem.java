package org.karnak.backend.model.profiles;

import java.util.List;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.profilepipe.HMAC;

public interface ProfileItem {
  ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac);

  ActionItem put(int tag, ActionItem action);

  ActionItem remove(int tag);

  void clearTagMap();

  String getName();

  String getCodeName();

  String getCondition();

  String getOption();

  List<ArgumentEntity> getArguments();

  Integer getPosition();

  void profileValidation() throws Exception;
}
