//package com.dreamgyf.thread;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Toast;
//
//import com.dreamgyf.activity.MainActivity;
//import com.dreamgyf.service.CallAPI;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class HttpThread {
//
//    private static SingleHttpThread singleHttpThread = new SingleHttpThread();
//
//    public static SingleHttpThread get() {
//        return singleHttpThread;
//    }
//
//    public static class SingleHttpThread {
//
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//
//        private class searchThread implements Runnable{
//            private String keywords;
//
//            private Handler handler;
//
//            public searchThread(Handler handler,String keywords){
//                this.keywords = keywords;
//                this.handler = handler;
//            }
//
//            @Override
//            public void run() {
//                try {
//                    String s = CallAPI.get().search(keywords);
//                    handler.post(new searchUpdateUI(s));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        private class searchUpdateUI implements Runnable{
//            private Object data;
//
//            public searchUpdateUI(Object data){
//                this.data = data;
//            }
//
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, data.toString(),  Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//}