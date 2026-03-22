import java.io.*;
import java.net.*;

public class HttpServer {
    private static final int PORT = 55555;
    private static AppointmentDB db;

    public static void main(String[] args) {
        db = new AppointmentDB();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Appointment Server started on port " + PORT);
            System.out.println("Open browser and go to http://127.0.0.1:" + PORT);
            System.out.println("Press Ctrl+C to stop the server.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new RequestHandler(clientSocket, db));
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}