package com.starcloud.ops.llm.langchain.core.utils;

import com.knuddels.jtokkit.api.ModelType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TokenCalculator {

    public static BigDecimal getTextPrice(Long tokens, ModelType modelType) {
        BigDecimal price = new BigDecimal(tokens).
                divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP)
                .multiply(getUnitPrice(modelType), MathContext.DECIMAL32);
        return price;
    }


    public static BigDecimal getUnitPrice(ModelType modelType) {
        // todo  不同模型计算基数补充
        BigDecimal unitPrice;
        switch (modelType) {
            case TEXT_EMBEDDING_ADA_002:
                unitPrice = new BigDecimal(0.0004);
                break;
            case GPT_3_5_TURBO:
            case TEXT_DAVINCI_003:
                unitPrice = new BigDecimal(0.002);
                break;
            default:
                unitPrice = new BigDecimal(0);
        }
        return unitPrice;
    }
}
