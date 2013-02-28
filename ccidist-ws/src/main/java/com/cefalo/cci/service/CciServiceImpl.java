package com.cefalo.cci.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class CciServiceImpl implements CciService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> getAllFileNamesInDirectory(final String directory) {
        String dir = Strings.nullToEmpty(directory);
        Preconditions.checkArgument(dir.trim().length() > 0, "Directory path may not be empty or null.");

        final List<String> epubFileNames = new ArrayList<String>();
        try {
            final Path directoryPath = Paths.get(directory);
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    epubFileNames.add(file.getFileName().toString());
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(directoryPath)) {
                        return super.preVisitDirectory(dir, attrs);
                    }

                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            logger.error("Error while trying to get the list of files.", e);
        }

        return epubFileNames;
    }

    public String getMediaType(String fileName) {
        try {
            return Files.probeContentType(Paths.get(fileName));
        } catch (IOException e) {
            return null;
        }
    }
}
