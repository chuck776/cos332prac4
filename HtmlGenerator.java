import java.util.List;
//ALL CRUD OPERATIONS HAVE TO BE DONE VIA HTML UNFORTUNATELY
public class HtmlGenerator {

    // Common CSS styles used across all pages
    private static String getStyles() {
        return "<style>\r\n" +
            "body { font-family: Arial, sans-serif; margin: 40px; " +
            "background: #1a1a2e; color: #eee; }\r\n" +
            "h1, h2 { color: #00d4ff; }\r\n" +
            "table { width: 100%; border-collapse: collapse; margin-top: 20px; }\r\n" +
            "th { background: #0f3460; color: #00d4ff; padding: 10px; " +
            "text-align: left; }\r\n" +
            "td { padding: 10px; border-bottom: 1px solid #0f3460; " +
            "vertical-align: middle; }\r\n" +
            "tr:hover { background: #16213e; }\r\n" +
            "input[type=text], input[type=date], input[type=time] { " +
            "padding: 8px; width: 100%; background: #16213e; " +
            "color: #eee; border: 1px solid #0f3460; border-radius: 4px; }\r\n" +
            "input[type=file] { color: #eee; }\r\n" +
            "input[type=submit] { padding: 10px 20px; background: #0f3460; " +
            "color: #00d4ff; border: none; border-radius: 4px; cursor: pointer; }\r\n" +
            "input[type=submit]:hover { background: #00d4ff; color: #1a1a2e; }\r\n" +
            ".nav { margin-bottom: 30px; }\r\n" +
            ".nav a { margin-right: 15px; color: #00d4ff; text-decoration: none; " +
            "padding: 8px 16px; background: #0f3460; border-radius: 4px; }\r\n" +
            ".nav a:hover { background: #00d4ff; color: #1a1a2e; }\r\n" +
            ".delete-btn { color: #ff6b6b; text-decoration: none; }\r\n" +
            ".delete-btn:hover { color: #ff0000; }\r\n" +
            ".success { color: #00ff88; margin: 10px 0; }\r\n" +
            ".error   { color: #ff6b6b; margin: 10px 0; }\r\n" +
            ".thumb   { width: 60px; height: 60px; object-fit: cover; " +
            "border-radius: 4px; }\r\n" +
            "form div { margin-bottom: 15px; }\r\n" +
            "label    { display: block; margin-bottom: 5px; color: #00d4ff; }\r\n" +
            "</style>\r\n";
    }

    // Common HTML page wrapper
    private static String pageStart(String title) {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\">\r\n" +
               "<head>\r\n" +
               "    <meta charset=\"UTF-8\">\r\n" +
               "    <meta http-equiv=\"Cache-Control\" content=\"no-store\">\r\n" +
               "    <title>" + title + "</title>\r\n" +
               getStyles() +
               "</head>\r\n" +
               "<body>\r\n" +
               "    <h1>Appointment Manager</h1>\r\n" +
               "    <div class=\"nav\">\r\n" +
               "        <a href=\"/\">View All</a>\r\n" +
               "        <a href=\"/add\">Add Appointment</a>\r\n" +
               "        <a href=\"/search\">Search</a>\r\n" +
               "    </div>\r\n";
    }

    private static String pageEnd() {
        return "</body>\r\n</html>\r\n";
    }

    // Main page — list all appointments
    public static String generateHomePage(List<Appointment> appointments, 
                                          String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Appointment Manager"));

        html.append("    <h2>All Appointments</h2>\r\n");

        // Show success or error message if present
        if (message != null && !message.isEmpty()) {
            String cls = isError ? "error" : "success";
            html.append("    <p class=\"" + cls + "\">" + message + "</p>\r\n");
        }

        if (appointments.isEmpty()) {
            html.append("    <p>No appointments found.</p>\r\n");
        } else {
            html.append("    <table>\r\n");
            html.append("        <tr>\r\n");
            html.append("            <th>#</th>\r\n");
            html.append("            <th>Date</th>\r\n");
            html.append("            <th>Time</th>\r\n");
            html.append("            <th>Person</th>\r\n");
            html.append("            <th>Description</th>\r\n");
            html.append("            <th>Image</th>\r\n");
            html.append("            <th>Action</th>\r\n");
            html.append("        </tr>\r\n");

            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                int num = i + 1;
                html.append("        <tr>\r\n");
                html.append("            <td>" + num + "</td>\r\n");
                html.append("            <td>" + a.getDate() + "</td>\r\n");
                html.append("            <td>" + a.getTime() + "</td>\r\n");
                html.append("            <td>" + a.getPerson() + "</td>\r\n");
                html.append("            <td>" + a.getDescription() + "</td>\r\n");

                // Show image thumbnail if available
                if (a.hasImage()) {
                    html.append("            <td><img class=\"thumb\" src=\"/image/" +
                        a.getImageFile() + "\" alt=\"photo\"></td>\r\n");
                } else {
                    html.append("            <td>No image</td>\r\n");
                }

                // Delete link
                html.append("            <td><a class=\"delete-btn\" " +
                    "href=\"/delete?index=" + num + "\">Delete</a></td>\r\n");
                html.append("        </tr>\r\n");
            }
            html.append("    </table>\r\n");
        }

        html.append(pageEnd());
        return html.toString();
    }

    // Add appointment form page
    public static String generateAddPage(String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Add Appointment"));
        html.append("    <h2>Add New Appointment</h2>\r\n");

        if (message != null && !message.isEmpty()) {
            String cls = isError ? "error" : "success";
            html.append("    <p class=\"" + cls + "\">" + message + "</p>\r\n");
        }

        // Multipart form required for file upload
        html.append("    <form method=\"POST\" action=\"/add\" " +
                    "enctype=\"multipart/form-data\">\r\n");
        html.append("        <div>\r\n");
        html.append("            <label>Date:</label>\r\n");
        html.append("            <input type=\"date\" name=\"date\" required>\r\n");
        html.append("        </div>\r\n");
        html.append("        <div>\r\n");
        html.append("            <label>Time:</label>\r\n");
        html.append("            <input type=\"time\" name=\"time\" required>\r\n");
        html.append("        </div>\r\n");
        html.append("        <div>\r\n");
        html.append("            <label>Person:</label>\r\n");
        html.append("            <input type=\"text\" name=\"person\" " +
                    "placeholder=\"Who are you meeting?\" required>\r\n");
        html.append("        </div>\r\n");
        html.append("        <div>\r\n");
        html.append("            <label>Description:</label>\r\n");
        html.append("            <input type=\"text\" name=\"description\" " +
                    "placeholder=\"What is the appointment for?\" required>\r\n");
        html.append("        </div>\r\n");
        html.append("        <div>\r\n");
        html.append("            <label>Photo (optional):</label>\r\n");
        html.append("            <input type=\"file\" name=\"image\" " +
                    "accept=\"image/*\">\r\n");
        html.append("        </div>\r\n");
        html.append("        <div>\r\n");
        html.append("            <input type=\"submit\" value=\"Add Appointment\">\r\n");
        html.append("        </div>\r\n");
        html.append("    </form>\r\n");

        html.append(pageEnd());
        return html.toString();
    }

    // Search form and results page
    public static String generateSearchPage(String keyword,
            List<Appointment> results) {
        StringBuilder html = new StringBuilder();
        html.append(pageStart("Search Appointments"));
        html.append("    <h2>Search Appointments</h2>\r\n");

        html.append("    <form method=\"GET\" action=\"/search\">\r\n");
        html.append("        <div style=\"display:flex; gap:10px;\">\r\n");
        html.append("            <input type=\"text\" name=\"keyword\" " +
                    "placeholder=\"Search by name, date, description...\" " +
                    "value=\"" + (keyword != null ? keyword : "") + "\">\r\n");
        html.append("            <input type=\"submit\" value=\"Search\">\r\n");
        html.append("        </div>\r\n");
        html.append("    </form>\r\n");

        // Show results if a search was performed
        if (keyword != null && !keyword.isEmpty()) {
            if (results == null || results.isEmpty()) {
                html.append("    <p class=\"error\">No appointments found " +
                            "matching: " + keyword + "</p>\r\n");
            } else {
                html.append("    <p class=\"success\">Found " + 
                            results.size() + " result(s):</p>\r\n");
                html.append("    <table>\r\n");
                html.append("        <tr>\r\n");
                html.append("            <th>Date</th><th>Time</th>" +
                            "<th>Person</th><th>Description</th><th>Image</th>\r\n");
                html.append("        </tr>\r\n");

                for (Appointment a : results) {
                    html.append("        <tr>\r\n");
                    html.append("            <td>" + a.getDate() + "</td>\r\n");
                    html.append("            <td>" + a.getTime() + "</td>\r\n");
                    html.append("            <td>" + a.getPerson() + "</td>\r\n");
                    html.append("            <td>" + a.getDescription() + "</td>\r\n");
                    if (a.hasImage()) {
                        html.append("            <td><img class=\"thumb\" src=\"/image/"
                            + a.getImageFile() + "\" alt=\"photo\"></td>\r\n");
                    } else {
                        html.append("            <td>No image</td>\r\n");
                    }
                    html.append("        </tr>\r\n");
                }
                html.append("    </table>\r\n");
            }
        }

        html.append(pageEnd());
        return html.toString();
    }

    // Simple 404 page
    public static String generate404() {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\"><head><meta charset=\"UTF-8\">" +
               "<title>404</title></head>\r\n" +
               "<body><h1>404 - Not Found</h1></body></html>\r\n";
    }

    // Simple 405 page
    public static String generate405() {
        return "<!DOCTYPE html>\r\n" +
               "<html lang=\"en\"><head><meta charset=\"UTF-8\">" +
               "<title>405</title></head>\r\n" +
               "<body><h1>405 - Method Not Allowed</h1></body></html>\r\n";
    }
}