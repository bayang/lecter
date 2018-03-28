package me.bayang.reader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

import me.bayang.reader.FXMain;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

public class StringUtils {
    
    public static final Pattern PATTERN = Pattern.compile("<center>[.*<>\\/\\a-zA-Z0-9]*Ads from Inoreader[.*<>\\/\\a-zA-Z0-9]*<\\/center>");
    public static final Pattern IMG_PATTERN = Pattern.compile("^\\s*(\\[[%-_a-zA-Z0-9].*(\\.png|\\.jp|\\.pn|\\.jpg|\\.jpeg|\\.JPEG|\\.JPG|\\.PNG|\\.bmp)\\])\\s*.+");
    
    public static String processContent(String content) {
        String s = stripHeadImages(StringEscapeUtils.unescapeHtml4(stripAds(content)));
        if (s.length() > 160) {
            return s.substring(0, 160);
        }
        return s;
    }
    
    public static String stripAds(String text) {
        Matcher m = PATTERN.matcher(text);
        if (m.find()) {
            String stripped = text.replaceAll(PATTERN.toString(), "");
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
        Matcher m = IMG_PATTERN.matcher(text);
        if (m.find()) {
            return (text.substring(m.end(1)).trim());
        }
        return text;
    }
    
    public static void openHyperlink(String url) {
        FXMain.getAppHostServices().showDocument(url);
    }

}
