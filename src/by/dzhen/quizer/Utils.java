package by.dzhen.quizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Utils {
    public static <T> T getRandomElementFrom(Set<T> set) {
        if (set.size() == 0) {
            throw new IllegalArgumentException("Set size must be more than 0.");
        }

        int rand = ThreadLocalRandom.current().nextInt(set.size());

        var i = set.iterator();
        T result = null;
        for (int j = 0; j <= rand; j++) {
            result = i.next();
        }

        return result;
    }

    public static boolean trueWithProbabilityOf(double probability) {
        if (!(probability >= 0 && probability <= 1)) {
            throw new RuntimeException("probability (that was " + probability + ") must be in range [0, 1].");
        }
        return probability > ThreadLocalRandom.current().nextDouble(0, 1);
    }

    public static List<Integer> getUniquePositiveDivisors(int a) {
        a = Math.abs(a);
        List<Integer> result = new ArrayList<>();
        result.add(1);
        for (int i = 2; i <= a; i++) {
            if (a % i == 0) {
                while (a % i == 0) {
                    a /= i;
                }
                result.add(i);
            }
        }

        return result;
    }
}
