package org.cloudfoundry.identity.uaa.login;

import com.google.inject.internal.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.env.MockPropertySource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TileInfoTest {

    private TileInfo tileInfo;
    private ObjectPropertyMockEnvironment environment = new ObjectPropertyMockEnvironment();
    private ArrayList<LinkedHashMap<String,String>> tiles;

    @Before
    public void setUp() throws Exception {
        LinkedHashMap<String,String> tile1 = new LinkedHashMap<>();
        tile1.put("login-link", "http://example.com/login");
        tile1.put("signup-link", "http://example.com/signup");

        LinkedHashMap<String,String> tile2 = new LinkedHashMap<>();
        tile2.put("login-link", "http://example.com/login");
        tile2.put("signup-link", "http://example.com/signup");

        LinkedHashMap<String,String> tile3 = new LinkedHashMap<>();
        tile3.put("login-link", "http://example.com/login");

        tiles = Lists.newArrayList(tile1, tile2, tile3);
    }

    @Test
    public void testLoginTiles() throws Exception {
        environment.setProperty("tiles", tiles);
        tileInfo = new TileInfo(environment);

        List<Map<String, String>> loginTiles = tileInfo.getLoginTiles();

        assertEquals(3, loginTiles.size());
        for (Map<String, String> loginTile : loginTiles) {
            assertFalse(StringUtils.isEmpty(loginTile.get("login-link")));
        }
    }

    @Test
    public void testLoginNoTiles() throws Exception {
        tileInfo = new TileInfo(environment);

        List<Map<String, String>> loginTiles = tileInfo.getLoginTiles();

        assertEquals(0, loginTiles.size());
    }

    @Test
    public void testSignupTiles() throws Exception {
        environment.setProperty("tiles", tiles);
        tileInfo = new TileInfo(environment);

        List<Map<String, String>> signupTiles = tileInfo.getSignupTiles();

        assertEquals(2, signupTiles.size());
        for (Map<String, String> loginTile : signupTiles) {
            assertFalse(StringUtils.isEmpty(loginTile.get("signup-link")));
        }
    }

    @Test
    public void testSignupNoTiles() throws Exception {
        tileInfo = new TileInfo(environment);

        List<Map<String, String>> signupTiles = tileInfo.getSignupTiles();

        assertEquals(0, signupTiles.size());
    }

    /**
     * Exposes {@link MockPropertySource}'s ability to set Objects as property values.
     * This is in contrast to {@link org.springframework.mock.env.MockEnvironment},
     * which only allows setting of String values.
     */
    private static class ObjectPropertyMockEnvironment extends AbstractEnvironment {

        private MockPropertySource propertySource = new MockPropertySource();

        private ObjectPropertyMockEnvironment() {
            getPropertySources().addLast(propertySource);
        }

        public void setProperty(String key, Object value) {
            propertySource.setProperty(key, value);
        }
    }
}