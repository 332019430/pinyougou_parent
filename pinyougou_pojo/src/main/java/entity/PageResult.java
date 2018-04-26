package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.entity
 * @date 2018/4/22/0022
 */
public class PageResult implements Serializable{
    private Long total;
    private List rows;

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
