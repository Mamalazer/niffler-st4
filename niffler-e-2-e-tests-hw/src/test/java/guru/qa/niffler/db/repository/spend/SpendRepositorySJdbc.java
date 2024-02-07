package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.UUID;

public class SpendRepositorySJdbc implements SpendRepository {

    private final JdbcTemplate spendTemplate = new JdbcTemplate(DataSourceProvider.INSTANCE.dataSource(Database.SPEND));

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        KeyHolder kh = new GeneratedKeyHolder();
        spendTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO spend (username, currency, spend_date, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setString(2, spend.getCurrency().name());
            ps.setDate(3, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setObject(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;
        }, kh);

        spend.setId((UUID) kh.getKeys().get("id"));
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        KeyHolder kh = new GeneratedKeyHolder();
        spendTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (category, username) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getCategory());
            ps.setString(2, category.getUsername());
            return ps;
        }, kh);

        category.setId((UUID) kh.getKeys().get("id"));
        return category;
    }
}
