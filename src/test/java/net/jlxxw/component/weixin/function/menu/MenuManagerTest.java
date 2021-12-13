package net.jlxxw.component.weixin.function.menu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.jlxxw.component.weixin.base.BaseTest;
import net.jlxxw.component.weixin.dto.menu.MenuDTO;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author chunyang.leng
 * @date 2021-12-13 3:15 下午
 */
public class MenuManagerTest extends BaseTest {
    @Autowired
    private MenuManager menuManager;

    public void createMenuTest(){
        JSONObject jsonObject = JSON.parseObject(getMockMenuData());
        JSONArray jsonArray = jsonObject.getJSONArray("button");
        List<MenuDTO> menuList = jsonArray.toJavaList(MenuDTO.class);
        menuManager.createMenu(menuList,(response)->{
            Integer errcode = response.getErrcode();
            Assert.assertEquals(0, (int) errcode);
        });
    }


    private static String getMockMenuData(){
        return "{\n" +
                "     \"button\":[\n" +
                "     {\t\n" +
                "          \"type\":\"click\",\n" +
                "          \"name\":\"今日歌曲\",\n" +
                "          \"key\":\"V1001_TODAY_MUSIC\"\n" +
                "      },\n" +
                "      {\n" +
                "           \"name\":\"菜单\",\n" +
                "           \"sub_button\":[\n" +
                "           {\t\n" +
                "               \"type\":\"view\",\n" +
                "               \"name\":\"搜索\",\n" +
                "               \"url\":\"http://www.soso.com/\"\n" +
                "            },\n" +
                "            {\n" +
                "                 \"type\":\"miniprogram\",\n" +
                "                 \"name\":\"wxa\",\n" +
                "                 \"url\":\"http://mp.weixin.qq.com\",\n" +
                "                 \"appid\":\"wx286b93c14bbf93aa\",\n" +
                "                 \"pagepath\":\"pages/lunar/index\"\n" +
                "             },\n" +
                "            {\n" +
                "               \"type\":\"click\",\n" +
                "               \"name\":\"赞一下我们\",\n" +
                "               \"key\":\"V1001_GOOD\"\n" +
                "            }]\n" +
                "       }]\n" +
                " }";
    }
}
