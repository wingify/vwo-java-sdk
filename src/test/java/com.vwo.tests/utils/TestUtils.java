package com.vwo.tests.utils;

import java.util.Random;

public class TestUtils {
  public static String[] getUsers() {
    return new String[]{
      "Ashley",
      "Bill",
      "Chris",
      "Dominic",
      "Emma",
      "Faizan",
      "Gimmy",
      "Harry",
      "Ian",
      "John",
      "King",
      "Lisa",
      "Mona",
      "Nina",
      "Olivia",
      "Pete",
      "Queen",
      "Robert",
      "Sarah",
      "Tierra",
      "Una",
      "Varun",
      "Will",
      "Xin",
      "You",
      "Zeba"
    };
  }

  public static String getRandomUser() {
    String[] users = TestUtils.getUsers();
    return users[new Random().nextInt(users.length)];
  }
}
