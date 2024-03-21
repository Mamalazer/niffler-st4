package guru.qa.niffler.db.repository.user;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.models.user.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

  private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(Database.AUTH);
  private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(Database.USERDATA);
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
              PreparedStatement invitesPs = conn.prepareStatement("DELETE FROM friendship WHERE friend_id = ?");
              PreparedStatement friendsPs = conn.prepareStatement("DELETE FROM friendship WHERE user_id = ?");
              PreparedStatement userPs = conn.prepareStatement("DELETE FROM \"user\" WHERE id = ?")
      ) {

        invitesPs.setObject(1, id);
        friendsPs.setObject(1, id);
        userPs.setObject(1, id);

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
  public Optional<UserAuthEntity> updateInAuth(UserAuthEntity userInfo) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);
      try (
              PreparedStatement userPs = conn.prepareStatement(
              "UPDATE \"user\" " +
                      "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, " +
                      "credentials_non_expired = ? where id = ?");
              PreparedStatement authorityPs = conn.prepareStatement(
                      "UPDATE \"authority\" SET authority = ? where id = ?"
              )
      ) {

        userPs.setString(1, userInfo.getUsername());
        userPs.setString(2, pe.encode(userInfo.getPassword()));
        userPs.setBoolean(3, userInfo.getEnabled());
        userPs.setBoolean(4, userInfo.getAccountNonExpired());
        userPs.setBoolean(5, userInfo.getAccountNonLocked());
        userPs.setBoolean(6, userInfo.getCredentialsNonExpired());
        userPs.setObject(7, userInfo.getId());

        userPs.executeUpdate();

        for (AuthorityEntity entity : userInfo.getAuthorities()) {
          authorityPs.setString(1, entity.getAuthority().name());
          authorityPs.setObject(2, entity.getId());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }

        authorityPs.executeBatch();
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

    return selectUserInfoFromAuthById(userInfo.getId());
  }

  @Override
  public Optional<UserEntity> updateInUserData(UserEntity userData) {
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

    return selectUserInfoFromUserDataById(userData.getId());
  }

  @Override
  public Optional<UserAuthEntity> selectUserInfoFromAuthById(UUID id) {

    UserAuthEntity userInfo = new UserAuthEntity();

    try (Connection conn = authDs.getConnection()) {
      try (
              PreparedStatement userPs = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");
              PreparedStatement authorityPs = conn.prepareStatement("SELECT * FROM \"authority\" WHERE user_id = ?")
        ) {

        userPs.setObject(1, id);
        authorityPs.setObject(1, id);

        try (
                ResultSet userSet = userPs.executeQuery();
                ResultSet authoritySet = authorityPs.executeQuery()
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
            entity.setAuthority(Authority.valueOf(authoritySet.getString("authority")));

            userInfo.getAuthorities().add(entity);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(userInfo);
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataById(UUID id) {
    UserEntity userEntity = new UserEntity();

    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
        ps.setObject(1, id);

        try (ResultSet set = ps.executeQuery()) {
          if (set.next()) {
            userEntity.setId(UUID.fromString(set.getString("id")));
            userEntity.setUsername(set.getString("username"));
            userEntity.setFirstname(set.getString("firstname"));
            userEntity.setSurname(set.getString("surname"));
            userEntity.setPhoto(set.getString("photo") != null ? set.getString("photo").getBytes() : null);
            userEntity.setCurrency(CurrencyValues.valueOf(set.getString("currency")));

          } else {
            throw new IllegalStateException("Can`t find row in user table");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(userEntity);
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataByName(String userName) {
    UserEntity userEntity = new UserEntity();

    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM \"user\" WHERE username = ?")) {
        ps.setObject(1, userName);

        try (ResultSet set = ps.executeQuery()) {
          if (set.next()) {
            userEntity.setId(UUID.fromString(set.getString("id")));
            userEntity.setUsername(set.getString("username"));
            userEntity.setFirstname(set.getString("firstname"));
            userEntity.setSurname(set.getString("surname"));
            userEntity.setPhoto(set.getString("photo") != null ? set.getString("photo").getBytes() : null);
            userEntity.setCurrency(CurrencyValues.valueOf(set.getString("currency")));

          } else {
            throw new IllegalStateException("Can`t find row in user table");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(userEntity);
  }

  @Override
  public void addFriend(String firstUser, String secondUser) {
    UserEntity user1 = selectUserInfoFromUserDataByName(firstUser).get();
    UserEntity user2 = selectUserInfoFromUserDataByName(secondUser).get();
    List<List<UUID>> userIds = List.of(
            List.of(user1.getId(), user2.getId()),
            List.of(user2.getId(), user1.getId())
    );

    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO friendship VALUES (?, ?, ?)")) {
        for (List<UUID> users : userIds) {
          ps.setObject(1, users.get(0));
          ps.setObject(2, users.get(1));
          ps.setBoolean(3, false);
          ps.addBatch();
          ps.clearParameters();
        }
        ps.executeBatch();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void createFriendInvite(String fromUser, String toUser) {
    UserEntity from = selectUserInfoFromUserDataByName(fromUser).get();
    UserEntity to = selectUserInfoFromUserDataByName(toUser).get();

    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO friendship VALUES (?, ?, ?)")) {
        ps.setObject(1, from.getId());
        ps.setObject(2, to.getId());
        ps.setBoolean(3, true);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
