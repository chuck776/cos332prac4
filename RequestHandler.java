import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RequestHandler implements Runnable {
    private Socket socket;
    private AppointmentDB db;
    
    // HTTP/1.1 compliant server version
    private static final String SERVER_VERSION = "AppointmentServer/2.0";

    public RequestHandler(Socket socket, AppointmentDB db) {
        this.socket = socket;
        this.db     = db;
    }

    @Override
    public void run() {
        try {
            InputStream  rawIn = socket.getInputStream();
            OutputStream out   = socket.getOutputStream();

            // Read request line e.g. "GET /?keyword=Smith HTTP/1.1"
            String requestLine = readLine(rawIn);
            if (requestLine == null || requestLine.isEmpty()) {
                socket.close();
                return;
            }

            System.out.println("\n--- REQUEST: " + requestLine);

            // Parse method and path
            String[] parts  = requestLine.split(" ");
            if (parts.length < 3) { 
                sendBadRequest(out, "Malformed request line");
                socket.close(); 
                return; 
            }

            String method = parts[0].toUpperCase();
            String fullPath = parts[1];
            String httpVersion = parts[2];
            
            // Validate HTTP version (RFC 2616 requirement)
            if (!httpVersion.startsWith("HTTP/1.")) {
                sendHttpVersionNotSupported(out);
                socket.close();
                return;
            }

            // Read headers into a map
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = readLine(rawIn)) != null) {
                if (headerLine.isEmpty()) break;
                int colon = headerLine.indexOf(':');
                if (colon > 0) {
                    String key   = headerLine.substring(0, colon).trim()
                                             .toLowerCase();
                    String value = headerLine.substring(colon + 1).trim();
                    headers.put(key, value);
                    System.out.println("  Header: " + key + ": " + value);
                }
            }
            
            // Validate Host header (required in HTTP/1.1 per RFC 2616)
            if (!headers.containsKey("host")) {
                sendBadRequest(out, "Missing required Host header");
                socket.close();
                return;
            }

            // Split path and query string
            // e.g. "/search?keyword=Smith" → path="/search", query="keyword=Smith"
            String path  = fullPath;
            String query = "";
            int qmark = fullPath.indexOf('?');
            if (qmark >= 0) {
                path  = fullPath.substring(0, qmark);
                query = fullPath.substring(qmark + 1);
            }

            // Handle favicon gracefully (return 404)
            if (path.equals("/favicon.ico")) {
                sendHtmlResponse(out, 404, "Not Found",
                    HtmlGenerator.generate404(), method);
                socket.close();
                return;
            }

            // Route request based on method and path
            switch (method) {
                case "GET":
                case "HEAD":
                    handleGet(path, query, method, out);
                    break;
                    
                case "POST":
                    handlePost(path, headers, rawIn, out);
                    break;
                    
                case "OPTIONS":
                    handleOptions(out);
                    break;
                    
                default:
                    // Method not allowed (405)
                    sendMethodNotAllowed(out, "GET, HEAD, POST, OPTIONS");
                    break;
            }

            socket.close();

        } catch (Exception e) {
            System.err.println("Error handling request: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    // Handle all GET and HEAD requests
    private void handleGet(String path, String query,
                           String method, OutputStream out)
                           throws IOException {

        switch (path) {
            case "/":
                // Home page — show all appointments
                String html = HtmlGenerator.generateHomePage(
                    db.getAllAppointments(), null, false);
                sendHtmlResponse(out, 200, "OK", html, method);
                break;

            case "/add":
                // Show add appointment form
                sendHtmlResponse(out, 200, "OK",
                    HtmlGenerator.generateAddPage(null, false), method);
                break;

            case "/edit":
                // Show edit appointment form
                Map<String, String> editParams = parseQueryString(query);
                String editIndexStr = editParams.get("index");
                
                if (editIndexStr == null || editIndexStr.isEmpty()) {
                    sendBadRequest(out, "Missing index parameter");
                    break;
                }
                
                try {
                    int editIndex = Integer.parseInt(editIndexStr);
                    Appointment appointmentToEdit = db.getAppointment(editIndex);
                    
                    if (appointmentToEdit == null) {
                        sendHtmlResponse(out, 404, "Not Found",
                            HtmlGenerator.generate404(), method);
                    } else {
                        sendHtmlResponse(out, 200, "OK",
                            HtmlGenerator.generateEditPage(appointmentToEdit, editIndex, null, false), method);
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(out, "Invalid index parameter");
                }
                break;

            case "/search":
                // Search page — perform search if keyword provided
                Map<String, String> searchParams = parseQueryString(query);
                String keyword = searchParams.get("keyword");
                List<Appointment> results = null;
                if (keyword != null && !keyword.isEmpty()) {
                    results = db.search(keyword);
                }
                sendHtmlResponse(out, 200, "OK",
                    HtmlGenerator.generateSearchPage(keyword, results), method);
                break;

            case "/delete":
                // Delete appointment by index (returns 204 No Content on success)
                Map<String, String> deleteParams = parseQueryString(query);
                String indexStr = deleteParams.get("index");
                
                if (indexStr == null || indexStr.isEmpty()) {
                    sendBadRequest(out, "Missing index parameter");
                    break;
                }
                
                try {
                    int index = Integer.parseInt(indexStr);
                    if (db.deleteAppointment(index)) {
                        // 303 See Other redirect after successful deletion (proper POST-REDIRECT-GET pattern)
                        sendRedirect(out, "/?message=" +
                            URLEncoder.encode("Appointment deleted successfully.", "UTF-8") +
                            "&error=false", 303);
                    } else {
                        // 404 Not Found - appointment doesn't exist
                        sendRedirect(out, "/?message=" +
                            URLEncoder.encode("Appointment not found.", "UTF-8") +
                            "&error=true", 303);
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(out, "Invalid index parameter");
                }
                break;

            default:
                // Check if it's an image request
                // e.g. /image/1234567890_photo.jpg
                if (path.startsWith("/image/")) {
                    serveImage(path.substring(7), out, method);
                } else if (path.equals("/logo")) {
                    // Serve the logo file
                    serveLogo(out, method);
                } else {
                    // 404 Not Found
                    sendHtmlResponse(out, 404, "Not Found",
                        HtmlGenerator.generate404(), method);
                }
                break;
        }
    }

    // Handle POST requests (add or edit appointment with optional image)
    private void handlePost(String path, Map<String, String> headers,
                            InputStream rawIn, OutputStream out)
                            throws IOException {

        if (!path.equals("/add") && !path.startsWith("/edit")) {
            sendHtmlResponse(out, 404, "Not Found",
                HtmlGenerator.generate404(), "POST");
            return;
        }

        String contentType   = headers.getOrDefault("content-type", "");
        String contentLength = headers.getOrDefault("content-length", "0");
        
        System.out.println("DEBUG: Content-Type: " + contentType);
        System.out.println("DEBUG: Content-Length: " + contentLength);
        
        int length;
        try {
            length = Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid Content-Length: " + contentLength);
            sendBadRequest(out, "Invalid Content-Length header");
            return;
        }
        
        if (length <= 0) {
            System.err.println("ERROR: Content-Length is 0 or negative");
            sendBadRequest(out, "Missing request body");
            return;
        }
        
        if (length > 10_000_000) { // 10MB limit
            System.err.println("ERROR: Request too large: " + length + " bytes");
            sendEntityTooLarge(out);
            return;
        }

        // Read the entire POST body as raw bytes
        System.out.println("DEBUG: Reading " + length + " bytes...");
        byte[] body = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            int r = rawIn.read(body, bytesRead, length - bytesRead);
            if (r == -1) break;
            bytesRead += r;
        }
        System.out.println("DEBUG: Read " + bytesRead + " bytes");

        // Parse multipart form data
        Map<String, String> fields = new HashMap<>();
        byte[] imageBytes = null;
        String imageFilename = "";

        if (contentType.contains("multipart/form-data")) {
            // Extract boundary from content type header
            String boundary = "";
            for (String part : contentType.split(";")) {
                part = part.trim();
                if (part.startsWith("boundary=")) {
                    boundary = "--" + part.substring(9);
                    break;
                }
            }
            
            if (boundary.isEmpty()) {
                System.err.println("ERROR: No boundary found in Content-Type");
                sendBadRequest(out, "Multipart boundary not found");
                return;
            }
            
            System.out.println("DEBUG: Boundary: " + boundary);

            // Parse multipart body
            try {
                MultipartResult result = parseMultipart(body, boundary);
                fields        = result.fields;
                imageBytes    = result.imageBytes;
                imageFilename = result.imageFilename;
                
                System.out.println("DEBUG: Parsed fields: " + fields.keySet());
                System.out.println("DEBUG: Image filename: " + imageFilename);
                if (imageBytes != null) {
                    System.out.println("DEBUG: Image size: " + imageBytes.length + " bytes");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to parse multipart: " + e.getMessage());
                e.printStackTrace();
                sendBadRequest(out, "Failed to parse multipart form data");
                return;
            }

        } else if (contentType.contains("application/x-www-form-urlencoded")) {
            // Regular form data without file upload
            fields = parseQueryString(new String(body, "UTF-8"));
            System.out.println("DEBUG: Parsed URL-encoded fields: " + fields.keySet());
        } else {
            System.err.println("ERROR: Unsupported Content-Type: " + contentType);
            sendBadRequest(out, "Unsupported Content-Type");
            return;
        }

        // Extract form fields
        String date        = fields.getOrDefault("date", "").trim();
        String time        = fields.getOrDefault("time", "").trim();
        String person      = fields.getOrDefault("person", "").trim();
        String description = fields.getOrDefault("description", "").trim();
        
        System.out.println("DEBUG: Date: " + date);
        System.out.println("DEBUG: Time: " + time);
        System.out.println("DEBUG: Person: " + person);
        System.out.println("DEBUG: Description: " + description);

        // Validate required fields
        if (date.isEmpty() || time.isEmpty() ||
            person.isEmpty() || description.isEmpty()) {
            System.err.println("ERROR: Missing required fields");
            
            if (path.startsWith("/edit")) {
                // For edit, we need the index
                String query = path.contains("?") ? path.substring(path.indexOf("?") + 1) : "";
                Map<String, String> params = parseQueryString(query);
                String indexStr = params.get("index");
                
                if (indexStr != null) {
                    try {
                        int index = Integer.parseInt(indexStr);
                        Appointment apt = db.getAppointment(index);
                        if (apt != null) {
                            sendHtmlResponse(out, 400, "Bad Request",
                                HtmlGenerator.generateEditPage(apt, index,
                                    "All fields are required.", true), "POST");
                            return;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            sendHtmlResponse(out, 400, "Bad Request",
                HtmlGenerator.generateAddPage(
                    "All fields are required.", true), "POST");
            return;
        }

        // Save image if provided
        String savedImageName = "";
        if (imageBytes != null && imageBytes.length > 0
                && !imageFilename.isEmpty()) {
            System.out.println("DEBUG: Saving image: " + imageFilename);
            savedImageName = db.saveImage(imageFilename, imageBytes);
            if (savedImageName.isEmpty()) {
                System.err.println("ERROR: Failed to save image");
                sendHtmlResponse(out, 500, "Internal Server Error",
                    HtmlGenerator.generateAddPage(
                        "Failed to save image file.", true), "POST");
                return;
            }
            System.out.println("DEBUG: Image saved as: " + savedImageName);
        }

        // Check if this is an edit or add
        if (path.startsWith("/edit")) {
            // EDIT appointment
            String query = path.contains("?") ? path.substring(path.indexOf("?") + 1) : "";
            Map<String, String> params = parseQueryString(query);
            String indexStr = params.get("index");
            
            if (indexStr == null) {
                sendBadRequest(out, "Missing index parameter");
                return;
            }
            
            try {
                int index = Integer.parseInt(indexStr);
                Appointment updatedAppointment = new Appointment(
                    date, time, person, description, savedImageName);
                
                if (db.updateAppointment(index, updatedAppointment)) {
                    System.out.println("DEBUG: Appointment updated successfully!");
                    sendRedirect(out, "/?message=" +
                        URLEncoder.encode("Appointment updated successfully!", "UTF-8") +
                        "&error=false", 303);
                } else {
                    sendHtmlResponse(out, 404, "Not Found",
                        HtmlGenerator.generate404(), "POST");
                }
            } catch (NumberFormatException e) {
                sendBadRequest(out, "Invalid index parameter");
            }
        } else {
            // ADD appointment
            System.out.println("DEBUG: Creating appointment...");
            Appointment a = new Appointment(
                date, time, person, description, savedImageName);
            db.addAppointment(a);
            System.out.println("DEBUG: Appointment saved successfully!");

            // 303 See Other redirect after successful POST (proper POST-REDIRECT-GET pattern)
            sendRedirect(out, "/?message=" +
                URLEncoder.encode("Appointment added successfully!", "UTF-8") +
                "&error=false", 303);
        }
    }
    
    // Handle OPTIONS requests (used in CORS preflight)
    private void handleOptions(OutputStream out) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 204 No Content\r\n");
        response.append("Allow: GET, HEAD, POST, OPTIONS\r\n");
        response.append(getCommonHeaders());
        response.append("\r\n");
        
        out.write(response.toString().getBytes("UTF-8"));
        out.flush();
    }

    // Parse query string into a map
    // e.g. "name=John&age=25" → {name: John, age: 25}
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key   = urlDecode(pair.substring(0, eq));
                String value = urlDecode(pair.substring(eq + 1));
                params.put(key, value);
            }
        }
        return params;
    }

    // Decode URL encoded strings
    // Converts + to space and %XX to the corresponding character
    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    // Parse multipart form data
    // This handles both text fields and file uploads
    private MultipartResult parseMultipart(byte[] body, String boundary)
            throws IOException {

        MultipartResult result = new MultipartResult();
        result.fields = new HashMap<>();

        // Convert boundary to bytes for searching
        byte[] boundaryBytes = boundary.getBytes("UTF-8");

        // Split body into parts by finding boundary positions
        java.util.List<Integer> boundaryPositions = new java.util.ArrayList<>();
        for (int i = 0; i <= body.length - boundaryBytes.length; i++) {
            if (matchesAt(body, i, boundaryBytes)) {
                boundaryPositions.add(i);
            }
        }
        
        System.out.println("DEBUG: Found " + boundaryPositions.size() + " boundaries");

        // Process each part between boundaries
        for (int b = 0; b < boundaryPositions.size() - 1; b++) {
            int start = boundaryPositions.get(b) + boundaryBytes.length + 2;
            int end   = boundaryPositions.get(b + 1) - 2;
            if (start >= end) continue;

            // Find end of part headers (blank line = \r\n\r\n)
            int headerEnd = -1;
            for (int i = start; i < end - 3; i++) {
                if (body[i] == '\r' && body[i+1] == '\n' &&
                    body[i+2] == '\r' && body[i+3] == '\n') {
                    headerEnd = i;
                    break;
                }
            }
            if (headerEnd == -1) continue;

            // Extract part headers as string
            String partHeaders = new String(body, start,
                headerEnd - start, "UTF-8");
            // Extract part body as bytes
            byte[] partBody = java.util.Arrays.copyOfRange(
                body, headerEnd + 4, end);

            // Parse Content-Disposition to get field name and filename
            String fieldName = "";
            String filename  = "";
            for (String line : partHeaders.split("\r\n")) {
                if (line.toLowerCase().startsWith("content-disposition:")) {
                    for (String item : line.split(";")) {
                        item = item.trim();
                        if (item.startsWith("name=\"")) {
                            fieldName = item.substring(6, item.length() - 1);
                        } else if (item.startsWith("filename=\"")) {
                            filename = item.substring(10, item.length() - 1);
                        }
                    }
                }
            }

            // If it has a filename it is a file upload
            if (!filename.isEmpty() && partBody.length > 0) {
                result.imageBytes    = partBody;
                result.imageFilename = filename;
                System.out.println("DEBUG: Found file part: " + filename + " (" + partBody.length + " bytes)");
            } else if (!fieldName.isEmpty()) {
                // Regular text field
                String value = new String(partBody, "UTF-8").trim();
                result.fields.put(fieldName, value);
                System.out.println("DEBUG: Found field: " + fieldName + " = " + value);
            }
        }

        return result;
    }

    // Helper to check if bytes match at a position
    private boolean matchesAt(byte[] data, int pos, byte[] pattern) {
        if (pos + pattern.length > data.length) return false;
        for (int i = 0; i < pattern.length; i++) {
            if (data[pos + i] != pattern[i]) return false;
        }
        return true;
    }
    
    // Read a line from raw InputStream (until \r\n)
    private String readLine(InputStream in) throws IOException {
        StringBuilder line = new StringBuilder();
        int current;
        int previous = -1;
        
        while ((current = in.read()) != -1) {
            if (previous == '\r' && current == '\n') {
                // Remove the \r we just added
                if (line.length() > 0) {
                    line.setLength(line.length() - 1);
                }
                break;
            }
            line.append((char) current);
            previous = current;
        }
        
        return line.toString();
    }

    // Serve an image file from the images directory
    private void serveImage(String filename, OutputStream out, String method)
            throws IOException {

        // Sanitize filename — prevent directory traversal attacks
        filename = new File(filename).getName();
        byte[] imageBytes = db.loadImage(filename);

        if (imageBytes == null) {
            sendHtmlResponse(out, 404, "Not Found",
                HtmlGenerator.generate404(), method);
            return;
        }

        // Determine content type from file extension (proper MIME types per RFC 2616)
        String contentType = "application/octet-stream"; // default
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) 
            contentType = "image/jpeg";
        else if (lowerName.endsWith(".png"))  
            contentType = "image/png";
        else if (lowerName.endsWith(".gif"))  
            contentType = "image/gif";
        else if (lowerName.endsWith(".webp")) 
            contentType = "image/webp";
        else if (lowerName.endsWith(".svg"))  
            contentType = "image/svg+xml";

        // Send image response with correct headers
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 200 OK\r\n");
        responseHeaders.append("Content-Type: " + contentType + "\r\n");
        responseHeaders.append("Content-Length: " + imageBytes.length + "\r\n");
        responseHeaders.append(getCommonHeaders());
        responseHeaders.append("\r\n");

        out.write(responseHeaders.toString().getBytes("UTF-8"));
        
        // HEAD method should not include body (RFC 2616)
        if (!method.equals("HEAD")) {
            out.write(imageBytes);
        }
        out.flush();
    }
    
    // Serve the logo file
    private void serveLogo(OutputStream out, String method) throws IOException {
        File logoFile = new File("appointlyLogo.png");
        
        if (!logoFile.exists()) {
            // Send a simple fallback if logo doesn't exist
            sendHtmlResponse(out, 404, "Not Found",
                HtmlGenerator.generate404(), method);
            return;
        }

        try (FileInputStream fis = new FileInputStream(logoFile)) {
            byte[] logoBytes = fis.readAllBytes();
            
            StringBuilder responseHeaders = new StringBuilder();
            responseHeaders.append("HTTP/1.1 200 OK\r\n");
            responseHeaders.append("Content-Type: image/png\r\n");
            responseHeaders.append("Content-Length: " + logoBytes.length + "\r\n");
            responseHeaders.append("Cache-Control: public, max-age=86400\r\n"); // Cache for 1 day
            responseHeaders.append(getCommonHeaders());
            responseHeaders.append("\r\n");

            out.write(responseHeaders.toString().getBytes("UTF-8"));
            
            if (!method.equals("HEAD")) {
                out.write(logoBytes);
            }
            out.flush();
        }
    }
    
    // Get RFC 2616 compliant Date header
    private String getHttpDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }
    
    // Get common HTTP headers that should appear in all responses
    private String getCommonHeaders() {
        return "Date: " + getHttpDate() + "\r\n" +
               "Server: " + SERVER_VERSION + "\r\n" +
               "Cache-Control: no-store, no-cache, must-revalidate\r\n" +
               "Pragma: no-cache\r\n" +
               "Connection: close\r\n";
    }

    // Send an HTTP redirect response (302 or 303)
    private void sendRedirect(OutputStream out, String location, int statusCode)
            throws IOException {
        String statusText = statusCode == 303 ? "See Other" : "Found";
        
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                          "Location: " + location + "\r\n" +
                          getCommonHeaders() +
                          "Content-Length: 0\r\n" +
                          "\r\n";
        out.write(response.getBytes("UTF-8"));
        out.flush();
    }
    
    // Convenience method for 302 redirect
    private void sendRedirect(OutputStream out, String location)
            throws IOException {
        sendRedirect(out, location, 302);
    }

    // Send a standard HTML response with full RFC 2616 compliance
    private void sendHtmlResponse(OutputStream out, int statusCode,
            String statusText, String body, String method)
            throws IOException {

        byte[] bodyBytes = body.getBytes("UTF-8");
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
        response.append("Content-Type: text/html; charset=UTF-8\r\n");
        response.append("Content-Length: " + bodyBytes.length + "\r\n");
        response.append(getCommonHeaders());
        response.append("\r\n");

        out.write(response.toString().getBytes("UTF-8"));
        
        // HEAD method should not include body (RFC 2616 Section 9.4)
        if (!method.equals("HEAD")) {
            out.write(bodyBytes);
        }
        out.flush();
    }
    
    // Send 400 Bad Request
    private void sendBadRequest(OutputStream out, String message) 
            throws IOException {
        String body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>400 Bad Request</title></head>" +
                      "<body><h1>400 Bad Request</h1><p>" + message + "</p></body></html>";
        sendHtmlResponse(out, 400, "Bad Request", body, "GET");
    }
    
    // Send 405 Method Not Allowed
    private void sendMethodNotAllowed(OutputStream out, String allowedMethods) 
            throws IOException {
        byte[] bodyBytes = HtmlGenerator.generate405().getBytes("UTF-8");
        
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 405 Method Not Allowed\r\n");
        response.append("Allow: " + allowedMethods + "\r\n");
        response.append("Content-Type: text/html; charset=UTF-8\r\n");
        response.append("Content-Length: " + bodyBytes.length + "\r\n");
        response.append(getCommonHeaders());
        response.append("\r\n");

        out.write(response.toString().getBytes("UTF-8"));
        out.write(bodyBytes);
        out.flush();
    }
    
    // Send 413 Request Entity Too Large
    private void sendEntityTooLarge(OutputStream out) throws IOException {
        String body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>413 Request Entity Too Large</title></head>" +
                      "<body><h1>413 Request Entity Too Large</h1><p>The request body exceeds the maximum allowed size of 10MB.</p></body></html>";
        sendHtmlResponse(out, 413, "Request Entity Too Large", body, "GET");
    }
    
    // Send 505 HTTP Version Not Supported
    private void sendHttpVersionNotSupported(OutputStream out) throws IOException {
        String body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>505 HTTP Version Not Supported</title></head>" +
                      "<body><h1>505 HTTP Version Not Supported</h1><p>This server only supports HTTP/1.0 and HTTP/1.1</p></body></html>";
        sendHtmlResponse(out, 505, "HTTP Version Not Supported", body, "GET");
    }

    // Inner class to hold multipart parsing results
    private static class MultipartResult {
        Map<String, String> fields;
        byte[]              imageBytes;
        String              imageFilename = "";
    }
}