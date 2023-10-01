package com.starcloud.ops.llm.langchain.core.chain;

import cn.hutool.core.map.MapUtil;
import com.starcloud.ops.llm.langchain.core.chain.base.Chain;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class SequentialChain extends Chain<HashMap> {

    private List<Chain> chains;

    /**
     * 返回全部链执行结果
     */
    private Boolean returnAll = false;

    public SequentialChain(List<Chain> chains) {
        this.chains = chains;
    }

    public SequentialChain(List<Chain> chains, List<String> inputKeys, List<String> outputKeys) {
        this.chains = chains;
        this.setInputKeys(inputKeys);
        this.setOutputKeys(outputKeys);
    }

    @Override
    protected HashMap _call(List<BaseVariable> baseVariables) {

        HashMap result = new HashMap();
        List<BaseVariable> allVariables = BaseVariable.copy(baseVariables);

        for (Chain chain : chains) {

            String output = chain.run(allVariables);

            result.put(chain.getReturnOutputKey(), output);

            allVariables.add(BaseVariable.newString(chain.getReturnOutputKey(), output));
        }

        if (this.getVerbose()) {
            log.info("SequentialChain result: {}", result);
        }

        if (this.getReturnAll()) {
            return result;
        } else {

            return (HashMap) MapUtil.filter(result, this.getOutputKeys().toArray());
        }

    }

    @Override
    public String run(List<BaseVariable> baseVariables) {
        return (String) this.call(baseVariables).getOrDefault(this.getReturnOutputKey(), "");
    }

    @Override
    public String run(String text) {
        return this.run(Arrays.asList(BaseVariable.newString("input", text)));
    }
}
