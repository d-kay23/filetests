import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTests {

    @DataProvider(name = "files", parallel = true)
    public Object[] provideFiles() throws IOException {
        Path path = Paths.get(System.getProperty("source.dir"));
        return Files.walk(path).toArray();
    }

    @Test(dataProvider = "files", description = "Test that file exists in destination directory.")
    public void testFileExists(Path sourcePath) {
        System.out.println(sourcePath);
        Path destPath = getDestPathFromSource(sourcePath);
        Boolean destFileExists = Files.exists(destPath, LinkOption.NOFOLLOW_LINKS);
        assertThat(destFileExists).as("Destination path should exist [" + destPath.toString() + "].").isTrue();
    }

    @DataProvider(name = "existingFiles", parallel = true)
    public Object[] provideExistingFiles() throws IOException {
        String onlyDir = System.getProperty("only.dir");
        Path path = onlyDir == null ? Paths.get(System.getProperty("source.dir")) : Paths.get(System.getProperty("source.dir"), onlyDir);
        List<Path> resultArray = new ArrayList<>();
        for (Object current : Files.walk(path).toArray()) {
            Path currentPath = (Path) current;
            Path destPath = getDestPathFromSource(currentPath);
            if (Files.exists(destPath)) resultArray.add(currentPath);
        }
        return resultArray.toArray();
    }

    @Test(dataProvider = "existingFiles", description = "Test that file last modified dates are the same.")
    public void testFileModificationTime(Path sourcePath) throws IOException {
        Path destPath = getDestPathFromSource(sourcePath);
        FileTime fmtSource = Files.getLastModifiedTime(sourcePath, LinkOption.NOFOLLOW_LINKS);
        FileTime fmtDest = Files.getLastModifiedTime(destPath, LinkOption.NOFOLLOW_LINKS);
        assertThat(fmtSource)
                .as("Source file modification time [" + sourcePath + ":" + fmtSource.toString() + "] should be equal to dst file modification time [" + destPath + ":" + fmtDest.toString() + "].")
                .isEqualTo(fmtDest);
    }

    @Test(dataProvider = "existingFiles", description = "Test that file owners are the same.")
    public void testFileOwner(Path sourcePath) throws IOException {
        Path destPath = getDestPathFromSource(sourcePath);
        UserPrincipal ownerSource = Files.getOwner(sourcePath, LinkOption.NOFOLLOW_LINKS);
        UserPrincipal ownerDest = Files.getOwner(destPath, LinkOption.NOFOLLOW_LINKS);
        assertThat(ownerDest)
                .as("Source file owner [" + sourcePath + ":" + ownerSource.toString() + "] should be equal to dst file owner [" + destPath + ":" + ownerDest.toString() + "].")
                .isEqualTo(ownerSource);
    }

    @Test(dataProvider = "existingFiles", description = "Test that file permissions are the same.")
    public void testFilePermissions(Path sourcePath) throws IOException {
        Path destPath = getDestPathFromSource(sourcePath);
        Set<PosixFilePermission> filePermissionsSource = Files.getPosixFilePermissions(sourcePath, LinkOption.NOFOLLOW_LINKS);
        Set<PosixFilePermission> filePermissionsDest = Files.getPosixFilePermissions(destPath, LinkOption.NOFOLLOW_LINKS);
        assertThat(filePermissionsDest)
                .as("Source file permissions should be equal to dst [" + destPath + "] file permissions.")
                .isEqualTo(filePermissionsSource);
    }

    @DataProvider(name = "symbolicLinks", parallel = true)
    public Object[] provideSymbolicLinks() throws IOException {
        Path path = Paths.get(System.getProperty("source.dir"));
        List<Path> resultArray = new ArrayList<>();
        for (Object current : Files.walk(path).toArray()) {
            Path currentPath = (Path) current;
            Path destPath = getDestPathFromSource(currentPath);
            if (Files.isSymbolicLink(destPath)) resultArray.add(currentPath);
        }
        return resultArray.toArray();
    }

    @Test(dataProvider = "symbolicLinks", description = "Test that symbolic link is saved.")
    public void testSymbolicLinks(Path sourcePath) {
        Path destPath = getDestPathFromSource(sourcePath);
        Boolean isDestFileSymbolicLink = Files.isSymbolicLink(destPath);
        assertThat(isDestFileSymbolicLink)
                .as("Destination file [" + destPath + "] should be a symbolic link.")
                .isTrue();
    }

    private Path getDestPathFromSource(Path sourcePath) {
        Path destPathDir = Paths.get(System.getProperty("dest.dir"));
        Path relativeDest = Paths.get(System.getProperty("source.dir")).relativize(sourcePath);
        return Paths.get(destPathDir.toString(), relativeDest.toString());
    }
}
