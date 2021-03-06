/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opentravel.schemacompiler.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base class for utility file managers that provide methods for creating,
 * restoring, and deleting backup files during save operations.
 */
public class AbstractFileUtils {
	
    /**
     * Creates a backup of the specified original file on the local file system. The location of the
     * newly-created backup file is returned by this method.
     * 
     * @param originalFile
     *            the original file to backup
     * @return File
     * @throws IOException
     *             thrown if the backup file cannot be created
     */
    public static File createBackupFile(File originalFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            File backupFile;

            if (!originalFile.exists()) {
                backupFile = null;

            } else {
                byte[] buffer = new byte[1024];
                int bytesRead;

                backupFile = new File(originalFile.getParentFile(), getBackupFilename(originalFile));
                in = new FileInputStream(originalFile);
                out = new FileOutputStream(backupFile);

                while ((bytesRead = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return backupFile;

        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Throwable t) {
            }
            try {
                if (out != null)
                    out.close();
            } catch (Throwable t) {
            }
        }
    }

    /**
     * Creates a backup of the specified original file on the local file system.
     * 
     * @param backupFile
     *            the backup file to restore
     * @param originalFilename
     *            the original name of the file to be restored (without the filepath)
     * @throws IOException
     *             thrown if the backup file cannot be restored
     */
    public static void restoreBackupFile(File backupFile, String originalFilename)
            throws IOException {
        if ((backupFile != null) && backupFile.exists()) {
            String filename = backupFile.getName();

            if (!filename.endsWith(".bak")) {
                throw new IllegalArgumentException("The specified file is not a valid backup.");
            }
            if (!backupFile.exists()) {
                throw new IllegalStateException("The specified backup file no longer exists: "
                        + backupFile.getAbsolutePath());
            }
            File originalFile = new File(backupFile.getParentFile(), originalFilename);

            if (originalFile.exists()) {
                if (!originalFile.delete()) {
                    throw new IOException(
                            "Unable to delete original file during restoration of backup: "
                                    + backupFile.getAbsolutePath());
                }
            }
            backupFile.renameTo(originalFile);
        }
    }

    /**
     * Deletes the inidicated backup file from the local file system.
     * 
     * @param backupFile
     *            the backup file to remove
     */
    public static void removeBackupFile(File backupFile) {
        if ((backupFile != null) && backupFile.exists()) {
            backupFile.delete();
        }
    }

    /**
     * Returns the name of the backup file for the specified original.
     * 
     * @param originalFile
     *            the original file for which a backup is being created
     * @return String
     */
    public static String getBackupFilename(File originalFile) {
        String filename = null;

        if (originalFile != null) {
            String originalFilename = originalFile.getName();
            int dotIdx = originalFilename.lastIndexOf('.');

            filename = ((dotIdx < 0) ? originalFilename : originalFilename.substring(0, dotIdx))
                    + ".bak";
        }
        return filename;
    }

    /**
     * Copies the specified source file's content to the destination file's location. If the
     * destination file already exists, it will be overwritten.
     * 
     * @param sourceFile
     *            the source file's location
     * @param destinationFile
     *            the destination file's location
     * @throws IOException
     *             thrown if the file cannot be copied
     */
    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }

        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Throwable t) {
            }
            try {
                if (out != null)
                    out.close();
            } catch (Throwable t) {
            }
        }
    }

}
