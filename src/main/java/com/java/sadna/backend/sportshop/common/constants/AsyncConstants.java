package com.java.sadna.backend.sportshop.common.constants;

public final class AsyncConstants {

    public static final class Executors {
        public static final String CLEANUP = "cleanupExecutor";
        public static final String SALES_FANOUT = "salesFanoutExecutor";

        private Executors() {}
    }

    private AsyncConstants() {}
}
