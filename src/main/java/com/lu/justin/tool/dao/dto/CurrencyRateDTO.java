package com.lu.justin.tool.dao.dto;

import com.lu.justin.tool.util.Money;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "currency_rate")
@CompoundIndex(unique = true, name = "cr_dft", def = "{'date':1, 'from':1, 'to':1}")
public class CurrencyRateDTO extends BaseDTO {

    @NotNull
    Date date;
    // product value
    Money rate;

    //currency from
    @NotNull
    String from;

    //currency to
    @NotNull
    String to;

    public CurrencyRateDTO() {
    }

    public CurrencyRateDTO(@NotNull Date date, @NotNull String from, @NotNull String to) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.setBaseInfo();
    }

    public void setRate(Money rate) {
        this.rate = rate;
    }
}
