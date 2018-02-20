package com.github.gmazzo.gradle.plugins.tasks

import com.github.gmazzo.gradle.plugins.FontAwesomePluginExtension
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

public abstract class ProcessSVGFontTask extends DefaultTask {

    @InputFile
    File svgFont

    @Input
    String fontStyle

    FontAwesomePluginExtension extension

    abstract void onGlyphFound(GPathResult svg, GPathResult glyph, String glyphName, String glyphPath, String resName, String resValue)

    @TaskAction
    void perform() {
        def parser = new XmlSlurper()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

        def svg = parser.parseText(svgFont.text)
        svg.defs.font.glyph.each { glyph ->
            def glyphName = glyph.'@glyph-name'.text()
            def glyphPath = glyph.'@d'.text()
            def resValue = glyph.'@unicode'.text()

            if (glyphPath && resValue && extension.shouldIncludeGlyph(glyphName)) {
                def resName = extension.glyphToResourceName(fontStyle, glyphName).toString()

                onGlyphFound(svg, glyph, glyphName, glyphPath, resName, resValue)
            }
        }
    }

}
