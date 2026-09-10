package br.gov.sifap;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Impoe os limites do Monolito Modular descritos na secao 2 de specs/001-social-program-catalog/plan.md.
 */
@AnalyzeClasses(packages = "br.gov.sifap", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

    @ArchTest
    static final ArchRule dominio_nao_depende_de_infraestrutura =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("o dominio nao pode conhecer JPA, REST nem qualquer detalhe de infraestrutura");

    @ArchTest
    static final ArchRule catalogo_so_enxerga_a_aplicacao_de_auditoria =
            noClasses()
                    .that().resideInAPackage("br.gov.sifap.catalogo..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "br.gov.sifap.auditoria.domain..",
                            "br.gov.sifap.auditoria.infrastructure..")
                    .because("o catalogo so atravessa o limite pela interface publicada em auditoria.application");
}
