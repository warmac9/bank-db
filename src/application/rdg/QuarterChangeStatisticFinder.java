package application.rdg;

import application.DbContext;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuarterChangeStatisticFinder {
    public static final String quarter_statistic_sql = "create or replace function get_active(active_date date) returns integer language plpgsql as $$\n" +
            "begin\n" +
            "    return(select count(distinct user_id) from account where active_date between activated_at and coalesce(deactivated_at, '2222-01-01') limit 1);\n" +
            "end\n" +
            "$$;\n" +
            "\n" +
            "drop table if exists \"temp\" cascade;\n" +
            "create table \"temp\"\n" +
            "(\n" +
            "    id serial primary key,\n" +
            "  \tdate date,\n" +
            "  \tchange int\n" +
            ");\n" +
            "\n" +
            "do $$\n" +
            "declare quarter date;\n" +
            "declare max_date date;\n" +
            "declare prev int;\n" +
            "declare cur int;\n" +
            "begin\n" +
            "\tselect date_trunc('quarter', min(activated_at)) into quarter from account;\n" +
            "    select date_trunc('quarter', now()) into max_date from account limit 1;\n" +
            "    prev := 0;\n" +
            "\twhile quarter < max_date loop\n" +
            "    \tquarter := quarter + interval '1 month' * 3;\n" +
            "        cur := get_active(quarter);\n" +
            "    \tinsert into \"temp\" (date, change) values (quarter, cur - prev);\n" +
            "        prev := cur;\n" +
            "    end loop;\n" +
            "end $$;\n" +
            "\n" +
            "select * from \"temp\";\n" +
            "drop table \"temp\";";

    private static QuarterChangeStatisticFinder instance;

    public static QuarterChangeStatisticFinder getInstance() {
        if(instance == null)
            instance = new QuarterChangeStatisticFinder();
        return instance;
    }

    public List<QuarterChangeStatistic> findAllQuarterStatistic() throws SQLException {
        try(CallableStatement statement = DbContext.getConnection().prepareCall(quarter_statistic_sql)) {
            boolean f = statement.execute();
            ResultSet res = statement.getResultSet();
            List<QuarterChangeStatistic> list = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                statement.getMoreResults();
                res = statement.getResultSet();
            }

            while (res.next()) {
                list.add(
                    new QuarterChangeStatistic()
                            .setDate(res.getDate("date"))
                            .setAmount(res.getInt("change"))
                );
            }

            return list;
        }
    }
}
