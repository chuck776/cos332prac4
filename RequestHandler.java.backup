import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler implements Runnable {
    private Socket socket;
    private AppointmentDB db;

    public RequestHandler(Socket socket, AppointmentDB db) {
        this.socket = socket;
        this.db     = db;
    }

    @Override
    public void run() {
        try {
            InputStream  rawIn = socket.getInputStream();
            OutputStream out   = socket.getOutputStream();

            // We need raw stream for POST body (binary image data)
            // but BufferedReader for reading headers line by line
            BufferedReader headerReader = new BufferedReader(
                new InputStreamReader(rawIn));

            // Read request line e.g. "GET /?keyword=Smith HTTP/1.1"
            String requestLine = headerReader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                socket.close();
                return;
            }

            System.out.println("\n--- REQUEST: " + requestLine);

            // Parse method and path
            String[] parts  = requestLine.split(" ");
            if (parts.length < 2) { socket.close(); return; }

            String method = parts[0].toUpperCase();
            String fullPath = parts[1];

            // Read headers into a map
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = headerReader.readLine()) != null) {
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

            // Split path and query string
            // e.g. "/search?keyword=Smith" → path="/search", query="keyword=Smith"
            String path  = fullPath;
            String query = "";
            int qmark = fullPath.indexOf('?');
            if (qmark >= 0) {
                path  = fullPath.substring(0, qmark);
                query = fullPath.substring(qmark + 1);
            }

            // Handle favicon gracefully
            if (path.equals("/favicon.ico")) {
                sendHtmlResponse(out, 404, "Not Found",
                    HtmlGenerator.generate404(), method);
                socket.close();
                return;
            }

            // Route request based on method and path
            if (method.equals("GET") || method.equals("HEAD")) {
                handleGet(path, query, method, out);
            } else if (method.equals("POST")) {
                handlePost(path, headers, rawIn, out);
            } else {
                sendHtmlResponse(out, 405, "Method Not Allowed",
                    HtmlGenerator.generate405(), method);
            }

            socket.close();

        } catch (IOException e) {
            // Client disconnected
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    // Handle all GET requests
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
                // Delete appointment by index
                Map<String, String> deleteParams = parseQueryString(query);
                String indexStr = deleteParams.get("index");
                String message;
                boolean isError;
                try {
                    int index = Integer.parseInt(indexStr);
                    if (db.deleteAppointment(index)) {
                        message = "Appointment deleted successfully.";
                        isError = false;
                    } else {
                        message = "Invalid appointment number.";
                        isError = true;
                    }
                } catch (NumberFormatException e) {
                    message = "Invalid request.";
                    isError = true;
                }
                // Redirect back to home page after delete
                sendRedirect(out, "/?message=" + 
                    URLEncoder.encode(message, "UTF-8") + 
                    "&error=" + isError);
                break;

            default:
                // Check if it's an image request
                // e.g. /image/1234567890_photo.jpg
                if (path.startsWith("/image/")) {
                    serveImage(path.substring(7), out);
                } else {
                    sendHtmlResponse(out, 404, "Not Found",
                        HtmlGenerator.generate404(), method);
                }
                break;
        }
    }

    // Handle POST requests (add appointment with optional image)
    private void handlePost(String path, Map<String, String> headers,
                            InputStream rawIn, OutputStream out)
                            throws IOException {

        if (!path.equals("/add")) {
            sendHtmlResponse(out, 404, "Not Found",
                HtmlGenerator.generate404(), "POST");
            return;
        }

        String contentType   = headers.getOrDefault("content-type", "");
        String contentLength = headers.getOrDefault("content-length", "0");
        int length = Integer.parseInt(contentLength);

        // Read the entire POST body as raw bytes
        byte[] body = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            int r = rawIn.read(body, bytesRead, length - bytesRead);
            if (r == -1) break;
            bytesRead += r;
        }

        // Parse multipart form data
        Map<String, String> fields = new HashMap<>();
        byte[] imageBytes = null;
        String imageFilename = "";

        if (contentType.contains("multipart/form-data")) {
            // Extract boundary from content type header
            // e.g. "multipart/form-data; boundary=----WebKitFormBoundary..."
            String boundary = "";
            for (String part : contentType.split(";")) {
                part = part.trim();
                if (part.startsWith("boundary=")) {
                    boundary = "--" + part.substring(9);
                    break;
                }
            }

            // Parse multipart body
            MultipartResult result = parseMultipart(body, boundary);
            fields        = result.fields;
            imageBytes    = result.imageBytes;
            imageFilename = result.imageFilename;

        } else if (contentType.contains("application/x-www-form-urlencoded")) {
            // Regular form data without file upload
            fields = parseQueryString(new String(body, "UTF-8"));
        }

        // Extract form fields
        String date        = fields.getOrDefault("date", "").trim();
        String time        = fields.getOrDefault("time", "").trim();
        String person      = fields.getOrDefault("person", "").trim();
        String description = fields.getOrDefault("description", "").trim();

        // Validate required fields
        if (date.isEmpty() || time.isEmpty() || 
            person.isEmpty() || description.isEmpty()) {
            sendHtmlResponse(out, 200, "OK",
                HtmlGenerator.generateAddPage(
                    "All fields are required.", true), "POST");
            return;
        }

        // Save image if provided
        String savedImageName = "";
        if (imageBytes != null && imageBytes.length > 0 
                && !imageFilename.isEmpty()) {
            savedImageName = db.saveImage(imageFilename, imageBytes);
        }

        // Save appointment
        Appointment a = new Appointment(
            date, time, person, description, savedImageName);
        db.addAppointment(a);

        // Redirect to home page with success message
        sendRedirect(out, "/?message=" + 
            URLEncoder.encode("Appointment added successfully!", "UTF-8") +
            "&error=false");
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
            } else if (!fieldName.isEmpty()) {
                // Regular text field
                result.fields.put(fieldName, 
                    new String(partBody, "UTF-8").trim());
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

    // Serve an image file from the images directory
    private void serveImage(String filename, OutputStream out)
            throws IOException {

        // Sanitize filename — prevent directory traversal attacks
        filename = new File(filename).getName();
        byte[] imageBytes = db.loadImage(filename);

        if (imageBytes == null) {
            sendHtmlResponse(out, 404, "Not Found",
                HtmlGenerator.generate404(), "GET");
            return;
        }

        // Determine content type from file extension
        String contentType = "image/jpeg";
        if (filename.toLowerCase().endsWith(".png"))  contentType = "image/png";
        if (filename.toLowerCase().endsWith(".gif"))  contentType = "image/gif";
        if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";

        // Send image response with correct headers
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 200 OK\r\n");
        responseHeaders.append("Content-Type: " + contentType + "\r\n");
        responseHeaders.append("Content-Length: " + imageBytes.length + "\r\n");
        responseHeaders.append("Cache-Control: no-store\r\n");
        responseHeaders.append("Connection: close\r\n");
        responseHeaders.append("\r\n");

        out.write(responseHeaders.toString().getBytes("UTF-8"));
        out.write(imageBytes);
        out.flush();
    }

    // Send an HTTP redirect response
    private void sendRedirect(OutputStream out, String location)
            throws IOException {
        String response = "HTTP/1.1 302 Found\r\n" +
                          "Location: " + location + "\r\n" +
                          "Connection: close\r\n" +
                          "Content-Length: 0\r\n" +
                          "\r\n";
        out.write(response.getBytes("UTF-8"));
        out.flush();
    }

    // Send a standard HTML response
    private void sendHtmlResponse(OutputStream out, int statusCode,
            String statusText, String body, String method)
            throws IOException {

        byte[] bodyBytes = body.getBytes("UTF-8");
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
        response.append("Content-Type: text/html; charset=UTF-8\r\n");
        response.append("Content-Length: " + bodyBytes.length + "\r\n");
        response.append("Cache-Control: no-store\r\n");
        response.append("Connection: close\r\n");
        response.append("Server: AppointmentServer/1.0\r\n");
        response.append("\r\n");

        out.write(response.toString().getBytes("UTF-8"));
        if (!method.equals("HEAD")) {
            out.write(bodyBytes);
        }
        out.flush();
    }

    // Inner class to hold multipart parsing results
    private static class MultipartResult {
        Map<String, String> fields;
        byte[]              imageBytes;
        String              imageFilename = "";
    }
}