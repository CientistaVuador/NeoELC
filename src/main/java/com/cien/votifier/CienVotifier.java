package com.cien.votifier;

import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import com.cien.Util;
import com.cien.data.Node;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CienVotifier implements VoteListener {

	public static final CienVotifier VOTIFIER = new CienVotifier();
	
	private final Properties prop = Properties.getProperties("(Module)CienVotifier");
	private final List<ItemStack> items = new ArrayList<>();
	private Votifier votifier = null;
	
	private CienVotifier() {
		VoteListenerManager.addListener(this);
		Util.schedule("Read Votes on Server Thread", () -> {
			VoteListenerManager.callListeners();
		}, 20);
		try {
			System.out.println("Carregando keys do votifier");
			KeyPair pair = KeyManager.readKeyPair();
			if (pair == null) {
				pair = KeyManager.newPair();
				KeyManager.writeKeyPair(pair);
				System.out.println("Novo par de keys do votifier geradas");
			}
			System.out.println("Keys do votifier carregadas.");
			votifier = new Votifier(pair, 1245);
			votifier.start();
			System.out.println("Votifier iniciado na porta 1245");
		} catch (IOException ex) {
			System.out.println("Não foi possível iniciar o votifier");
			ex.printStackTrace();
		}
		Util.run("Load Items", () -> {
			Node items = prop.getNode("items");
			for (Node n:items.getNodes()) {
				this.items.add(Util.getItemStackFromNode(n));
			}
		}, 3);
	}
	
	public ItemStack[] getItems() {
		ItemStack[] array = new ItemStack[items.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = items.get(i).copy();
		}
		return array;
	}
	
	public boolean setItem(ItemStack s, boolean nbt) {
		s = s.copy();
		if (!nbt) {
			s.setTagCompound(new NBTTagCompound());
		}
		for (ItemStack f:items.toArray(new ItemStack[items.size()])) {
			if (Item.getIdFromItem(f.getItem()) == Item.getIdFromItem(s.getItem())) {
				if (f.getItemDamage() == s.getItemDamage()) {
					NBTTagCompound a = f.getTagCompound();
					NBTTagCompound b = s.getTagCompound();
					if (a != null && b != null && nbt) {
						if (a.equals(b)) {
							items.remove(f);
							return false;
						}
					} else {
						items.remove(f);
						return false;
					}
				}
			}
		}
		items.add(s);
		Node f = new Node("items");
		int index = 0;
		for (ItemStack g:getItems()) {
			f.addNode(Util.getNodeFromItemStack(Integer.toString(index), g, true));
			index++;
		}
		prop.setNode("items", f);
		return true;
	}
	
	public void setLink(String link) {
		prop.set("voteLink", link);
	}
	
	public String getLink() {
		return prop.get("voteLink");
	}
	
	public void setVoteNumberFor(String player, int votes) {
		Properties prop = Properties.getProperties(player);
		prop.set("votes", Integer.toString(votes));
	}
	
	public int getVoteNumberFor(String player) {
		Properties prop = Properties.getProperties(player);
		String votes = prop.get("votes");
		if (votes == null) {
			return 0;
		}
		return Integer.parseInt(votes);
	}
	
	@Override
	public void onVote(Vote v) {
		String player = Util.getPlayerInexact(v.getNick());
		if (player == null) {
			Util.sendMessageToEveryone(Util.getPrefix()+v.getNick()+" Votou mas não foi encontrado.");
		} else {
			setVoteNumberFor(player, getVoteNumberFor(player)+1);
			Util.sendMessageToEveryone(Util.getPrefix()+player+" Votou, vote com /vote");
			CienDiscord.DISCORD.sendGlobalMessage(":regional_indicator_v: "+player+" Votou!");
		}
	}

}
