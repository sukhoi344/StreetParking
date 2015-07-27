package chau.streetparking.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chauthai on 12/19/14.
 */
public class FileUtil {

    private static final int BUFFER_SIZE = 1024 * 10;

    /** for validating folder name */
    public static final String folderRegex = "(([a-z])| |([A-Z])|\\(|\\)"
            + "|[0-9]|_|-|\\+|=|`|~|;|!|@|\\.|\\[|\\]|\\{|\\})+";

    /**
     * Read bytes from a file path
     * @param path path to the file
     * @return null if fails
     */
    public static byte[] getBytesFromPath(String path) {
        if (path == null || path.isEmpty())
            return null;

        File file = new File(path);
        if (file.length() <=0)
            return null;

        InputStream is = null;

        try {
            int size = (int) file.length();
            byte[] data = new byte[size];
            is = new BufferedInputStream(new FileInputStream(file));
            is.read(data);

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable ignore) {}
            }
        }

        return null;
    }

    /**
     * Read text from from a file object
     * @param file A text file.
     * @return String which the text file has
     * @throws IOException
     */
    public static String readTextFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }
        br.close();

        return sb.toString();
    }

    /**
     * Save a file from Uri via ContentResolver.
     * @param uri path to database, which can be used to create an InputStream object
     * @param fullDestPath full destination path (includes file name and extension)
     * @return A real file stored in the destination directory
     * @throws IOException
     */
    public static File saveFileFromUri(Context context, Uri uri, String fullDestPath) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream is = contentResolver.openInputStream(uri);

        return saveFileFromStream(is, fullDestPath);
    }

    /**
     * Retrieve data from a InputStream object and save the data into a file. Existing file will be deleted.
     * @param is InputStream object
     * @param fullDestPath full destination path (includes file name and extension)
     * @return new File object contains the stream's data
     * @throws IOException
     */
    public static File saveFileFromStream(InputStream is, String fullDestPath) throws IOException {
        File file = new File(fullDestPath);

        if (file.exists())
            file.delete();

        file = new File(fullDestPath);
        OutputStream os = null;

        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) != -1)
                os.write(buffer, 0, read);

            os.flush();
        } finally {
            if (os != null)
                os.close();
            if (is != null)
                is.close();
        }

        return file;
    }

    /**
     * Get file name from an uri via ContentResolver
     * @return null if fail
     */
    public static String getFileNameFromUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = { MediaStore.MediaColumns.DISPLAY_NAME };
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                return cursor.getString(nameIndex);
            }
        }

        return null;
    }

    /**
     * Get file size in bytes from an uri via ContentResolver
     * @return -1 if fail
     */
    public static long getFileSizeFromUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = { MediaStore.MediaColumns.SIZE };
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            if (nameIndex >= 0) {
                return cursor.getLong(nameIndex);
            }
        }

        return -1;
    }

    /**
     * Return list of directory names in the given directory
     */
    public static List<String> getDirectoryNames(String dirPath) {
        List<String> list = new ArrayList<>();

        if (dirPath != null && !dirPath.isEmpty()) {
            File file = new File(dirPath);

            String[] directories = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return new File(dir, filename).isDirectory();
                }
            });

            for (String s : directories)
                list.add(s);
        }

        return list;
    }

    /**
     * Create a directory with a provided full path. If directory is
     * already existed, add "(1)" to the directory name.
     * @param dirPath full path of the new directory
     * @return new directory path, null if fail
     */
    public static String createDirectory(String dirPath) {
        if (dirPath == null || dirPath.isEmpty())
            return null;

        if (!dirPath.matches(folderRegex))
            return null;

        File newDir = new File(dirPath);

        if (!newDir.exists()) {
            newDir.mkdirs();
        } else {
            dirPath += "(1)";
            newDir = new File(dirPath);
            newDir.mkdirs();
        }

        return dirPath;
    }

    /**
     * Check if file is already exists. If it exists, add mark number of
     * increment the mark number and return the new file name
     * @param fileFullPath full path of the file to be checked
     * @return null if fail. If file doesn't exist, return the same file name.
     * If file exists, return new file new with mark number in the end .
     */
    public static String checkDuplicateFile(String fileFullPath) {
        if(fileFullPath == null || fileFullPath.isEmpty())
            return null;

        File file = new File(fileFullPath);

        if(file.exists()) {
            if(fileFullPath.matches(".+\\([0-9]\\)\\.([a-z]|[A-Z])+$")) {

                int dotIndex = fileFullPath.lastIndexOf(".");
                String nameWithoutExt = fileFullPath.substring(0, dotIndex);
                String ext = fileFullPath.substring(dotIndex, fileFullPath.length());

                int markNumber = Integer.parseInt(
                        fileFullPath.substring(dotIndex - 2, dotIndex - 1));

                String newName = nameWithoutExt.substring(0, dotIndex - 3)
                        + "(" + ++markNumber + ")" + ext;

                return checkDuplicateFile(newName);

            } else {
                int dotIndex = fileFullPath.lastIndexOf(".");

                String nameWithoutExt = fileFullPath.substring(0, dotIndex);
                String ext = fileFullPath.substring(dotIndex, fileFullPath.length());
                String newName = nameWithoutExt + "(1)" + ext;

                return checkDuplicateFile(newName);
            }
        }

        return fileFullPath;
    }

    /**
     * Save bytes to file in the private storage
     * @param data in bytes
     * @param fileName
     * @return false if fail
     */
    public static boolean savePrivateFileFromBytes(Context context, byte[] data, String fileName) {
        OutputStream os = null;

        try {
            os = new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            os.write(data);
            os.flush();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable ignore) {}
            }
        }

        return false;
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException in case cleaning is unsuccessful
     */
    public static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (final File file : files) {
            file.delete();
        }

        if (null != exception) {
            throw exception;
        }
    }
}
