package services;

import models.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class UserCredentialsServiceTest {

    UserCredentialsService userCredentialsService;

    @Before
    public void setup() {
        userCredentialsService = new UserCredentialsService();
    }

    @Test
    public void getUserCredentials_GivenValidJwt_ReturnParsedCredentials() {
        UserCredentials userCredentials = userCredentialsService.getUserCredentials(getJwt());
        assertEquals("+15184285664", userCredentials.getPhoneNumber());
        assertEquals("zsedefian", userCredentials.getUsername());
    }

    @Test
    public void getUserCredentials_GivenValidJwtWithoutPrefix_ReturnParsedCredentials() {
        UserCredentials userCredentials = userCredentialsService.getUserCredentials(getJwtWithoutPrefix());
        assertEquals("+15184285664", userCredentials.getPhoneNumber());
        assertEquals("zsedefian", userCredentials.getUsername());
    }

    private String getJwt() {
        return "Bearer eyJraWQiOiIzS2t4ZkZmZjU0XC9cLzFcL0VURDBxRmNEM1NUNUxSZDEzRUl5S0lRUCt5Um5NPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjOWRlMTBhMC1iMmIzLTQwN2EtYjYzZi0yZGUyOGRkMDQ1NDAiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMi5hbWF6b25hd3MuY29tXC91cy1lYXN0LTJfVG84d3g5eEU5IiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6InpzZWRlZmlhbiIsImF1ZCI6IjM2cG5oa2w4bHZjMnRtNWtqbWVzN2djOThlIiwiZXZlbnRfaWQiOiI5MTczNjM4MC1kZDNlLTRkN2EtOWVkYS02ZTE5NDc2Y2FlYWIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4MzI3Mjk2NywicGhvbmVfbnVtYmVyIjoiKzE1MTg0Mjg1NjY0IiwiZXhwIjoxNTgzNDI5NjM2LCJpYXQiOjE1ODM0MjYwMzYsImVtYWlsIjoiemFjaEBudXZhbGVuY2UuaW8ifQ.CVNAkzHtGKBufPdofNko5F2VGp33sVIFbCB9E29vxptDrIEWyV1ZJ7o5T5qC3a_f9TXsyGAH5xL5Vsz17yHaEmpzrWWvmHinlGgD5icgFd7hCpCYycFinRiWA6YXIo0jZFDT_Tsvaf12WtnIApC8sRZZFY1ambuS5UToBc-BcavWoRAh8b0dtwtJ5rxNKQOZoFVTu2Qg80KbdJLjf9FdzOKsPmy5jWi7AvBuaWQDv7vCzlqbPTl8f7danh6siMod_J7KdxzraZCPHf7pZyPvv_VkckR9bboDOvIVnBkMfvr6l4K0AGngcFHASC6zJk9sArXUwPdYfzeqRHQScKeQKA";
    }

    private String getJwtWithoutPrefix() {
        return "eyJraWQiOiIzS2t4ZkZmZjU0XC9cLzFcL0VURDBxRmNEM1NUNUxSZDEzRUl5S0lRUCt5Um5NPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjOWRlMTBhMC1iMmIzLTQwN2EtYjYzZi0yZGUyOGRkMDQ1NDAiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMi5hbWF6b25hd3MuY29tXC91cy1lYXN0LTJfVG84d3g5eEU5IiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6InpzZWRlZmlhbiIsImF1ZCI6IjM2cG5oa2w4bHZjMnRtNWtqbWVzN2djOThlIiwiZXZlbnRfaWQiOiI5MTczNjM4MC1kZDNlLTRkN2EtOWVkYS02ZTE5NDc2Y2FlYWIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4MzI3Mjk2NywicGhvbmVfbnVtYmVyIjoiKzE1MTg0Mjg1NjY0IiwiZXhwIjoxNTgzNDI5NjM2LCJpYXQiOjE1ODM0MjYwMzYsImVtYWlsIjoiemFjaEBudXZhbGVuY2UuaW8ifQ.CVNAkzHtGKBufPdofNko5F2VGp33sVIFbCB9E29vxptDrIEWyV1ZJ7o5T5qC3a_f9TXsyGAH5xL5Vsz17yHaEmpzrWWvmHinlGgD5icgFd7hCpCYycFinRiWA6YXIo0jZFDT_Tsvaf12WtnIApC8sRZZFY1ambuS5UToBc-BcavWoRAh8b0dtwtJ5rxNKQOZoFVTu2Qg80KbdJLjf9FdzOKsPmy5jWi7AvBuaWQDv7vCzlqbPTl8f7danh6siMod_J7KdxzraZCPHf7pZyPvv_VkckR9bboDOvIVnBkMfvr6l4K0AGngcFHASC6zJk9sArXUwPdYfzeqRHQScKeQKA";
    }
}
