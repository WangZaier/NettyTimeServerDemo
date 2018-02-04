package com.wangzai.PIO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerExecutePool {
    //定义线程池
    private ExecutorService executorService;

    //参数 程池中核心线程的数量 , 线程池中最大线程数量 , 非核心线程的超时时长 , 第三个参数的单位 , 线程池中的任务队列
    public TimeServerHandlerExecutePool(int MAX_POOL_SIZE , int QUEUE_SIZE) {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() ,
                MAX_POOL_SIZE , 120L , TimeUnit.SECONDS,
                new ArrayBlockingQueue(QUEUE_SIZE));
    }

    //添加线程
    public void pushExecutor(Runnable task){
        executorService.execute(task);
    }
}
