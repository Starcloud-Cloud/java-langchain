package com.starcloud.ops.llm.langchain.core.model.llm.azure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class AzureAiService extends OpenAiService {


    public AzureAiService(String token) {
        super(token);
    }



    public AzureAiService(OpenAiApi api) {
        super(api);
    }

    public AzureAiService(OpenAiApi api, ExecutorService executorService) {
        super(api, executorService);
    }
}
