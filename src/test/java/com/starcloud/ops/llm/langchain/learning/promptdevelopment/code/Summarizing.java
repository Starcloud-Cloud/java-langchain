package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Summarizing extends BaseTests {

    //评论示例
    private String review = "这个熊猫公仔是我给女儿的生日礼物，她很喜欢，去哪都带着。\n" +
            "公仔很软，超级可爱，面部表情也很和善。但是相比于价钱来说，\n" +
            "它有点小，我感觉在别的地方用同样的价钱能买到更大的。\n" +
            "快递比预期提前了一天到货，所以在送给女儿之前，我自己玩了会。";

    @Test
    public void iterative1() {

        String prompt = "您的任务是从电子商务网站上生成一个产品评论的简短摘要。\n" +
                "请对三个反引号之间的评论文本进行概括，最多30个词汇。\n" +
                "评论: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative1:\n{}", message);
    }

    @Test
    public void iterative2() {

        String prompt = "您的任务是从电子商务网站上生成一个产品评论的简短摘要。\n" +
                "请对三个反引号之间的评论文本进行概括，最多30个词汇，并且聚焦在产品运输上。\n" +
                "评论: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative2:\n{}", message);
    }

    @Test
    public void iterative3() {

        String prompt = "您的任务是从电子商务网站上生成一个产品评论的简短摘要。\n" +
                "请对三个反引号之间的评论文本进行概括，最多30个词汇，并且聚焦在产品价格和质量上。\n" +
                "评论: ```{" + review + "}```\n";

        String message = this.getCompletion(prompt);

        log.info("iterative3:\n{}", message);
    }


    @Test
    public void iterative4() {

        String prompt = "您的任务是从电子商务网站上的产品评论中提取相关信息。\n" +
                "请从以下三个反引号之间的评论文本中提取产品运输相关的信息，最多30个词汇。\n" +
                "评论: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative4:\n{}", message);
    }


    @Test
    public void iterative5() {

        String review = "Needed a nice lamp for my bedroom, and this one had additional storage and not too high of a price point. Got it fast - arrived in 2 days. " +
                "The string to the lamp broke during the transit and the company happily sent over a new one. Came within a few days as well. " +
                "It was easy to put together. Then I had a missing part, so I contacted their support and they very quickly got me the missing piece! Seems to me to be a great company that cares about their customers and products. ";

        String review2 = "My dental hygienist recommended an electric toothbrush, which is why I got this. The battery life seems to be pretty impressive so far. After initial charging and leaving the charger plugged in for the first week to condition the battery, I've unplugged the charger and been using it for twice daily brushing for the last 3 weeks all on the same charge. " +
                "But the toothbrush head is too small. I’ve seen baby toothbrushes bigger than this one. I wish the head was bigger with different length bristles to get between teeth better because this one doesn’t.  " +
                "Overall if you can get this one around the $50 mark, it's a good deal. The manufactuer's replacements heads are pretty expensive, but you can get generic ones that're more reasonably priced. This toothbrush makes me feel like I've been to the dentist every day. My teeth feel sparkly clean!";

        String review3 = "So, they still had the 17 piece system on seasonal sale for around $49 in the month of November, about half off, but for some reason (call it price gouging) around the second week of December the prices all went up to about anywhere from between $70-$89 for the same system. " +
                "And the 11 piece system went up around $10 or so in price also from the earlier sale price of $29. So it looks okay, but if you look at the base, the part where the blade locks into place doesn’t look as good as in previous editions from a few years ago, " +
                "but I plan to be very gentle with it (example, I crush very hard items like beans, ice, rice, etc. in the blender first then pulverize them in the serving size I want in the blender then switch to the whipping blade for a finer flour, and use the cross cutting blade first when making smoothies, then use the flat blade if I need them finer/less pulpy). Special tip when making smoothies, finely cut and freeze the fruits and vegetables (if using spinach-lightly stew soften the spinach then freeze until ready for use-and if making sorbet, use a small to medium sized food processor) that you plan to use that way you can avoid adding so much ice if at all-when making your smoothie. After about a year, the motor was making a funny noise. I called customer service but the warranty expired already, so I had to buy another one. FYI: The overall quality has gone done in these types of products, so they are kind of counting on brand recognition and consumer loyalty to maintain sales. Got it in about two days. ";

        List<String> reviews = Arrays.asList(review, review2, review3);

        for (int i = 0; i < reviews.size(); i++) {

            String prompt = "你的任务是从电子商务网站上的产品评论中提取相关信息。\n" +
                    "    请对三个反引号之间的评论文本进行概括，最多20个词汇。\n" +
                    "    评论文本: ```{" + reviews.get(i) + "}```";

            String message = this.getCompletion(prompt);

            log.info("review {}: {} \n", i, message);
        }


    }


}
