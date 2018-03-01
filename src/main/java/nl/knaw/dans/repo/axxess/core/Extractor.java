package nl.knaw.dans.repo.axxess.core;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class Extractor<T extends Extractor> implements Codex.Listener {

    private ExtractorDef extractorDef = new ExtractorDef();

    private List<Throwable> errorList = new ArrayList<>();
    private List<Throwable> warningList = new ArrayList<>();

    public ExtractorDef getExtractorDef() {
        return extractorDef;
    }

    public void setExtractorDef(ExtractorDef extractorDef) {
        this.extractorDef = extractorDef;
    }

    public String getDefaultOutputDirectory() {
        return "axxess-out";
    }

    public T withTargetDirectory(String targetDirectory) throws IOException {
        return withTargetDirectory(new File(targetDirectory));
    }

    @SuppressWarnings("unchecked")
    public T withTargetDirectory(File targetDirectory) throws IOException {
        extractorDef.setTargetDirectory(targetDirectory);
        return (T) this;
    }

    public File getTargetDirectory() {
        return extractorDef.getTargetDirectory(getDefaultOutputDirectory());
    }

    @SuppressWarnings("unchecked")
    public T withFilenameComposer(FilenameComposer filenameComposer) {
        extractorDef.setFilenameComposer(filenameComposer);
        return (T) this;
    }

    public FilenameComposer getFilenameComposer() {
        return extractorDef.getFilenameComposer();
    }

    @SuppressWarnings("unchecked")
    public T withTargetCharset(Charset charset) {
        extractorDef.setTargetCharset(charset);
        return (T) this;
    }

    public Charset getTargetCharset() {
        return extractorDef.getTargetCharset();
    }

    @SuppressWarnings("unchecked")
    public T withCodex(Codex codex) {
        extractorDef.setCodex(codex);
        return (T) this;
    }

    public Codex getCodex() {
        return extractorDef.getCodex(this);
    }

    @SuppressWarnings("unchecked")
    public T withCSVFormat(CSVFormat csvFormat) {
        extractorDef.setCsvFormat(csvFormat);
        return (T) this;
    }

    public CSVFormat getCSVFormat() {
        return extractorDef.getCsvFormat();
    }

    public int getErrorCount() {
        return errorList.size();
    }

    public int getWarningCount() {
        return warningList.size();
    }

    public List<Throwable> getErrorList() {
        return errorList;
    }

    public List<Throwable> getWarningList() {
        return warningList;
    }

    @Override
    public void reportWarning(String message, Throwable cause) {
        message = message + ", @" + Thread.currentThread().getStackTrace()[2];
        warningList.add(new Throwable(message, cause));
    }

    @Override
    public void reportError(String message, Throwable cause) {
        message = message + ", @" + Thread.currentThread().getStackTrace()[2];
        errorList.add(new Throwable(message, cause));
    }

    protected void reset() {
        errorList.clear();
        warningList.clear();
    }

    protected String buildPaths(String basename) {
        String filename = FilenameUtils.concat(getTargetDirectory().getAbsolutePath(), basename);
        File directory = new File(filename).getParentFile();
        assert directory.exists() || directory.mkdirs();
        return filename;
    }

}
