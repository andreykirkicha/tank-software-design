package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeTest {
    @Test
    public void storesCoordinates() {
        Tree tree = new Tree(2, 3);

        assertEquals(2, tree.getX());
        assertEquals(3, tree.getY());
    }
}
