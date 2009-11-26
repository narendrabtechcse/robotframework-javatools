package hudson.plugins.robotframework;

import static org.junit.Assert.*;
import net.htmlparser.jericho.StreamedSource;

import org.junit.Test;


public class RobotFrameworkHtmlParserTest {

    @Test
    public void testParinsg() {
        StreamedSource source = new StreamedSource(REPORT_HTML);
        RobotFrameworkHtmlParser parser = new RobotFrameworkHtmlParser();
        String html = parser.parse(source);
        assertEquals(EXPECTED_OUTPUT, html);
    }
    
    private static final String REPORT_HTML = 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" + 
        "<html>\n" + 
        "<head>\n" + 
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + 
        "<meta http-equiv=\"Expires\" content=\"Mon, 20 Jan 2001 20:01:21 GMT\" />\n" + 
        "<meta name=\"generator\" content=\"Robot trunk 20091020 (Python 2.6.2 on linux2)\" />\n" + 
        "<style media=\"all\" type=\"text/css\">\n" + 
        "  /* Background color (green or red) */\n" + 
        "  body {\n" + 
        "    background: #99FF66;\n" + 
        "  }\n" + 
        "  /* Generic Table Styles */\n" + 
        "  table {\n" + 
        "    background: white;\n" + 
        "    border: 1px solid black;\n" + 
        "    border-collapse: collapse;\n" + 
        "    empty-cells: show;\n" + 
        "    margin: 0px 1px;\n" + 
        "  }\n" + 
        "  th, td {\n" + 
        "    border: 1px solid black;\n" + 
        "    padding: 1px 5px;\n" + 
        "  }\n" + 
        "  th {\n" + 
        "    background: #C6C6C6;\n" + 
        "    color: black;\n" + 
        "  }\n" + 
        "  /* Test by Suite/Tag Tables */\n" + 
        "  table.tests_by_suite, table.tests_by_tag {\n" + 
        "    width: 100%;\n" + 
        "  }\n" + 
        "  .col_name {\n" + 
        "    width: 13em;\n" + 
        "    font-weight: bold;\n" + 
        "  }\n" + 
        "  .col_doc {\n" + 
        "    min-width: 13em;\n" + 
        "  }\n" + 
        "  .col_tags {\n" + 
        "    width: 10em;\n" + 
        "  }\n" + 
        "  .col_crit {\n" + 
        "    width: 2em;\n" + 
        "    text-align: center;\n" + 
        "  }\n" + 
        "  .col_status {\n" + 
        "    width: 3.5em;\n" + 
        "    text-align: center;\n" + 
        "  }\n" + 
        "  .col_msg {\n" + 
        "    min-width: 13em;\n" + 
        "  }\n" + 
        "  .col_times {\n" + 
        "    width: 9em;\n" + 
        "  }\n" + 
        "  td.col_times{\n" + 
        "    text-align: right;    \n" + 
        "  }\n" + 
        "  .suite_row, .tag_row{\n" + 
        "    background: #E9E9E9;\n" + 
        "  }\n" + 
        "  /* Metadata */\n" + 
        "  .meta_name {\n" + 
        "    font-weight: bold;\n" + 
        "  }\n" + 
        "  /* Details Table */\n" + 
        "  table.details {\n" + 
        "    width: 58em;\n" + 
        "  }\n" + 
        "  table.details th {\n" + 
        "    background: white;\n" + 
        "    width: 9em;\n" + 
        "    text-align: left;\n" + 
        "    vertical-align: top;\n" + 
        "    padding-right: 1em;\n" + 
        "    border: none;\n" + 
        "    padding: 2px 4px;\n" + 
        "  }\n" + 
        "  table.details td {\n" + 
        "    vertical-align: top;\n" + 
        "    border: none;\n" + 
        "    padding: 2px 4px;\n" + 
        "  }\n" + 
        "  .status_fail {\n" + 
        "    color: red;\n" + 
        "    font-weight: bold;\n" + 
        "  }        \n" + 
        "  .status_pass {\n" + 
        "    color: #009900;\n" + 
        "  }\n" + 
        "</style>\n" + 
        "<style media=\"all\" type=\"text/css\">\n" + 
        "  /* Generic styles */ \n" + 
        "  body {\n" + 
        "    font-family: sans-serif;\n" + 
        "    font-size: 0.8em;\n" + 
        "    color: black;\n" + 
        "    padding: 6px; \n" + 
        "  }      \n" + 
        "  h2 {\n" + 
        "    margin-top: 1.2em;\n" + 
        "  }\n" + 
        "  /* Statistics Table */\n" + 
        "  table.statistics {\n" + 
        "    width: 58em;\n" + 
        "    border: 1px solid black;\n" + 
        "    border-collapse: collapse;\n" + 
        "    empty-cells: show;\n" + 
        "    margin-bottom: 1em;\n" + 
        "  }\n" + 
        "  table.statistics td, table.statistics th {\n" + 
        "    border: 1px solid black;\n" + 
        "    padding: 1px 4px;\n" + 
        "    margin: 0px;\n" + 
        "  }\n" + 
        "  table.statistics th {\n" + 
        "    background: #C6C6C6;\n" + 
        "  }\n" + 
        "  .col_stat_name {\n" + 
        "    width: 40em;        \n" + 
        "  }\n" + 
        "  .col_stat {\n" + 
        "    width: 3em;\n" + 
        "    text-align: center;\n" + 
        "  }\n" + 
        "  .stat_name {\n" + 
        "    float: left;\n" + 
        "  }\n" + 
        "  .stat_name a, .stat_name span {\n" + 
        "    font-weight: bold;\n" + 
        "  }\n" + 
        "  .tag_links {\n" + 
        "    font-size: 0.9em;\n" + 
        "    float: right;\n" + 
        "    margin-top: 0.05em;\n" + 
        "  }\n" + 
        "  .tag_links span {\n" + 
        "    margin-left: 0.2em;\n" + 
        "  }\n" + 
        "  /* Statistics Table Graph */\n" + 
        "  .pass_bar { \n" + 
        "    background: #00f000;\n" + 
        "  }\n" + 
        "  .fail_bar {\n" + 
        "    background: red;\n" + 
        "  }\n" + 
        "  .no_tags_bar {\n" + 
        "    background: #E9E9E9;\n" + 
        "  }\n" + 
        "  .graph { \n" + 
        "    position: relative;\n" + 
        "    border: 1px solid black;\n" + 
        "    width: 11em;\n" + 
        "    height: 0.75em;\n" + 
        "    padding: 0px;\n" + 
        "    background: #E9E9E9;\n" + 
        "  }\n" + 
        "  .graph b {\n" + 
        "    display: block;\n" + 
        "    position: relative;\n" + 
        "    height: 100%;\n" + 
        "    float: left;\n" + 
        "    font-size: 4px;  /* to make graphs thin also in IE */\n" + 
        "  }\n" + 
        "  /* Tables in documentation */\n" + 
        "  table.doc {\n" + 
        "    border: 1px solid gray;\n" + 
        "    background: transparent;\n" + 
        "    border-collapse: collapse;\n" + 
        "    empty-cells: show;\n" + 
        "    font-size: 0.9em;\n" + 
        "  }\n" + 
        "  table.doc td {\n" + 
        "    border: 1px solid gray;\n" + 
        "    padding: 0.1em 0.3em;\n" + 
        "    height: 1.2em;\n" + 
        "  }\n" + 
        "  /* Misc Styles */\n" + 
        "  .not_available {\n" + 
        "    color: gray;      /* no grey in IE */\n" + 
        "    font-weight: normal;\n" + 
        "  }\n" + 
        "  .parent_name {\n" + 
        "    font-size: 0.7em;\n" + 
        "    letter-spacing: -0.07em;\n" + 
        "  }\n" + 
        "  a:link, a:visited {\n" + 
        "    text-decoration: none;\n" + 
        "    color: blue;\n" + 
        "  }\n" + 
        "  a:hover, a:active {\n" + 
        "    text-decoration: underline;\n" + 
        "    color: purple;\n" + 
        "  }\n" + 
        "  /* Headers */\n" + 
        "  .header {\n" + 
        "    width: 58em;\n" + 
        "    margin: 6px 0px;\n" + 
        "  }\n" + 
        "  h1 {\n" + 
        "    margin: 0px;\n" + 
        "    width: 70%;\n" + 
        "    float: left;\n" + 
        "  }\n" + 
        "  .times {\n" + 
        "    width: 29%;\n" + 
        "    float: right;\n" + 
        "    text-align: right;\n" + 
        "  }\n" + 
        "  .generated_time, .generated_ago {\n" + 
        "    font-size: 0.9em;\n" + 
        "  }              \n" + 
        "  .spacer {\n" + 
        "    font-size: 0.8em;\n" + 
        "    clear: both;\n" + 
        "  }\n" + 
        "  /* Status text colors */\n" + 
        "  .error, .fail {\n" + 
        "    color: red;\n" + 
        "  }\n" + 
        "  .pass {\n" + 
        "    color: #009900;\n" + 
        "  }\n" + 
        "  .warn {\n" + 
        "    color: #FFCC00;\n" + 
        "  }\n" + 
        "  .not_run {\n" + 
        "    color: #663300;\n" + 
        "  }\n" + 
        "</style>\n" + 
        "<style media=\"print\" type=\"text/css\">\n" + 
        "  body {\n" + 
        "    background: white;\n" + 
        "    padding: 0px; \n" + 
        "    font-size: 8pt;\n" + 
        "  }\n" + 
        "  a:link, a:visited {\n" + 
        "    color: black;\n" + 
        "  }\n" + 
        "  .header, table.details, table.statistics {\n" + 
        "    width: 100%;\n" + 
        "  }\n" + 
        "  .generated_ago, .expand {\n" + 
        "    display: none;\n" + 
        "  }\n" + 
        "</style>\n" + 
        "<title>Test Test Report</title>\n" + 
        "</head>\n" + 
        "<body>\n" + 
        "<div class=\"header\">\n" + 
        "  <h1>Test Test Report</h1>\n" + 
        "  <div class=\"times\">\n" + 
        "    <span class=\"generated_time\">Generated<br />20091125&nbsp;15:09:08&nbsp;GMT&nbsp;+03:00</span><br />\n" + 
        "    <span class=\"generated_ago\">\n" + 
        "<script type=\"text/javascript\">\n" + 
        "  function get_end(number) {\n" + 
        "    if (number == 1) { return ' ' }\n" + 
        "    return 's '\n" + 
        "  }\n" + 
        "  function get_sec_str(secs) {\n" + 
        "    return secs + ' second' + get_end(secs)\n" + 
        "  }\n" + 
        "  function get_min_str(mins) {\n" + 
        "    return mins + ' minute' + get_end(mins)\n" + 
        "  }\n" + 
        "  function get_hour_str(hours) {\n" + 
        "    return hours + ' hour' + get_end(hours)\n" + 
        "  }\n" + 
        "  function get_day_str(days) {\n" + 
        "    return days + ' day' + get_end(days)\n" + 
        "  }\n" + 
        "  function get_year_str(years) {\n" + 
        "    return years + ' year' + get_end(years)\n" + 
        "  }\n" + 
        "  generated = 1259154548\n" + 
        "  current = Math.round(new Date().getTime() / 1000)  // getTime returns millis\n" + 
        "  elapsed = current - generated\n" + 
        "  // elapsed should only be negative if clocks are not in sync\n" + 
        "  if (elapsed < 0) {\n" + 
        "    elapsed = Math.abs(elapsed)\n" + 
        "    prefix = '- '\n" + 
        "  }\n" + 
        "  else {\n" + 
        "    prefix = ''\n" + 
        "  }\n" + 
        "  secs  = elapsed % 60\n" + 
        "  mins  = Math.floor(elapsed / 60) % 60\n" + 
        "  hours = Math.floor(elapsed / (60*60)) % 24\n" + 
        "  days  = Math.floor(elapsed / (60*60*24)) % 365\n" + 
        "  years = Math.floor(elapsed / (60*60*24*365))\n" + 
        "  if (years > 0) { \n" + 
        "    // compencate the effect of leap years (not perfect but should be enough)\n" + 
        "    days = days - Math.floor(years / 4)\n" + 
        "    if (days < 0) { days = 0 }\n" + 
        "    output = get_year_str(years) + get_day_str(days)\n" + 
        "  }\n" + 
        "  else if (days > 0) { \n" + 
        "    output = get_day_str(days) +  get_hour_str(hours)\n" + 
        "  }\n" + 
        "  else if (hours > 0) {\n" + 
        "    output = get_hour_str(hours) + get_min_str(mins)\n" + 
        "  }\n" + 
        "  else if (mins > 0) {\n" + 
        "    output = get_min_str(mins) + get_sec_str(secs)\n" + 
        "  }\n" + 
        "  else {\n" + 
        "    output = get_sec_str(secs)\n" + 
        "  }\n" + 
        "  document.write(prefix + output + 'ago')\n" + 
        "</script>\n" + 
        "    </span>\n" + 
        "  </div>\n" + 
        "</div>\n" + 
        "<div class=\"spacer\">&nbsp;</div>\n" + 
        "<h2>Summary Information</h2>\n" + 
        "\n" + 
        "<table class=\"details\">\n" + 
        "<tr>\n" + 
        "  <th>Status:</th>\n" + 
        "  <td class=\"status_pass\">All tests passed</td>\n" + 
        "</tr>\n" + 
        "  <tr><th>Start Time:</th><td>20091125 15:09:08.134</td></tr>\n" + 
        "  <tr><th>End Time:</th><td>20091125 15:09:08.162</td></tr>\n" + 
        "<tr><th>Elapsed Time:</th><td>00:00:00.028</td></tr>\n" + 
        "</table>\n" + 
        "<h2>Test Statistics</h2>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Total Statistics</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><span>Critical Tests</span></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><span>All Tests</span></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Statistics by Tag</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">No Tags</td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"no_tags_bar\" style=\"width: 100%;\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Statistics by Suite</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><a href=\"#suite_Test\" title=\"Test\">Test</a></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<h2>Test Details by Suite</h2>\n" + 
        "<table class=\"tests_by_suite\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_name\">Name</th>\n" + 
        "<th class=\"col_doc\">Documentation</th>\n" + 
        "<th class=\"col_tags\">Metadata&nbsp;/&nbsp;Tags</th>\n" + 
        "<th class=\"col_crit\">Crit.</th>\n" + 
        "<th class=\"col_status\">Status</th>\n" + 
        "<th class=\"col_msg\">Message</th>\n" + 
        "<th class=\"col_times\">Start&nbsp;/&nbsp;Elapsed</th>\n" + 
        "</tr>\n" + 
        "<tr class=\"suite_row\">\n" + 
        "<td class=\"col_name\"><a href=\"log.html#suite_Test\" id=\"suite_Test\" title=\"Test\">Test</a></td>\n" + 
        "<td class=\"col_doc\"></td>\n" + 
        "<td class=\"col_tags\">\n" + 
        "</td>\n" + 
        "<td class=\"col_crit not_available\">N/A</td>\n" + 
        "<td class=\"col_status pass\">PASS</td>\n" + 
        "<td class=\"col_msg\">1&nbsp;critical&nbsp;test,&nbsp;1&nbsp;passed,&nbsp;<span>0&nbsp;failed</span><br />1&nbsp;test&nbsp;total,&nbsp;1&nbsp;passed,&nbsp;<span>0&nbsp;failed</span></td>\n" + 
        "<td class=\"col_times\">20091125&nbsp;15:09:08<br />00:00:00</td>\n" + 
        "</tr>\n" + 
        "<tr class=\"test_row\">\n" + 
        "<td class=\"col_name\"><a href=\"log.html#test_Test.Foo\" id=\"test_Test.Foo\" title=\"Test.Foo\">Foo</a></td>\n" + 
        "<td class=\"col_doc\"></td>\n" + 
        "<td class=\"col_tags\"></td>\n" + 
        "<td class=\"col_crit\">yes</td>\n" + 
        "<td class=\"col_status pass\">PASS</td>\n" + 
        "<td class=\"col_msg\"></td>\n" + 
        "<td class=\"col_times\">20091125&nbsp;15:09:08<br />00:00:00</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "</body>\n" + 
        "</html>";
    
    private static final String EXPECTED_OUTPUT = 
        "\n" +
        "<div class=\"header\">\n" + 
        "  <h1>Test Test Report</h1>\n" + 
        "  <div class=\"times\">\n" + 
        "    <span class=\"generated_time\">Generated<br />20091125&nbsp;15:09:08&nbsp;GMT&nbsp;+03:00</span><br />\n" + 
        "    <span class=\"generated_ago\">\n" + 
        "<script type=\"text/javascript\">\n" + 
        "  function get_end(number) {\n" + 
        "    if (number == 1) { return ' ' }\n" + 
        "    return 's '\n" + 
        "  }\n" + 
        "  function get_sec_str(secs) {\n" + 
        "    return secs + ' second' + get_end(secs)\n" + 
        "  }\n" + 
        "  function get_min_str(mins) {\n" + 
        "    return mins + ' minute' + get_end(mins)\n" + 
        "  }\n" + 
        "  function get_hour_str(hours) {\n" + 
        "    return hours + ' hour' + get_end(hours)\n" + 
        "  }\n" + 
        "  function get_day_str(days) {\n" + 
        "    return days + ' day' + get_end(days)\n" + 
        "  }\n" + 
        "  function get_year_str(years) {\n" + 
        "    return years + ' year' + get_end(years)\n" + 
        "  }\n" + 
        "  generated = 1259154548\n" + 
        "  current = Math.round(new Date().getTime() / 1000)  // getTime returns millis\n" + 
        "  elapsed = current - generated\n" + 
        "  // elapsed should only be negative if clocks are not in sync\n" + 
        "  if (elapsed < 0) {\n" + 
        "    elapsed = Math.abs(elapsed)\n" + 
        "    prefix = '- '\n" + 
        "  }\n" + 
        "  else {\n" + 
        "    prefix = ''\n" + 
        "  }\n" + 
        "  secs  = elapsed % 60\n" + 
        "  mins  = Math.floor(elapsed / 60) % 60\n" + 
        "  hours = Math.floor(elapsed / (60*60)) % 24\n" + 
        "  days  = Math.floor(elapsed / (60*60*24)) % 365\n" + 
        "  years = Math.floor(elapsed / (60*60*24*365))\n" + 
        "  if (years > 0) { \n" + 
        "    // compencate the effect of leap years (not perfect but should be enough)\n" + 
        "    days = days - Math.floor(years / 4)\n" + 
        "    if (days < 0) { days = 0 }\n" + 
        "    output = get_year_str(years) + get_day_str(days)\n" + 
        "  }\n" + 
        "  else if (days > 0) { \n" + 
        "    output = get_day_str(days) +  get_hour_str(hours)\n" + 
        "  }\n" + 
        "  else if (hours > 0) {\n" + 
        "    output = get_hour_str(hours) + get_min_str(mins)\n" + 
        "  }\n" + 
        "  else if (mins > 0) {\n" + 
        "    output = get_min_str(mins) + get_sec_str(secs)\n" + 
        "  }\n" + 
        "  else {\n" + 
        "    output = get_sec_str(secs)\n" + 
        "  }\n" + 
        "  document.write(prefix + output + 'ago')\n" + 
        "</script>\n" + 
        "    </span>\n" + 
        "  </div>\n" + 
        "</div>\n" + 
        "<div class=\"spacer\">&nbsp;</div>\n" + 
        "<h2>Summary Information</h2>\n" + 
        "\n" + 
        "<table class=\"details\">\n" + 
        "<tr>\n" + 
        "  <th>Status:</th>\n" + 
        "  <td class=\"status_pass\">All tests passed</td>\n" + 
        "</tr>\n" + 
        "  <tr><th>Start Time:</th><td>20091125 15:09:08.134</td></tr>\n" + 
        "  <tr><th>End Time:</th><td>20091125 15:09:08.162</td></tr>\n" + 
        "<tr><th>Elapsed Time:</th><td>00:00:00.028</td></tr>\n" + 
        "</table>\n" + 
        "<h2>Test Statistics</h2>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Total Statistics</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><span>Critical Tests</span></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><span>All Tests</span></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Statistics by Tag</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">No Tags</td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_stat\"></td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"no_tags_bar\" style=\"width: 100%;\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<table class=\"statistics\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_stat_name\">Statistics by Suite</th>\n" + 
        "<th class=\"col_stat\">Total</th>\n" + 
        "<th class=\"col_stat\">Pass</th>\n" + 
        "<th class=\"col_stat\">Fail</th>\n" + 
        "<th class=\"col_graph\">Graph</th>\n" + 
        "</tr>\n" + 
        "<tr>\n" + 
        "<td class=\"col_stat_name\">\n" + 
        "<div class=\"stat_name\"><a href=\"#suite_Test\" title=\"Test\">Test</a></div>\n" + 
        "</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">1</td>\n" + 
        "<td class=\"col_stat\">0</td>\n" + 
        "<td class=\"col_graph\">\n" + 
        "<div class=\"graph\">\n" + 
        "<b class=\"pass_bar\" style=\"width: 99.99%;\" title=\"100.0%\"></b>\n" + 
        "<b class=\"fail_bar\" style=\"width: 0.00%;\" title=\"0.0%\"></b>\n" + 
        "</div>\n" + 
        "</td>\n" + 
        "</tr>\n" + 
        "</table>\n" + 
        "<h2>Test Details by Suite</h2>\n" + 
        "<table class=\"tests_by_suite\">\n" + 
        "<tr>\n" + 
        "<th class=\"col_name\">Name</th>\n" + 
        "<th class=\"col_doc\">Documentation</th>\n" + 
        "<th class=\"col_tags\">Metadata&nbsp;/&nbsp;Tags</th>\n" + 
        "<th class=\"col_crit\">Crit.</th>\n" + 
        "<th class=\"col_status\">Status</th>\n" + 
        "<th class=\"col_msg\">Message</th>\n" + 
        "<th class=\"col_times\">Start&nbsp;/&nbsp;Elapsed</th>\n" + 
        "</tr>\n" + 
        "<tr class=\"suite_row\">\n" + 
        "<td class=\"col_name\"><a href=\"log.html#suite_Test\" id=\"suite_Test\" title=\"Test\">Test</a></td>\n" + 
        "<td class=\"col_doc\"></td>\n" + 
        "<td class=\"col_tags\">\n" + 
        "</td>\n" + 
        "<td class=\"col_crit not_available\">N/A</td>\n" + 
        "<td class=\"col_status pass\">PASS</td>\n" + 
        "<td class=\"col_msg\">1&nbsp;critical&nbsp;test,&nbsp;1&nbsp;passed,&nbsp;<span>0&nbsp;failed</span><br />1&nbsp;test&nbsp;total,&nbsp;1&nbsp;passed,&nbsp;<span>0&nbsp;failed</span></td>\n" + 
        "<td class=\"col_times\">20091125&nbsp;15:09:08<br />00:00:00</td>\n" + 
        "</tr>\n" + 
        "<tr class=\"test_row\">\n" + 
        "<td class=\"col_name\"><a href=\"log.html#test_Test.Foo\" id=\"test_Test.Foo\" title=\"Test.Foo\">Foo</a></td>\n" + 
        "<td class=\"col_doc\"></td>\n" + 
        "<td class=\"col_tags\"></td>\n" + 
        "<td class=\"col_crit\">yes</td>\n" + 
        "<td class=\"col_status pass\">PASS</td>\n" + 
        "<td class=\"col_msg\"></td>\n" + 
        "<td class=\"col_times\">20091125&nbsp;15:09:08<br />00:00:00</td>\n" + 
        "</tr>\n" + 
        "</table>\n";
    
}
