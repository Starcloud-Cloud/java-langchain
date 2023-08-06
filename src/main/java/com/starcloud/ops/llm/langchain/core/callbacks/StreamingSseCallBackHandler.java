package com.starcloud.ops.llm.langchain.core.callbacks;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

/**
 * @author starcloud
 */
public class StreamingSseCallBackHandler implements BaseCallbackHandler {
    private SseEmitter emitter;

    private String conversationUid;

    public StreamingSseCallBackHandler(SseEmitter emitter) {
        this.emitter = emitter;
    }

    public StreamingSseCallBackHandler(SseEmitter emitter, String conversationUid) {
        this.emitter = emitter;
        this.conversationUid = conversationUid;
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
        StreamResult streamResult = new StreamResult(200, objects[0].toString(), conversationUid);
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
        emitter.send(new StreamResult(500, message, conversationUid));
    }


    @Override
    @SneakyThrows
    public void onLLMError(String message, Throwable throwable) {
        if (emitter == null) {
            return;
        }
        if (message != null && message.contains("timeout")) {

            emitter.send(new StreamResult(500, "[Timeout] " + throwable.getMessage(), conversationUid));

        } else if (message != null && message.contains("Incorrect API key")) {
            emitter.send(new StreamResult(500, "[Incorrect Key]", conversationUid));
        } else {
            emitter.send(new StreamResult(500, "[Other] Please try again later", conversationUid));
        }
        emitter.complete();

    }

    @Override
    public <T> T onChainStart(Object... objects) {
        return null;
    }

    @Override
    public <T> T onToolStart(Object... objects) {
        return null;
    }


    public void completeWithError(Throwable throwable) {

    }

    @Data
    private class StreamResult {

        private int code;

        private String content;

        private String conversationUid;

        public StreamResult(int code, String content) {
            this.code = code;
            this.content = content;
        }

        public StreamResult(int code, String content, String conversationUid) {
            this.code = code;
            this.content = content;
            this.conversationUid = conversationUid;
        }

    }

}
