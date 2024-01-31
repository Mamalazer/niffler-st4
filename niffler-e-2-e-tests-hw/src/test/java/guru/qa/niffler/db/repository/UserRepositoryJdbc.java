package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

  private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH);
  private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA);

  private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  @Override
  public UserAuthEntity createInAuth(UserAuthEntity user) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement userPs = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
              "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
           PreparedStatement authorityPs = conn.prepareStatement(
               "INSERT INTO \"authority\" " +
                   "(user_id, authority) " +
                   "VALUES (?, ?)")
      ) {

        userPs.setString(1, user.getUsername());
        userPs.setString(2, pe.encode(user.getPassword()));
        userPs.setBoolean(3, user.getEnabled());
        userPs.setBoolean(4, user.getAccountNonExpired());
        userPs.setBoolean(5, user.getAccountNonLocked());
        userPs.setBoolean(6, user.getCredentialsNonExpired());

        userPs.executeUpdate();

        UUID authUserId;
        try (ResultSet keys = userPs.getGeneratedKeys()) {
          if (keys.next()) {
            authUserId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }

        for (Authority authority : Authority.values()) {
          authorityPs.setObject(1, authUserId);
          authorityPs.setString(2, authority.name());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }

        authorityPs.executeBatch();
        conn.commit();
        user.setId(authUserId);
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public UserEntity createInUserdata(UserEntity user) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, currency) " +
              "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getCurrency().name());
        ps.executeUpdate();

        UUID userId;
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (keys.next()) {
            userId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }
        user.setId(userId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public void deleteInAuthById(UUID id) {

    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);
      try (
           PreparedStatement userPs = conn.prepareStatement("DELETE FROM \"user\" WHERE id = ?");
           PreparedStatement authorityPs = conn.prepareStatement("DELETE FROM \"authority\" WHERE user_id = ?")
      ) {

        authorityPs.setObject(1, id);
        authorityPs.executeUpdate();

        userPs.setObject(1, id);
        userPs.executeUpdate();

        conn.commit();
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteInUserdataById(UUID id) {
    try (Connection conn = udDs.getConnection()) {
      conn.setAutoCommit(false);
      try (
              PreparedStatement userPs = conn.prepareStatement("DELETE FROM \"user\" WHERE id = ?");
              PreparedStatement friendsPs = conn.prepareStatement("DELETE FROM friendship WHERE user_id = ?");
              PreparedStatement invitesPs = conn.prepareStatement("DELETE FROM friendship WHERE friend_id = ?")
      ) {
        userPs.setObject(1, id);
        friendsPs.setObject(1, id);
        invitesPs.setObject(1, id);

        userPs.executeUpdate();
        friendsPs.executeUpdate();
        invitesPs.executeUpdate();

        conn.commit();
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateInAuthById(UserAuthEntity userInfo) {
    try (Connection conn = authDs.getConnection()) {
      try (PreparedStatement userPs = conn.prepareStatement(
              "UPDATE \"user\" " +
                      "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, " +
                      "credentials_non_expired = ? where id = ?");
      ) {

        userPs.setString(1, userInfo.getUsername());
        userPs.setString(2, pe.encode(userInfo.getPassword()));
        userPs.setBoolean(3, userInfo.getEnabled());
        userPs.setBoolean(4, userInfo.getAccountNonExpired());
        userPs.setBoolean(5, userInfo.getAccountNonLocked());
        userPs.setBoolean(6, userInfo.getCredentialsNonExpired());
        userPs.setObject(7, userInfo.getId());

        userPs.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateInUserDataById(UserEntity userData) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
              "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, photo = ? WHERE id = ?"
      )) {

        ps.setString(1, userData.getUsername());
        ps.setString(2, userData.getCurrency().name());
        ps.setString(3, userData.getFirstname());
        ps.setString(4, userData.getSurname());
        ps.setObject(5, userData.getPhoto());
        ps.setObject(6, userData.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public UserAuthEntity selectUserInfoFromAuthById(UUID id) {

    UserAuthEntity userInfo = new UserAuthEntity();

    try (Connection conn = authDs.getConnection()) {
      try (
              PreparedStatement userPs = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");
              PreparedStatement authorityPs = conn.prepareStatement("SELECT * FROM \"authority\" WHERE user_id = ?");
        ) {

        userPs.setObject(1, id);
        authorityPs.setObject(1, id);

        try (
                ResultSet userSet = userPs.executeQuery();
                ResultSet authoritySet = authorityPs.executeQuery();
          ) {

          if (userSet.next()) {
            userInfo.setId(UUID.fromString(userSet.getString("id")));
            userInfo.setUsername(userSet.getString("username"));
            userInfo.setPassword(userSet.getString("password"));
            userInfo.setEnabled(userSet.getBoolean("enabled"));
            userInfo.setAccountNonExpired(userSet.getBoolean("account_non_expired"));
            userInfo.setAccountNonLocked(userSet.getBoolean("account_non_locked"));
            userInfo.setCredentialsNonExpired(userSet.getBoolean("credentials_non_expired"));
          } else {
            throw new IllegalStateException("Can`t find row in user table");
          }

          while (authoritySet.next()) {
            AuthorityEntity entity = new AuthorityEntity();
            entity.setId(UUID.fromString(authoritySet.getString("id")));

            switch (authoritySet.getString("authority")) {
              case "read" :
                entity.setAuthority(Authority.read);
                break;
              case "write" :
                entity.setAuthority(Authority.write);
                break;
              default :
                throw new IllegalStateException("Unknown Authority");
            }

            userInfo.getAuthorities().add(entity);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return userInfo;
  }

  @Override
  public UserEntity selectUserInfoFromUserDataById(UUID id) {
    UserEntity userEntity = new UserEntity();

    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");) {
        ps.setObject(1, id);

        try (ResultSet set = ps.executeQuery()) {
          if (set.next()) {
            userEntity.setId(UUID.fromString(set.getString("id")));
            userEntity.setUsername(set.getString("username"));
            userEntity.setFirstname(set.getString("firstname"));
            userEntity.setSurname(set.getString("surname"));
            userEntity.setPhoto(set.getString("photo") != null ? set.getString("photo").getBytes() : null);

            switch (set.getString("currency")) {
              case "RUB" :
                userEntity.setCurrency(CurrencyValues.RUB);
                break;
              case "USD" :
                userEntity.setCurrency(CurrencyValues.USD);
                break;
              case "EUR" :
                userEntity.setCurrency(CurrencyValues.EUR);
                break;
              case "KZT" :
                userEntity.setCurrency(CurrencyValues.KZT);
                break;
              default :
                throw new IllegalStateException("Unknown currency value");
            }
          } else {
            throw new IllegalStateException("Can`t find row in user table");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return userEntity;
  }
}
