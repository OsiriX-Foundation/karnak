package org.karnak.profile.action;

import org.dcm4che3.data.VR;
import org.karnak.profile.action.deident.ActionVR;
public class Action implements Command {

   private ActionVR actionVR;

   public Action(ActionVR actionVR) {
      this.actionVR = actionVR;
   }

   @Override // Command
   public void execute(VR vr){
      switch(vr){
         case LO:
            this.actionVR.LO();
            break;
         case TM:
            this.actionVR.TM();
            break;
         default:
      }
   }
}