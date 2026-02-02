package com.example.backendapi.reports;

import com.example.backendapi.reports.dto.CustomerBillLineItemDto;
import com.example.backendapi.reports.dto.MonthlyCustomerSummaryRowDto;
import com.example.backendapi.reports.dto.RevenueByCategoryRowDto;
import com.example.backendapi.reports.dto.RevenueByCustomerRowDto;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC reporting repository (aggregates and joins) mirroring legacy reports.jsp behavior. */
@Repository
public class ReportsRepository {

    private static final RowMapper<CustomerBillLineItemDto> CUSTOMER_BILL_MAPPER =
            (rs, rowNum) ->
                    new CustomerBillLineItemDto(
                            rs.getDate("date_logged").toLocalDate(),
                            rs.getString("user_name"),
                            rs.getString("category_name"),
                            rs.getBigDecimal("hours"),
                            rs.getBigDecimal("hourly_rate"),
                            rs.getBigDecimal("line_total"),
                            rs.getString("note"));

    private static final RowMapper<MonthlyCustomerSummaryRowDto> MONTHLY_ROW_MAPPER =
            (rs, rowNum) ->
                    new MonthlyCustomerSummaryRowDto(
                            rs.getString("customer_name"),
                            rs.getBigDecimal("total_hours"),
                            rs.getBigDecimal("total_amount"));

    private static final RowMapper<RevenueByCustomerRowDto> REV_BY_CUSTOMER_MAPPER =
            (rs, rowNum) ->
                    new RevenueByCustomerRowDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("total_hours"),
                            rs.getBigDecimal("total_revenue"),
                            rs.getBigDecimal("avg_rate"));

    private static final RowMapper<RevenueByCategoryRowDto> REV_BY_CATEGORY_MAPPER =
            (rs, rowNum) ->
                    new RevenueByCategoryRowDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("hourly_rate"),
                            rs.getBigDecimal("total_hours"),
                            rs.getBigDecimal("total_revenue"));

    private final JdbcTemplate jdbcTemplate;

    public ReportsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CustomerBillLineItemDto> customerBillLineItems(long customerId) {
        String sql =
                """
                SELECT
                  bh.date_logged,
                  u.name AS user_name,
                  bc.name AS category_name,
                  bh.hours,
                  bc.hourly_rate,
                  (bh.hours * bc.hourly_rate) AS line_total,
                  bh.note
                FROM public.billable_hours bh
                JOIN public.users u ON bh.user_id = u.id
                JOIN public.billing_categories bc ON bh.category_id = bc.id
                WHERE bh.customer_id = ?
                ORDER BY bh.date_logged DESC, bh.id DESC
                """;
        return jdbcTemplate.query(sql, CUSTOMER_BILL_MAPPER, customerId);
    }

    public List<MonthlyCustomerSummaryRowDto> monthlySummary(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        String sql =
                """
                SELECT
                  c.name AS customer_name,
                  SUM(bh.hours) AS total_hours,
                  SUM(bh.hours * bc.hourly_rate) AS total_amount
                FROM public.billable_hours bh
                JOIN public.customers c ON bh.customer_id = c.id
                JOIN public.billing_categories bc ON bh.category_id = bc.id
                WHERE bh.date_logged >= ?
                  AND bh.date_logged <= ?
                GROUP BY c.name
                ORDER BY total_amount DESC
                """;
        return jdbcTemplate.query(sql, MONTHLY_ROW_MAPPER, Date.valueOf(start), Date.valueOf(end));
    }

    public List<RevenueByCustomerRowDto> revenueByCustomer() {
        String sql =
                """
                SELECT
                  c.id,
                  c.name,
                  COALESCE(SUM(bh.hours), 0) AS total_hours,
                  COALESCE(SUM(bh.hours * bc.hourly_rate), 0) AS total_revenue,
                  AVG(bc.hourly_rate) AS avg_rate
                FROM public.customers c
                LEFT JOIN public.billable_hours bh ON c.id = bh.customer_id
                LEFT JOIN public.billing_categories bc ON bh.category_id = bc.id
                GROUP BY c.id, c.name
                ORDER BY total_revenue DESC
                """;
        return jdbcTemplate.query(sql, REV_BY_CUSTOMER_MAPPER);
    }

    public List<RevenueByCategoryRowDto> revenueByCategory() {
        String sql =
                """
                SELECT
                  bc.id,
                  bc.name,
                  bc.hourly_rate,
                  COALESCE(SUM(bh.hours), 0) AS total_hours,
                  COALESCE(SUM(bh.hours * bc.hourly_rate), 0) AS total_revenue
                FROM public.billing_categories bc
                LEFT JOIN public.billable_hours bh ON bc.id = bh.category_id
                GROUP BY bc.id, bc.name, bc.hourly_rate
                ORDER BY total_revenue DESC
                """;
        return jdbcTemplate.query(sql, REV_BY_CATEGORY_MAPPER);
    }

    public BigDecimal totalRevenue() {
        BigDecimal total =
                jdbcTemplate.queryForObject(
                        "SELECT COALESCE(SUM(bh.hours * bc.hourly_rate), 0) "
                                + "FROM public.billable_hours bh JOIN public.billing_categories bc ON bc.id = bh.category_id",
                        BigDecimal.class);
        return total == null ? BigDecimal.ZERO : total;
    }
}
