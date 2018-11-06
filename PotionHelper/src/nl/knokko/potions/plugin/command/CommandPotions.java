package nl.knokko.potions.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import nl.knokko.potions.plugin.PotionsPlugin;

public class CommandPotions implements CommandExecutor {
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("potionhelper")) {
			sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command!");
			return true;
		}
		if (args.length > 0) {
			if (args[0].equals("register")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getItemInHand();
					if (item != null && item.getType() == Material.POTION && item.getAmount() > 0) {
						if (args.length > 1) {
							PotionsPlugin.getPotions().put(args[1], item.clone());
							sender.sendMessage(ChatColor.GREEN + "The potion in your hand has been registered successfully.");
						} else {
							sender.sendMessage(ChatColor.RED + "You should use /potions register <potion name>");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Hold the potion you wish to register in your hand.");
					}
				} else {
					sender.sendMessage("Only players can register the potion in their hand");
				}
			} else if (args[0].equals("give")) {
				if (args.length > 1) {
					Player receiver = null;
					if (args.length > 2) {
						receiver = Bukkit.getPlayer(args[2]);
						if (receiver == null)
							sender.sendMessage(ChatColor.RED + "The player '" + args[2] + "' is not online.");
					} else if (sender instanceof Player){
						receiver = (Player) sender;
					} else {
						sender.sendMessage("You should use /potions give <potion name> <player name>");
					}
					if (receiver != null) {
						ItemStack potion = PotionsPlugin.getPotions().get(args[1]);
						if (potion != null) {
							receiver.getInventory().addItem(potion.clone());
							sender.sendMessage(ChatColor.GREEN + "The potion has been given to " + receiver.getName());
						} else {
							sender.sendMessage(ChatColor.RED + "There is no potion registered under name '" + args[1] + "'");
						}
					}
				} else {
					sender.sendMessage("You should use /potions give <potion name> [player name]");
				}
			} else if (args[0].equals("delete")) {
				if (args.length > 1) {
					if (PotionsPlugin.getPotions().remove(args[1]) != null) {
						sender.sendMessage(ChatColor.YELLOW + "Potion " + args[1] + " has been deleted.");
					} else {
						sender.sendMessage(ChatColor.RED + "There is no potion registered under name '" + args[1] + "'");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You should use /potions delete <potion name>");
				}
			} else if (args[0].equals("addeffect")) {
				if (args.length > 3) {
					Player target = null;
					if (args.length > 4) {
						target = Bukkit.getPlayer(args[4]);
						if (target == null)
							sender.sendMessage(ChatColor.RED + "The player '" + args[4] + "' is not online.");
					} else if (sender instanceof Player) {
						target = (Player) sender;
					} else {
						sender.sendMessage("You should use /potions addeffect <effect name> <duration> <level> <player>");
					}
					if (target != null) {
						ItemStack item = target.getItemInHand();
						if (item != null && item.getType() == Material.POTION && item.getAmount() > 0) {
							int potionType = -1;
							PotionEffectType type = PotionEffectType.getByName(args[1]);
							if (type != null)
								potionType = type.getId();
							else {
								try {
									potionType = Integer.parseInt(args[1]);
								} catch(NumberFormatException nfe) {
									sender.sendMessage("There is no potion effect with name or id '" + args[1] + "'");
								}
							}
							if (potionType != -1) {
								try {
									int duration = Integer.parseInt(args[2]);
									try {
										int level = Integer.parseInt(args[3]);
										net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
										NBTTagCompound tag = nms.getTag();
										if (tag == null)
											tag = new NBTTagCompound();
										// I'm not sure about the 10...
										NBTTagList effects = tag.getList("CustomPotionEffects", 10);
										if (effects == null)
											effects = new NBTTagList();
										NBTTagCompound newEffect = new NBTTagCompound();
										newEffect.setInt("Id", potionType);
										newEffect.setInt("Duration", duration);
										newEffect.setInt("Amplifier", level - 1);
										effects.add(newEffect);
										tag.set("CustomPotionEffects", effects);
										nms.setTag(tag);
										target.setItemInHand(CraftItemStack.asBukkitCopy(nms));
										sender.sendMessage(ChatColor.GREEN + "The effect has been added to the potion.");
									} catch (NumberFormatException nfe) {
										sender.sendMessage(ChatColor.RED + "The level (" + args[3] + ") should be an integer.");
									}
								} catch (NumberFormatException nfe) {
									sender.sendMessage(ChatColor.RED + "The duration (" + args[2] + ") should be an integer.");
								}
							}
						} else {
							if (target == sender)
								sender.sendMessage(ChatColor.RED + "You need to hold the potion in your hand.");
							else
								sender.sendMessage(ChatColor.RED + target.getName() + " should hold the potion in his hand.");
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You should use /potions addeffect <effect name> <duration> <level> [player]");
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Use " + command.getUsage());
		}
		return true;
	}
}