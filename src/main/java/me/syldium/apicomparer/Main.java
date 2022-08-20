package me.syldium.apicomparer;

import me.syldium.apicomparer.io.SourcesVisitor;
import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.VersionDiff;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: apidiff <java source A> <java source B>");
            System.exit(1);
        }
        try {
            final SourcesContent from = SourcesVisitor.exploreZip(Paths.get(args[0]));
            final SourcesContent to = SourcesVisitor.exploreZip(Paths.get(args[1]));
            final VersionDiff diff = from.diff(to);
            System.out.println("Added files: " + diff.addedFiles());
            System.out.println("Removed files: " + diff.removedFiles());
        } catch (FileSystemException ex) {
            System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }
}
