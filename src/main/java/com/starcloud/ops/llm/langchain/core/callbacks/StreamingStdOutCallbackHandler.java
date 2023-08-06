package com.starcloud.ops.llm.langchain.core.callbacks;

import cn.hutool.core.lang.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class StreamingStdOutCallbackHandler implements BaseCallbackHandler {

    private HttpServletResponse httpServletResponse;

    private ServletOutputStream outputStream;

    public StreamingStdOutCallbackHandler(HttpServletResponse httpServletResponse) {
        Assert.notNull(httpServletResponse, "StreamingStdOutCallbackHandler is fail, httpServletResponse is must");
        this.httpServletResponse = httpServletResponse;
    }

    protected ServletOutputStream initOutputStream() {

        try {
            if (this.outputStream == null) {
                this.outputStream = this.httpServletResponse.getOutputStream();
            }
        } catch (Exception e) {
            log.error("StreamingStdOutCallbackHandler initOutputStream is fail: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return this.outputStream;
    }

    @Override
    public Void onLLMStart(Object... objects) {

        try {

            this.httpServletResponse.setContentType("text/plain;charset=utf-8");
            this.httpServletResponse.setCharacterEncoding("UTF-8");

            this.initOutputStream().write("&start&".getBytes(StandardCharsets.UTF_8));
            this.initOutputStream().flush();

        } catch (Exception e) {

        }

        return null;
    }

    @Override
    public void onLLMNewToken(Object... objects) {

        try {

            this.initOutputStream().write(String.valueOf(objects[0]).getBytes(StandardCharsets.UTF_8));
            this.initOutputStream().flush();

        } catch (Exception e) {

        }

    }

    @Override
    public void onLLMEnd(Object... objects) {


        try {

            if ("stop".equals(String.valueOf(objects[0]))) {
                this.initOutputStream().write("&end&".getBytes(StandardCharsets.UTF_8));
            }

            this.initOutputStream().flush();

        } catch (Exception e) {

        }

    }

    @Override
    public void onLLMError(String message, Throwable throwable) {


        if (message != null && message.contains("timeout")) {

            message = "[Timeout] " + throwable.getMessage();

        } else if (message != null && message.contains("Incorrect API key")) {

            message = "[Incorrect Key]";

        } else {

            message = "[Other] Please try again later";
        }


        String error = "&error&" + message;

        this.onLLMNewToken(error);

    }

    @Override
    public <T> T onChainStart(Object... objects) {
        return null;
    }

    @Override
    public <T> T onToolStart(Object... objects) {
        return null;
    }
}
