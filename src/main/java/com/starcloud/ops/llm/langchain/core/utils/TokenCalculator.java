package com.starcloud.ops.llm.langchain.core.utils;

import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TokenCalculator {

    public static ModelTypeEnum fromName(String model) {
        return ModelTypeEnum.fromName(model).orElse(ModelTypeEnum.GPT_3_5_TURBO);
    }

    public static BigDecimal getTextPrice(Long tokens, ModelTypeEnum modelType) {
        BigDecimal price = new BigDecimal(tokens).
                divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP)
                .multiply(getUnitPrice(modelType, true), MathContext.DECIMAL32);
        return price;
    }

    public static BigDecimal getTextPrice(Long tokens, ModelTypeEnum modelType, Boolean isInput) {
        BigDecimal price = new BigDecimal(tokens).
                divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP)
                .multiply(getUnitPrice(modelType, isInput), MathContext.DECIMAL32);
        return price;
    }


    public static BigDecimal getUnitPrice(ModelTypeEnum modelType, Boolean isInput) {
        // todo  不同模型计算基数补充
        BigDecimal unitPrice;
        switch (modelType) {
            case TEXT_EMBEDDING_ADA_002:
                unitPrice = new BigDecimal(0.0001);
                break;
            case TEXT_EMBEDDING_3_SMALL:
                unitPrice = new BigDecimal(0.00002);
                break;
            case TEXT_EMBEDDING_3_LARGE:
                unitPrice = new BigDecimal(0.00013);
                break;
            case GPT_3_5_TURBO:
//            case TEXT_DAVINCI_003:
                unitPrice = isInput ? new BigDecimal(0.0005) : new BigDecimal(0.0015);
                break;
            case GPT_3_5_TURBO_16K:
                unitPrice = isInput ? new BigDecimal(0.0005) : new BigDecimal(0.0015);
                break;
            case GPT_4:
                unitPrice = isInput ? new BigDecimal(0.03) : new BigDecimal(0.06);
                break;
            case GPT_4_O:
                unitPrice = isInput ? new BigDecimal(0.005) : new BigDecimal(0.015);
                break;
            case GPT_4_TURBO:
                unitPrice = isInput ? new BigDecimal(0.01) : new BigDecimal(0.03);
                break;
            case GPT_4_32K:
                unitPrice = isInput ? new BigDecimal(0.06) : new BigDecimal(0.12);
                break;
            case QWEN:
                //0.008元/1000tokens  0。008/7.2
                unitPrice = isInput ? new BigDecimal(0.0003) : new BigDecimal(0.0008);
                break;
            case QWEN_MAX:
                //0.12元/1000tokens  0.12/7.2
                unitPrice = isInput ? new BigDecimal(0.006) : new BigDecimal(0.016);
                break;
            default:
                unitPrice = new BigDecimal(0);
        }
        return unitPrice;
    }


}
