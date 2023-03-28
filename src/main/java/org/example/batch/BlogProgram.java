package org.example.batch;

import org.example.openai.ChatApi;
import org.example.openai.ImageApi;
import org.example.tistory.BlogWriter;
import org.example.utils.HtmlUtil;
import org.example.utils.Topics;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class BlogProgram {

    private final ChatApi chatApi;
    private final ImageApi imageApi;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    private int index = 0;

    public BlogProgram(ChatApi chatApi, ImageApi imageApi, int numThreads) {
        this.chatApi = chatApi;
        this.imageApi = imageApi;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
        executorService.shutdown();
    }

    public void start() {
        Runnable task = this::execute;
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 86400 / 15, TimeUnit.SECONDS);
    }

    public void execute() {
        System.out.println("upload initiated");
        Future<String[]> contentFuture = executorService.submit(() -> chatApi.getContent(1000));

        String[] result;
        try {
            result = contentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }

        try {
            BlogWriter.upload(result[0], result[1]);
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("writeBlog ended");
        }
    }
}