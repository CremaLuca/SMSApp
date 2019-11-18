package com.gruppo4.RingApplication.ringCommands;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PasswordManagerTest {

    private static Context context = null;
    private static PasswordManager passwordManager = null;
    private static final String VALID_PASSWORD = "password";
    private static final String SHOULD_NOT = "It should not have thrown an exception";

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        passwordManager = new PasswordManager();
    }

    @Test
    public void setPassword_isOk() {
        try {
            passwordManager.setPassword(context, VALID_PASSWORD);
            //Success
        } catch (Exception e) {
            Assert.fail(SHOULD_NOT);
        }
    }

    @Test
    public void deletePassword_isOk() {
        try{
            passwordManager.deletePassword(context);
            //Success
        }catch(Exception e){
            Assert.fail(SHOULD_NOT);
        }
    }

    @Test
    public void getPassword_passwords_areEquals() {
        passwordManager.setPassword(context, VALID_PASSWORD);
        Assert.assertEquals(VALID_PASSWORD, PasswordManager.getPassword(context));
    }

    @Test
    public void isPassSaved_password_isSaved() {
        passwordManager.setPassword(context, VALID_PASSWORD);
        Assert.assertEquals(true, passwordManager.isPassSaved(context));
    }

    @Test
    public void isPassSaved_password_isNotSaved(){
        passwordManager.deletePassword(context);
        Assert.assertEquals(false, passwordManager.isPassSaved(context));
    }


}