package parser.type;

record RecordExample(int min, int max) {
    RecordExample {
        if (min > max) {
            throw new IllegalArgumentException("min must be less or equal than max");
        }
    }

    int diff() {
        return this.max - this.min;
    }
}
