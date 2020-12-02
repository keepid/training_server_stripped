package User;

import Config.DeploymentLevel;
import Config.Message;
import Database.UserDao;
import Database.UserDaoFactory;
import Logger.LogFactory;
import TestUtils.EntityFactory;
import User.Services.LoginService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

public class LoginServiceUnitTests {
    public UserDao userDao;
    public Logger logger;

    @Before
    public void initialize() {
        this.userDao = UserDaoFactory.create(DeploymentLevel.IN_MEMORY);
        this.logger = new LogFactory().createLogger();
    }

    @After
    public void reset() {
        userDao.clear();
    }

    @Test
    public void userNotFound() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username2", "password2");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    // TODO: add more tests
    // Write at least 5 unit tests to test the LoginService. Think
    // about the edge cases (null username/passwords, invalid
    // username/passwords, etc.). At the beginning of each unit test, use
    // the EntityFactory to create User objects for you with unique
    // information with the .withX() functions, where X is the name of
    // the user fields. There will be one example in the
    // LoginServiceUnitTests file.

    @Test
    public void passwordNotFound() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username1", "password");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    @Test
    public void success() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username1", "password123");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_SUCCESS);
    }

    @Test
    public void auth_success() {
        EntityFactory.createUser()
                .withFirstName("ham")
                .withLastName("jam")
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username1", "password123");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_SUCCESS);
    }

    @Test
    public void nullUsername() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, null, "password2");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    @Test
    public void nullPassword() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username2", null);
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    @Test
    public void invalidPassword() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "username1", "pass");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    @Test
    public void invalidUsername() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "", "password123");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }

    @Test
    public void emptyParameters() {
        EntityFactory.createUser()
                .withUsername("username1")
                .withPasswordToHash("password123")
                .buildAndPersist(userDao);
        LoginService loginService = new LoginService(userDao, logger, "", "");
        Message message = loginService.executeAndGetResponse();
        assertEquals(message, UserMessage.AUTH_FAILURE);
    }


}
