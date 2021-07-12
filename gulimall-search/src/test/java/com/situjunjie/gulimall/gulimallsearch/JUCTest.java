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
