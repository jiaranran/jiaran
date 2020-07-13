package org.jeecg.modules.test;

import java.util.concurrent.CyclicBarrier;

public class ThreadTest {

    public static void main(String[] args) throws Exception {

        final int threadNum = 10;
        CyclicBarrier cb = new CyclicBarrier(threadNum + 1);//注意：10个子线程 + 1个主线程

        for (int i = 0; i < threadNum; i++) {
            new Thread(new MyRunable(cb, i)).start();
        }

        cb.await();
        System.out.println("-----------\n所有thread执行完成！");
    }

    static class MyRunable implements Runnable {
        CyclicBarrier _cb;
        int _i = 0;

        public MyRunable(CyclicBarrier cb, int i) {
            this._cb = cb;
            this._i = i;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 100));
                System.out.println("thread " + _i + " done，正在等候其它线程完成...");
                _cb.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
