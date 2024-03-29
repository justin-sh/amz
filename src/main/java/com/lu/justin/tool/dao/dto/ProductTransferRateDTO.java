package com.lu.justin.tool.dao.dto;

import com.lu.justin.tool.util.Money;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

//@Document(collection = "product_transfer_rate")
@Entity
@Table(name = "product_transfer_rate")
//@CompoundIndex(unique = true, name = "date_time", def = "{'date':1, 'time':1}")
public class ProductTransferRateDTO extends BaseDTO {

    @NotNull
//    @Indexed(unique = true)
    Date date;

    @NotNull
    Money avgSuccessRatio;

    @NotNull
    String actionType;

    public ProductTransferRateDTO() {
    }

    public ProductTransferRateDTO(@NotNull Date date, String actionType, BigDecimal avgSuccessRatio) {
        this.date = date;
        this.avgSuccessRatio = Money.fromCNY(avgSuccessRatio);
        this.actionType = actionType;
        super.setBaseInfo();
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAvgSuccessRatio() {
        return avgSuccessRatio.toCNY();
    }

    public String getActionType() {
        return actionType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAvgSuccessRatio(Money avgSuccessRatio) {
        this.avgSuccessRatio = avgSuccessRatio;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
