package me.syldium.apicomparer.model.type;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class TypeAdapter {

    private TypeAdapter() {}

    public static @NotNull TypeDeclaration typeDeclaration(@NotNull org.eclipse.jdt.core.dom.TypeDeclaration node) {
        return new TypeDeclaration(
                node.getName().getFullyQualifiedName(),
                Arrays.stream(node.getFields())
                        .map(field -> ((VariableDeclarationFragment) field.fragments().get(0)).getName().getIdentifier())
                        .toList(),
                Arrays.stream(node.getMethods())
                        .map(method -> new MethodSignature(
                                method.getModifiers(),
                                method.getName().getIdentifier(),
                                methodParameters(method.parameters())
                        ))
                        .toList()
        );
    }

    public static @NotNull MethodSignature methodSignature(@NotNull MethodDeclaration method) {
        return new MethodSignature(
                method.getModifiers(),
                method.getName().getIdentifier(),
                methodParameters(method.parameters())
        );
    }

    public static @NotNull MethodParameter methodParameter(@NotNull SingleVariableDeclaration declaration) {
        return new MethodParameter(javaType(declaration.getType()), declaration.getName().getIdentifier());
    }

    public static @NotNull List<MethodParameter> methodParameters(@NotNull List<SingleVariableDeclaration> declarations) {
        return declarations.stream().map(TypeAdapter::methodParameter).toList();
    }

    public static @NotNull JavaType javaType(@NotNull Type type) {
        return switch (type) {
            case ArrayType array -> new JavaType.Array(javaType(array.getElementType()), array.getDimensions());
            case NameQualifiedType nameQualified -> new JavaType.Simple(nameQualified.getName().toString());
            case PrimitiveType primitive -> primitiveType(primitive.getPrimitiveTypeCode());
            case ParameterizedType parameterized -> new JavaType.Parameterized(javaType(parameterized.getType()), parameterized.typeArguments()); // TODO type arguments
            case SimpleType simple -> new JavaType.Simple(simple.toString());
            default -> throw new IllegalArgumentException(type.getClass().getSimpleName() + " for " + type);
        };
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
        }
        return null;
    }
}
