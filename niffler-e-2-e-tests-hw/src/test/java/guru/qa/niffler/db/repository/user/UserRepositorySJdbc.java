package guru.qa.niffler.db.repository.user;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.models.sjdbc.UserAuthEntityResultSetExtractor;
import guru.qa.niffler.db.models.sjdbc.UserEntityRowMapper;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.db.models.user.UserEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositorySJdbc implements UserRepository {

  private final TransactionTemplate authTxt;
  private final TransactionTemplate udTxt;
  private final JdbcTemplate authTemplate;
  private final JdbcTemplate udTemplate;

  private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public UserRepositorySJdbc() {
    JdbcTransactionManager authTm = new JdbcTransactionManager(
        DataSourceProvider.INSTANCE.dataSource(Database.AUTH)
    );
    JdbcTransactionManager udTm = new JdbcTransactionManager(
        DataSourceProvider.INSTANCE.dataSource(Database.USERDATA)
    );

    this.authTxt = new TransactionTemplate(authTm);
    this.udTxt = new TransactionTemplate(udTm);
    this.authTemplate = new JdbcTemplate(authTm.getDataSource());
    this.udTemplate = new JdbcTemplate(udTm.getDataSource());
  }

  @Override
  public UserAuthEntity createInAuth(UserAuthEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    return authTxt.execute(status -> {
      authTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO \"user\" " +
                "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, user.getUsername());
        ps.setString(2, pe.encode(user.getPassword()));
        ps.setBoolean(3, user.getEnabled());
        ps.setBoolean(4, user.getAccountNonExpired());
        ps.setBoolean(5, user.getAccountNonLocked());
        ps.setBoolean(6, user.getCredentialsNonExpired());
        return ps;
      }, kh);

      user.setId((UUID) kh.getKeys().get("id"));

      authTemplate.batchUpdate("INSERT INTO \"authority\" " +
          "(user_id, authority) " +
          "VALUES (?, ?)", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setObject(1, user.getId());
          ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
        }

        @Override
        public int getBatchSize() {
          return user.getAuthorities().size();
        }
      });

      return user;
    });
  }

  @Override
  public Optional<UserAuthEntity> selectUserInfoFromAuthById(UUID id) {
    try {
      return Optional.ofNullable(
          authTemplate.query(
              "SELECT * " +
                  "FROM \"user\" u " +
                  "JOIN \"authority\" a ON u.id = a.user_id " +
                  "where u.id = ?",
              UserAuthEntityResultSetExtractor.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public UserEntity createInUserdata(UserEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    udTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (username, currency) VALUES (?, ?)",
          PreparedStatement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      return ps;
    }, kh);

    user.setId((UUID) kh.getKeys().get("id"));
    return user;
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataById(UUID id) {
    try {
      return Optional.ofNullable(
          udTemplate.queryForObject(
              "SELECT * FROM \"user\" WHERE id = ?",
              UserEntityRowMapper.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataByName(String userName) {
    try {
      return Optional.ofNullable(
              udTemplate.queryForObject(
                      "SELECT * FROM \"user\" WHERE username = ?",
                      UserEntityRowMapper.instance,
                      userName
              )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void addFriend(String firstUser, String secondUser) {
    UserEntity user1 = selectUserInfoFromUserDataByName(firstUser).get();
    UserEntity user2 = selectUserInfoFromUserDataByName(secondUser).get();
    List<List<UUID>> userIds = List.of(
            List.of(user1.getId(), user2.getId()),
            List.of(user2.getId(), user1.getId())
    );

    udTemplate.batchUpdate("INSERT INTO friendship VALUES (?, ?, ?)", new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setObject(1, userIds.get(i).get(0));
        ps.setObject(2, userIds.get(i).get(1));
        ps.setBoolean(3, false);
      }

      @Override
      public int getBatchSize() {
        return userIds.size();
      }
    });
  }

  @Override
  public void createFriendInvite(String fromUser, String toUser) {
    UserEntity from = selectUserInfoFromUserDataByName(fromUser).get();
    UserEntity to = selectUserInfoFromUserDataByName(toUser).get();

    udTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement("INSERT INTO friendship VALUES (?, ?, ?)");
      ps.setObject(1, from.getId());
      ps.setObject(2, to.getId());
      ps.setBoolean(3, true);
      return ps;
    });
  }

  @Override
  public void deleteInAuthById(UUID id) {
    authTxt.execute(status -> {
      authTemplate.update("DELETE FROM \"authority\" WHERE user_id = ?", id);
      authTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
      return null;
    });
  }

  @Override
  public void deleteInUserdataById(UUID id) {
    udTxt.execute(status -> {
      udTemplate.update("DELETE FROM friendship WHERE friend_id = ?", id);
      udTemplate.update("DELETE FROM friendship WHERE user_id = ?", id);
      udTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
      return null;
    });
  }

  @Override
  public Optional<UserAuthEntity> updateInAuth(UserAuthEntity userInfo) {
    return authTxt.execute(status -> {
      authTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
                "UPDATE \"user\" " +
                        "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, " +
                        "credentials_non_expired = ? where id = ?"
        );

        ps.setString(1, userInfo.getUsername());
        ps.setString(2, pe.encode(userInfo.getPassword()));
        ps.setBoolean(3, userInfo.getEnabled());
        ps.setBoolean(4, userInfo.getAccountNonExpired());
        ps.setBoolean(5, userInfo.getAccountNonLocked());
        ps.setBoolean(6, userInfo.getCredentialsNonExpired());
        ps.setObject(7, userInfo.getId());

        return ps;
      });

      authTemplate.batchUpdate("UPDATE \"authority\" SET authority = ? where id = ?", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setString(1, userInfo.getAuthorities().get(i).getAuthority().name());
          ps.setObject(2, userInfo.getId());
        }

        @Override
        public int getBatchSize() {
          return userInfo.getAuthorities().size();
        }
      });

      return selectUserInfoFromAuthById(userInfo.getId());
    });
  }

  @Override
  public Optional<UserEntity> updateInUserData(UserEntity userData) {
    udTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
              "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, photo = ? WHERE id = ?"
      );
      ps.setString(1, userData.getUsername());
      ps.setString(2, userData.getCurrency().name());
      ps.setString(3, userData.getFirstname());
      ps.setString(4, userData.getSurname());
      ps.setObject(5, userData.getPhoto());
      ps.setObject(6, userData.getId());
      return ps;
    });

    return selectUserInfoFromUserDataById(userData.getId());
  }
}
