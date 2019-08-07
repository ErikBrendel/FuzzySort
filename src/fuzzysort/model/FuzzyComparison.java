package fuzzysort.model;

public class FuzzyComparison {
    public static float ACCURACY_HIGH = 0;
    public static float ACCURACY_MIDDLE = 2;
    public static float ACCURACY_LOW = 4;
    public static float ACCURACY_VAGUE = 6;

    public final FuzzySortInstance instance;
    public final String item1;
    public final String item2;
    public final float relation;
    public final float accuracy;

    public FuzzyComparison(FuzzySortInstance instance, String data) {
        this(instance, data.split(",")[0], data.split(",")[1],
                Float.parseFloat(data.split(",")[2]), Float.parseFloat(data.split(",")[3]));
    }

    public String serialize() {
        return item1 + "," + item2 + "," + relation + "," + accuracy;
    }

    public FuzzyComparison(FuzzySortInstance instance, String item1, String item2, float relation, float accuracy) {
        this.instance = instance;
        this.item1 = item1;
        this.item2 = item2;
        this.relation = relation;
        this.accuracy = accuracy;
    }
}
