package com.example.backendapi.hours;

import com.example.backendapi.hours.dto.BillableHourViewDto;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC repository for billable_hours table. */
@Repository
public class BillableHourRepository {

    private static final RowMapper<BillableHourViewDto> HOUR_VIEW_MAPPER =
            (rs, rowNum) -> {
                Timestamp created = rs.getTimestamp("created_at");
                Instant createdAt = created == null ? null : created.toInstant();
                Date dateLoggedSql = rs.getDate("date_logged");
                LocalDate dateLogged = dateLoggedSql == null ? null : dateLoggedSql.toLocalDate();

                BigDecimal hours = rs.getBigDecimal("hours");
                BigDecimal rate = rs.getBigDecimal("hourly_rate");
                BigDecimal lineTotal =
                        hours == null || rate == null ? null : hours.multiply(rate);

                return new BillableHourViewDto(
                        rs.getLong("id"),
                        dateLogged,
                        rs.getLong("customer_id"),
                        rs.getString("customer_name"),
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getLong("category_id"),
                        rs.getString("category_name"),
                        hours,
                        rate,
                        lineTotal,
                        rs.getString("note"),
                        createdAt);
            };

    private final JdbcTemplate jdbcTemplate;

    public BillableHourRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BillableHourViewDto insert(
            long customerId,
            long userId,
            long categoryId,
            BigDecimal hours,
            String note,
            LocalDate dateLogged) {

        LocalDate effectiveDate = dateLogged == null ? LocalDate.now() : dateLogged;

        // Insert then return the joined view row (so client gets enriched info).
        Long id =
                jdbcTemplate.queryForObject(
                        "INSERT INTO public.billable_hours (customer_id, user_id, category_id, hours, note, date_logged, created_at) "
                                + "VALUES (?, ?, ?, ?, ?, ?, NOW()) RETURNING id",
                        Long.class,
                        customerId,
                        userId,
                        categoryId,
                        hours,
                        note,
                        Date.valueOf(effectiveDate));

        if (id == null) {
            throw new IllegalStateException("Failed to create billable hour entry");
        }
        return findByIdView(id).get(0);
    }

    public List<BillableHourViewDto> findRecent(int limit) {
        int effectiveLimit = Math.max(1, Math.min(limit, 200));
        String sql =
                """
                SELECT
                  bh.id,
                  bh.date_logged,
                  bh.customer_id,
                  c.name AS customer_name,
                  bh.user_id,
                  u.name AS user_name,
                  bh.category_id,
                  bc.name AS category_name,
                  bh.hours,
                  bc.hourly_rate,
                  bh.note,
                  bh.created_at
                FROM public.billable_hours bh
                JOIN public.customers c ON bh.customer_id = c.id
                JOIN public.users u ON bh.user_id = u.id
                JOIN public.billing_categories bc ON bh.category_id = bc.id
                ORDER BY bh.date_logged DESC, bh.id DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, HOUR_VIEW_MAPPER, effectiveLimit);
    }

    private List<BillableHourViewDto> findByIdView(long id) {
        String sql =
                """
                SELECT
                  bh.id,
                  bh.date_logged,
                  bh.customer_id,
                  c.name AS customer_name,
                  bh.user_id,
                  u.name AS user_name,
                  bh.category_id,
                  bc.name AS category_name,
                  bh.hours,
                  bc.hourly_rate,
                  bh.note,
                  bh.created_at
                FROM public.billable_hours bh
                JOIN public.customers c ON bh.customer_id = c.id
                JOIN public.users u ON bh.user_id = u.id
                JOIN public.billing_categories bc ON bh.category_id = bc.id
                WHERE bh.id = ?
                """;
        return jdbcTemplate.query(sql, HOUR_VIEW_MAPPER, id);
    }
}
