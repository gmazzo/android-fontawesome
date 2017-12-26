package com.github.gmazzo.gradle.plugins

class FontAwesomePluginExtension {

    /**
     * A set of glyphs to include when processing the font. By default includes all resources
     */
    Set<String> includeGlyphs

    /**
     * A glyphs name Pattern (RexExp) to include when processing the font. By default includes all resources
     */
    String includeGlyphsPattern

    /**
     * A set of glyphs to exclude when processing the font. By default excludes none
     */
    Set<String> excludeGlyphs

    /**
     * A glyphs name Pattern (RexExp) to exclude when processing the font. By default excludes none
     */
    String excludeGlyphsPattern

    Closure<Boolean> shouldIncludeGlyph = { String glyphName ->
        return (!includeGlyphs || includeGlyphs.contains(glyphName)) &&
                (!includeGlyphsPattern || glyphName.matches(includeGlyphsPattern)) &&
                (!excludeGlyphs || !excludeGlyphs.contains(glyphName)) &&
                (!excludeGlyphsPattern || !glyphName.matches(excludeGlyphsPattern))
    }

    Closure<String> glyphToResourceName = { String glyphName -> glyphName.toLowerCase().replaceAll('[^a-z0-9_]+', '_') }

}
