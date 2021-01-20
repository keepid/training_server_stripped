package User.Services;

import Config.Message;
import Config.Service;
import Database.UserDao;
import User.User;
import User.UserMessage;
import Validation.ValidationUtils;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;

public class GetUserInfoService implements Service {
  private UserDao userDao;
  private Logger logger;
  private String username;
  private User user;

  public GetUserInfoService(UserDao userDao, Logger logger, String username) {
    this.userDao = userDao;
    this.logger = logger;
    this.username = username;
  }

  @Override
  public Message executeAndGetResponse() {
    if (!ValidationUtils.isValidUsername(this.username)) {
      return UserMessage.USER_NOT_FOUND;
    }

    Optional<User> user = userDao.get(username);

    // Indicates username does not exist in db
    if (user.isEmpty()) {
      return UserMessage.USER_NOT_FOUND;
    }

    this.user = user.get();
    return UserMessage.SUCCESS;
  }

  public JSONObject getUserFields() {
    return getFieldsFromUser(this.user);
  }

  /**
   * Generate JSONObject with publicly-accessible User fields.
   * @param user The User instance containing the data with which the JSONObject is populated
   * @return The JSONObject representation of the passed User, which can be safely sent to client
   */
  public static JSONObject getFieldsFromUser(User user) {
    Objects.requireNonNull(user);
    JSONObject userObject = new JSONObject();
    userObject.put("organization", user.getOrganization());
    userObject.put("firstName", user.getFirstName());
    userObject.put("lastName", user.getLastName());
    userObject.put("birthDate", user.getBirthDate());
    userObject.put("address", user.getAddress());
    userObject.put("city", user.getCity());
    userObject.put("state", user.getState());
    userObject.put("zipcode", user.getZipcode());
    userObject.put("email", user.getEmail());
    userObject.put("phone", user.getPhone());
    userObject.put("twoFactorOn", user.getTwoFactorOn());
    userObject.put("username", user.getUsername());
    return userObject;
  }
}
