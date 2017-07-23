package com.individual.thinking.traitorstown.model;

public enum EffectType {
    ADD {
        @Override
        public int apply(int x, int y) {
            return x + y;
        }
    },
    REMOVE {
        @Override
        public int apply(int x, int y) {
            return x - y;
        }
    };

    public abstract int apply(int x, int y);
}
