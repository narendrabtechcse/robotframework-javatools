package hudson.plugins.robotframework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

public class RobotFrameworkHtmlParser {

    public String parseFrom(String htmlReportPath) throws MalformedURLException, IOException {
        StreamedSource source = new StreamedSource(new BufferedReader(new FileReader(htmlReportPath)));
        return parse(source);
    }

    public String parse(StreamedSource source) {
        StringBuilder html = new StringBuilder();
        boolean inBody = false;
        for (Segment segment: source) {
           if (segment instanceof Tag) {
               Tag tag = (Tag)segment;
               if (tag.getName().equalsIgnoreCase("body"))
                   inBody = !inBody;
           }
           if (inBody)
               html.append(segment.toString());
        }
        return html.toString();
    }    
}
