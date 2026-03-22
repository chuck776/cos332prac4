import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDB {
    private static final String DB_FILE    = "appointments.txt"; //save appointments to a text file like pra 2
    private static final String IMAGE_DIR  = "images/";
    private List<Appointment> appointments;

    public AppointmentDB() {
        appointments = new ArrayList<>();
        // Create image directory if it doesn't exist
        new File(IMAGE_DIR).mkdirs();
        loadFromFile();
    }

    // Load appointments from file into memory
    private void loadFromFile() {
        appointments.clear();
        File file = new File(DB_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Appointment a = Appointment.fromFileLine(line);
                    if (a != null) appointments.add(a);
                }
            }
        } catch (IOException e) {
            // Start with empty list if file cannot be read
        }
    }

    // Save all appointments to file
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DB_FILE))) {
            for (Appointment a : appointments) {
                writer.println(a.toFileLine());
            }
        } catch (IOException e) {
            // Silent fail
        }
    }

    // Add a new appointment
    public void addAppointment(Appointment a) {
        appointments.add(a);
        saveToFile();
    }

    // Update appointment by 1-based index
    public boolean updateAppointment(int index, Appointment newAppointment) {
        int i = index - 1;
        if (i < 0 || i >= appointments.size()) return false;

        Appointment oldAppointment = appointments.get(i);
        
        // If new appointment doesn't have an image but old one did, keep the old image
        String imageFile = newAppointment.getImageFile();
        if (imageFile.isEmpty() && oldAppointment.hasImage()) {
            // Keep the old image
            imageFile = oldAppointment.getImageFile();
            // Create new appointment with old image
            newAppointment = new Appointment(
                newAppointment.getDate(),
                newAppointment.getTime(),
                newAppointment.getPerson(),
                newAppointment.getDescription(),
                imageFile
            );
        } else if (!imageFile.isEmpty() && oldAppointment.hasImage() && 
                   !imageFile.equals(oldAppointment.getImageFile())) {
            // New image uploaded, delete old image
            File oldImageFile = new File(IMAGE_DIR + oldAppointment.getImageFile());
            if (oldImageFile.exists()) oldImageFile.delete();
        }
        
        appointments.set(i, newAppointment);
        saveToFile();
        return true;
    }

    // Get appointment by 1-based index
    public Appointment getAppointment(int index) {
        int i = index - 1;
        if (i < 0 || i >= appointments.size()) return null;
        return appointments.get(i);
    }

    // Delete appointment by 1-based index
    // Also deletes associated image file if present
    public boolean deleteAppointment(int index) {
        int i = index - 1;
        if (i < 0 || i >= appointments.size()) return false;

        Appointment a = appointments.get(i);

        // Delete image file if it exists
        if (a.hasImage()) {
            File imageFile = new File(IMAGE_DIR + a.getImageFile());
            if (imageFile.exists()) imageFile.delete();
        }

        appointments.remove(i);
        saveToFile();
        return true;
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    // Search all fields for keyword
    public List<Appointment> search(String keyword) {
        List<Appointment> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Appointment a : appointments) {
            if (a.getDate().toLowerCase().contains(lower)        ||
                a.getTime().toLowerCase().contains(lower)        ||
                a.getPerson().toLowerCase().contains(lower)      ||
                a.getDescription().toLowerCase().contains(lower)) {
                results.add(a);
            }
        }
        return results;
    }

    // Save an uploaded image to the images directory
    // Returns the filename it was saved as
    public String saveImage(String filename, byte[] imageBytes) {
        // Sanitize filename — remove any path components
        String safeName = new File(filename).getName();
        // Prepend timestamp to avoid filename collisions
        String uniqueName = System.currentTimeMillis() + "_" + safeName;
        File dest = new File(IMAGE_DIR + uniqueName);

        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(imageBytes);
            return uniqueName;
        } catch (IOException e) {
            return "";
        }
    }

    // Load image bytes from the images directory
    public byte[] loadImage(String filename) {
        File file = new File(IMAGE_DIR + filename);
        if (!file.exists()) return null;

        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    // Get the image directory path
    public static String getImageDir() {
        return IMAGE_DIR;
    }

    // Get total number of appointments
    public int count() {
        return appointments.size();
    }
}
