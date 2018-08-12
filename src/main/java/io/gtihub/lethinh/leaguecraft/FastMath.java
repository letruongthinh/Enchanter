package io.gtihub.lethinh.leaguecraft;

/**
 * @author Raven
 */
public final class FastMath {

    private FastMath() {

    }

    private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
    private static final float radFull, radToIndex;
    private static final float degFull, degToIndex;
    private static final float[] SIN_TABLE, COS_TABLE;


    public static float sin(double angle) {
        return SIN_TABLE[(int) (angle * radToIndex) & SIN_MASK];
    }

    public static float cos(double angle) {
        return COS_TABLE[(int) (angle * radToIndex) & SIN_MASK];
    }

    static {
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;

        radFull = (float) (Math.PI * 2D);
        degFull = (float) 360D;
        radToIndex = SIN_COUNT / radFull;
        degToIndex = SIN_COUNT / degFull;

        SIN_TABLE = new float[SIN_COUNT];
        COS_TABLE = new float[SIN_COUNT];

        for (int i = 0; i < SIN_COUNT; ++i) {
            SIN_TABLE[i] = (float) Math.sin((i + 0.5F) / SIN_COUNT * radFull);
            COS_TABLE[i] = (float) Math.cos((i + 0.5F) / SIN_COUNT * radFull);
        }

        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(Math.toRadians(i));
            COS_TABLE[(int) (i * degToIndex) & SIN_MASK] = (float) Math.cos(Math.toRadians(i));
        }
    }

}
