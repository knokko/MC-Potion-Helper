package nl.knokko.potions.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.potions.plugin.command.CommandPotions;

public class PotionsPlugin extends JavaPlugin {
	
	public static PotionsPlugin INSTANCE;
	
	public static Map<String,ItemStack> getPotions(){
		return INSTANCE.potions;
	}
	
	private Map<String,ItemStack> potions;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		potions = new HashMap<String,ItemStack>();
		reloadConfig();
		FileConfiguration config = getConfig();
		ConfigurationSection potionsSection = config.getConfigurationSection("potions");
		Set<String> keys = potionsSection.getKeys(false);
		for (String key : keys)
			potions.put(key, potionsSection.getItemStack(key));
		getCommand("potions").setExecutor(new CommandPotions());
	}
	
	@Override
	public void onDisable() {
		FileConfiguration config = getConfig();
		ConfigurationSection potionsSection = config.createSection("potions");
		Set<Entry<String,ItemStack>> entrySet = potions.entrySet();
		for (Entry<String,ItemStack> entry : entrySet)
			potionsSection.set(entry.getKey(), entry.getValue());
		saveConfig();
		INSTANCE = null;
	}
}