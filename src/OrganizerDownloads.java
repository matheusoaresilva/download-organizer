import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class OrganizerDownloads{

    private static final String PATH_DOWNLOADS = System.getProperty("user.home") + "/Downloads";

    private static final Map<String, String> CATEGORIES = new HashMap<>();

    static {
        CATEGORIES.put("jpg", "Images");
        CATEGORIES.put("png", "Images");
        CATEGORIES.put("jpeg", "Images");
        CATEGORIES.put("gif", "Images");
        CATEGORIES.put("webp", "Images");

        CATEGORIES.put("pdf", "Docs");
        CATEGORIES.put("doc", "Docs");
        CATEGORIES.put("docx", "Docs");
        CATEGORIES.put("txt", "Docs");

        CATEGORIES.put("mp4", "Videos");
        CATEGORIES.put("mkv", "Videos");
        CATEGORIES.put("mov", "Videos");

        CATEGORIES.put("mp3", "Audios");
        CATEGORIES.put("wav", "Audios");
        CATEGORIES.put("aac", "Audios");

    }

    public static void main(String[] args) {
        OrganizerDownloads downloads = new OrganizerDownloads();
        downloads.organizeDownloads();
    }

    public void organizeDownloads(){
        try {
            Path path = Paths.get(PATH_DOWNLOADS);
            if (!Files.exists(path)){
                System.out.println("path not found: " + PATH_DOWNLOADS);
                return;
            }
            System.out.println("initializing..");

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){

                int filesMoved =0;
                int filesIgnored = 0;

                for (Path file : stream){
                    if (Files.isDirectory(file)){
                        continue;
                    }

                    String extension = getExtension(file.getFileName().toString());

                    if (extension != null && CATEGORIES.containsKey(extension)){
                        String category = CATEGORIES.get(extension);

                        if (moveFile(file, category)){
                            filesMoved++;
                            System.out.println("moved: "+ file.getFileName()+ " - " + category);
                        }
                    } else{
                        if (moveFile(file, "Others")){
                            filesMoved++;
                            System.out.println("moved: "+ file.getFileName() + " - Others");
                        }else {
                            filesIgnored++;
                        }
                    }


                }
                System.out.println("---------------------------------------");
                System.out.println("Files moved: "+ filesMoved);
                System.out.println("Files ignored: " + filesIgnored);
                System.out.println("Success!");
            }
        } catch (IOException e){
            System.out.println("Error accessing the download folder: "+ e.getMessage());
        }

    }

    private boolean moveFile(Path file, String category) {
        try {
            Path destinationFolder = Paths.get(PATH_DOWNLOADS, category);

            if (!Files.exists(destinationFolder)){
                Files.createDirectories(destinationFolder);
                System.out.println("Folder created: "+ category);
            }

            Path destinationFile = destinationFolder.resolve(file.getFileName());

            if (Files.exists(destinationFile)){
                destinationFile = genName(destinationFile);
            }

            Files.move(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path genName(Path destinationFile) {
        String name = destinationFile.getFileName().toString();
        String extension ="";
        String nameWithoutExtension = name;

        int lastPoint = name.lastIndexOf('.');
        if (lastPoint > 0){
            nameWithoutExtension = name.substring(0, lastPoint);
            extension = name.substring(lastPoint);
        }

        int count = 1;
        Path newFile;

        do {
            String newName = nameWithoutExtension + count + extension;
            newFile = destinationFile.getParent().resolve(newName);
            count++;
        }while (Files.exists(newFile));

        return newFile;
    }

    private String getExtension(String fileName) {
        int point = fileName.lastIndexOf('.');
        if (point > 0 && point < fileName.length() -1){
            return fileName.substring(point + 1).toLowerCase();
        }
        return null;
    }

}