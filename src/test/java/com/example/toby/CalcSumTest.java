package com.example.toby;


import com.example.toby.jiw.템플릿콜백.Calculator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

public class CalcSumTest {

    Calculator calculator;
    String numFilepath;

    @BeforeEach
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilepath = getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilepath), Matchers.is(10));
    }

    @Test
    public void multiOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(numFilepath), Matchers.is(24));
    }

    @Test
    public void addStringNumbers() throws IOException {
        assertThat(calculator.addString(numFilepath), Matchers.is("1234"));
    }
}
