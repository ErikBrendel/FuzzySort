package fuzzysort.model;

public class FuzzyComparison {
    private static final String COMMA = ",";

    public final FuzzySortInstance instance;
    public final String item1;
    public final String item2;
    public final float relation;
    public final float accuracy;

    public FuzzyComparison(FuzzySortInstance instance, String data) {
        this(instance, data.split(COMMA)[0], data.split(COMMA)[1],
                Float.parseFloat(data.split(COMMA)[2]), Float.parseFloat(data.split(COMMA)[3]));
    }

    public String serialize() {
        return item1 + COMMA + item2 + COMMA + relation + COMMA + accuracy;
    }

    public FuzzyComparison(FuzzySortInstance instance, String item1, String item2, float relation, float accuracy) {
        this.instance = instance;
        this.item1 = item1;
        this.item2 = item2;
        this.relation = relation;
        this.accuracy = accuracy;
    }
}
