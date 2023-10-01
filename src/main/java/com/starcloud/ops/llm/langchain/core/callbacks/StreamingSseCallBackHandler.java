package com.starcloud.ops.llm.langchain.core.callbacks;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author starcloud
 */
@Slf4j
@Data
public class StreamingSseCallBackHandler implements BaseCallbackHandler {

    private SseEmitter emitter;

    public StreamingSseCallBackHandler(SseEmitter emitter) {
        this.emitter = emitter;
    }


    @Override
    public Void onLLMStart(Object... objects) {

        return null;

    }

    @Override
    @SneakyThrows
    public void onLLMNewToken(Object... objects) {
        if (emitter == null) {
            return;
        }
        StreamResult streamResult = new StreamResult(200, objects[0].toString());
        emitter.send(streamResult);
    }

    @SneakyThrows
    @Override
    public void onLLMEnd(Object... objects) {

    }


    @Override
    @SneakyThrows
    public void onLLMError(String message) {
        if (emitter == null) {
            return;
        }
        emitter.send(new StreamResult(500, message));
    }


    @Override
    @SneakyThrows
    public void onLLMError(String message, Throwable throwable) {
        if (emitter == null) {
            return;
        }
        emitter.send(new StreamResult(500, parseOpenApiError(message, throwable)));
    }

    protected String parseOpenApiError(String message, Throwable throwable) {

        String error = "";
        if (message != null && message.contains("timeout")) {
            error = "[Timeout] " + throwable.getMessage();
        } else if (message != null && message.contains("Incorrect API key")) {
            error = "[Incorrect Key]";
        } else {
            error = "[Other] Please try again later";
        }

        return error;
    }

    @Override
    public <T> T onChainStart(Object... objects) {
        return null;
    }

    @Override
    public <T> T onToolStart(Object... objects) {
        return null;
    }


    @Data
    public static class StreamResult {

        private int code;

        private String type;

        private String content;

        private String error;

        public StreamResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }
}
