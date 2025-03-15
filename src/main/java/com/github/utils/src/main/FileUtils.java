package com.github.utils.src.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for file operations. This class provides methods for reading,
 * writing, copying, moving, deleting files, managing directories, and working
 * with file compression (ZIP).
 */
public class FileUtils {

    /**
     * Reads a file and returns its content as a String.
     *
     * @param filePath Path to the file.
     * @return File content as a string.
     * @throws IOException if file read fails.
     */
    public static String readFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    /**
     * Writes content to a file (overwrites if exists).
     *
     * @param filePath Path to the file.
     * @param content  Content to write.
     * @throws IOException if writing fails.
     */
    public static void writeFile(String filePath, String content) throws IOException {
        Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Reads file content as a list of lines.
     *
     * @param filePath Path to the file.
     * @return List of lines in the file.
     * @throws IOException if reading fails.
     */
    public static List<String> readFileAsLines(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
    }

    /**
     * Appends content to a file.
     *
     * @param filePath Path to the file.
     * @param content  Content to append.
     * @throws IOException if writing fails.
     */
    public static void appendToFile(String filePath, String content) throws IOException {
        Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Copies a file from source to destination.
     *
     * @param source      Source file path.
     * @param destination Destination file path.
     * @throws IOException if copy fails.
     */
    public static void copyFile(String source, String destination) throws IOException {
        Files.copy(Path.of(source), Path.of(destination), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Moves a file from source to destination.
     *
     * @param source      Source file path.
     * @param destination Destination file path.
     * @throws IOException if move fails.
     */
    public static void moveFile(String source, String destination) throws IOException {
        Files.move(Path.of(source), Path.of(destination), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Deletes a file.
     *
     * @param filePath Path to the file.
     * @return True if deleted, false otherwise.
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates a directory if it doesn't exist.
     *
     * @param dirPath Directory path.
     * @throws IOException if creation fails.
     */
    public static void createDirectory(String dirPath) throws IOException {
        Files.createDirectories(Path.of(dirPath));
    }

    /**
     * Checks if a file exists.
     *
     * @param filePath Path to the file.
     * @return True if exists, false otherwise.
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Path.of(filePath));
    }

    /**
     * Gets the file size in bytes.
     *
     * @param filePath Path to the file.
     * @return File size in bytes.
     * @throws IOException if file access fails.
     */
    public static long getFileSize(String filePath) throws IOException {
        return Files.size(Path.of(filePath));
    }

    /**
     * Gets the last modified time of a file.
     *
     * @param filePath Path to the file.
     * @return Last modified time as a string.
     * @throws IOException if file access fails.
     */
    public static String getLastModifiedTime(String filePath) throws IOException {
        return Files.getLastModifiedTime(Path.of(filePath)).toString();
    }

    /**
     * Gets the file extension.
     *
     * @param filePath Path to the file.
     * @return File extension (e.g., "txt", "jpg"), or empty string if no extension.
     */
    public static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex + 1);
    }

    /**
     * Compresses a file into a ZIP archive.
     *
     * @param sourceFile Path to the file.
     * @param zipFile    Destination ZIP file.
     * @throws IOException if compression fails.
     */
    public static void compressFile(String sourceFile, String zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(sourceFile)) {

            ZipEntry zipEntry = new ZipEntry(new File(sourceFile).getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    /**
     * Extracts a ZIP file to a specified directory.
     *
     * @param zipFilePath Path to the ZIP file.
     * @param destDir     Destination directory.
     * @throws IOException if extraction fails.
     */
    public static void extractZipFile(String zipFilePath, String destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Gets the MIME type of file based on its content.
     *
     * @param filePath Path to the file.
     * @return MIME type of the file (e.g., "text/plain", "image/jpeg").
     * @throws IOException if file access fails.
     */
    public static String getMimeType(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.probeContentType(path);
    }

    /**
     * Lists all files in a directory.
     *
     * @param directoryPath Path to the directory.
     * @return A list of filenames in the directory. If the directory is invalid or does not exist, returns an empty list.
     */
    public static List<String> listFiles(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return List.of(); // Return empty list if invalid directory
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Reads a file in chunks of a specified size.
     *
     * @param filePath Path to the file.
     * @param chunkSize The size of each chunk in bytes.
     * @throws IOException if reading the file fails.
     */
    public static void readFileInChunks(String filePath, int chunkSize) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                System.out.println("Read " + bytesRead + " bytes");
            }
        }
    }

    /**
     * Creates a temporary file with the given prefix and suffix.
     *
     * @param prefix The prefix for the temporary file name.
     * @param suffix The suffix for the temporary file name.
     * @return The absolute path of the created temporary file.
     * @throws IOException if creating the temporary file fails.
     */
    public static String createTempFile(String prefix, String suffix) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        return tempFile.toAbsolutePath().toString();
    }
    
}

