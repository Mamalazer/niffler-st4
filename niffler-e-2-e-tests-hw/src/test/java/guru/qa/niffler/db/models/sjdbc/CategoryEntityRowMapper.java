package guru.qa.niffler.db.models.sjdbc;

import guru.qa.niffler.db.models.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(UUID.fromString(rs.getString("id")));
        categoryEntity.setCategory(rs.getString("category"));
        categoryEntity.setUsername(rs.getString("username"));
        return categoryEntity;
    }
}
