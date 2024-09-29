package systems.petrichor.yttm.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Song implements Runnable {

    /* 
    ██╗   ██╗ █████╗ ██████╗ ██╗ █████╗ ██████╗ ██╗     ███████╗███████╗
    ██║   ██║██╔══██╗██╔══██╗██║██╔══██╗██╔══██╗██║     ██╔════╝██╔════╝
    ██║   ██║███████║██████╔╝██║███████║██████╔╝██║     █████╗  ███████╗
    ╚██╗ ██╔╝██╔══██║██╔══██╗██║██╔══██║██╔══██╗██║     ██╔══╝  ╚════██║
     ╚████╔╝ ██║  ██║██║  ██║██║██║  ██║██████╔╝███████╗███████╗███████║
      ╚═══╝  ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝╚══════╝╚══════╝
    */

    private Description description;

    private Path dirPath = null;
    private Path intermediatePath = null;
    private Path finishedPath = null;
    private Path imgFilePath = null;
    private Path mp3FilePath = null;
    private Path descriptionFilePath = null;

    private String urlString;

    public boolean failed = false;

    /* 
     ██████╗ ██████╗ ███╗   ██╗███████╗████████╗██████╗ ██╗   ██╗ ██████╗████████╗ ██████╗ ██████╗ ███████╗
    ██╔════╝██╔═══██╗████╗  ██║██╔════╝╚══██╔══╝██╔══██╗██║   ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝
    ██║     ██║   ██║██╔██╗ ██║███████╗   ██║   ██████╔╝██║   ██║██║        ██║   ██║   ██║██████╔╝███████╗
    ██║     ██║   ██║██║╚██╗██║╚════██║   ██║   ██╔══██╗██║   ██║██║        ██║   ██║   ██║██╔══██╗╚════██║
    ╚██████╗╚██████╔╝██║ ╚████║███████║   ██║   ██║  ██║╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║███████║
     ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝
    */

    /**
     * Constructor for a song object which represents a song auto-generated by Youtube.
     * Contains all data needed for initial download and will be processed and formatted
     * later on.
     *
     * @param url       URL of the video to be downloaded.
     * @param directory Directory where files end up.
     */
    public Song(String url, String directory) {

        this.urlString = url;
        this.dirPath = Paths.get(directory);
        this.intermediatePath = Paths.get(dirPath.toAbsolutePath().toString() + "\\intermediate");
        this.finishedPath = Paths.get(dirPath.toAbsolutePath().toString() + "\\finished");

        //throw new IllegalArgumentException("HALTED.");
    }

    /*
    ██╗███╗   ██╗████████╗███████╗██████╗ ███╗   ███╗███████╗██████╗ ██╗ █████╗ ██████╗ ██╗   ██╗
    ██║████╗  ██║╚══██╔══╝██╔════╝██╔══██╗████╗ ████║██╔════╝██╔══██╗██║██╔══██╗██╔══██╗╚██╗ ██╔╝
    ██║██╔██╗ ██║   ██║   █████╗  ██████╔╝██╔████╔██║█████╗  ██║  ██║██║███████║██████╔╝ ╚████╔╝ 
    ██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗██║╚██╔╝██║██╔══╝  ██║  ██║██║██╔══██║██╔══██╗  ╚██╔╝  
    ██║██║ ╚████║   ██║   ███████╗██║  ██║██║ ╚═╝ ██║███████╗██████╔╝██║██║  ██║██║  ██║   ██║   
    ╚═╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝╚═════╝ ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   

    ██████╗ ██████╗  ██████╗  ██████╗███████╗███████╗███████╗███████╗███████╗                    
    ██╔══██╗██╔══██╗██╔═══██╗██╔════╝██╔════╝██╔════╝██╔════╝██╔════╝██╔════╝                    
    ██████╔╝██████╔╝██║   ██║██║     █████╗  ███████╗███████╗█████╗  ███████╗                    
    ██╔═══╝ ██╔══██╗██║   ██║██║     ██╔══╝  ╚════██║╚════██║██╔══╝  ╚════██║                    
    ██║     ██║  ██║╚██████╔╝╚██████╗███████╗███████║███████║███████╗███████║                    
    ╚═╝     ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚══════╝╚══════╝╚══════╝╚══════╝╚══════╝                    
    */

    public int downloadFiles() throws IOException, InterruptedException {

        String outputTemplate = (dirPath.toAbsolutePath().toString() + "\\%(title)s.%(ext)s");

        String[] commandStringArray = {
            ".\\lib\\yt-dlp.exe",
            "--no-playlist",
            "--write-description",
            "--limit-rate", "100G",
            "--windows-filenames",
            "--ffmpeg-location", "lib\\",
            "--write-thumbnail",
            "--convert-thumbnails", "jpg",
            "--extract-audio",
            "--audio-format", "mp3",
            "--audio-quality", "0",
            "-o", outputTemplate,
            ("\"" + this.urlString + "\""),
        };

        return startProcess(commandStringArray);

    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public void findFiles() throws IOException {
        Files.list(this.dirPath).forEach(path -> {
            if (path.toAbsolutePath().toString().toLowerCase().endsWith(".mp3")) {
                this.mp3FilePath = path.toAbsolutePath();
            } else if (path.toAbsolutePath().toString().toLowerCase().endsWith(".description")) {
                this.descriptionFilePath = path.toAbsolutePath();
            } else if (path.toAbsolutePath().toString().toLowerCase().endsWith(".jpg")) {
                this.imgFilePath = path.toAbsolutePath();
            }
        });
        if (mp3FilePath == null || descriptionFilePath == null || imgFilePath == null) {
            new FileNotFoundException("Required file(s) not found.").printStackTrace();
        }
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public void initializeDescription() {
        try {
            this.description = new Description(String.join("\n", Files.readAllLines(descriptionFilePath)));
        } catch (IllegalArgumentException e) {
            System.err.println("Song is not of the proper format (description does not start with 'Provided to YouTube by ...'). Song must be autogenerated by YouTube.");
            failed = true;
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Song description file could not be found.");
            failed = true;
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error occured while reading file");
            failed = true;
            e.printStackTrace();
        }
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

public int applyMetadata() throws IOException, InterruptedException, IllegalArgumentException {

    // Ensure the intermediate directory is created
    if (!Files.exists(this.intermediatePath)) {
        Files.createDirectories(this.intermediatePath);
    }

    // Base command arguments
    List<String> commandList = new ArrayList<>(Arrays.asList(
        ".\\lib\\ffmpeg.exe",
        "-nostdin",
        "-i", ("\"" + this.mp3FilePath.toAbsolutePath().toString() + "\""),
        "-metadata", "title=" + description.getTitleString(),
        "-metadata", "artist=" + description.getAllArtistsString(),
        "-metadata", "album_artist=" + description.getMainArtistString(),
        "-metadata", "album=" + description.getAlbumString(),
        "-id3v2_version", "3",
        "-write_id3v1", "1",
        "-y",
        "-c", "copy",
        ("\"" + this.intermediatePath.toAbsolutePath().toString() + "\\" + this.mp3FilePath.getFileName().toString() + "\"")
    ));

    // Add year metadata only if it's available
    if (this.description.getYearString() != null) {
        commandList.add(8, "-metadata");
        commandList.add(9, "date=" + description.getYearString());
    }

    // Convert the list back to an array
    String[] commandStringArray = commandList.toArray(new String[0]);

    return startProcess(commandStringArray);
}


//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public int cropCoverArt() throws IOException, InterruptedException {

        String[] coverArtStringArray = {
            ".\\lib\\ffmpeg.exe",
            "-nostdin",
            "-i", ("\"" + this.imgFilePath.toAbsolutePath().toString() + "\""),
            "-y",
            "-vf",
            "crop='min(iw,ih):min(iw,ih)'",
            "-update", "1",
            ("\"" + this.imgFilePath.toAbsolutePath().toString() + "\""),
        };

        return startProcess(coverArtStringArray);
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public int applyCoverArt() throws IOException, InterruptedException {
        if (!Files.exists(this.finishedPath)) {
            Files.createDirectories(this.finishedPath);
        }

        String[] coverArtStringArray = {
            ".\\lib\\ffmpeg.exe",
            "-nostdin",
            "-i", ("\"" + this.intermediatePath.toAbsolutePath().toString() + "\\" + this.mp3FilePath.getFileName().toString() + "\""),
            "-i", ("\"" + this.imgFilePath.toAbsolutePath().toString() + "\""),
            "-y",
            "-map", "0",
            "-map", "1:0",
            "-codec", "copy",
            ("\""
                + this.finishedPath.toAbsolutePath().toString()
                + "\\"
                + sanitizeFilename(this.description.getMainArtistString())
                + " - "
                + sanitizeFilename(this.description.getTitleString())
                + ".mp3"
                + "\""
            ),
        };

        return startProcess(coverArtStringArray);
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public void moveFile() throws IOException {

        String filenameString = ("\\" + sanitizeFilename(this.description.getMainArtistString()) + " - " + sanitizeFilename(this.description.getTitleString()) + ".mp3");
        Path inputPath = Paths.get(this.finishedPath.toAbsolutePath() + filenameString);
        Path outputPath = Paths.get(this.dirPath.toAbsolutePath().getParent().toAbsolutePath() + filenameString);

        Files.move(inputPath, outputPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public void purgeDir() throws IOException {

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(dirPath)) {
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }

    /*
    ██╗  ██╗███████╗██╗     ██████╗ ███████╗██████╗              
    ██║  ██║██╔════╝██║     ██╔══██╗██╔════╝██╔══██╗             
    ███████║█████╗  ██║     ██████╔╝█████╗  ██████╔╝             
    ██╔══██║██╔══╝  ██║     ██╔═══╝ ██╔══╝  ██╔══██╗             
    ██║  ██║███████╗███████╗██║     ███████╗██║  ██║             
    ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚══════╝╚═╝  ╚═╝             

    ███╗   ███╗███████╗████████╗██╗  ██╗ ██████╗ ██████╗ ███████╗
    ████╗ ████║██╔════╝╚══██╔══╝██║  ██║██╔═══██╗██╔══██╗██╔════╝
    ██╔████╔██║█████╗     ██║   ███████║██║   ██║██║  ██║███████╗
    ██║╚██╔╝██║██╔══╝     ██║   ██╔══██║██║   ██║██║  ██║╚════██║
    ██║ ╚═╝ ██║███████╗   ██║   ██║  ██║╚██████╔╝██████╔╝███████║
    ╚═╝     ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝
    */

    private int startProcess(String[] commandStringArray) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(commandStringArray);

        InputStream errorStream = process.getErrorStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.err.println(line);
        }

        int exitCode = process.waitFor();
        return exitCode;
    }

//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████
//██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████

    public static String sanitizeFilename(String input) {
        StringBuilder sanitized = new StringBuilder();

        for (char c : input.toCharArray()) {
            switch (c) {
                case '\\' -> sanitized.append('⧵');
                case '/' -> sanitized.append('⧸');
                case ':' -> sanitized.append('꞉');
                case '*' -> sanitized.append('＊');
                case '?' -> sanitized.append('？');
                case '"' -> sanitized.append('ʺ');
                case '<' -> sanitized.append('‹');
                case '>' -> sanitized.append('›');
                case '|' -> sanitized.append('¦');
                default -> sanitized.append(c);
            }
        }

        return sanitized.toString();
    }

    /* 
    ██████╗ ███████╗████████╗████████╗███████╗██████╗ ███████╗
    ██╔════╝ ██╔════╝╚══██╔══╝╚══██╔══╝██╔════╝██╔══██╗██╔════╝
    ██║  ███╗█████╗     ██║      ██║   █████╗  ██████╔╝███████╗
    ██║   ██║██╔══╝     ██║      ██║   ██╔══╝  ██╔══██╗╚════██║
    ╚██████╔╝███████╗   ██║      ██║   ███████╗██║  ██║███████║
     ╚═════╝ ╚══════╝   ╚═╝      ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝    
    */

    public String getUrlString() {
        return urlString;
    }
    
    /*
     ██████╗ ██████╗ ███╗   ███╗██████╗ ██╗███╗   ██╗███████╗██████╗  
    ██╔════╝██╔═══██╗████╗ ████║██╔══██╗██║████╗  ██║██╔════╝██╔══██╗ 
    ██║     ██║   ██║██╔████╔██║██████╔╝██║██╔██╗ ██║█████╗  ██║  ██║ 
    ██║     ██║   ██║██║╚██╔╝██║██╔══██╗██║██║╚██╗██║██╔══╝  ██║  ██║ 
    ╚██████╗╚██████╔╝██║ ╚═╝ ██║██████╔╝██║██║ ╚████║███████╗██████╔╝ 
     ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝╚═════╝  

    ███████╗██╗   ██╗███╗   ██╗ ██████╗████████╗██╗ ██████╗ ███╗   ██╗
    ██╔════╝██║   ██║████╗  ██║██╔════╝╚══██╔══╝██║██╔═══██╗████╗  ██║
    █████╗  ██║   ██║██╔██╗ ██║██║        ██║   ██║██║   ██║██╔██╗ ██║
    ██╔══╝  ██║   ██║██║╚██╗██║██║        ██║   ██║██║   ██║██║╚██╗██║
    ██║     ╚██████╔╝██║ ╚████║╚██████╗   ██║   ██║╚██████╔╝██║ ╚████║
    ╚═╝      ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝
    */

    @Override
    public void run() {
        try {
            downloadFiles();
            findFiles();
            initializeDescription();
            applyMetadata();
            cropCoverArt();
            applyCoverArt();
            moveFile();
            purgeDir();
        } catch (IOException e) {
            System.out.println("IO exception occured.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Process was interrupted.");
            e.printStackTrace();
        }
    }

}
