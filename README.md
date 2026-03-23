# Appointly - HTTP Appointment Server

A Java-based HTTP server for managing appointments, built for COS 332 PA4 at the University of Pretoria.

## Requirements

- Java JDK (to compile)
- Java JRE (to run)
- A standard web browser (Chrome, Firefox, etc.)

## Building

Clean old compiled files and build a fresh executable JAR:

```bash
rm -f *.class MANIFEST.MF
javac *.java
echo "Main-Class: HttpServer" > MANIFEST.MF
jar cfm Appointly.jar MANIFEST.MF *.class appointlyLogo.png appointments.txt images/
```

## Running

```bash
java -jar Appointly.jar

javac *.java
java HttpServer
```

Then open browser and go to:

```
http://localhost:55555
```

## MD5 Verification

To generate the MD5 digest of the executable for submission:

```bash
md5sum Appointly.jar //3add26fdc362ab5d15542c093714b683 Appointly.jar
OR
md5sum HttpServer.java //b811e8967b77fc46b53085f3ea24330d  HttpServer.java
```


The JAR file is the executable, this is what should be hashed and submitted. we can also use HttpServer file as its the "executable"

## Project Structure

```
Appointly/
├── HttpServer.java          # Main entry point, handles HTTP server logic
├── RequestHandler.java      # Parses and handles incoming HTTP requests
├── AppointmentDB.java       # Appointment data storage and retrieval
├── HtmlGenerator.java       # Generates HTML responses for the browser
├── appointments.txt         # Persistent appointment storage
├── appointlyLogo.png        # Logo served to the browser
├── images/                  # Images for appointments
└── Appointly.jar            # Compiled executable JAR
```

## Features

- Add, search, and delete appointments via a browser interface
- Serves HTML pages over raw HTTP (no frameworks)
- Supports image uploads for appointment contacts
- Fully operable from a standard browser — no command-line interaction needed after startup

## Notes

- This server implements HTTP manually using raw sockets, as required by COS 332.
- No HTTP libraries are used, the server speaks the HTTP protocol directly.
- added features include:
## full CRUD operations:
- Create - Add appointments
- Read - View all, search
- Update - Edit appointments
- Delete - Remove appointments
## Http RFC 2616 Compliance demonstrated:
- Multiple query parameters in GET request
- Proper URL encoding of search params
- Clean query string handling
- RESTful search endpoint
- 7 proper status codes (200, 204, 303, 400, 404, 405, 413, 505)
- 9 comprehensive headers (Date, Server, Content-Type, Cache-Control, etc.)
- 4 HTTP methods (GET, HEAD, POST, OPTIONS)
- Request validation (HTTP version, Host header, Content-Length)
- POST-Redirect-GET pattern (303 See Other)
- Proper MIME types for all content
- Security features (XSS prevention, input validation, size limits)
