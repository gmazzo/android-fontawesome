package com.github.gmazzo.gradle.plugins.tasks

import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import org.gradle.api.tasks.OutputFile

public class GenerateFontStringResourcesTask extends ProcessSVGFontTask {

    @OutputFile
    File outputFile

    Map<String, String> resValues

    @Override
    void perform() {
        resValues = [:]

        super.perform()

        def xml = new MarkupBuilder(outputFile.newWriter())
        xml.mkp.xmlDeclaration version: '1.0', encoding: 'UTF-8'
        xml.resources() {
            resValues.each { String k, String v ->
                string(name: k, translatable: false) {
                    mkp.yieldUnescaped(v.collectReplacements({
                        "&#" + ((int) it.charValue()) + ";"
                    }))
                }
            }
        }
    }

    @Override
    void onGlyphFound(GPathResult svg, GPathResult glyph, String glyphName, String glyphPath, String resName, String resValue) {
        resValues.put(resName, resValue)
    }

}
