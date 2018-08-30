package com.lu.justin.tool.dao.dto;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "product_summary")
//@CompoundIndex(unique = true, name = "date_time", def = "{'date':1, 'time':1}")
public class ProductSummaryDTO extends BaseDTO {

    // every minutes

    @NotNull
    @Indexed(unique = true)
    Date date;
    // product value
    BigDecimal value;

    //product transfer amount
    BigDecimal amount;
    int count;

    public ProductSummaryDTO() {
    }

    public ProductSummaryDTO(@NotNull Date date) {
        this.date = date;
    }

    public ProductSummaryDTO(@NotNull Date date, BigDecimal value, BigDecimal amount, int count) {
        this.date = date;
        this.value = value;
        this.amount = amount;
        this.count = count;
        super.setBaseInfo();
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
}
