package net.jlxxw.wechat.function.push;

import com.alibaba.fastjson.JSON;
import net.jlxxw.wechat.base.BaseTest;
import net.jlxxw.wechat.dto.template.WeChatTemplateDTO;
import net.jlxxw.wechat.enums.ColorEnums;
import net.jlxxw.wechat.function.token.WeChatTokenManager;
import net.jlxxw.wechat.response.WeChatResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author chunyang.leng
 * @date 2021-03-08 3:25 下午
 */
public class SyncPushTemplateTest extends BaseTest {


    @Autowired
    private SyncPushTemplate syncPushTemplate;
    @Autowired
    private WeChatTokenManager weChatTokenManager;

    /**
     * 多线程共享token
     */
    private final Supplier<String> volatileToken = this::getToken;

    @Test
    public void pushTemplateTest() {
        String token = weChatTokenManager.getTokenFromLocal();
        String url = "xxxxxx";

        WeChatTemplateDTO weChatTemplateDTO = new WeChatTemplateDTO();
        weChatTemplateDTO
                .buildToUser(openId)
                .buildUrl(url)
                .buildTemplateCode(templateId)
                .buildFirstData("first DATA的具体值", ColorEnums.BLUE)
                .buildKeyWord1Data("keyword1 DATA的具体值", null)
                .buildOtherData("abc", "abc DATA的具体值", ColorEnums.ORANGE);
        WeChatResponse weChatResponse = syncPushTemplate.pushTemplate(weChatTemplateDTO);

        Assert.assertEquals("微信返回状态错误，当前为：" + JSON.toJSONString(weChatResponse), 0L, (int) weChatResponse.getErrcode());
    }

    /**
     * 批量推送
     */
    @Test
    public void pushTemplateListTest() {
        String token = weChatTokenManager.getTokenFromLocal();
        String url = "xxxxxx";

        WeChatTemplateDTO weChatTemplateDTO = new WeChatTemplateDTO();
        weChatTemplateDTO
                .buildToUser(openId)
                .buildUrl(url)
                .buildTemplateCode(templateId)
                .buildFirstData("first DATA的具体值", ColorEnums.BLUE)
                .buildKeyWord1Data("keyword1 DATA的具体值", null)
                .buildOtherData("abc", "abc DATA的具体值", ColorEnums.ORANGE);
        List<WeChatTemplateDTO> templateList = new ArrayList<>();
        templateList.add(weChatTemplateDTO);
        List<WeChatResponse> weChatResponse = syncPushTemplate.pushTemplate(templateList);
        Assert.assertFalse("模版推送结果不应为空", CollectionUtils.isEmpty(weChatResponse));
    }


}
