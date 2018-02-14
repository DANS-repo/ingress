package nl.knaw.dans.repo.ingress.impl;


import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import nl.knaw.dans.repo.ingress.MetadataWriter;
import nl.knaw.dans.repo.ingress.core.KeyTypeValueMatrix;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MetadataWriterTest {

  //private String databaseFile = "../mdb_testset/Admiraal Evertsen_1815.mdb";
  private String databaseFile = "../mdb_testset/CLIWOC21_97.mdb";
  //private String databaseFile = "../mdb_testset/ingress_test.accdb";

  @Test
  public void writesDatabaseMetadata() throws Exception {
    File dbFile = new File(databaseFile);
    System.out.println(dbFile.getCanonicalPath());
    Database db = null;
    try {
      db = DatabaseBuilder.open(new File(databaseFile));
      MetadataWriter mdWriter = new MetadataWriter();

      // mdWriter.setRootDirectory("target/test-output/wide");
      // mdWriter.writeMetadata(db, KeyTypeValueMatrix.Orientation.HORIZONTAL);
      //
      // mdWriter.setRootDirectory("target/test-output/high");
      // mdWriter.writeMetadata(db, KeyTypeValueMatrix.Orientation.VERTICAL);

      mdWriter.setRootDirectory("target/test-output/one");
      mdWriter.writeMetadata(db);

    } finally {
      if (db != null) {
        db.close();
      }
    }

  }

}
