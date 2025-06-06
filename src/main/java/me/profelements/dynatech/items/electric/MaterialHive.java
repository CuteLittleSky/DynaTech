package me.profelements.dynatech.items.electric;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.profelements.dynatech.items.abstracts.AbstractElectricMachine;
import me.profelements.dynatech.items.misc.Bee;
import me.profelements.dynatech.registries.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MaterialHive extends AbstractElectricMachine implements Radioactive {

    private static final int[] BACKGROUND_SLOTS = new int[] { 0, 1, 2, 6, 7, 8, 31, 36, 37, 38, 39, 40, 41, 42, 43,
            44 };
    private static final int[] INPUT_BORDER_SLOTS = new int[] { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private static final int[] OUTPUT_BORDER_SLOTS = new int[] { 14, 15, 16, 17, 23, 26, 32, 33, 34, 35 };

    private static final int[] BORDER_KEY = new int[] { 3, 5, 13 };
    private static final SlimefunItemStack UI_KEY = new SlimefunItemStack("_UI_KEY",
            Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ");

    private static final int[] INPUT_SLOTS = new int[] { 19, 20, 4 };
    private static final int[] OUTPUT_SLOTS = new int[] { 24, 25 };

    public final ItemSetting<List<String>> vanillaItemsAccepted = new ItemSetting<>(this, "vanilla-items-accepted",
            getDefaultAllowedVanillaItems());
    public final ItemSetting<List<String>> slimefunItemsAccepted = new ItemSetting<>(this, "slimefun-items-accepted",
            getDefaultAllowedSlimefunItems());

    public MaterialHive(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemSetting(vanillaItemsAccepted, slimefunItemsAccepted);
    }

    @Override
    public MachineRecipe findNextRecipe(BlockMenu inv) {
        ItemStack key = inv.getItemInSlot(getInputSlots()[2]);
        for (MachineRecipe recipe : recipes) {
            ItemStack input = recipe.getInput()[0].clone();
            if (SlimefunUtils.isItemSimilar(key, input, true) && key.getAmount() == 64) {

                int seconds = 1800;
                ItemStack b1 = inv.getItemInSlot(getInputSlots()[0]);
                ItemStack b2 = inv.getItemInSlot(getInputSlots()[1]);

                if (b1 != null) {
                    SlimefunItem bee1 = SlimefunItem.getByItem(b1);
                    if (bee1 instanceof Bee) {
                        seconds -= ((Bee) bee1).getSpeedMultipler() * b1.getAmount();
                    }
                }
                if (b2 != null) {
                    SlimefunItem bee2 = SlimefunItem.getByItem(b2);
                    if (bee2 instanceof Bee) {
                        seconds -= ((Bee) bee2).getSpeedMultipler() * b2.getAmount();
                    }

                    if (b1 != null && SlimefunUtils.isItemSimilar(b1, b2, true) && b1.getAmount() == 64
                            && b2.getAmount() == 64) {
                        if (bee2.getId().equals(Items.BEE.stack().getItemId())) {
                            seconds = 1500;
                        }
                        if (bee2.getId().equals(Items.ROBOTIC_BEE.stack().getItemId())) {
                            seconds = 900;
                        }
                        if (bee2.getId().equals(Items.ADVANCED_ROBOTIC_BEE.stack().getItemId())) {
                            seconds = 300;
                        }
                    }
                }
                input.setAmount(1);
                return new MachineRecipe(seconds, new ItemStack[] { recipe.getInput()[0] }, new ItemStack[] { input });
            }
        }
        return null;
    }

    public void registerDefaultHiveRecipes() {
        for (String slimefunItem : slimefunItemsAccepted.getValue()) {
            SlimefunItem sfItem = SlimefunItem.getById(slimefunItem);
            if (sfItem != null) {
                ItemStack item = sfItem.getItem().clone();
                item.setAmount(64);
                registerRecipe(new MachineRecipe(1800, new ItemStack[] { item },
                        new ItemStack[] { SlimefunItem.getById(slimefunItem).getItem() }));

            }
        }
        for (String material : vanillaItemsAccepted.getValue()) {
            ItemStack item = new ItemStack(Material.matchMaterial(material), 64);
            registerRecipe(new MachineRecipe(1800, new ItemStack[] { item },
                    new ItemStack[] { new ItemStack(Material.matchMaterial(material)) }));
        }
    }

    @Override
    public void postRegister() {
        super.postRegister();
        registerDefaultHiveRecipes();
    }

    @Nonnull
    @Override
    public Radioactivity getRadioactivity() {
        return Radioactivity.HIGH;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.NETHERITE_HOE);
    }

    private static List<String> getDefaultAllowedVanillaItems() {
        List<String> materialsAllowed = new ArrayList<>();

        materialsAllowed.add("IRON_INGOT");
        materialsAllowed.add("GOLD_INGOT");
        materialsAllowed.add("EMERALD");
        materialsAllowed.add("LAPIS_LAZULI");
        materialsAllowed.add("QUARTZ");
        materialsAllowed.add("REDSTONE");
        materialsAllowed.add("COAL");

        return materialsAllowed;
    }

    private static List<String> getDefaultAllowedSlimefunItems() {
        List<String> sfItemsAllowed = new ArrayList<>();

        // Ingots
        sfItemsAllowed.add("COPPER_INGOT");
        sfItemsAllowed.add("TIN_INGOT");
        sfItemsAllowed.add("SILVER_INGOT");
        sfItemsAllowed.add("ALUMINUM_INGOT");
        sfItemsAllowed.add("LEAD_INGOT");
        sfItemsAllowed.add("ZINC_INGOT");
        sfItemsAllowed.add("MAGNESIUM_INGOT");

        // Alloys
        sfItemsAllowed.add("STEEL_INGOT");
        sfItemsAllowed.add("DURALUMIN_INGOT");
        sfItemsAllowed.add("BILLON_INGOT");
        sfItemsAllowed.add("BRASS_INGOT");
        sfItemsAllowed.add("ALUMINUM_BRASS_INGOT");
        sfItemsAllowed.add("ALUMINUM_BRONZE_INGOT");
        sfItemsAllowed.add("CORINTHIAN_BRONZE_INGOT");
        sfItemsAllowed.add("SOLDER_INGOT");
        sfItemsAllowed.add("DAMASCUS_STEEL_INGOT");
        sfItemsAllowed.add("FERROSILICON");
        sfItemsAllowed.add("GILDED_IRON");
        sfItemsAllowed.add("NICKEL_INGOT");
        sfItemsAllowed.add("COBALT_INGOT");
        sfItemsAllowed.add("REDSTONE_ALLOY"); 
        //Gems
        sfItemsAllowed.add("SYNTHETIC_EMERALD");
        sfItemsAllowed.add("SYNTHETIC_SAPPHIRE");

        return sfItemsAllowed;
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> display = new ArrayList<>();

        for (MachineRecipe recipe : recipes) {
            display.add(recipe.getInput()[0]);
            display.add(recipe.getOutput()[0]);
        }

        return display;

    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        for (int slot : BACKGROUND_SLOTS) {
            preset.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : INPUT_BORDER_SLOTS) {
            preset.addItem(slot, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : OUTPUT_BORDER_SLOTS) {
            preset.addItem(slot, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(getProgressSlot(), new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "),
                ChestMenuUtils.getEmptyClickHandler());

        for (int slot : getOutputSlots()) {
            preset.addMenuClickHandler(slot, new ChestMenu.AdvancedMenuClickHandler() {
                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor,
                        ClickAction action) {
                    return cursor.getType().isAir();
                }

                @Override
                public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
                    return false;
                }
            });
        }
        ;

        preset.drawBackground(UI_KEY, BORDER_KEY);
    }

    @Override
    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Override
    protected int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

}
