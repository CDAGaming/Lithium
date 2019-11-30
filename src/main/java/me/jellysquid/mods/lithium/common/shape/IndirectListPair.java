package me.jellysquid.mods.lithium.common.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import me.jellysquid.mods.lithium.common.voxels.DoubleListPair;

public final class IndirectListPair extends DoubleListPair {
    private final double[] merged;
    private final int[] indicesFirst;
    private final int[] indicesSecond;

    private final DoubleArrayList list;

    private int count = 0;

    public IndirectListPair(int size) {
        this.merged = new double[size];
        this.indicesFirst = new int[size];
        this.indicesSecond = new int[size];

        this.list = DoubleArrayList.wrap(this.merged);
    }

    public void clear() {
        this.count = 0;
    }

    public double[] getContents(DoubleList list) {
        double[] araw;

        if (list instanceof DoubleArrayList) {
            araw = ((DoubleArrayList) list).elements();
        } else if (list instanceof PrecomputedFractionalDoubleList) {
            araw = ((PrecomputedFractionalDoubleList) list).items;
        } else {
            araw = new double[list.size()];

            for (int i = 0; i < list.size(); i++) {
                araw[i] = list.getDouble(i);
            }
        }

        return araw;
    }

    public void initBest(DoubleList a, DoubleList b, boolean flag1, boolean flag2) {
        if (a instanceof DoubleArrayList && b instanceof DoubleArrayList) {
            this.init(((DoubleArrayList) a).elements(), ((DoubleArrayList) b).elements(), a.size(), b.size(), flag1, flag2);
        } else {
            double[] araw = getContents(a);
            double[] braw = getContents(b);

            this.init(araw, braw, a.size(), b.size(), flag1, flag2);
        }
    }

    private void init(double[] a, double[] b, int aSize, int bSize, boolean flag1, boolean flag2) {
        int aIdx = 0;
        int bIdx = 0;

        double prev = 0.0D;

        int i = 0, j = 0;

        while (true) {
            boolean aWithinBounds;
            boolean bWithinBounds;
            boolean flip;
            double value;

            do {
                do {
                    aWithinBounds = aIdx < aSize;
                    bWithinBounds = bIdx < bSize;

                    if (!aWithinBounds && !bWithinBounds) {
                        if (j == 0) {
                            this.merged[j++] = Math.min(a[aSize - 1], b[bSize - 1]);
                        }

                        this.count = j;
                        this.list.size(j);

                        return;
                    }

                    flip = aWithinBounds && (!bWithinBounds || a[aIdx] < b[bIdx] + 1.0E-7D);
                    value = flip ? a[aIdx++] : b[bIdx++];
                } while ((aIdx == 0 || !aWithinBounds) && !flip && !flag2);
            } while ((bIdx == 0 || !bWithinBounds) && flip && !flag1);

            if (j == 0 || prev < value - 1.0E-7D) {
                this.indicesFirst[i] = aIdx - 1;
                this.indicesSecond[i++] = bIdx - 1;
                this.merged[j++] = value;
                prev = value;
            } else if (j > 0) {
                this.indicesFirst[i - 1] = aIdx - 1;
                this.indicesSecond[i - 1] = bIdx - 1;
            }
        }
    }

    @Override
    public boolean forAllOverlappingSections(SectionPairPredicate predicate) {
        for (int i = 0; i < this.count - 1; ++i) {
            if (!predicate.merge(this.indicesFirst[i], this.indicesSecond[i], i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public DoubleList getMergedList() {
        return this.list;
    }

    public int getSize() {
        return this.indicesFirst.length;
    }
}
