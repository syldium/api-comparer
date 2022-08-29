package parser.type;

record RecordExample(int min, int max) {

    static final RecordExample ZERO = new RecordExample(0, 0);

    RecordExample {
        if (min > max) {
            throw new IllegalArgumentException("min must be less or equal than max");
        }
    }

    int diff() {
        return this.max - this.min;
    }
}
