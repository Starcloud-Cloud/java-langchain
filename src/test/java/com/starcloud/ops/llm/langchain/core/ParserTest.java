package com.starcloud.ops.llm.langchain.core;

import com.starcloud.ops.llm.langchain.LangChainConfiguration;
import com.starcloud.ops.llm.langchain.core.indexes.splitter.SplitterContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest(classes = {LangChainConfiguration.class})
@RunWith(SpringRunner.class)
public class ParserTest {

    @Test
    public void splitTest() {
        String str = "【析评】\n" +
                "\n" +
                "此诗作于渊明三十九岁。敬远是渊明的堂弟，他们自幼关系亲密，成人后亦志趣相投，感情融洽。这一年敬远二十三岁，同渊明住在一起，并一道读书躬耕。这年春天，诗人开始到怀古田舍躬耕。一年的劳动，收成甚微。寒冷与贫乏，都预示着躬耕自资道路的极端艰辛。这首诗就是在年终腊月之时，渊明写给敬远，以寄托深刻的慨叹之情。\n" +
                "\n" +
                "寝息柴门，与世俗隔绝，荆扉常关，寒风袭来，穷困潦倒，固穷自守，历览千年古书，时常看见操守品德高尚的先贤烈士，自己愧然。这首诗是陶公归隐后第一年的纪实“录像”。\n" +
                "\n" +
                "在陶渊明面前有两条路：一是在官场里不断运作和升迁，那是阳关大道（“平津”）；另一条是退守田园，栖迟于衡门之下，这是独木小桥。陶渊明说，既然前一条路走不成，那么只好走后一条，这也不算是“拙”。话是这么说，却总是有点不得已而求其次的味道，有自我安慰的意思。\n" +
                "\n" +
                "陈祚明评选《采菽堂古诗选》卷十三：“倾耳”二句写风雪得神，而高旷之怀，超脱如睹。……起四句，一句一意，一意一转，曲折尽致，全得子卿“骨肉缘枝叶”章法，而无揣摹之迹。\n" +
                "\n" +
                "延君寿《老生常谈》：“凄凄岁暮风……在目皓已洁。”自是咏雪名句。下接云“劲气侵襟袖，箪瓢谢屡设”。接得沉着有力量。又云“高操非所攀……栖迟讵为拙”，想见作者之磊落光明，傲物自高。每闻人称陶公恬淡，固也；然试想此等人物，如松柏之耐岁寒，其劲直之气与有生俱来，安能不偶然流露于楮墨之间\n";
        List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(str, 100, null);
        Assert.isTrue(splitText.size() > 0, "split text error");
    }
}
