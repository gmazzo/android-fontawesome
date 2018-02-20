package com.github.gmazzo.gradle.plugins;

/**
 * Created by guillermo.mazzola on 20/02/2018.
 */

public enum FontStyle {

    SOLID("fa-solid-900"),

    REGULAR("fa-regular-400"),

    BRANDS("fa-brands-400");

    final String fontFile;

    FontStyle(String fontFile) {
        this.fontFile = fontFile;
    }

}
