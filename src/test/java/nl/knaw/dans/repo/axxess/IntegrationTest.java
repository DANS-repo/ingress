package nl.knaw.dans.repo.axxess;

import com.healthmarketscience.jackcess.Database;
import nl.knaw.dans.repo.axxess.acc2csv.AxxessToCsvConverter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static File baseDirectory = new File("src/test/resources/integration").getAbsoluteFile();

    private static String[] dbDirectories = {
      "avereest",   // File format: V1997 [VERSION_3]   AccessVersion: 07.53
      "walcheren",  // File format: V2000 [VERSION_4]   AccessVersion: 08.50
      "kohier"      // File format: V2007 [VERSION_12]  AccessVersion: 09.50

    };

    @BeforeAll
    static void beforeAll() throws Exception {
        for (String name : dbDirectories) {
            assert deleteDirectory(getAcc2csvDir(name));
            assert deleteDirectory(getCsv2accDir(name));
        }
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        if (directoryToBeDeleted.exists()) {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
            return directoryToBeDeleted.delete();
        }
        return true;
    }

    private static File getDbFile(String name) throws IOException {
        File dbDir = FileUtils.getFile(baseDirectory, name, "db");
        File[] files = dbDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (Database.FileFormat fm : Database.FileFormat.values()) {
                    if (name.toLowerCase().endsWith(fm.getFileExtension())) {
                        return true;
                    }
                }
                return false;
            }
        });
        if (files == null || files.length < 1) throw new IOException("dbFile does not exist: " + name);
        return files[0];
    }

    private static File getZipFile(String name) throws IOException {
        String zipFilename = getDbName(name) + ".csv.zip";
        return new File(getAcc2csvZipDir(name), zipFilename);
    }

    private static File getMetadataFile(String name) throws IOException {
        String metadataFilename = getDbName(name) + "._metadata.csv";
        return  new File(getAcc2csvFilesDir(name), metadataFilename);
    }

    private static File getAcc2csvDir(String name) {
        return FileUtils.getFile(baseDirectory, name, "acc2csv");
    }

    private static File getAcc2csvZipDir(String name) {
        return new File(getAcc2csvDir(name), "zipped");
    }

    private static File getAcc2csvFilesDir(String name) {
        return new File(getAcc2csvDir(name), "files");
    }

    private static File getCsv2accDir(String name) {
        return FileUtils.getFile(baseDirectory, name, "csv2acc");
    }

    private static String getDbName(String name) throws IOException {
        return getDbFile(name).getName();
    }

    @Test
    void acc2csv2acc() throws Exception {
        for (String name : dbDirectories) {
            acc2csvZipped(name);
            acc2csvFiled(name);
        }
    }

    private void acc2csvZipped(String name) throws Exception {
        List<File> fileList = new AxxessToCsvConverter()
          .withTargetDirectory(getAcc2csvZipDir(name))
          .withArchiveResults(true)
          .withCompressArchive(true)
          .withManifest(true)
          .convert(getDbFile(name));
        assertEquals(1, fileList.size());
        assertEquals(getZipFile(name), fileList.get(0));
    }

    private void acc2csvFiled(String name) throws Exception {
        List<File> fileList = new AxxessToCsvConverter()
          .withTargetDirectory(getAcc2csvFilesDir(name))
          .withManifest(true)
          .convert(getDbFile(name));
        assertTrue(fileList.contains(getMetadataFile(name)));
        assertTrue(getMetadataFile(name).exists());
    }


}
