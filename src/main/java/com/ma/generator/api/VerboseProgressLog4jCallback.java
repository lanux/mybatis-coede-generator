package com.ma.generator.api;

import org.mybatis.generator.api.ProgressCallback;

import java.util.logging.Logger;

/**
 * Created by lanux on 2016/4/22.
 */
public class VerboseProgressLog4jCallback implements ProgressCallback {
    private static final Logger LOGGER = Logger.getLogger("generator");

    @Override
    public void introspectionStarted(int totalTasks) {
        LOGGER.info("total task:" + totalTasks);
    }

    @Override
    public void generationStarted(int totalTasks) {
        LOGGER.info("total task:" + totalTasks);
    }

    @Override
    public void saveStarted(int totalTasks) {
        LOGGER.info("total task:" + totalTasks);
    }

    @Override
    public void startTask(String taskName) {
        LOGGER.info(taskName);
    }

    @Override
    public void done() {
        LOGGER.info("finish");
    }

    @Override
    public void checkCancel() throws InterruptedException {
        LOGGER.info("cancel");
    }
}
