package com.hyundai.h2;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ModuleDependencyRuleTest {

  @Test
  public void domainLayerDoesNotDependOnAdapterLayer() {
    noClasses()
        .that()
        .resideInAPackage("com.hkmc.ccs..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("com.hkmc.global.ccs..")
        .check(new ClassFileImporter().importPackages("com.hkmc.."));
  }

}
