package com.example.backendapi.categories;

import com.example.backendapi.categories.dto.BillingCategoryDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC repository for billing_categories table. */
@Repository
public class BillingCategoryRepository {

    private static final RowMapper<BillingCategoryDto> CATEGORY_WITH_AGG_MAPPER =
            (rs, rowNum) ->
                    new BillingCategoryDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("hourly_rate"),
                            rs.getBigDecimal("total_hours"),
                            rs.getBigDecimal("total_revenue"));

    private static final RowMapper<BillingCategoryDto> CATEGORY_BASE_MAPPER =
            (rs, rowNum) ->
                    new BillingCategoryDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("hourly_rate"),
                            BigDecimal.ZERO,
                            BigDecimal.ZERO);

    private final JdbcTemplate jdbcTemplate;

    public BillingCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        Long cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.billing_categories", Long.class);
        return cnt == null ? 0L : cnt;
    }

    public List<BillingCategoryDto> findAllWithAggregates() {
        // Mirrors categories.jsp: total hours and total revenue per category.
        String sql =
                """
                SELECT
                  bc.id,
                  bc.name,
                  bc.description,
                  bc.hourly_rate,
                  COALESCE(SUM(bh.hours), 0) AS total_hours,
                  COALESCE(SUM(bh.hours * bc.hourly_rate), 0) AS total_revenue
                FROM public.billing_categories bc
                LEFT JOIN public.billable_hours bh ON bh.category_id = bc.id
                GROUP BY bc.id, bc.name, bc.description, bc.hourly_rate
                ORDER BY bc.id ASC
                """;
        return jdbcTemplate.query(sql, CATEGORY_WITH_AGG_MAPPER);
    }

    public Optional<BillingCategoryDto> findById(long id) {
        List<BillingCategoryDto> rows =
                jdbcTemplate.query(
                        "SELECT id, name, description, hourly_rate FROM public.billing_categories WHERE id = ?",
                        CATEGORY_BASE_MAPPER,
                        id);
        return rows.stream().findFirst();
    }

    public BillingCategoryDto insert(String name, String description, BigDecimal hourlyRate) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO public.billing_categories (name, description, hourly_rate) VALUES (?, ?, ?) "
                        + "RETURNING id, name, description, hourly_rate",
                CATEGORY_BASE_MAPPER,
                name,
                description,
                hourlyRate);
    }

    public Optional<BillingCategoryDto> updateRate(long id, BigDecimal newRate) {
        int updated =
                jdbcTemplate.update(
                        "UPDATE public.billing_categories SET hourly_rate = ? WHERE id = ?", newRate, id);
        if (updated <= 0) {
            return Optional.empty();
        }
        return findById(id);
    }
}
