package org.karnak.backend.model.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.karnak.backend.model.standard.Module;

public class ValidatorModule {

  public static List<Module> missingMandatoryModule(ArrayList<Module> modulesBySOP, HashSet<Module> modules) {
    List<Module> mandatoryModulesBySOP = modulesBySOP.stream().filter(module ->
        Module.moduleIsMandatory(module) ? !modules.contains(module) : false
    ).collect(Collectors.toList());

    return mandatoryModulesBySOP;
  }
}
