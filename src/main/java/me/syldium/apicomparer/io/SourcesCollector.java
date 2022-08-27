package me.syldium.apicomparer.io;

import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.type.TypeAdapter;
import me.syldium.apicomparer.model.type.TypeDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
            final List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
            SourcesCollector.this.sources.register(new TypeDeclaration.Enum(
                    node.getModifiers(),
                    node.getName().getFullyQualifiedName(),
                    ((List<EnumConstantDeclaration>) node.enumConstants()).stream()
                            .map(field -> field.getName().getIdentifier())
                            .toList(),
                    bodyDeclarations.stream()
                            .filter((declaration) -> declaration instanceof FieldDeclaration)
                            .map((field) -> TypeAdapter.methodParameter((FieldDeclaration) field))
                            .toList(),
                    bodyDeclarations.stream()
                            .filter((declaration) -> declaration instanceof MethodDeclaration)
                            .map((method) -> TypeAdapter.methodSignature((MethodDeclaration) method))
                            .toList()
            ));
            return true;
        }

        @Override
        public boolean visit(RecordDeclaration node) {
            SourcesCollector.this.sources.register(new TypeDeclaration.ClassOrInterface(
                    node.getModifiers(),
                    node.getName().getFullyQualifiedName(),
                    Arrays.stream(node.getFields())
                            .map(TypeAdapter::methodParameter)
                            .toList(),
                    Arrays.stream(node.getMethods()).map(TypeAdapter::methodSignature).toList()
            ));
            return true;
        }

        @Override
        public boolean visit(org.eclipse.jdt.core.dom.TypeDeclaration node) {
            SourcesCollector.this.sources.register(TypeAdapter.typeDeclaration(node));
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
