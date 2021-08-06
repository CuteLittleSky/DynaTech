package me.profelements.dynatech.items.misc;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.data.PersistentDataAPI;
import me.profelements.dynatech.DynaTech;
import net.guizhanss.minecraft.dynatech.utils.ItemBandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemBand extends SlimefunItem {

    public static final NamespacedKey KEY = new NamespacedKey(DynaTech.getInstance(), "item_band");
    private final PotionEffect[] potionEffects;

    public ItemBand(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, PotionEffect[] potionEffects) {
        super(category, item, recipeType, recipe);

        this.potionEffects = potionEffects;
    }

    public static boolean containsItemBand(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
            return PersistentDataAPI.getString(item.getItemMeta(), KEY) != null;
        } else {
            return false;
        }
    }

    @Nullable
    public static ItemStack removeFromItem(@Nullable ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta im = item.getItemMeta();
            List<String> lore = im.getLore();

            im.getPersistentDataContainer().remove(KEY);

            lore.removeIf(line -> line.contains(ChatColor.WHITE + "物品模组效果: "));

            im.setLore(lore);
            item.setItemMeta(im);

            return item;
        }
        return null;
    }

    public PotionEffect[] getPotionEffects() {
        return potionEffects;
    }

    @Nullable
    public ItemStack applyToItem(@Nullable ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {


            ItemMeta im = item.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();

            lore.add(ChatColor.WHITE + "物品模组效果: " + ItemBandUtils.getBandaid(getPotionEffects()[0].getType().getName()));
            PersistentDataAPI.setString(im, KEY, this.getId());

            im.setLore(lore);
            item.setItemMeta(im);
            return item;
        }
        return null;
    }

}
