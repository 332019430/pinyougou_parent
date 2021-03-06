import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小郑
 * @version 1.0
 * @description PACKAGE_NAME
 * @date 2018/5/7/0007
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class HighlightTest {
    @Autowired
    private ItemSearchService itemSearchService;

    @Test
    public void searchHighlight(){
        Map map = new HashMap<>();
        map.put("keywords","三星");

        Map search = itemSearchService.search(map);

        /*List<TbItem> tbItems = (List<TbItem>) search.get("rows");
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }*/
    }
}
