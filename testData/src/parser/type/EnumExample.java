package parser.type;

enum EnumExample {
    PRESENT,
    ABSENT;

    public static final int VALUES_LEN = 2;

    static EnumExample from(boolean value) {
        return value ? PRESENT : ABSENT;
    }

    @Override
    public String toString() {
        return switch (this) {
            case PRESENT -> "present";
            case ABSENT -> "absent";
        };
    }
}
