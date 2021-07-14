package com.situjunjie.gulimall.gulimallsearch;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * 创建多线程的四种方式：
 * 1.继承Thread
 * 2.实现Runable接口
 * 3.Juc
 * 4.线程池
 */
public class JUCTest {

     public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 继承Thread的方式开启一个线程
     */
    @Test
    public void testNewThreadByExtendThread(){
        System.out.println("开始多线程-通过继承Thread");

        Thread01 thread01 = new Thread01();
        thread01.start();
        System.out.println("结束多线程-通过继承Thread");
    }

    /**
     *  实现Runmable接口的方式开启一个线程
     */
    @Test
    public void testNewThreadByImplementsRunable(){
        System.out.println("开始多线程-通过实现Runable接口");
        Thread thread = new Thread(new Thread02());
        thread.start();
        System.out.println("结束多线程-通过实现Runable接口");
    }

    /**
     * Jdk1.5之后提供的Juc 来开启一个线程
     */
    @Test
    public void testNewThreadByJUC() throws ExecutionException, InterruptedException {
        System.out.println("开始多线程-通过JUC");
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        });
        new Thread(futureTask).start();
        Integer integer = futureTask.get();
        System.out.println("结束多线程-通过JUC,输出结果是 "+integer);
        
    }

    /**
     * 通过线程池开一个线程
     */
    @Test
    public void testNewThreadByPool() throws ExecutionException, InterruptedException {
        System.out.println("开始多线程-通过线程池");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<Integer> future = executorService.submit(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        });
        System.out.println("结束多线程-通过线程池 ,输出结果是 "+future.get());
    }

    /**
     * 试用CompletableFuture
     */
    @Test
    public void testCompletableFuture(){
        System.out.println("开始多线程-测试CompletableFuture异步任务");
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(new Thread02(), executorService);
        System.out.println("结束多线程-测试CompletableFuture异步任务");
    }

    /**
     * 试用CompletableFuture02
     */
    @Test
    public void testCompletableFuture02(){
        System.out.println("开始多线程-测试CompletableFuture异步任务");
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            //int i = 10 / 5;
            int i = 10 / 0;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        }, executorService).whenCompleteAsync((r,t)->{
            System.out.println("线程:" + Thread.currentThread().getName() + " 计算得到结果是 "+r+"  异常信息："+t);
        }).exceptionally(t->{
            System.out.println("线程:" + Thread.currentThread().getName() +"  补抓到异常信息："+t);
            return 10;
        });
        System.out.println("结束多线程-测试CompletableFuture异步任务");
    }

    /**
     * 试用CompletableFuture 结合handle方法做异步处理
     */
    @Test
    public void testCompletableFutureHandle() throws ExecutionException, InterruptedException {
        System.out.println("开始多线程-测试CompletableFuture异步任务");
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            //int i = 10 / 0;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        }, executorService).handle((r,t)->{
            if(r!=null){
                return r*2;
            }else{
                System.out.println("捕抓到异常="+t);
                return 0;
            }
        });

        System.out.println("结束多线程-测试CompletableFuture异步任务 最终结果="+integerCompletableFuture.get());
    }

    /**
     * 试用CompletableFuture 结合then方法做异步处理
     * thenRun  不接受结果不返回
     * thenAccept 能接受结果 无返回
     * thenApply 能接受结果 并返回
     */
    @Test
    public void testCompletableFutureThen() throws ExecutionException, InterruptedException {
        System.out.println("开始多线程-测试CompletableFuture异步任务");
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            //int i = 10 / 0;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        }, executorService).thenApplyAsync(res -> {
            return "Hello " + res;
        });

        System.out.println("结束多线程-测试CompletableFuture异步任务 最终结果="+stringCompletableFuture.get());
    }

    /**
     * 试用CompletableFuture 结合both
     * thenRun  不接受结果不返回
     * thenAccept 能接受结果 无返回
     * thenApply 能接受结果 并返回
     */
    @Test
    public void testCompletableFutureCombine() throws ExecutionException, InterruptedException {
        System.out.println("开始多线程-测试CompletableFuture异步任务");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            //int i = 10 / 0;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return i;
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 开始了");
            int i = 10 / 5;
            //int i = 10 / 0;
            System.out.println("线程:" + Thread.currentThread().getName() + " 执行完成，结果=" + i);
            return "hello";
        });
        CompletableFuture<String> stringCompletableFuture = future1.thenCombineAsync(future2, (r1, r2) -> {
            System.out.println("线程:" + Thread.currentThread().getName() + " 处理结果结果=" + r1 + "-->" + r2);
            return r1 + " " + r2 + " " + "haha";
        }, executorService);
        System.out.println("结束多线程-测试CompletableFuture异步任务 最终结果="+stringCompletableFuture.get());
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("线程:"+Thread.currentThread().getName()+" 开始了");
            int i = 10/5;
            System.out.println("线程:"+Thread.currentThread().getName()+" 执行完成，结果="+i);
        }
    }

    public static class Thread02 implements Runnable{

        @Override
        public void run() {
            System.out.println("线程:"+Thread.currentThread().getName()+" 开始了");
            int i = 10/5;
            System.out.println("线程:"+Thread.currentThread().getName()+" 执行完成，结果="+i);
        }
    }


}
