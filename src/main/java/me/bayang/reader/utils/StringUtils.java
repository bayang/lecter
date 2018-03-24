package me.bayang.reader.utils;

import java.util.regex.Matcher;

import org.apache.commons.text.StringEscapeUtils;

import me.bayang.reader.controllers.RssController;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

public class StringUtils {
    
    public static String processContent(String content) {
        String s = stripHeadImages(StringEscapeUtils.unescapeHtml4(stripAds(content)));
        if (s.length() > 160) {
            return s.substring(0, 160);
        }
        return s;
    }
    
    public static String stripAds(String text) {
        Matcher m = RssController.PATTERN.matcher(text);
        if (m.find()) {
            String stripped = text.replaceAll(RssController.PATTERN.toString(), "");
            Source source = new Source(stripped);
            source.setLogger(null);
            Renderer renderer = source.getRenderer();
            renderer.setDecorateFontStyles(true);
            return renderer.toString();
        }
        Source source = new Source(text);
        source.setLogger(null);
        Renderer renderer = source.getRenderer();
        renderer.setDecorateFontStyles(true);
        return renderer.toString();
    }
    
    public static String stripHeadImages(String text) {
        Matcher m = RssController.IMG_PATTERN.matcher(text);
        if (m.find()) {
            return (text.substring(m.end(1)).trim());
        }
        return text;
    }

}
