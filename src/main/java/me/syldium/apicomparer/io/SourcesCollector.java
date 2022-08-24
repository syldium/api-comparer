package me.syldium.apicomparer.io;

import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.type.TypeDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SourcesCollector {

    private final SourcesContent sources = new SourcesContent();
    private final Visitor visitor = new Visitor();

    public SourcesCollector(@NotNull String[] sources) {
        final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        final Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_17, options);
        parser.setCompilerOptions(options);
        final String[] encodings = new String[sources.length];
        Arrays.fill(encodings, StandardCharsets.UTF_8.name());
        parser.createASTs(sources, encodings, new String[0], new Requestor(), null);
    }

    public SourcesContent getSources() {
        return sources;
    }

    private class Visitor extends ASTVisitor {

        private Visitor() {
            super(false);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean visit(EnumDeclaration node) {
            SourcesCollector.this.sources.register(new TypeDeclaration(
                    node.getName().getFullyQualifiedName(),
                    ((List<EnumConstantDeclaration>) node.enumConstants()).stream().map(field -> field.getName().getIdentifier()).toList(),
                    Collections.emptyList()
            ));
            return true;
        }

        @Override
        public boolean visit(RecordDeclaration node) {
            SourcesCollector.this.sources.register(new TypeDeclaration(
                    node.getName().getFullyQualifiedName(),
                    Arrays.stream(node.getFields()).map(field -> ((VariableDeclarationFragment) field.fragments().get(0)).getName().getIdentifier()).toList(),
                    Arrays.stream(node.getMethods()).map(method -> method.getName().getIdentifier()).toList()
            ));
            return true;
        }

        @Override
        public boolean visit(org.eclipse.jdt.core.dom.TypeDeclaration node) {
            SourcesCollector.this.sources.register(new TypeDeclaration(
                    node.getName().getFullyQualifiedName(),
                    Arrays.stream(node.getFields()).map(field -> ((VariableDeclarationFragment) field.fragments().get(0)).getName().getIdentifier()).toList(),
                    Arrays.stream(node.getMethods()).map(method -> method.getName().getIdentifier()).toList()
            ));
            return true;
        }
    }

    private class Requestor extends FileASTRequestor {

        @Override
        public void acceptAST(String sourceFilePath, CompilationUnit ast) {
            ast.accept(SourcesCollector.this.visitor);
        }
    }
}
