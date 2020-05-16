package com.cien.kits.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.kits.CienKits;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class KitBuilder extends CienCommandBase {

	public KitBuilder() {
		super("kbuilder", "Construtor de kits.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.kbuilder")) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
			return;
		}
		if (args.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder <new/delete/items/set/kits>"));
		} else {
			switch (args[0]) {
			case "new":
				onNew(player, args);
				return;
			case "delete":
				onDelete(player, args);
				return;
			case "items":
				onItems(player, args);
				return;
			case "set":
				onSet(player, args);
				return;
			case "kits":
				onKits(player, args);
				return;
			}
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder <new/delete/items/set/kits>"));
		}
	}
	
	private void onNew(EntityPlayerMP player, String[] args) {
		if (args.length != 2) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder new <Nome do Kit>"));
		} else {
			if (CienKits.KITS.containsKit(args[1])) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit já existe."));
			} else {
				com.cien.kits.Kit t = new com.cien.kits.Kit(args[1]);
				CienKits.KITS.addKit(t);
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Kit '"+args[1]+"' criado com sucesso!"));
			}
		}
	}
	
	private void onDelete(EntityPlayerMP player, String[] args) {
		if (args.length != 2) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder delete <Nome do Kit>"));
		} else {
			if (!CienKits.KITS.containsKit(args[1])) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit não existe."));
			} else {
				com.cien.kits.Kit t = CienKits.KITS.getKit(args[1]);
				CienKits.KITS.removeKit(t);
				t.getProp().delete();
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Kit '"+args[1]+"' removido com sucesso!"));
			}
		}
	}
	
	private void onItems(EntityPlayerMP player, String[] args) {
		if (args.length < 3) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder items <Nome do Kit> <add/remove/list>"));
		} else {
			if (!CienKits.KITS.containsKit(args[1])) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit não existe."));
			} else {
				com.cien.kits.Kit t = CienKits.KITS.getKit(args[1]);
				switch (args[2]) {
				case "add":
					onAdd(player, t, args);
					return;
				case "remove":
					onRemove(player, t, args);
					return;
				case "list":
					onList(player, t, args);
					return;
				}
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder items <Nome do Kit> <add/remove/list>"));
			}
		}
	}
	
	private void onAdd(EntityPlayerMP player, com.cien.kits.Kit k, String[] args) {
		ItemStack s = player.getCurrentEquippedItem();
		if (s == null) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Coloque o item em sua mão."));
		} else {
			k.addItemStack(s);
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Item '"+Util.getItemNameID(s.getItem())+":"+s.getItemDamage()+"' foi adicionado ao kit."));
		}
	}
	
	private void onRemove(EntityPlayerMP player, com.cien.kits.Kit k, String[] args) {
		if (args.length != 4) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder items "+k.getName()+" remove <Index>"));
		} else {
			int index;
			try {
				index = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage()));
				return;
			}
			try {
				k.removeItemStack(index);
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Item Removido com sucesso, cheque a lista novamente, os indexes mudaram."));
			} catch (IndexOutOfBoundsException ex) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Index inválido."));
			}
		}
	}
	
	private void onList(EntityPlayerMP player, com.cien.kits.Kit k, String[] args) {
		int size = k.getSize();
		if (size == 0) {
			player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit não possui itens."));
		} else {
			player.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"Lista de itens em "+k.getName()+": "));
			for (int i = 0; i < size; i++) {
				ItemStack s = k.getItemStack(i);
				player.addChatComponentMessage(Util.fixColors(" §6"+i+"- "+s.stackSize+" de '"+s.getDisplayName()+"'"));
			}
		}
	}
	
	private void onSet(EntityPlayerMP player, String[] args) {
		if (args.length < 3) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder set <Nome do Kit> <keepNbt/delay>"));
		} else {
			if (!CienKits.KITS.containsKit(args[1])) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit não existe."));
			} else {
				com.cien.kits.Kit t = CienKits.KITS.getKit(args[1]);
				switch (args[2]) {
				case "keepNbt":
					onKeepNbt(player, t, args);
					return;
				case "delay":
					onDelay(player, t, args);
					return;
				}
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder set <Nome do Kit> <keepNbt/delay>"));
			}
		}
	}
	
	private void onKeepNbt(EntityPlayerMP player, com.cien.kits.Kit k, String[] args) {
		boolean keepNbt = !k.isKeepNbt();
		k.setKeepNbt(keepNbt);
		player.addChatMessage(Util.fixColors(Util.getPrefix()+"keepNbt de '"+k.getName()+"' alterado para '"+keepNbt+"'"));
	}
	
	private void onDelay(EntityPlayerMP player, com.cien.kits.Kit k, String[] args) {
		if (args.length != 4) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /kbuilder set "+k.getName()+" delay <Delay em ms>"));
		} else {
			long delay;
			try {
				delay = Long.parseLong(args[3]);
			} catch (NumberFormatException ex) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage()));
				return;
			}
			k.setDelay(delay);
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Delay de '"+k.getName()+"' alterado para "+k.getDelay()+"ms"));
		}
	}
	
	private void onKits(EntityPlayerMP player, String[] args) {
		com.cien.kits.Kit[] kits = CienKits.KITS.getKits();
		if (kits.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Não há kits criados."));
		} else {
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Kits:"));
			for (com.cien.kits.Kit k:kits) {
				player.addChatMessage(Util.fixColors(" §6N: "+k.getName()+", D: "+k.getDelay()+", P: "+k.getPermission()+", K: "+k.isKeepNbt()));
			}
		}
	}

}
