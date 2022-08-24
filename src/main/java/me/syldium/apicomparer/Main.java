package me.syldium.apicomparer;

import me.syldium.apicomparer.io.SourcesProcessor;
import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.VersionDiff;
import me.syldium.apicomparer.model.type.TypeDeclaration;

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
            final SourcesContent from = SourcesProcessor.process(Paths.get(args[0]));
            final SourcesContent to = SourcesProcessor.process(Paths.get(args[1]));
            final VersionDiff diff = from.diff(to);
            System.out.println("Added types: " + diff.addedTypes().stream().map(TypeDeclaration::name).toList());
            System.out.println("Changed types: " + diff.changedTypes().stream().map(TypeDeclaration::name).toList());
            System.out.println("Removed types: " + diff.removedTypes().stream().map(TypeDeclaration::name).toList());
        } catch (FileSystemException ex) {
            System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }
}
