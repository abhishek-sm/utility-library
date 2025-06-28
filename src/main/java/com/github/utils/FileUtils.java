package com.github.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.experimental.UtilityClass;

/**
 * Utility class for file operations. This class provides methods for reading,
 * writing, copying, moving, deleting files, managing directories, and working
 * with file compression (ZIP).
 */
@UtilityClass
public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

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
     * Reads a file and returns its content as a String with specified charset.
     *
     * @param filePath Path to the file.
     * @param charset Charset to use for reading.
     * @return File content as a string.
     * @throws IOException if file read fails.
     */
    public static String readFile(String filePath, Charset charset) throws IOException {
        return Files.readString(Path.of(filePath), charset);
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
     * Writes content to a file with specified charset (overwrites if exists).
     *
     * @param filePath Path to the file.
     * @param content  Content to write.
     * @param charset  Charset to use for writing.
     * @throws IOException if writing fails.
     */
    public static void writeFile(String filePath, String content, Charset charset) throws IOException {
        Files.writeString(Path.of(filePath), content, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
     * Reads file content as a list of lines with specified charset.
     *
     * @param filePath Path to the file.
     * @param charset  Charset to use for reading.
     * @return List of lines in the file.
     * @throws IOException if reading fails.
     */
    public static List<String> readFileAsLines(String filePath, Charset charset) throws IOException {
        return Files.readAllLines(Path.of(filePath), charset);
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
     * Appends content to a file with specified charset.
     *
     * @param filePath Path to the file.
     * @param content  Content to append.
     * @param charset  Charset to use for writing.
     * @throws IOException if writing fails.
     */
    public static void appendToFile(String filePath, String content, Charset charset) throws IOException {
        Files.writeString(Path.of(filePath), content, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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
     * Copies a file from source to destination with copy options.
     *
     * @param source      Source file path.
     * @param destination Destination file path.
     * @param options     Copy options to use.
     * @throws IOException if copy fails.
     */
    public static void copyFile(String source, String destination, CopyOption... options) throws IOException {
        Files.copy(Path.of(source), Path.of(destination), options);
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
     * Moves a file from source to destination with move options.
     *
     * @param source      Source file path.
     * @param destination Destination file path.
     * @param options     Move options to use.
     * @throws IOException if move fails.
     */
    public static void moveFile(String source, String destination, CopyOption... options) throws IOException {
        Files.move(Path.of(source), Path.of(destination), options);
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
     * Deletes a directory and all its contents recursively.
     *
     * @param dirPath Path to the directory.
     * @return True if deleted successfully, false otherwise.
     */
    public static boolean deleteDirectory(String dirPath) {
        try {
            Files.walk(Path.of(dirPath))
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
            return true;
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
     * Checks if a path is a directory.
     *
     * @param path Path to check.
     * @return True if it's a directory, false otherwise.
     */
    public static boolean isDirectory(String path) {
        return Files.isDirectory(Path.of(path));
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
     * @return Last modified time as a FileTime object.
     * @throws IOException if file access fails.
     */
    public static FileTime getLastModifiedTime(String filePath) throws IOException {
        return Files.getLastModifiedTime(Path.of(filePath));
    }

    /**
     * Gets the last modified time of a file as a formatted string.
     *
     * @param filePath Path to the file.
     * @param format   Date format pattern.
     * @return Last modified time as a formatted string.
     * @throws IOException if file access fails.
     */
    public static String getLastModifiedTimeFormatted(String filePath, String format) throws IOException {
        FileTime fileTime = Files.getLastModifiedTime(Path.of(filePath));
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(fileTime.toMillis()));
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
     * Gets the file name without extension.
     *
     * @param filePath Path to the file.
     * @return File name without extension.
     */
    public static String getFileNameWithoutExtension(String filePath) {
        String fileName = new File(filePath).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Gets the file name with extension.
     *
     * @param filePath Path to the file.
     * @return File name with extension.
     */
    public static String getFileName(String filePath) {
        return new File(filePath).getName();
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

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    /**
     * Compresses multiple files into a ZIP archive.
     *
     * @param sourceFiles List of paths to the files.
     * @param zipFile     Destination ZIP file.
     * @throws IOException if compression fails.
     */
    public static void compressFiles(List<String> sourceFiles, String zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String sourceFile : sourceFiles) {
                File file = new File(sourceFile);
                if (!file.exists()) {
                    continue;
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    /**
     * Compresses a directory into a ZIP archive.
     *
     * @param sourceDir Directory to compress.
     * @param zipFile   Destination ZIP file.
     * @throws IOException if compression fails.
     */
    public static void compressDirectory(String sourceDir, String zipFile) throws IOException {
        Path sourcePath = Path.of(sourceDir);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            String relativePath = sourcePath.relativize(path).toString();
                            ZipEntry zipEntry = new ZipEntry(relativePath);
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            // Handle or log the exception
                        }
                    });
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

                // Create parent directories if they don't exist
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
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
                .filter(File::isFile)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Lists all directories in a directory.
     *
     * @param directoryPath Path to the directory.
     * @return A list of directory names in the directory. If the directory is invalid or does not exist, returns an empty list.
     */
    public static List<String> listDirectories(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return List.of(); // Return empty list if invalid directory
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Lists all files in a directory and its subdirectories recursively.
     *
     * @param directoryPath Path to the directory.
     * @return A list of file paths (relative to the given directory).
     * @throws IOException if directory access fails.
     */
    public static List<String> listFilesRecursively(String directoryPath) throws IOException {
        Path start = Path.of(directoryPath);
        if (!Files.exists(start) || !Files.isDirectory(start)) {
            return List.of(); // Return empty list if invalid directory
        }

        return Files.walk(start)
                .filter(Files::isRegularFile)
                .map(path -> start.relativize(path).toString())
                .collect(Collectors.toList());
    }

    /**
     * Reads a file in chunks of a specified size.
     *
     * @param filePath  Path to the file.
     * @param chunkSize The size of each chunk in bytes.
     * @param consumer  Consumer function to process each chunk.
     * @throws IOException if reading the file fails.
     */
    public static void readFileInChunks(String filePath, int chunkSize,
                                        Consumer<byte[]> consumer) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                if (bytesRead < chunkSize) {
                    // Create a smaller array for the last chunk if needed
                    byte[] lastChunk = Arrays.copyOf(buffer, bytesRead);
                    consumer.accept(lastChunk);
                } else {
                    consumer.accept(buffer);
                }
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

    /**
     * Creates a temporary directory with the given prefix.
     *
     * @param prefix The prefix for the temporary directory name.
     * @return The absolute path of the created temporary directory.
     * @throws IOException if creating the temporary directory fails.
     */
    public static String createTempDirectory(String prefix) throws IOException {
        Path tempDir = Files.createTempDirectory(prefix);
        return tempDir.toAbsolutePath().toString();
    }

    /**
     * Calculates the MD5 hash of a file.
     *
     * @param filePath Path to the file.
     * @return MD5 hash as a hex string.
     * @throws IOException if file access fails.
     */
    public static String calculateMD5(String filePath) throws IOException {
        try (InputStream is = Files.newInputStream(Path.of(filePath))) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] md5sum = digest.digest();
            StringBuilder hex = new StringBuilder();

            for (byte b : md5sum) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * Calculates the SHA-256 hash of a file.
     *
     * @param filePath Path to the file.
     * @return SHA-256 hash as a hex string.
     * @throws IOException if file access fails.
     */
    public static String calculateSHA256(String filePath) throws IOException {
        try (InputStream is = Files.newInputStream(Path.of(filePath))) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] sha256sum = digest.digest();
            StringBuilder hex = new StringBuilder();

            for (byte b : sha256sum) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Sets file permissions (POSIX).
     * Note: This only works on POSIX-compliant file systems.
     *
     * @param filePath Path to the file.
     * @param permissions Permission string, e.g., "rwxr-xr--"
     * @throws IOException if setting permissions fails.
     */
    public static void setFilePermissions(String filePath, String permissions) throws IOException {
        Path path = Path.of(filePath);
        Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(permissions);
        Files.setPosixFilePermissions(path, filePermissions);
    }

    /**
     * Gets file permissions as a POSIX string.
     * Note: This only works on POSIX-compliant file systems.
     *
     * @param filePath Path to the file.
     * @return Permission string, e.g., "rwxr-xr--"
     * @throws IOException if getting permissions fails.
     */
    public static String getFilePermissions(String filePath) throws IOException {
        Path path = Path.of(filePath);
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
        return PosixFilePermissions.toString(permissions);
    }

    /**
     * Copies a directory recursively.
     *
     * @param sourceDir Source directory path.
     * @param targetDir Target directory path.
     * @throws IOException if copying fails.
     */
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        Path sourcePath = Path.of(sourceDir);
        Path targetPath = Path.of(targetDir);

        Files.walk(sourcePath)
                .forEach(source -> {
                    try {
                        Path target = targetPath.resolve(sourcePath.relativize(source));
                        if (Files.isDirectory(source)) {
                            if (!Files.exists(target)) {
                                Files.createDirectories(target);
                            }
                        } else {
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    /**
     * Monitors a directory for file changes.
     *
     * @param directoryPath Path to the directory to monitor.
     * @param callback Callback to be called when a file change is detected.
     * @throws IOException if monitoring fails.
     */
    public static WatchService monitorDirectory(String directoryPath,
                                                Consumer<WatchEvent<?>> callback) throws IOException {
        Path path = Path.of(directoryPath);
        WatchService watchService = FileSystems.getDefault().newWatchService();

        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        // Start a thread to watch for changes
        Thread watchThread = new Thread(() -> {
            try {
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        callback.accept(event);
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        watchThread.setDaemon(true);
        watchThread.start();

        return watchService;
    }

    /**
     * Gets the canonical path of a file.
     *
     * @param filePath Path to the file.
     * @return The canonical path.
     * @throws IOException if file access fails.
     */
    public static String getCanonicalPath(String filePath) throws IOException {
        return new File(filePath).getCanonicalPath();
    }

    /**
     * Compares two files for equality.
     *
     * @param file1 Path to the first file.
     * @param file2 Path to the second file.
     * @return True if files are identical, false otherwise.
     * @throws IOException if file access fails.
     */
    public static boolean compareFiles(String file1, String file2) throws IOException {
        if (Files.size(Path.of(file1)) != Files.size(Path.of(file2))) {
            return false;
        }

        try (InputStream is1 = Files.newInputStream(Path.of(file1));
             InputStream is2 = Files.newInputStream(Path.of(file2))) {

            byte[] buf1 = new byte[DEFAULT_BUFFER_SIZE];
            byte[] buf2 = new byte[DEFAULT_BUFFER_SIZE];
            int read1, read2;

            do {
                read1 = is1.read(buf1);
                read2 = is2.read(buf2);

                if (read1 != read2) {
                    return false;
                }

                if (read1 > 0 && !Arrays.equals(buf1, 0, read1, buf2, 0, read2)) {
                    return false;
                }
            } while (read1 > 0);

            return true;
        }
    }

    /**
     * Writes binary data to a file.
     *
     * @param filePath Path to the file.
     * @param data Binary data to write.
     * @throws IOException if writing fails.
     */
    public static void writeBinaryFile(String filePath, byte[] data) throws IOException {
        Files.write(Path.of(filePath), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Reads a file as binary data.
     *
     * @param filePath Path to the file.
     * @return Binary data from the file.
     * @throws IOException if reading fails.
     */
    public static byte[] readBinaryFile(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }
}