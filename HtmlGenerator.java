import java.util.List;
//ALL CRUD OPERATIONS HAVE TO BE DONE VIA HTML UNFORTUNATELY
public class HtmlGenerator {

    // Common CSS styles used across all pages
    private static String getStyles() {
        return "<style>\r\n" +
            "* { margin: 0; padding: 0; box-sizing: border-box; }\r\n" +
            "\r\n" +
            "body { \r\n" +
            "    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;\r\n" +
            "    background: #f8f9fa;\r\n" +
            "    min-height: 100vh;\r\n" +
            "    color: #1a2332;\r\n" +
            "    line-height: 1.6;\r\n" +
            "    display: flex;\r\n" +
            "    flex-direction: column;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Header Section */\r\n" +
            ".header {\r\n" +
            "    background: #1a2332;\r\n" +
            "    color: white;\r\n" +
            "    padding: 0;\r\n" +
            "    border-bottom: 1px solid #2d3e50;\r\n" +
            "    box-shadow: 0 2px 4px rgba(0,0,0,0.1);\r\n" +
            "}\r\n" +
            "\r\n" +
            ".header-content {\r\n" +
            "    max-width: 1400px;\r\n" +
            "    margin: 0 auto;\r\n" +
            "    padding: 20px 40px;\r\n" +
            "    display: flex;\r\n" +
            "    align-items: center;\r\n" +
            "    justify-content: space-between;\r\n" +
            "    flex-wrap: wrap;\r\n" +
            "    gap: 20px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".logo-section {\r\n" +
            "    display: flex;\r\n" +
            "    align-items: center;\r\n" +
            "    gap: 16px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".logo {\r\n" +
            "    height: 45px;\r\n" +
            "    width: auto;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".brand {\r\n" +
            "    display: flex;\r\n" +
            "    flex-direction: column;\r\n" +
            "}\r\n" +
            "\r\n" +
            "h1 {\r\n" +
            "    color: white;\r\n" +
            "    font-size: 1.5em;\r\n" +
            "    margin: 0;\r\n" +
            "    font-weight: 600;\r\n" +
            "    letter-spacing: -0.3px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".subtitle {\r\n" +
            "    color: #a8b2c1;\r\n" +
            "    font-size: 0.8em;\r\n" +
            "    margin: 0;\r\n" +
            "    font-weight: 400;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".nav {\r\n" +
            "    display: flex;\r\n" +
            "    gap: 8px;\r\n" +
            "    flex-wrap: wrap;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".nav a {\r\n" +
            "    padding: 10px 20px;\r\n" +
            "    background: transparent;\r\n" +
            "    color: #a8b2c1;\r\n" +
            "    text-decoration: none;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    font-weight: 500;\r\n" +
            "    border: 1px solid #2d3e50;\r\n" +
            "    transition: all 0.2s ease;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".nav a:hover {\r\n" +
            "    background: #2d3e50;\r\n" +
            "    color: white;\r\n" +
            "    border-color: #3d4e60;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Main Content */\r\n" +
            ".content {\r\n" +
            "    max-width: 1400px;\r\n" +
            "    margin: 0 auto;\r\n" +
            "    padding: 40px;\r\n" +
            "    flex: 1;\r\n" +
            "}\r\n" +
            "\r\n" +
            "h2 {\r\n" +
            "    color: #1a2332;\r\n" +
            "    font-size: 1.5em;\r\n" +
            "    margin-bottom: 24px;\r\n" +
            "    font-weight: 600;\r\n" +
            "    letter-spacing: -0.3px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".stats {\r\n" +
            "    display: inline-block;\r\n" +
            "    background: white;\r\n" +
            "    padding: 8px 16px;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    font-size: 0.85em;\r\n" +
            "    color: #5a6c7d;\r\n" +
            "    font-weight: 500;\r\n" +
            "    margin-bottom: 20px;\r\n" +
            "    border: 1px solid #e1e8ed;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Table Styling */\r\n" +
            "table {\r\n" +
            "    width: 100%;\r\n" +
            "    border-collapse: separate;\r\n" +
            "    border-spacing: 0;\r\n" +
            "    background: white;\r\n" +
            "    border-radius: 8px;\r\n" +
            "    overflow: hidden;\r\n" +
            "    border: 1px solid #e1e8ed;\r\n" +
            "}\r\n" +
            "\r\n" +
            "th {\r\n" +
            "    background: #f8f9fa;\r\n" +
            "    color: #5a6c7d;\r\n" +
            "    padding: 14px 20px;\r\n" +
            "    text-align: left;\r\n" +
            "    font-weight: 600;\r\n" +
            "    font-size: 0.8em;\r\n" +
            "    text-transform: uppercase;\r\n" +
            "    letter-spacing: 0.5px;\r\n" +
            "    border-bottom: 2px solid #e1e8ed;\r\n" +
            "}\r\n" +
            "\r\n" +
            "td {\r\n" +
            "    padding: 16px 20px;\r\n" +
            "    border-bottom: 1px solid #f0f3f5;\r\n" +
            "    vertical-align: middle;\r\n" +
            "    background: white;\r\n" +
            "    color: #2d3e50;\r\n" +
            "    font-size: 0.95em;\r\n" +
            "}\r\n" +
            "\r\n" +
            "tr:last-child td {\r\n" +
            "    border-bottom: none;\r\n" +
            "}\r\n" +
            "\r\n" +
            "tbody tr:hover td {\r\n" +
            "    background: #f8f9fa;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".delete-btn {\r\n" +
            "    color: #dc3545;\r\n" +
            "    text-decoration: none;\r\n" +
            "    font-weight: 500;\r\n" +
            "    padding: 6px 14px;\r\n" +
            "    border-radius: 4px;\r\n" +
            "    transition: all 0.2s ease;\r\n" +
            "    display: inline-block;\r\n" +
            "    border: 1px solid #f5c2c7;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".delete-btn:hover {\r\n" +
            "    background: #dc3545;\r\n" +
            "    color: white;\r\n" +
            "    border-color: #dc3545;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".thumb {\r\n" +
            "    width: 50px;\r\n" +
            "    height: 50px;\r\n" +
            "    object-fit: cover;\r\n" +
            "    border-radius: 4px;\r\n" +
            "    border: 1px solid #e1e8ed;\r\n" +
            "    transition: transform 0.2s ease;\r\n" +
            "    cursor: pointer;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".thumb:hover {\r\n" +
            "    transform: scale(2.5);\r\n" +
            "    box-shadow: 0 8px 24px rgba(0,0,0,0.15);\r\n" +
            "    z-index: 10;\r\n" +
            "    border-color: #1a2332;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Form Styling */\r\n" +
            "form {\r\n" +
            "    background: white;\r\n" +
            "    padding: 40px;\r\n" +
            "    border-radius: 8px;\r\n" +
            "    border: 1px solid #e1e8ed;\r\n" +
            "    max-width: 600px;\r\n" +
            "}\r\n" +
            "\r\n" +
            "form div {\r\n" +
            "    margin-bottom: 24px;\r\n" +
            "}\r\n" +
            "\r\n" +
            "label {\r\n" +
            "    display: block;\r\n" +
            "    margin-bottom: 8px;\r\n" +
            "    color: #2d3e50;\r\n" +
            "    font-weight: 600;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=text], input[type=date], input[type=time] {\r\n" +
            "    width: 100%;\r\n" +
            "    padding: 11px 14px;\r\n" +
            "    border: 1px solid #d0d7de;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    font-size: 0.95em;\r\n" +
            "    transition: all 0.2s ease;\r\n" +
            "    background: #f8f9fa;\r\n" +
            "    color: #1a2332;\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=text]:focus, input[type=date]:focus, input[type=time]:focus {\r\n" +
            "    outline: none;\r\n" +
            "    border-color: #1a2332;\r\n" +
            "    background: white;\r\n" +
            "    box-shadow: 0 0 0 3px rgba(26, 35, 50, 0.1);\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=file] {\r\n" +
            "    padding: 11px;\r\n" +
            "    border: 1px solid #d0d7de;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    width: 100%;\r\n" +
            "    cursor: pointer;\r\n" +
            "    background: #f8f9fa;\r\n" +
            "    transition: all 0.2s ease;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=file]:hover {\r\n" +
            "    border-color: #1a2332;\r\n" +
            "    background: white;\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=submit] {\r\n" +
            "    padding: 12px 28px;\r\n" +
            "    background: #1a2332;\r\n" +
            "    color: white;\r\n" +
            "    border: none;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    font-size: 0.95em;\r\n" +
            "    font-weight: 600;\r\n" +
            "    cursor: pointer;\r\n" +
            "    transition: all 0.2s ease;\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=submit]:hover {\r\n" +
            "    background: #2d3e50;\r\n" +
            "    transform: translateY(-1px);\r\n" +
            "    box-shadow: 0 4px 12px rgba(26, 35, 50, 0.15);\r\n" +
            "}\r\n" +
            "\r\n" +
            "input[type=submit]:active {\r\n" +
            "    transform: translateY(0);\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Alerts */\r\n" +
            ".success {\r\n" +
            "    background: #d1e7dd;\r\n" +
            "    color: #0f5132;\r\n" +
            "    padding: 14px 18px;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    margin-bottom: 24px;\r\n" +
            "    border-left: 3px solid #0a3622;\r\n" +
            "    font-weight: 500;\r\n" +
            "    font-size: 0.95em;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".error {\r\n" +
            "    background: #f8d7da;\r\n" +
            "    color: #721c24;\r\n" +
            "    padding: 14px 18px;\r\n" +
            "    border-radius: 6px;\r\n" +
            "    margin-bottom: 24px;\r\n" +
            "    border-left: 3px solid #721c24;\r\n" +
            "    font-weight: 500;\r\n" +
            "    font-size: 0.95em;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Empty State */\r\n" +
            ".empty-state {\r\n" +
            "    text-align: center;\r\n" +
            "    padding: 80px 20px;\r\n" +
            "    background: white;\r\n" +
            "    border-radius: 8px;\r\n" +
            "    border: 1px solid #e1e8ed;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".empty-state svg {\r\n" +
            "    width: 100px;\r\n" +
            "    height: 100px;\r\n" +
            "    margin-bottom: 20px;\r\n" +
            "    opacity: 0.4;\r\n" +
            "    color: #5a6c7d;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".empty-state h3 {\r\n" +
            "    color: #2d3e50;\r\n" +
            "    margin-bottom: 8px;\r\n" +
            "    font-weight: 600;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".empty-state p {\r\n" +
            "    color: #5a6c7d;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Search */\r\n" +
            ".search-wrapper {\r\n" +
            "    display: flex;\r\n" +
            "    gap: 12px;\r\n" +
            "    align-items: flex-end;\r\n" +
            "    max-width: 600px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".search-wrapper > div {\r\n" +
            "    flex: 1;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Footer */\r\n" +
            ".footer {\r\n" +
            "    background: #1a2332;\r\n" +
            "    color: #a8b2c1;\r\n" +
            "    padding: 40px 0;\r\n" +
            "    margin-top: 60px;\r\n" +
            "    border-top: 1px solid #2d3e50;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-content {\r\n" +
            "    max-width: 1400px;\r\n" +
            "    margin: 0 auto;\r\n" +
            "    padding: 0 40px;\r\n" +
            "    display: grid;\r\n" +
            "    grid-template-columns: 2fr 1fr 1fr 1fr;\r\n" +
            "    gap: 40px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section h4 {\r\n" +
            "    color: white;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "    font-weight: 600;\r\n" +
            "    margin-bottom: 16px;\r\n" +
            "    text-transform: uppercase;\r\n" +
            "    letter-spacing: 0.5px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section p {\r\n" +
            "    font-size: 0.9em;\r\n" +
            "    line-height: 1.6;\r\n" +
            "    margin-bottom: 12px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section ul {\r\n" +
            "    list-style: none;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section ul li {\r\n" +
            "    margin-bottom: 10px;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section a {\r\n" +
            "    color: #a8b2c1;\r\n" +
            "    text-decoration: none;\r\n" +
            "    font-size: 0.9em;\r\n" +
            "    transition: color 0.2s ease;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-section a:hover {\r\n" +
            "    color: white;\r\n" +
            "}\r\n" +
            "\r\n" +
            ".footer-bottom {\r\n" +
            "    max-width: 1400px;\r\n" +
            "    margin: 32px auto 0;\r\n" +
            "    padding: 24px 40px 0;\r\n" +
            "    border-top: 1px solid #2d3e50;\r\n" +
            "    text-align: center;\r\n" +
            "    font-size: 0.85em;\r\n" +
            "    color: #5a6c7d;\r\n" +
            "}\r\n" +
            "\r\n" +
            "/* Responsive */\r\n" +
            "@media (max-width: 768px) {\r\n" +
            "    .header-content { \r\n" +
            "        padding: 20px;\r\n" +
            "        flex-direction: column;\r\n" +
            "        align-items: flex-start;\r\n" +
            "    }\r\n" +
            "    .content { padding: 24px 20px; }\r\n" +
            "    h1 { font-size: 1.3em; }\r\n" +
            "    h2 { font-size: 1.3em; }\r\n" +
            "    table { font-size: 0.85em; }\r\n" +
            "    th, td { padding: 10px 12px; }\r\n" +
            "    .nav a { font-size: 0.85em; padding: 8px 14px; }\r\n" +
            "    form { padding: 24px; }\r\n" +
            "    .footer-content {\r\n" +
            "        grid-template-columns: 1fr;\r\n" +
            "        padding: 0 20px;\r\n" +
            "    }\r\n" +
            "    .footer-bottom { padding: 24px 20px 0; }\r\n" +
            "}\r\n" +
            "</style>\r\n";
    }
 
    // Common HTML page wrapper with header, logo, and footer
    private static String pageStart(String title) {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\">\r\n" +
               "<head>\r\n" +
               "    <meta charset=\"UTF-8\">\r\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" +
               "    <meta http-equiv=\"Cache-Control\" content=\"no-store\">\r\n" +
               "    <title>" + title + "</title>\r\n" +
               getStyles() +
               "</head>\r\n" +
               "<body>\r\n" +
               "    <div class=\"header\">\r\n" +
               "        <div class=\"header-content\">\r\n" +
               "            <div class=\"logo-section\">\r\n" +
               "                <img src=\"/logo\" alt=\"Appointly Logo\" class=\"logo\">\r\n" +
               "                <div class=\"brand\">\r\n" +
               "                    <h1>Appointly</h1>\r\n" +
               "                    <p class=\"subtitle\">Professional Scheduling Platform</p>\r\n" +
               "                </div>\r\n" +
               "            </div>\r\n" +
               "            <div class=\"nav\">\r\n" +
               "                <a href=\"/\">All Appointments</a>\r\n" +
               "                <a href=\"/add\">New Appointment</a>\r\n" +
               "                <a href=\"/search\">Search</a>\r\n" +
               "            </div>\r\n" +
               "        </div>\r\n" +
               "    </div>\r\n" +
               "    <div class=\"content\">\r\n";
    }
 
    private static String pageEnd() {
        return "    </div>\r\n" +
               "    <footer class=\"footer\">\r\n" +
               "        <div class=\"footer-content\">\r\n" +
               "            <div class=\"footer-section\">\r\n" +
               "                <h4>About Appointly</h4>\r\n" +
               "                <p>A professional appointment management system designed for efficient scheduling and meeting coordination.</p>\r\n" +
               "                <p>&copy; 2026 Appointly. All rights reserved.</p>\r\n" +
               "            </div>\r\n" +
               "            <div class=\"footer-section\">\r\n" +
               "                <h4>Product</h4>\r\n" +
               "                <ul>\r\n" +
               "                    <li><a href=\"/\">Dashboard</a></li>\r\n" +
               "                    <li><a href=\"/add\">Schedule</a></li>\r\n" +
               "                    <li><a href=\"/search\">Search</a></li>\r\n" +
               "                </ul>\r\n" +
               "            </div>\r\n" +
               "            <div class=\"footer-section\">\r\n" +
               "                <h4>Resources</h4>\r\n" +
               "                <ul>\r\n" +
               "                    <li><a href=\"#\">Documentation</a></li>\r\n" +
               "                    <li><a href=\"#\">API Reference</a></li>\r\n" +
               "                    <li><a href=\"#\">Support</a></li>\r\n" +
               "                </ul>\r\n" +
               "            </div>\r\n" +
               "            <div class=\"footer-section\">\r\n" +
               "                <h4>Company</h4>\r\n" +
               "                <ul>\r\n" +
               "                    <li><a href=\"#\">About Us</a></li>\r\n" +
               "                    <li><a href=\"#\">Contact</a></li>\r\n" +
               "                    <li><a href=\"#\">Privacy Policy</a></li>\r\n" +
               "                </ul>\r\n" +
               "            </div>\r\n" +
               "        </div>\r\n" +
               "        <div class=\"footer-bottom\">\r\n" +
               "            <p>Built with precision. Powered by innovation.</p>\r\n" +
               "        </div>\r\n" +
               "    </footer>\r\n" +
               "</body>\r\n</html>\r\n";
    }
 
    // Main page — list all appointments
    public static String generateHomePage(List<Appointment> appointments,
                                          String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Appointment Manager"));
 
        html.append("        <h2>All Appointments</h2>\r\n");
 
        // Show stats
        html.append("        <div class=\"stats\">Total: " + 
                    appointments.size() + " appointment" + 
                    (appointments.size() != 1 ? "s" : "") + "</div>\r\n");
 
        // Show success or error message if present
        if (message != null && !message.isEmpty()) {
            String cls = isError ? "error" : "success";
            html.append("        <div class=\"" + cls + "\">" + message + "</div>\r\n");
        }
 
        if (appointments.isEmpty()) {
            html.append("        <div class=\"empty-state\">\r\n");
            html.append("            <svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\">\r\n");
            html.append("                <path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z\"></path>\r\n");
            html.append("            </svg>\r\n");
            html.append("            <h3>No appointments yet</h3>\r\n");
            html.append("            <p>Click 'Add Appointment' to get started!</p>\r\n");
            html.append("        </div>\r\n");
        } else {
            html.append("        <table>\r\n");
            html.append("            <tr>\r\n");
            html.append("                <th>#</th>\r\n");
            html.append("                <th>Date</th>\r\n");
            html.append("                <th>Time</th>\r\n");
            html.append("                <th>Person</th>\r\n");
            html.append("                <th>Description</th>\r\n");
            html.append("                <th>Photo</th>\r\n");
            html.append("                <th>Actions</th>\r\n");
            html.append("            </tr>\r\n");
 
            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                int num = i + 1;
                html.append("            <tr>\r\n");
                html.append("                <td><strong>" + num + "</strong></td>\r\n");
                html.append("                <td>" + escapeHtml(a.getDate()) + "</td>\r\n");
                html.append("                <td>" + escapeHtml(a.getTime()) + "</td>\r\n");
                html.append("                <td>" + escapeHtml(a.getPerson()) + "</td>\r\n");
                html.append("                <td>" + escapeHtml(a.getDescription()) + "</td>\r\n");
 
                // Show image thumbnail if available
                if (a.hasImage()) {
                    html.append("                <td><img class=\"thumb\" src=\"/image/" +
                        escapeHtml(a.getImageFile()) + "\" alt=\"" + 
                        escapeHtml(a.getPerson()) + "\"></td>\r\n");
                } else {
                    html.append("                <td style=\"color: #a0aec0;\">—</td>\r\n");
                }
 
                // Edit and Delete links
                html.append("                <td>\r\n");
                html.append("                    <a href=\"/edit?index=" + num + "\" style=\"color:#1a2332;text-decoration:none;font-weight:500;padding:6px 14px;border-radius:4px;display:inline-block;border:1px solid #d0d7de;font-size:0.9em;margin-right:8px;transition:all 0.2s ease;\">Edit</a>\r\n");
                html.append("                    <a class=\"delete-btn\" " +
                    "href=\"/delete?index=" + num + "\" " +
                    "onclick=\"return confirm('Delete this appointment?');\">Delete</a>\r\n");
                html.append("                </td>\r\n");
                html.append("            </tr>\r\n");
            }
            html.append("        </table>\r\n");
        }
 
        html.append(pageEnd());
        return html.toString();
    }
 
    // Add appointment form page
    public static String generateAddPage(String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Add Appointment"));
        html.append("        <h2>Add New Appointment</h2>\r\n");
 
        if (message != null && !message.isEmpty()) {
            String cls = isError ? "error" : "success";
            html.append("        <div class=\"" + cls + "\">" + message + "</div>\r\n");
        }
 
        // Multipart form required for file upload
        html.append("        <form method=\"POST\" action=\"/add\" " +
                    "enctype=\"multipart/form-data\">\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Date</label>\r\n");
        html.append("                <input type=\"date\" name=\"date\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Time</label>\r\n");
        html.append("                <input type=\"time\" name=\"time\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Person</label>\r\n");
        html.append("                <input type=\"text\" name=\"person\" " +
                    "placeholder=\"Contact name\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Description</label>\r\n");
        html.append("                <input type=\"text\" name=\"description\" " +
                    "placeholder=\"Meeting details\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Photo (optional)</label>\r\n");
        html.append("                <input type=\"file\" name=\"image\" " +
                    "accept=\"image/*\">\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <input type=\"submit\" value=\"Create Appointment\">\r\n");
        html.append("            </div>\r\n");
        html.append("        </form>\r\n");
 
        html.append(pageEnd());
        return html.toString();
    }
 
    // Edit appointment form page
    public static String generateEditPage(Appointment appointment, int index, 
                                          String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Edit Appointment"));
        html.append("        <h2>Edit Appointment</h2>\r\n");
 
        if (message != null && !message.isEmpty()) {
            String cls = isError ? "error" : "success";
            html.append("        <div class=\"" + cls + "\">" + message + "</div>\r\n");
        }
 
        // Multipart form required for file upload
        html.append("        <form method=\"POST\" action=\"/edit?index=" + index + "\" " +
                    "enctype=\"multipart/form-data\">\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Date</label>\r\n");
        html.append("                <input type=\"date\" name=\"date\" value=\"" + 
                    escapeHtml(appointment.getDate()) + "\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Time</label>\r\n");
        html.append("                <input type=\"time\" name=\"time\" value=\"" + 
                    escapeHtml(appointment.getTime()) + "\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Person</label>\r\n");
        html.append("                <input type=\"text\" name=\"person\" value=\"" + 
                    escapeHtml(appointment.getPerson()) + "\" " +
                    "placeholder=\"Contact name\" required>\r\n");
        html.append("            </div>\r\n");
        html.append("            <div>\r\n");
        html.append("                <label>Description</label>\r\n");
        html.append("                <input type=\"text\" name=\"description\" value=\"" + 
                    escapeHtml(appointment.getDescription()) + "\" " +
                    "placeholder=\"Meeting details\" required>\r\n");
        html.append("            </div>\r\n");
        
        // Show current image if exists
        if (appointment.hasImage()) {
            html.append("            <div>\r\n");
            html.append("                <label>Current Photo</label>\r\n");
            html.append("                <div style=\"margin-bottom:12px;\">\r\n");
            html.append("                    <img src=\"/image/" + escapeHtml(appointment.getImageFile()) + 
                        "\" style=\"width:120px;height:120px;object-fit:cover;border-radius:8px;border:1px solid #e1e8ed;\">\r\n");
            html.append("                </div>\r\n");
            html.append("            </div>\r\n");
        }
        
        html.append("            <div>\r\n");
        html.append("                <label>Change Photo (optional)</label>\r\n");
        html.append("                <input type=\"file\" name=\"image\" accept=\"image/*\">\r\n");
        if (appointment.hasImage()) {
            html.append("                <p style=\"font-size:0.85em;color:#5a6c7d;margin-top:6px;\">Leave empty to keep current photo</p>\r\n");
        }
        html.append("            </div>\r\n");
        
        html.append("            <div style=\"display:flex;gap:12px;\">\r\n");
        html.append("                <input type=\"submit\" value=\"Update Appointment\">\r\n");
        html.append("                <a href=\"/\" style=\"padding:12px 28px;background:#f8f9fa;color:#2d3e50;border:1px solid #d0d7de;border-radius:6px;text-decoration:none;font-size:0.95em;font-weight:600;transition:all 0.2s ease;display:inline-block;\">Cancel</a>\r\n");
        html.append("            </div>\r\n");
        html.append("        </form>\r\n");
 
        html.append(pageEnd());
        return html.toString();
    }
 
    // Search form and results page
    public static String generateSearchPage(String keyword,
            List<Appointment> results) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Search Appointments"));
        html.append("        <h2>Search Appointments</h2>\r\n");
 
        // Advanced search form
        html.append("        <form method=\"GET\" action=\"/search\">\r\n");
        html.append("            <div style=\"background:white;padding:32px;border-radius:8px;border:1px solid #e1e8ed;max-width:800px;\">\r\n");
        html.append("                <h3 style=\"margin:0 0 24px 0;color:#1a2332;font-size:1.1em;font-weight:600;\">Advanced Search</h3>\r\n");
        
        // Search grid
        html.append("                <div style=\"display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:20px;\">\r\n");
        
        // Keyword search
        html.append("                    <div style=\"grid-column:1/-1;\">\r\n");
        html.append("                        <label>Keyword</label>\r\n");
        html.append("                        <input type=\"text\" name=\"keyword\" " +
                    "placeholder=\"Search by name or description...\" " +
                    "value=\"" + (keyword != null ? escapeHtml(keyword) : "") + "\">\r\n");
        html.append("                    </div>\r\n");
        
        // Date range
        html.append("                    <div>\r\n");
        html.append("                        <label>From Date</label>\r\n");
        html.append("                        <input type=\"date\" name=\"fromDate\">\r\n");
        html.append("                    </div>\r\n");
        html.append("                    <div>\r\n");
        html.append("                        <label>To Date</label>\r\n");
        html.append("                        <input type=\"date\" name=\"toDate\">\r\n");
        html.append("                    </div>\r\n");
        
        // Time range
        html.append("                    <div>\r\n");
        html.append("                        <label>From Time</label>\r\n");
        html.append("                        <input type=\"time\" name=\"fromTime\">\r\n");
        html.append("                    </div>\r\n");
        html.append("                    <div>\r\n");
        html.append("                        <label>To Time</label>\r\n");
        html.append("                        <input type=\"time\" name=\"toTime\">\r\n");
        html.append("                    </div>\r\n");
        
        // Person filter
        html.append("                    <div>\r\n");
        html.append("                        <label>Person</label>\r\n");
        html.append("                        <input type=\"text\" name=\"person\" placeholder=\"Filter by contact name\">\r\n");
        html.append("                    </div>\r\n");
        
        // Sort options
        html.append("                    <div>\r\n");
        html.append("                        <label>Sort By</label>\r\n");
        html.append("                        <select name=\"sortBy\" style=\"width:100%;padding:11px 14px;border:1px solid #d0d7de;border-radius:6px;font-size:0.95em;background:#f8f9fa;color:#1a2332;\">\r\n");
        html.append("                            <option value=\"date\">Date (Newest First)</option>\r\n");
        html.append("                            <option value=\"date_asc\">Date (Oldest First)</option>\r\n");
        html.append("                            <option value=\"time\">Time (Earliest First)</option>\r\n");
        html.append("                            <option value=\"person\">Person (A-Z)</option>\r\n");
        html.append("                        </select>\r\n");
        html.append("                    </div>\r\n");
        
        html.append("                </div>\r\n");
        
        // Search buttons
        html.append("                <div style=\"display:flex;gap:12px;\">\r\n");
        html.append("                    <input type=\"submit\" value=\"Search\">\r\n");
        html.append("                    <a href=\"/search\" style=\"padding:12px 28px;background:#f8f9fa;color:#2d3e50;border:1px solid #d0d7de;border-radius:6px;text-decoration:none;font-size:0.95em;font-weight:600;transition:all 0.2s ease;\">Clear Filters</a>\r\n");
        html.append("                </div>\r\n");
        html.append("            </div>\r\n");
        html.append("        </form>\r\n");
 
        // Show results if a search was performed
        if (keyword != null && !keyword.isEmpty()) {
            if (results == null || results.isEmpty()) {
                html.append("        <div class=\"error\" style=\"margin-top:24px;\">No appointments found matching: <strong>" + 
                            escapeHtml(keyword) + "</strong></div>\r\n");
            } else {
                html.append("        <div style=\"margin-top:32px;\">\r\n");
                html.append("            <div style=\"display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;\">\r\n");
                html.append("                <h3 style=\"color:#1a2332;margin:0;font-size:1.2em;font-weight:600;\">Search Results</h3>\r\n");
                html.append("                <div class=\"stats\">Found " +
                            results.size() + " result" + 
                            (results.size() != 1 ? "s" : "") + "</div>\r\n");
                html.append("            </div>\r\n");
                html.append("            <table>\r\n");
                html.append("                <tr>\r\n");
                html.append("                    <th>Date</th><th>Time</th>" +
                            "<th>Person</th><th>Description</th><th>Photo</th>\r\n");
                html.append("                </tr>\r\n");
 
                for (Appointment a : results) {
                    html.append("                <tr>\r\n");
                    html.append("                    <td><strong>" + escapeHtml(a.getDate()) + "</strong></td>\r\n");
                    html.append("                    <td>" + escapeHtml(a.getTime()) + "</td>\r\n");
                    html.append("                    <td>" + escapeHtml(a.getPerson()) + "</td>\r\n");
                    html.append("                    <td>" + escapeHtml(a.getDescription()) + "</td>\r\n");
                    if (a.hasImage()) {
                        html.append("                    <td><img class=\"thumb\" src=\"/image/"
                            + escapeHtml(a.getImageFile()) + "\" alt=\"" + 
                            escapeHtml(a.getPerson()) + "\"></td>\r\n");
                    } else {
                        html.append("                    <td style=\"color:#a0aec0;\">—</td>\r\n");
                    }
                    html.append("                </tr>\r\n");
                }
                html.append("            </table>\r\n");
                html.append("        </div>\r\n");
            }
        }
 
        html.append(pageEnd());
        return html.toString();
    }
 
    // Simple 404 page
    public static String generate404() {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\"><head><meta charset=\"UTF-8\">" +
               "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<title>404 Not Found</title>" +
               getStyles() +
               "</head>\r\n" +
               "<body>" +
               "<div class=\"header\"><div class=\"header-content\">" +
               "<h1>404</h1><p class=\"subtitle\">Page Not Found</p>" +
               "<div class=\"nav\"><a href=\"/\">← Back to Home</a></div>" +
               "</div></div>" +
               "<div class=\"content\"><div style=\"background:white;padding:60px;border-radius:12px;text-align:center;box-shadow:0 2px 12px rgba(0,0,0,0.08);\">" +
               "<h2>The page you're looking for doesn't exist.</h2></div></div>" +
               "</body></html>\r\n";
    }
 
    // Simple 405 page
    public static String generate405() {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\"><head><meta charset=\"UTF-8\">" +
               "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<title>405 Method Not Allowed</title>" +
               getStyles() +
               "</head>\r\n" +
               "<body>" +
               "<div class=\"header\"><div class=\"header-content\">" +
               "<h1>405</h1><p class=\"subtitle\">Method Not Allowed</p>" +
               "<div class=\"nav\"><a href=\"/\">← Back to Home</a></div>" +
               "</div></div>" +
               "<div class=\"content\"><div style=\"background:white;padding:60px;border-radius:12px;text-align:center;box-shadow:0 2px 12px rgba(0,0,0,0.08);\">" +
               "<h2>The HTTP method used is not supported for this resource.</h2></div></div>" +
               "</body></html>\r\n";
    }
 
    // Escape HTML to prevent XSS attacks
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}