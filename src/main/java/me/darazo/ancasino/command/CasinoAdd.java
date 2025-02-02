package me.darazo.ancasino.command;

import java.util.Random;
import org.bukkit.entity.Player;

import me.darazo.ancasino.AnCasino;
import me.darazo.ancasino.slot.SlotMachine;

public class CasinoAdd extends AnCommand {
	
	private String name;
	private String type;
	private String owner;
	private String world;
	
	// Command for adding unmanaged slot machine
	public CasinoAdd(AnCasino plugin, String[] args, Player player) {
		super(plugin, args, player);
	}
	
	public Boolean process() {
		
		// Permissions
		if(!plugin.permission.canCreate(player)) {
			noPermission();
			return true;
		}
		// Valid command format
		if(args.length >= 1 && args.length <= 2) {
						
			Random ra = new Random();
			String leName = player.getName()+ ra.nextInt();
			// Slot does not exist
			if(!plugin.slotData.isSlot(leName)) {
							
				this.name = leName;
								
					// Valid type
				if(args.length < 3) {
					
					this.type = "default";
				}
				
				else if(plugin.typeData.isType(args[1])) {
					String typeName = args[1];
					
					// Has type permission
					if(!plugin.permission.canCreate(player, typeName)) {
						sendMessage("Invalid type " + typeName);
						return true;
					}
					else {
						this.type = typeName;
					}
				}
				
				// Invalid type
				else {
					sendMessage("Invalid type " + args[1]);
					return true;
				}
				
				this.owner = player.getName();
				
				// Creation cost
				Double createCost = plugin.typeData.getType(type).getCreateCost();
				if(plugin.economy.has(owner, createCost)) {
					plugin.economy.withdrawPlayer(owner, createCost);
				}
				else {
					sendMessage(player.getName());
					sendMessage("You can't afford to create this slot machine. Cost: " + createCost);
					return true;
				}
				
				// Good to go
				this.world = player.getWorld().getName();
				SlotMachine slot = new SlotMachine(name, type, owner, world, false);
				plugin.slotData.toggleCreatingSlots(player, slot);
				sendMessage("Punch a block to serve as the base for this slot machine.");
			}
			
			// Slot exists
			else {
				sendMessage("Slot machine " + args[1] +" already registered.");
			}
		}
		
		// incorrect command format
		else {
			sendMessage("Usage:");
			sendMessage("/casino add <name> (<type>)");
		}
		return true;

	}

}
