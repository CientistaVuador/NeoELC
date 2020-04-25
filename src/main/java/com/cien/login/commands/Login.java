package com.cien.login.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.login.CienLogin;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class Login extends CienCommandBase {

	public Login() {
		super("login", "Efetua login no servidor");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (Properties.hasProperties(sender.getCommandSenderName())) {
			if (args.length < 1) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /login <Senha>")));
			} else {
				Properties prop = Properties.getProperties(sender.getCommandSenderName());
				String pas1 = args[0];
				String pas2 = prop.get("password");
				if (pas1.equals(pas2)) {
					boolean b = CienLogin.NEED_LOGIN.remove((EntityPlayerMP)sender);
					if (b) {
			    		ItemStack[][] stack = (ItemStack[][]) prop.getMemory("storedInv");
			    		if (stack != null) {
			    			ItemStack[] armor = stack[0];
			    			ItemStack[] inv = stack[1];
			    			EntityPlayerMP mp = (EntityPlayerMP)sender;
			    			mp.inventory.armorInventory = armor;
			    			mp.inventory.mainInventory = inv;
			    		}
						sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
					} else {
						sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Você já está logado.")));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Senha incorreta.")));
				}
			}
		} else {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Você não está registrado")));
		}
	}

}
