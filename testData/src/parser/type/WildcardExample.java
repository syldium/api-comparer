package parser.type;

import java.util.function.Consumer;

final class WildcardExample {

    static <T> void apply(Iterable<? extends T> input, Consumer<? super T> consumer) {
        input.forEach(consumer);
    }
}
