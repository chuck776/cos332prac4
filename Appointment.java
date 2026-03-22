public class Appointment {
    private String date;
    private String time;
    private String person;
    private String description;
    private String imageFile; // filename of stored image for bonus mark attempt , or empty string if none

    public Appointment(String date, String time, String person, 
                       String description, String imageFile) {
        this.date        = date;
        this.time        = time;
        this.person      = person;
        this.description = description;
        this.imageFile   = (imageFile == null) ? "" : imageFile; //similar to pra 2
    }

    public String getDate()        { return date; }
    public String getTime()        { return time; }
    public String getPerson()      { return person; }
    public String getDescription() { return description; }
    public String getImageFile()   { return imageFile; }
    public boolean hasImage()      { return !imageFile.isEmpty(); }

    // Save to file as pipe separated line
    public String toFileLine() {
        return date + "|" + time + "|" + person + "|" + description + "|" + imageFile;
    }

    // Load from pipe separated file line
    public static Appointment fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length == 5) {
            return new Appointment(
                parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        // Handle old format without image field
        if (parts.length == 4) {
            return new Appointment(
                parts[0], parts[1], parts[2], parts[3], "");
        }
        return null;
    }

    // Display index is 1-based for user facing display
    public String toDisplayString(int index) {
        return String.format("%d. %s at %s with %s — %s",
            index, date, time, person, description);
    }
}