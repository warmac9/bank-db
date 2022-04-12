package application.rdg;

import application.DbContext;

import java.math.BigDecimal;
import java.sql.*;

public class QuarterChangeStatistic {
    private Integer id;
    private Date date;
    private Integer amount;

    public int getId() {
        return id;
    }

    public QuarterChangeStatistic setId(int id) {
        this.id = id;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public QuarterChangeStatistic setDate(Date date) {
        this.date = date;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public QuarterChangeStatistic setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }


}
