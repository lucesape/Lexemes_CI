package ru.muravev;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SolutionTest {

    Solution solution = new Solution();

    @Test
    public void multipleInt() {
        Assert.assertEquals(54, solution.multipleInt(1,4));
    }
}