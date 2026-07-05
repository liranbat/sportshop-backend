package com.java.sadna.backend.sportshop.common.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

// Rejects SVGs that carry executable content. Pattern-based, not a sanitizer.
public final class SvgSecurityScanner {

    // <script> elements run JS when the SVG is rendered as a document -> XSS.
    private static final Pattern SCRIPT_TAG = Pattern.compile(
            "<\\s*script\\b",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // on*= inline event handlers (onload, onclick, ...) execute JS -> XSS.
    private static final Pattern EVENT_HANDLER = Pattern.compile(
            "\\bon[a-z]+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    // (xlink:)?href with any URL scheme (http:, https:, file:, data:, javascript:, ...)
    // -> XSS / SSRF / data-exfil; only fragment refs ("#id") are allowed.
    private static final Pattern HREF_SCHEME = Pattern.compile(
            "(?:xlink:)?href\\s*=\\s*[\"']?\\s*[a-z][a-z0-9+.-]*:",
            Pattern.CASE_INSENSITIVE
    );

    // <!DOCTYPE / <!ENTITY enable XXE on parsers that resolve external entities.
    private static final Pattern DTD_DECLARATION = Pattern.compile(
            "<!\\s*(?:DOCTYPE|ENTITY)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private SvgSecurityScanner() {
    }

    public static boolean isUnsafe(byte[] svgBytes) {
        if (svgBytes == null || svgBytes.length == 0) {
            return false;
        }
        // Reject UTF-16: ASCII regexes below can't see "<script" interleaved with null bytes.
        if (isUtf16(svgBytes)) {
            return true;
        }
        String content = new String(svgBytes, StandardCharsets.UTF_8);
        return SCRIPT_TAG.matcher(content).find()
                || EVENT_HANDLER.matcher(content).find()
                || HREF_SCHEME.matcher(content).find()
                || DTD_DECLARATION.matcher(content).find();
    }

    private static boolean isUtf16(byte[] data) {
        if (data.length < 2) {
            return false;
        }
        int b0 = data[0] & 0xFF;
        int b1 = data[1] & 0xFF;
        return (b0 == 0xFE && b1 == 0xFF) || (b0 == 0xFF && b1 == 0xFE);
    }
}
