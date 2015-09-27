package buildcraft.core.lib;

import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import net.minecraft.item.Item;

import buildcraft.api.transport.ObjectDefinition;
import buildcraft.api.transport.ObjectDefinitionFactory;
import buildcraft.api.transport.PipeDefinition;

public class HashDefinitionMapTester {

    @Test
    public void testEmpty() {
        HashDefinitionMap<Item, PipeDefinition> map = HashDefinitionMap.create();

        // Check to make sure that it never returns null
        Assert.assertNotNull(map.getTripleSet());

        // Check to make sure the set is truly empty
        Assert.assertEquals(true, map.getTripleSet().isEmpty());

        // Check to make sure that it really doesn't contain any nulls
        Assert.assertEquals(false, map.containsTag(null));
        Assert.assertEquals(false, map.containsItem(null));
        Assert.assertEquals(false, map.containsDefinition(null));

        // Make sure it returns null for all keys
        Assert.assertNull(map.getTag((Item) null));
        Assert.assertNull(map.getTag((PipeDefinition) null));
        Assert.assertNull(map.getItem((String) null));
        Assert.assertNull(map.getItem((PipeDefinition) null));
        Assert.assertNull(map.getDefinition((String) null));
        Assert.assertNull(map.getDefinition((Item) null));
    }

    @Test
    public void testSingleKeyValue() {
        HashDefinitionMap<Item, ObjectDefinition> map = HashDefinitionMap.create();

        Item testItem = new Item();
        // Unfortunately we cannot actually test the construction of any ObjectDefinitions
        ObjectDefinition testDefinition = ObjectDefinitionFactory.create("mod", "unique_tag");

        String tag = testDefinition.globalUniqueTag;

        map.put(testItem, testDefinition);

        // Tag Testing
        Assert.assertEquals(true, map.containsTag(tag));
        Assert.assertEquals(tag, map.getTag(testItem));
        Assert.assertEquals(tag, map.getTag(testDefinition));

        // Item Testing
        Assert.assertEquals(true, map.containsItem(testItem));
        Assert.assertEquals(testItem, map.getItem(testDefinition));
        Assert.assertEquals(testItem, map.getItem(tag));

        // Definition Testing
        Assert.assertEquals(true, map.containsDefinition(testDefinition));
        Assert.assertEquals(testDefinition, map.getDefinition(tag));
        Assert.assertEquals(testDefinition, map.getDefinition(testItem));

        // Set testing
        Set<Triple<String, Item, ObjectDefinition>> set = map.getTripleSet();
        Assert.assertEquals(1, set.size());

        Triple<String, Item, ObjectDefinition> triple = set.iterator().next();
        Assert.assertNotNull(triple);
        Assert.assertEquals(tag, triple.getLeft());
        Assert.assertEquals(testItem, triple.getMiddle());
        Assert.assertEquals(testDefinition, triple.getRight());
    }

    @Test(expected = NullPointerException.class)
    public void testNullKey() {
        HashDefinitionMap<Item, ObjectDefinition> map = HashDefinitionMap.create();
        map.put(null, null);
        Assert.fail("This should have thrown an exception already!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateTag() {
        HashDefinitionMap<Item, ObjectDefinition> map = HashDefinitionMap.create();

        Item testItem1 = new Item().setUnlocalizedName("testItem1");
        Item testItem2 = new Item().setUnlocalizedName("testItem2");

        ObjectDefinition def1 = ObjectDefinitionFactory.create("example", "definition");
        ObjectDefinition def2 = ObjectDefinitionFactory.create("example", "definition");

        map.put(testItem1, def1);
        map.put(testItem2, def2);
        Assert.fail("This should have thrown an exception already!");
    }
}
