package com.redhat.labs.demo.spring;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserResourceTest {

    @Test
    public void hello() {
        UserResource underTest = new UserResource();

        assertTrue("Response MUST be equal to expected value.", underTest.hello("Test").contentEquals("Hello Test!"));
    }
}