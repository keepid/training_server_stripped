package User.Services;

import Config.Message;
import Config.Service;
import Database.UserDao;
import Security.SecurityUtils;
import User.User;
import User.UserMessage;
import Validation.ValidationUtils;
import org.slf4j.Logger;

public class LoginService implements Service {
  private Logger logger;
  private UserDao userDao;
  private String username;
  private String password;
  private User user;

  public LoginService(UserDao userDao, Logger logger, String username, String password) {
    this.userDao = userDao;
    this.logger = logger;
    this.username = username;
    this.password = password;
  }

  public Message executeAndGetResponse() {
    if (!ValidationUtils.isValidUsername(this.username)
        || !ValidationUtils.isValidPassword(this.password)) {
      logger.info("Invalid username and/or password");
      return UserMessage.AUTH_FAILURE;
    }
    // TODO: see UserMessage for appropriate return types
    return null;
  }

  public boolean verifyPassword(String inputPassword, String userHash) {
    SecurityUtils.PassHashEnum verifyPasswordStatus =
        SecurityUtils.verifyPassword(inputPassword, userHash);
    switch (verifyPasswordStatus) {
      case SUCCESS:
        return true;
      case ERROR:
        {
          logger.error("Failed to hash password");
          return false;
        }
      case FAILURE:
        {
          logger.info("Incorrect password");
          return false;
        }
    }
    return false;
  }
}
