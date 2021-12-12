package utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 */
public class ThreadPoolUtils {
    private static final int CORE_COUNT = 10;

    private static final int THREAD_COUNT = 20;

    private static final int WORK_QUEUE_SIZE = 50;

    private static final long KEEP_ALIVE = 10L;

    private static final AtomicInteger THREAD_ID = new AtomicInteger(1);

    private static  final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_COUNT, THREAD_COUNT, KEEP_ALIVE,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(WORK_QUEUE_SIZE), new CommonThreadFactory());

    private ThreadPoolUtils() {
    }

    /**
     * Submit task to execute
     *
     * @param task runnable task
     */
    public static void submit(Runnable task) {
        executor.submit(task);
    }

    /**
     * ThreadFactory
     *
     */
    static class CommonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            String threadName;
            if (THREAD_ID.get() == Integer.MAX_VALUE) {
                threadName = "threadpool-common-" + THREAD_ID.getAndSet(1);
            } else {
                threadName = "threadpool-common-" + THREAD_ID.incrementAndGet();
            }
            return new Thread(runnable, threadName);
        }
    }
}
