package com.lu.justin.tool.dao.dto;

import com.lu.justin.tool.util.Money;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

//@Document(collection = "product_summary")
@Entity
@Table(name = "product_summary")
//@CompoundIndex(unique = true, name = "date_time", def = "{'date':1, 'time':1}")
public class ProductSummaryDTO extends BaseDTO {

    @NotNull
    Date date;
    // product value
    Money value;

    //product transfer amount
    Money amount;
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

    Money maxMarkDown;
    Money maxMarkDownRate;

    int countOfOverdue;

    public ProductSummaryDTO() {
    }

    public ProductSummaryDTO(@NotNull Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Money getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = Money.fromCNY(value);
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = Money.fromCNY(amount);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount3() {
        return count3;
    }

    public void setCount3(int count3) {
        this.count3 = count3;
    }

    public int getCount5() {
        return count5;
    }

    public void setCount5(int count5) {
        this.count5 = count5;
    }

    public int getCount10() {
        return count10;
    }

    public void setCount10(int count10) {
        this.count10 = count10;
    }

    public int getCount99() {
        return count99;
    }

    public void setCount99(int count99) {
        this.count99 = count99;
    }

    public Money getMaxMarkDown() {
        return maxMarkDown;
    }

    public void setMaxMarkDown(BigDecimal maxMarkDown) {
        this.maxMarkDown = Money.fromCNY(maxMarkDown);
    }

    public Money getMaxMarkDownRate() {
        return maxMarkDownRate;
    }

    public void setMaxMarkDownRate(BigDecimal maxMarkDownRate) {
        this.maxMarkDownRate = Money.fromCNY(maxMarkDownRate);
    }

    public int getCountOfOverdue() {
        return countOfOverdue;
    }

    public void setCountOfOverdue(int countOfOverdue) {
        this.countOfOverdue = countOfOverdue;
    }
}
