package com.redhat.labs.demo.spring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserResourceTest {

    @Test
    public void hello() {
        UserResource underTest = new UserResource();

        assertTrue(underTest.hello("Test").contentEquals("Hello Test!"), "Response MUST be equal to expected value.");
    }
}