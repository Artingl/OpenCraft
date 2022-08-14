package OpenCraft.utils;

import java.util.concurrent.ThreadLocalRandom;

public class Random {

    public static int inRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
