package com.lu.justin.tool.dao.dto;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "product_summary")
//@CompoundIndex(unique = true, name = "date_time", def = "{'date':1, 'time':1}")
public class ProductSummaryDTO extends BaseDTO {

    @NotNull
    @Indexed(unique = true)
    Date date;
    // product value
    BigDecimal value;

    //product transfer amount
    BigDecimal amount;
    /**
     * total count
     */
    int count;

    /**
     * 0~1W
     */
    int count1;
    /**
     * 1~3W
     */
    int count3;
    /**
     * 3~5W
     */
    int count5;
    /**
     * 5~10W
     */
    int count10;
    /**
     * 10W~
     */
    int count99;

    BigDecimal maxMarkDown;
    BigDecimal maxMarkDownRate;

    public ProductSummaryDTO() {
    }

    public ProductSummaryDTO(@NotNull Date date) {
        this.date = date;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public void setCount3(int count3) {
        this.count3 = count3;
    }

    public void setCount5(int count5) {
        this.count5 = count5;
    }

    public void setCount10(int count10) {
        this.count10 = count10;
    }

    public void setCount99(int count99) {
        this.count99 = count99;
    }

    public void setMaxMarkDown(BigDecimal maxMarkDown) {
        this.maxMarkDown = maxMarkDown;
    }

    public void setMaxMarkDownRate(BigDecimal maxMarkDownRate) {
        this.maxMarkDownRate = maxMarkDownRate;
    }
}
