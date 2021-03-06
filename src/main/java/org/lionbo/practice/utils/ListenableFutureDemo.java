package org.lionbo.practice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ListenableFutureDemo {
    static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    static ExecutorService eservice = Executors.newFixedThreadPool(10);

    @SuppressWarnings("unchecked")
    public static void parallel() {
        final long start = System.currentTimeMillis();
        ListenableFuture<Object> taskFuture1 = service.submit(new Task1());
        ListenableFuture<Object> taskFuture2 = service.submit(new Task2());
        ListenableFuture<Object> taskFuture3 = service.submit(new Task3());

        ListenableFuture<List<Object>> successfulQueries = Futures.successfulAsList(taskFuture1, taskFuture2,
                taskFuture3);
        List<Object> res = new ArrayList<Object>();

        Futures.addCallback(successfulQueries, new FutureCallbackData(res));

        System.out.println(res);

    }

    public static void synchronous() {
        final long start = System.currentTimeMillis();
        Future<Object> taskFuture1 = eservice.submit(new Task1());
        Future<Object> taskFuture2 = eservice.submit(new Task2());
        Future<Object> taskFuture3 = eservice.submit(new Task3());

        try {
            Object result1 = taskFuture1.get();
            Object result2 = taskFuture2.get();
            Object result3 = taskFuture3.get();
            System.out.println(result1 + " " + result2 + " " + result3);
            System.out.println("synchronous:" + (System.currentTimeMillis() - start));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // synchronous();
        parallel();

        shutdown();
    }

    private static void shutdown() {
        service.shutdown();
        eservice.shutdown();
    }

    public static class Task1 implements Callable<Object> {

        public Object call() throws Exception {
            Thread.sleep(5000);
            return "Task1";
        }
    }

    public static class Task2 implements Callable<Object> {

        public Object call() throws Exception {
            Thread.sleep(3000);
            return "Task2";
        }
    }

    public static class Task3 implements Callable<Object> {

        public Object call() throws Exception {
            Thread.sleep(4000);
            return 3;
        }
    }

}