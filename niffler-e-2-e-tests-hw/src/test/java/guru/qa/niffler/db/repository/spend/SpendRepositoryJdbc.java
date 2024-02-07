package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private final DataSource spendDs = DataSourceProvider.INSTANCE.dataSource(Database.SPEND);

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (Connection conn = spendDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO spend " +
                            "(username, currency, spend_date, amount, description, category_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, spend.getUsername());
                ps.setString(2, spend.getCurrency().name());
                ps.setDate(3, new java.sql.Date(spend.getSpendDate().getTime()));
                ps.setObject(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());
                ps.executeUpdate();

                UUID spendId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        spendId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
                spend.setId(spendId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (Connection conn = spendDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO category (category, username) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, category.getCategory());
                ps.setString(2, category.getUsername());
                ps.executeUpdate();

                UUID categoryId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        categoryId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
                category.setId(categoryId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }
}
