package com.cien.economy;

import com.cien.data.Properties;

public class CienEconomy {
	
	public static final CienEconomy ECONOMY = new CienEconomy();
	
	private CienEconomy() {
		
	}
	
	public LongDecimal getPlayerMoney(String player) {
		Properties prop = Properties.getProperties(player);
		String money = prop.get("money");
		if (money == null) {
			return LongDecimal.valueOf(0);
		}
		return LongDecimal.parse(money);
	}
	
	public void setPlayerMoney(String player, LongDecimal dec) {
		Properties prop = Properties.getProperties(player);
		prop.set("money", dec.toString());
	}
	
	public void addPlayerMoney(String player, LongDecimal dec) {
		setPlayerMoney(player, getPlayerMoney(player).sum(dec));
	}
	
	public boolean removePlayerMoney(String player, LongDecimal dec) {
		LongDecimal money = getPlayerMoney(player);
		if (money.isBiggerThan(dec) || money.equals(money)) {
			LongDecimal result = money.minus(dec);
			setPlayerMoney(player, result);
			return true;
		}
		return false;
	}
}
