package me.syldium.apicomparer.model.type;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class TypeAdapter {

    private TypeAdapter() {}

    public static @NotNull TypeDeclaration.ClassOrInterface classOrInterfaceType(@NotNull org.eclipse.jdt.core.dom.TypeDeclaration declaration) {
        return new TypeDeclaration.ClassOrInterface(
                declaration.getModifiers(),
                declaration.getName().getFullyQualifiedName(),
                typeParameters(declaration.typeParameters()),
                declaration.getSuperclassType() == null ? null : javaType(declaration.getSuperclassType()),
                javaTypes(declaration.superInterfaceTypes()),
                fields(declaration.getFields()),
                methods(declaration.getMethods())
        );
    }

    public static @NotNull TypeDeclaration.Enum enumType(@NotNull EnumDeclaration node) {
        final List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
        return new TypeDeclaration.Enum(
                node.getModifiers(),
                node.getName().getFullyQualifiedName(),
                ((List<EnumConstantDeclaration>) node.enumConstants()).stream()
                        .map(field -> field.getName().getIdentifier())
                        .toList(),
                bodyDeclarations.stream()
                        .filter((declaration) -> declaration instanceof org.eclipse.jdt.core.dom.FieldDeclaration)
                        .map((field) -> field((org.eclipse.jdt.core.dom.FieldDeclaration) field))
                        .toList(),
                bodyDeclarations.stream()
                        .filter((declaration) -> declaration instanceof MethodDeclaration)
                        .map((method) -> method((MethodDeclaration) method))
                        .toList()
        );
    }

    public static @NotNull TypeDeclaration.Record recordType(@NotNull RecordDeclaration declaration) {
        return new TypeDeclaration.Record(
                declaration.getModifiers(),
                declaration.getName().getFullyQualifiedName(),
                typeParameters(declaration.typeParameters()),
                variables(declaration.recordComponents()),
                javaTypes(declaration.superInterfaceTypes()),
                fields(declaration.getFields()),
                methods(declaration.getMethods())
        );
    }

    public static @NotNull FieldDeclaration field(@NotNull org.eclipse.jdt.core.dom.FieldDeclaration field) {
        return new FieldDeclaration(
                field.getModifiers(),
                new VariableDeclaration(
                        javaType(field.getType()),
                        ((VariableDeclarationFragment) field.fragments().get(0)).getName().getIdentifier()
                )
        );
    }

    public static @NotNull List<FieldDeclaration> fields(@NotNull org.eclipse.jdt.core.dom.FieldDeclaration[] fields) {
        return Arrays.stream(fields).map(TypeAdapter::field).toList();
    }

    public static @NotNull MethodSignature method(@NotNull MethodDeclaration method) {
        final Type returnType = method.getReturnType2();
        return new MethodSignature(
                method.getModifiers(),
                typeParameters(method.typeParameters()),
                returnType == null ? null : javaType(returnType),
                method.getName().getIdentifier(),
                variables(method.parameters()),
                javaTypes(method.thrownExceptionTypes())
        );
    }

    public static @NotNull List<MethodSignature> methods(@NotNull MethodDeclaration[] methods) {
        return Arrays.stream(methods).map(TypeAdapter::method).toList();
    }

    public static @NotNull VariableDeclaration variable(@NotNull SingleVariableDeclaration declaration) {
        return new VariableDeclaration(javaType(declaration.getType()), declaration.getName().getIdentifier());
    }

    public static @NotNull List<VariableDeclaration> variables(@NotNull List<SingleVariableDeclaration> declarations) {
        return declarations.stream().map(TypeAdapter::variable).toList();
    }

    public static @NotNull JavaType javaType(@NotNull Type type) {
        return switch (type) {
            case ArrayType array ->
                    new JavaType.Array(javaType(array.getElementType()), array.getDimensions());
            case NameQualifiedType nameQualified ->
                    new JavaType.Simple(nameQualified.getName().getFullyQualifiedName());
            case PrimitiveType primitive ->
                    primitiveType(primitive.getPrimitiveTypeCode());
            case ParameterizedType parameterized ->
                    new JavaType.Parameterized(
                            javaType(parameterized.getType()),
                            javaTypes(parameterized.typeArguments())
                    );
            case SimpleType simple ->
                    new JavaType.Simple(simple.getName().getFullyQualifiedName());
            case WildcardType wildcard ->
                    new JavaType.Wildcard(
                            wildcard.getBound() == null
                                    ? null
                                    : new JavaType.Wildcard.Bound(
                                            wildcard.isUpperBound()
                                                    ? JavaType.Wildcard.Constraint.EXTENDS
                                                    : JavaType.Wildcard.Constraint.SUPER,
                                    javaType(wildcard.getBound())
                            )
                    );
            default ->
                    throw new IllegalArgumentException(type.getClass().getSimpleName() + " for " + type);
        };
    }

    public static @NotNull List<JavaType> javaTypes(@NotNull List<Type> types) {
        return types.stream().map(TypeAdapter::javaType).toList();
    }

    public static @NotNull JavaType.Primitive primitiveType(@NotNull PrimitiveType.Code primitive) {
        if (primitive == PrimitiveType.BYTE) {
            return JavaType.Primitive.BYTE;
        } else if (primitive == PrimitiveType.SHORT) {
            return JavaType.Primitive.SHORT;
        } else if (primitive == PrimitiveType.INT) {
            return JavaType.Primitive.INT;
        } else if (primitive == PrimitiveType.LONG) {
            return JavaType.Primitive.LONG;
        } else if (primitive == PrimitiveType.FLOAT) {
            return JavaType.Primitive.FLOAT;
        } else if (primitive == PrimitiveType.DOUBLE) {
            return JavaType.Primitive.DOUBLE;
        } else if (primitive == PrimitiveType.BOOLEAN) {
            return JavaType.Primitive.BOOLEAN;
        } else if (primitive == PrimitiveType.CHAR) {
            return JavaType.Primitive.CHAR;
        } else if (primitive == PrimitiveType.VOID) {
            return JavaType.Primitive.VOID;
        }
        throw new IllegalArgumentException(primitive.toString());
    }

    public static @NotNull TypeParameter typeParameter(@NotNull org.eclipse.jdt.core.dom.TypeParameter type) {
        return new TypeParameter(type.getName().getIdentifier(), javaTypes(type.typeBounds()));
    }

    public static @NotNull List<TypeParameter> typeParameters(@NotNull List<org.eclipse.jdt.core.dom.TypeParameter> types) {
        return types.stream().map(TypeAdapter::typeParameter).toList();
    }
}
